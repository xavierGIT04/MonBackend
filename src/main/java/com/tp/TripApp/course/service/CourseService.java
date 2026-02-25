package com.tp.TripApp.course.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tp.TripApp.course.dto.*;
import com.tp.TripApp.course.entity.Course;
import com.tp.TripApp.course.entity.LocalisationConducteur;
import com.tp.TripApp.course.enums.ModePaiement;
import com.tp.TripApp.course.enums.StatutCourse;
import com.tp.TripApp.course.enums.StatutPaiement;
import com.tp.TripApp.course.repository.CourseRepository;
import com.tp.TripApp.course.repository.LocalisationConducteurRepository;
import com.tp.TripApp.course.utils.GeometryUtils;
import com.tp.TripApp.security.entity.CompteUtilisateur;
import com.tp.TripApp.security.entity.ProfilConducteur;
import com.tp.TripApp.security.enums.Statut_Service;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CourseService {

    private static final double RAYON_RECHERCHE_METRES = 5_000;  // 5 km
    private static final double RAYON_CARTE_METRES     = 10_000; // 10 km

    private final CourseRepository courseRepository;
    private final LocalisationConducteurRepository localisationRepository;

    public CourseService(CourseRepository courseRepository,
                         LocalisationConducteurRepository localisationRepository) {
        this.courseRepository = courseRepository;
        this.localisationRepository = localisationRepository;
    }

    // ─── Utilitaire auth ──────────────────────────────────────────────────────

    private CompteUtilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CompteUtilisateur) auth.getPrincipal();
    }

    // ─── Estimation ──────────────────────────────────────────────────────────

    /**
     * Calcule distance et prix estimé AVANT commande.
     * La distance est calculée côté Java (Haversine).
     * PostGIS recalculera la distance réelle une fois la course créée.
     */
    public Map<String, Object> estimerCourse(
            double dLat, double dLng, double aLat, double aLng) {

        double distKm = GeometryUtils.distanceKm(dLat, dLng, aLat, aLng);
        double prix   = GeometryUtils.calculerPrix(distKm);

        return Map.of(
            "distance_km", Math.round(distKm * 10.0) / 10.0,
            "prix_estime", prix
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PARCOURS PASSAGER
    // ═══════════════════════════════════════════════════════════════════════

    /** 1. Passager commande une course */
    public CourseResponse commanderCourse(CommandeCourseRequest req) {
        CompteUtilisateur passager = getUtilisateurConnecte();

        // Vérifier absence de course active
        boolean courseActive = courseRepository.findFirstByPassagerAndStatutIn(
            passager,
            List.of(StatutCourse.EN_ATTENTE, StatutCourse.ACCEPTEE, StatutCourse.EN_COURS)
        ).isPresent();
        if (courseActive) throw new IllegalStateException("Vous avez déjà une course en cours");

        // Créer les Points PostGIS
        var departPoint = GeometryUtils.createPoint(req.getDepart_lat(), req.getDepart_lng());
        var destPoint   = GeometryUtils.createPoint(req.getDestination_lat(), req.getDestination_lng());

        // Distance et prix
        double distKm = GeometryUtils.distanceKm(
            req.getDepart_lat(), req.getDepart_lng(),
            req.getDestination_lat(), req.getDestination_lng()
        );

        Course course = new Course();
        course.setPassager(passager);
        course.setDepart_position(departPoint);
        course.setDepart_adresse(req.getDepart_adresse());
        course.setDestination_position(destPoint);
        course.setDestination_adresse(req.getDestination_adresse());
        course.setDistance_km(Math.round(distKm * 10.0) / 10.0);
        course.setPrix_estime(GeometryUtils.calculerPrix(distKm));
        course.setMode_paiement(
            ModePaiement.valueOf(req.getMode_paiement() != null ? req.getMode_paiement() : "ESPECES")
        );
        course.setStatut(StatutCourse.EN_ATTENTE);
        course.setDate_commande(LocalDateTime.now());

        Course saved = courseRepository.save(course);

        // PostGIS recalcule la distance exacte après persistance
        Double distPostGIS = courseRepository.calculerDistanceKm(saved.getId());
        if (distPostGIS != null) {
            saved.setDistance_km(Math.round(distPostGIS * 10.0) / 10.0);
            saved.setPrix_estime(GeometryUtils.calculerPrix(distPostGIS));
            saved = courseRepository.save(saved);
        }

        return CourseResponse.from(saved);
    }

    /** Course active du passager (polling) */
    @Transactional(readOnly = true)
    public CourseResponse getCourseActivePassager() {
        CompteUtilisateur passager = getUtilisateurConnecte();
        Course course = courseRepository.findFirstByPassagerAndStatutIn(
            passager,
            List.of(StatutCourse.EN_ATTENTE, StatutCourse.ACCEPTEE,
                    StatutCourse.EN_COURS, StatutCourse.ARRIVEE)
        ).orElseThrow(() -> new EntityNotFoundException("Aucune course active"));
        return CourseResponse.from(course);
    }

    /** Passager annule sa course */
    public CourseResponse annulerCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course introuvable"));
        if (course.getStatut() == StatutCourse.TERMINEE)
            throw new IllegalStateException("Impossible d'annuler une course terminée");

        course.setStatut(StatutCourse.ANNULEE);
        if (course.getConducteur() != null)
            course.getConducteur().setStatut_service(Statut_Service.LIBRE);

        return CourseResponse.from(courseRepository.save(course));
    }

    /** Paiement simulé (T-Money / Moov / Espèces) */
    public CourseResponse confirmerPaiement(Long courseId, PaiementRequest req) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course introuvable"));

        if (course.getStatut() != StatutCourse.ARRIVEE)
            throw new IllegalStateException("Le conducteur n'est pas encore arrivé à destination");
        if (req.getCode_pin() == null || req.getCode_pin().length() < 4)
            throw new IllegalArgumentException("Code PIN invalide");

        course.setMode_paiement(ModePaiement.valueOf(req.getMode_paiement()));
        course.setPrix_final(course.getPrix_estime());
        course.setStatut_paiement(StatutPaiement.CONFIRME);
        course.setStatut(StatutCourse.TERMINEE);
        course.setDate_fin(LocalDateTime.now());

        if (course.getConducteur() != null)
            course.getConducteur().setStatut_service(Statut_Service.LIBRE);

        return CourseResponse.from(courseRepository.save(course));
    }

    /** Passager note le conducteur (1-5 étoiles) */
    public CourseResponse noterConducteur(Long courseId, NotationRequest req) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course introuvable"));

        if (course.getStatut() != StatutCourse.TERMINEE)
            throw new IllegalStateException("La course doit être terminée pour noter");
        if (req.getNote() < 1 || req.getNote() > 5)
            throw new IllegalArgumentException("La note doit être entre 1 et 5");

        course.setNote_conducteur(req.getNote());
        course.setCommentaire(req.getCommentaire());
        courseRepository.save(course);

        // Recalculer la note moyenne via la DB
        if (course.getConducteur() != null) {
            ProfilConducteur conducteur = course.getConducteur();
            Double moyenne = courseRepository.calculerNoteMoyenne(conducteur);
            conducteur.setNote_moyenne(moyenne != null
                ? (float) (Math.round(moyenne * 10) / 10.0) : 0f);
        }

        return CourseResponse.from(course);
    }

    /** Historique passager */
    @Transactional(readOnly = true)
    public List<CourseResponse> getHistoriquePassager() {
        CompteUtilisateur passager = getUtilisateurConnecte();
        return courseRepository.findByPassagerOrderByDate_CommandeDesc(passager)
            .stream().map(CourseResponse::from).toList();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PARCOURS CONDUCTEUR
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Conducteur met à jour sa position GPS.
     * Utilise l'upsert PostGIS natif (INSERT ON CONFLICT UPDATE).
     */
    public void mettreAJourLocalisation(LocalisationRequest req) {
        CompteUtilisateur user = getUtilisateurConnecte();
        ProfilConducteur conducteur = user.getProfilConducteur();
        if (conducteur == null) throw new IllegalStateException("Pas un conducteur");

        // Upsert atomique via PostGIS
        localisationRepository.upsertLocalisation(
            conducteur.getId(),
            req.getLatitude(),
            req.getLongitude()
        );
    }

    /**
     * Courses EN_ATTENTE proches du conducteur — requête PostGIS.
     * Rayon : 5 km.
     */
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesProches() {
        CompteUtilisateur user = getUtilisateurConnecte();
        ProfilConducteur conducteur = user.getProfilConducteur();

        LocalisationConducteur loc = localisationRepository
            .findByConducteur(conducteur)
            .orElseThrow(() -> new EntityNotFoundException("Position non définie. Activez votre GPS."));

        return courseRepository.findCoursesEnAttenteProches(
            loc.getLatitude(), loc.getLongitude(), RAYON_RECHERCHE_METRES
        ).stream().map(CourseResponse::from).toList();
    }

    /** Conducteur accepte une course */
    public CourseResponse accepterCourse(Long courseId) {
        CompteUtilisateur user = getUtilisateurConnecte();
        ProfilConducteur conducteur = user.getProfilConducteur();

        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course introuvable"));

        if (course.getStatut() != StatutCourse.EN_ATTENTE)
            throw new IllegalStateException("Cette course n'est plus disponible");

        course.setConducteur(conducteur);
        course.setStatut(StatutCourse.ACCEPTEE);
        course.setDate_acceptation(LocalDateTime.now());
        conducteur.setStatut_service(Statut_Service.OCCUPE);

        return CourseResponse.from(courseRepository.save(course));
    }

    /** Conducteur démarre (passager à bord) */
    public CourseResponse demarrerCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course introuvable"));
        course.setStatut(StatutCourse.EN_COURS);
        course.setDate_debut(LocalDateTime.now());
        return CourseResponse.from(courseRepository.save(course));
    }

    /** Conducteur signale l'arrivée → déclenche paiement côté passager */
    public CourseResponse signalerArrivee(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course introuvable"));
        course.setStatut(StatutCourse.ARRIVEE);
        return CourseResponse.from(courseRepository.save(course));
    }

    /** Course active conducteur (polling) */
    @Transactional(readOnly = true)
    public CourseResponse getCourseActiveConducteur() {
        CompteUtilisateur user = getUtilisateurConnecte();
        ProfilConducteur conducteur = user.getProfilConducteur();
        Course course = courseRepository.findFirstByConducteurAndStatutIn(
            conducteur,
            List.of(StatutCourse.ACCEPTEE, StatutCourse.EN_COURS, StatutCourse.ARRIVEE)
        ).orElseThrow(() -> new EntityNotFoundException("Aucune course active"));
        return CourseResponse.from(course);
    }

    /** Historique conducteur + stats du jour */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatsConducteur() {
        CompteUtilisateur user = getUtilisateurConnecte();
        ProfilConducteur conducteur = user.getProfilConducteur();

        List<CourseResponse> historique = courseRepository
            .findByConducteurOrderByDate_CommandeDesc(conducteur)
            .stream().map(CourseResponse::from).toList();

        Double gainsDuJour = courseRepository.gainsDuJour(conducteur);
        Long totalCourses  = courseRepository.countCoursesTerminees(conducteur);

        return Map.of(
            "historique",    historique,
            "gains_du_jour", gainsDuJour != null ? gainsDuJour.intValue() : 0,
            "total_courses", totalCourses
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  CARTE PASSAGER — conducteurs actifs
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Positions des conducteurs LIBRES dans un rayon de 10 km
     * — utilisé pour afficher les marqueurs sur la carte passager.
     * PostGIS + index GiST → requête très rapide.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getConducteursActifs(double lat, double lng) {
        return localisationRepository
            .findConducteursActifsProches(lat, lng, RAYON_CARTE_METRES)
            .stream()
            .map(loc -> Map.<String, Object>of(
                "conducteur_id", loc.getConducteur().getPublicId(),
                "latitude",      loc.getLatitude(),
                "longitude",     loc.getLongitude(),
                "type_vehicule", loc.getConducteur().getType_vehicule() != null
                    ? loc.getConducteur().getType_vehicule().name() : "ZEM"
            ))
            .toList();
    }
}
