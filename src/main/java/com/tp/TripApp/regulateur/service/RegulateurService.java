package com.tp.TripApp.regulateur.service;

import com.tp.TripApp.course.entity.Course;
import com.tp.TripApp.course.enums.StatutCourse;
import com.tp.TripApp.course.repository.CourseRepository;
import com.tp.TripApp.regulateur.dto.ConducteurListeDTO;
import com.tp.TripApp.regulateur.dto.CourseResumeeDTO;
import com.tp.TripApp.regulateur.dto.KycDossierDTO;
import com.tp.TripApp.regulateur.dto.TrafficStatsDTO;
import com.tp.TripApp.security.Repository.ConducteurRepository;
import com.tp.TripApp.security.entity.CompteUtilisateur;
import com.tp.TripApp.security.entity.ProfilConducteur;
import com.tp.TripApp.security.enums.Statut_Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegulateurService {

    private final ConducteurRepository conducteurRepository;
    private final CourseRepository courseRepository;

    public RegulateurService(ConducteurRepository conducteurRepository,
                             CourseRepository courseRepository) {
        this.conducteurRepository = conducteurRepository;
        this.courseRepository = courseRepository;
    }

    // ═══════════════════════════════════════════════════════════════
    // KYC
    // ═══════════════════════════════════════════════════════════════

    /**
     * Dossiers en attente = est_valide_par_admin = false
     * C'est la file d'attente principale du régulateur
     */
    public List<KycDossierDTO> getDossiersEnAttente() {
        return conducteurRepository.findPendingConducteurs()
                .stream()
                .map(this::toKycDto)
                .collect(Collectors.toList());
    }

    /**
     * Tous les conducteurs (validés + en attente)
     */
    public List<KycDossierDTO> getTousDossiers() {
        return conducteurRepository.findAll()
                .stream()
                .map(this::toKycDto)
                .collect(Collectors.toList());
    }

    /**
     * Approuver un dossier KYC :
     * - est_valide_par_admin → true
     * - statut_service → LIBRE (le conducteur peut désormais recevoir des courses)
     * - est_actif du CompteUtilisateur → true
     */
    @Transactional
    public void approuverKyc(Long conducteurId) {
        ProfilConducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new EntityNotFoundException("Conducteur introuvable : " + conducteurId));

        conducteur.setEst_valide_par_admin(true);
        conducteur.setStatut_service(Statut_Service.LIBRE);

        // Activer le compte utilisateur associé
        CompteUtilisateur compte = conducteur.getCompte();
        compte.setEst_actif(true);

        conducteurRepository.save(conducteur);
        // CompteUtilisateur est sauvé en cascade via la relation OneToOne
    }

    /**
     * Rejeter un dossier KYC :
     * - est_valide_par_admin → false (reste false)
     * - statut_service → null (pas de service autorisé)
     * - est_actif du CompteUtilisateur → false
     *
     * NOTE : le champ motifRejet n'existe pas encore en base.
     * Si tu veux le persister, ajoute un champ String motif_rejet dans ProfilConducteur.
     */
    @Transactional
    public void rejeterKyc(Long conducteurId, String motif) {
        ProfilConducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new EntityNotFoundException("Conducteur introuvable : " + conducteurId));

        conducteur.setEst_valide_par_admin(false);
        conducteur.setStatut_service(null);

        CompteUtilisateur compte = conducteur.getCompte();
        compte.setEst_actif(false);

        conducteurRepository.save(conducteur);
        // TODO : si tu ajoutes le champ motif_rejet dans ProfilConducteur → conducteur.setMotif_rejet(motif);
    }

    // ═══════════════════════════════════════════════════════════════
    // TRAFIC LIVE
    // ═══════════════════════════════════════════════════════════════

    public TrafficStatsDTO getStatsTrafic() {
        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour   = debutJour.plusDays(1);

        List<StatutCourse> statutsActifs = List.of(
                StatutCourse.EN_COURS,
                StatutCourse.ACCEPTEE,
                StatutCourse.ARRIVEE
        );

        long enCours     = courseRepository.countByStatutIn(statutsActifs);
        long enAttente   = courseRepository.countByStatut(StatutCourse.EN_ATTENTE);
        long terminees   = courseRepository.countTermineesEntre(StatutCourse.TERMINEE, debutJour, finJour);
        long condDispos  = conducteurRepository.countAvailableAndValidated(Statut_Service.LIBRE);
        Double gains     = courseRepository.sumPrixFinalEntre(StatutCourse.TERMINEE, debutJour, finJour);

        List<CourseResumeeDTO> actives = courseRepository.findByStatutIn(statutsActifs)
                .stream()
                .map(this::toCourseResumee)
                .collect(Collectors.toList());

        TrafficStatsDTO dto = new TrafficStatsDTO();
        dto.setCoursesEnCours(enCours);
        dto.setCoursesEnAttente(enAttente);
        dto.setCoursesTermineesAujourdhui(terminees);
        dto.setConducteursDispo(condDispos);
        dto.setGainsTotauxAujourdhui(gains != null ? gains : 0.0);
        dto.setCoursesActives(actives);
        return dto;
    }

    // ═══════════════════════════════════════════════════════════════
    // GESTION CONDUCTEURS
    // ═══════════════════════════════════════════════════════════════

    public List<ConducteurListeDTO> getTousConducteurs() {
        return conducteurRepository.findAll()
                .stream()
                .map(this::toConducteurListe)
                .collect(Collectors.toList());
    }

    /**
     * Bloquer un conducteur :
     * - est_valide_par_admin → false
     * - statut_service → null
     * - compte.est_actif → false
     */
    @Transactional
    public void bloquerConducteur(Long conducteurId) {
        ProfilConducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new EntityNotFoundException("Conducteur introuvable : " + conducteurId));

        conducteur.setEst_valide_par_admin(false);
        conducteur.setStatut_service(null);
        conducteur.getCompte().setEst_actif(false);
        conducteurRepository.save(conducteur);
    }

    /**
     * Débloquer un conducteur :
     * - est_valide_par_admin → true
     * - statut_service → LIBRE
     * - compte.est_actif → true
     */
    @Transactional
    public void debloquerConducteur(Long conducteurId) {
        ProfilConducteur conducteur = conducteurRepository.findById(conducteurId)
                .orElseThrow(() -> new EntityNotFoundException("Conducteur introuvable : " + conducteurId));

        conducteur.setEst_valide_par_admin(true);
        conducteur.setStatut_service(Statut_Service.LIBRE);
        conducteur.getCompte().setEst_actif(true);
        conducteurRepository.save(conducteur);
    }

    // ═══════════════════════════════════════════════════════════════
    // MAPPERS INTERNES
    // ═══════════════════════════════════════════════════════════════

    private KycDossierDTO toKycDto(ProfilConducteur c) {
        CompteUtilisateur compte = c.getCompte();
        KycDossierDTO dto = new KycDossierDTO();
        dto.setConducteurId(c.getId());
        dto.setNom(compte.getNom());
        dto.setPrenom(compte.getPrenom());
        dto.setTelephone(compte.getUsername());
        dto.setPhotoProfil(compte.getPhotoProfil());
        dto.setPhotoPermis(c.getPhoto_permis());
        dto.setPhotoCni(c.getPhoto_cni());
        dto.setPhotoVehicule(c.getPhoto_vehicule());
        dto.setNumeroPermis(c.getNumero_permis());
        dto.setImmatriculation(c.getImmatriculation());
        dto.setTypeVehicule(c.getType_vehicule());
        dto.setEstValideParAdmin(c.getEst_valide_par_admin());
        dto.setStatutService(c.getStatut_service() != null ? c.getStatut_service().name() : null);
        dto.setNoteMoyenne(c.getNote_moyenne());
        return dto;
    }

    private  CourseResumeeDTO toCourseResumee(Course c) {
        CourseResumeeDTO dto = new CourseResumeeDTO();
        dto.setCourseId(c.getId());
        dto.setStatut(c.getStatut().name());
        dto.setDateCommande(c.getDate_commande());
        dto.setPrixFinal(c.getPrix_final());
        dto.setPrixEstime(c.getPrix_estime());
        dto.setDistanceKm(c.getDistance_km());
        dto.setDepartAdresse(c.getDepart_adresse());
        dto.setDestinationAdresse(c.getDestination_adresse());

        // Passager : CompteUtilisateur direct (Course.passager = CompteUtilisateur)
        if (c.getPassager() != null) {
            dto.setPassagerNom(c.getPassager().getNom() + " " + c.getPassager().getPrenom());
        }

        // Conducteur : ProfilConducteur → getCompte() pour nom/prénom
        if (c.getConducteur() != null && c.getConducteur().getCompte() != null) {
            dto.setConducteurNom(
                c.getConducteur().getCompte().getNom() + " " +
                c.getConducteur().getCompte().getPrenom()
            );
        }
        return dto;
    }

    private ConducteurListeDTO toConducteurListe(ProfilConducteur c) {
        CompteUtilisateur compte = c.getCompte();
        ConducteurListeDTO dto = new ConducteurListeDTO();
        dto.setConducteurId(c.getId());
        dto.setNom(compte.getNom());
        dto.setPrenom(compte.getPrenom());
        dto.setTelephone(compte.getUsername());
        dto.setPhotoProfil(compte.getPhotoProfil());
        dto.setEstValideParAdmin(c.getEst_valide_par_admin());
        dto.setStatutService(c.getStatut_service() != null ? c.getStatut_service().name() : null);
        dto.setNoteMoyenne(c.getNote_moyenne());
        dto.setImmatriculation(c.getImmatriculation());
        dto.setTypeVehicule(c.getType_vehicule());

        // Nombre de courses terminées via la requête existante dans CourseRepository
        Long total = courseRepository.countCoursesTerminees(c);
        dto.setTotalCourses(total);
        return dto;
    }
}