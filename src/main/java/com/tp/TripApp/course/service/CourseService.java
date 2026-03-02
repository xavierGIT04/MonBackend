package com.tp.TripApp.course.service;

import java.time.LocalDate;
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
import com.tp.TripApp.security.enums.TypeVehicule;
import com.tp.TripApp.notification.entity.TypeNotification;
import com.tp.TripApp.notification.service.NotificationService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class CourseService {

    private static final double RAYON_RECHERCHE_METRES = 5_000;  // 5 km
    private static final double RAYON_CARTE_METRES     = 10_000; // 10 km

    private final CourseRepository courseRepository;
    private final LocalisationConducteurRepository localisationRepository;
    private final NotificationService notificationService;

    public CourseService(CourseRepository courseRepository,
                         LocalisationConducteurRepository localisationRepository,
                         NotificationService notificationService) {
        this.courseRepository = courseRepository;
        this.localisationRepository = localisationRepository;
        this.notificationService = notificationService;
    }

    // ─── Utilitaire auth ──────────────────────────────────────────────────────

    private CompteUtilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CompteUtilisateur) auth.getPrincipal();
    }

    // ─── Estimation ──────────────────────────────────────────────────────────

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

        if (req.getType_vehicule() != null) {
            try {
                course.setType_vehicule_demande(TypeVehicule.valueOf(req.getType_vehicule()));
            } catch (IllegalArgumentException ignored) {
                course.setType_vehicule_demande(TypeVehicule.ZEM);
            }
        } else {
            course.setType_vehicule_demande(TypeVehicule.ZEM);
        }

        Course saved = courseRepository.save(course);

        // PostGIS recalcule la distance exacte après persistance
        Double distPostGIS = courseRepository.calculerDistanceKm(saved.getId());
        if (distPostGIS != null) {
            saved.setDistance_km(Math.round(distPostGIS * 10.0) / 10.0);
            saved.setPrix_estime(GeometryUtils.calculerPrix(distPostGIS));
            saved = courseRepository.save(saved);
        }

        // ── NOUVEAU : Notifier les conducteurs proches ────────────────────────
        _notifierConducteursProches(saved, req.getDepart_lat(), req.getDepart_lng());

        return CourseResponse.from(saved);
    }

    /**
     * Envoie une notification NOUVELLE_COURSE à tous les conducteurs
     * validés, disponibles (LIBRE) et dans un rayon de 5 km du départ,
     * en filtrant par type de véhicule si spécifié.
     */
    private void _notifierConducteursProches(Course course, double lat, double lng) {
    try {
        String typeVehicule = course.getType_vehicule_demande() != null
            ? course.getType_vehicule_demande().name() : null;

        List<LocalisationConducteur> conducteursPro;

        // Utiliser la requête filtrée par type si disponible
        if (typeVehicule != null) {
            conducteursPro = localisationRepository
                .findConducteursActifsProchesParType(lat, lng, RAYON_RECHERCHE_METRES, typeVehicule);
        } else {
            conducteursPro = localisationRepository
                .findConducteursActifsProches(lat, lng, RAYON_RECHERCHE_METRES);
        }


        String prixStr = course.getPrix_estime() != null
            ? course.getPrix_estime().intValue() + " FCFA" : "prix estimé";
        String destAdresse = course.getDestination_adresse() != null
            ? course.getDestination_adresse() : "destination";

        for (LocalisationConducteur loc : conducteursPro) {
            ProfilConducteur conducteur = loc.getConducteur();
            notificationService.creer(
                conducteur.getCompte(),
                TypeNotification.NOUVELLE_COURSE,
                "Nouvelle course disponible ! 🔔",
                "Course vers " + destAdresse + " — " + prixStr,
                course.getId()
            );
           
        }

       

    } catch (Exception e) {
        System.err.println("Erreur notification conducteurs : " + e.getMessage());
        e.printStackTrace();
    }
}
    
    /** Course active du passager (polling) */
    @Transactional(readOnly = true)
    public CourseResponse getCourseActivePassager() {
        CompteUtilisateur passager = getUtilisateurConnecte();
        return courseRepository.findFirstByPassagerAndStatutIn(
            passager,
            List.of(StatutCourse.EN_ATTENTE, StatutCourse.ACCEPTEE,
                    StatutCourse.EN_COURS, StatutCourse.ARRIVEE)
        ).map(CourseResponse::from).orElse(null);
    }

    /** Passager annule sa course */
    public CourseResponse annulerCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course introuvable"));
        if (course.getStatut() == StatutCourse.TERMINEE)
            throw new IllegalStateException("Impossible d'annuler une course terminée");

        course.setStatut(StatutCourse.ANNULEE);
        if (course.getConducteur() != null) {
            course.getConducteur().setStatut_service(Statut_Service.LIBRE);
            notificationService.creer(
                course.getConducteur().getCompte(),
                TypeNotification.COURSE_ANNULEE,
                "Course annulée ❌",
                "Le passager a annulé la course.",
                course.getId()
            );
        }

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

        notificationService.creer(
            course.getConducteur().getCompte(),
            TypeNotification.PAIEMENT_CONFIRME,
            "Paiement reçu ",
            "Le paiement de " + course.getPrix_final().intValue() + " FCFA a été confirmé.",
            course.getId()
        );
        notificationService.creer(
            course.getPassager(),
            TypeNotification.COURSE_TERMINEE,
            "Course terminée ",
            "Merci d'avoir utilisé Zém & Taxi ! Paiement de "
                + course.getPrix_final().intValue() + " FCFA confirmé.",
            course.getId()
        );

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
        return courseRepository.findByPassagerOrderByDateCommandeDesc(passager)
            .stream().map(CourseResponse::from).toList();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  PARCOURS CONDUCTEUR
    // ═══════════════════════════════════════════════════════════════════════

    public void mettreAJourLocalisation(LocalisationRequest req) {
        CompteUtilisateur user = getUtilisateurConnecte();
        ProfilConducteur conducteur = user.getProfilConducteur();
        if (conducteur == null) throw new IllegalStateException("Pas un conducteur");

        localisationRepository.upsertLocalisation(
            conducteur.getId(),
            req.getLatitude(),
            req.getLongitude()
        );
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesProches() {
        CompteUtilisateur user = getUtilisateurConnecte();
        ProfilConducteur conducteur = user.getProfilConducteur();

        if (!Boolean.TRUE.equals(conducteur.getEst_valide_par_admin())) {
            throw new IllegalStateException(
                "Votre compte n'est pas encore validé par le régulateur. " +
                "Vous ne pouvez pas recevoir de courses pour l'instant."
            );
        }

        LocalisationConducteur loc = localisationRepository
            .findByConducteur(conducteur)
            .orElseThrow(() -> new EntityNotFoundException(
                "Position non définie. Activez votre GPS."));

        if (conducteur.getType_vehicule() != null) {
            return courseRepository.findCoursesEnAttenteProchesParType(
                loc.getLatitude(),
                loc.getLongitude(),
                RAYON_RECHERCHE_METRES,
                conducteur.getType_vehicule().name()
            ).stream().map(CourseResponse::from).toList();
        }

        return courseRepository.findCoursesEnAttenteProches(
            loc.getLatitude(), loc.getLongitude(), RAYON_RECHERCHE_METRES
        ).stream().map(CourseResponse::from).toList();
    }

    public CourseResponse accepterCourse(Long courseId) {
        CompteUtilisateur user = getUtilisateurConnecte();
        ProfilConducteur conducteur = user.getProfilConducteur();

        if (!Boolean.TRUE.equals(conducteur.getEst_valide_par_admin())) {
            throw new IllegalStateException(
                "Votre compte n'est pas encore validé par le régulateur."
            );
        }

        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course introuvable"));

        if (course.getStatut() != StatutCourse.EN_ATTENTE)
            throw new IllegalStateException("Cette course n'est plus disponible");

        course.setConducteur(conducteur);
        course.setStatut(StatutCourse.ACCEPTEE);
        course.setDate_acceptation(LocalDateTime.now());
        conducteur.setStatut_service(Statut_Service.OCCUPE);

        notificationService.creer(
            course.getPassager(),
            TypeNotification.COURSE_ACCEPTEE,
            "Conducteur trouvé ! ",
            conducteur.getCompte().getPrenom() + " " + conducteur.getCompte().getNom()
                + " arrive vers vous.",
            course.getId()
        );

        return CourseResponse.from(courseRepository.save(course));
    }

    public CourseResponse demarrerCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course introuvable"));
        course.setStatut(StatutCourse.EN_COURS);
        course.setDate_debut(LocalDateTime.now());

        notificationService.creer(
            course.getPassager(),
            TypeNotification.COURSE_DEMARREE,
            "Course démarrée ",
            "Votre conducteur est en route vers votre destination.",
            course.getId()
        );

        return CourseResponse.from(courseRepository.save(course));
    }

    public CourseResponse signalerArrivee(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new EntityNotFoundException("Course introuvable"));
        course.setStatut(StatutCourse.ARRIVEE);

        notificationService.creer(
            course.getPassager(),
            TypeNotification.COURSE_ARRIVEE,
            "Arrivée à destination ",
            "Vous êtes arrivé à destination. Veuillez confirmer le paiement.",
            course.getId()
        );

        return CourseResponse.from(courseRepository.save(course));
    }

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

    @Transactional(readOnly = true)
    public Map<String, Object> getStatsConducteur() {
        CompteUtilisateur user = getUtilisateurConnecte();
        ProfilConducteur conducteur = user.getProfilConducteur();

        List<CourseResponse> historique = courseRepository
            .findByConducteurOrderByDateCommandeDesc(conducteur)
            .stream().map(CourseResponse::from).toList();

        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour   = debutJour.plusDays(1);
        Double gainsDuJour = courseRepository.gainsDuJour(conducteur, debutJour, finJour);
        Long totalCourses  = courseRepository.countCoursesTerminees(conducteur);

        return Map.of(
            "historique",    historique,
            "gains_du_jour", gainsDuJour != null ? gainsDuJour.intValue() : 0,
            "total_courses", totalCourses
        );
    }

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