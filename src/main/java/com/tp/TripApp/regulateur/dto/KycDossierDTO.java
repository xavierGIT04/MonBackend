package com.tp.TripApp.regulateur.dto;

import com.tp.TripApp.security.enums.TypeVehicule;

// ═══════════════════════════════════════════════════════════════
// KYC — dossier conducteur pour le régulateur
// ═══════════════════════════════════════════════════════════════

public class KycDossierDTO {

    private Long conducteurId;           // ProfilConducteur.id
    private String nom;                  // CompteUtilisateur.nom
    private String prenom;               // CompteUtilisateur.prenom
    private String telephone;            // CompteUtilisateur.telephone
    private String photoProfil;          // CompteUtilisateur.photoProfil
    private String photoPermis;          // ProfilConducteur.photo_permis
    private String photoCni;             // ProfilConducteur.photo_cni
    private String photoVehicule;        // ProfilConducteur.photo_vehicule
    private String numeroPermis;         // ProfilConducteur.numero_permis
    private String immatriculation;      // ProfilConducteur.immatriculation
    private TypeVehicule typeVehicule;   // ProfilConducteur.type_vehicule
    private Boolean estValideParAdmin;   // ProfilConducteur.est_valide_par_admin
    private String statutService;        // ProfilConducteur.statut_service
    private Float noteMoyenne;           // ProfilConducteur.note_moyenne
    // "EN_ATTENTE" | "APPROUVE" est déduit de est_valide_par_admin
    // Pour les rejetés on ajoute un champ motif (non persisté en base pour l'instant)

    public Long getConducteurId() { return conducteurId; }
    public void setConducteurId(Long v) { this.conducteurId = v; }
    public String getNom() { return nom; }
    public void setNom(String v) { this.nom = v; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String v) { this.prenom = v; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String v) { this.telephone = v; }
    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String v) { this.photoProfil = v; }
    public String getPhotoPermis() { return photoPermis; }
    public void setPhotoPermis(String v) { this.photoPermis = v; }
    public String getPhotoCni() { return photoCni; }
    public void setPhotoCni(String v) { this.photoCni = v; }
    public String getPhotoVehicule() { return photoVehicule; }
    public void setPhotoVehicule(String v) { this.photoVehicule = v; }
    public String getNumeroPermis() { return numeroPermis; }
    public void setNumeroPermis(String v) { this.numeroPermis = v; }
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String v) { this.immatriculation = v; }
    public TypeVehicule getTypeVehicule() { return typeVehicule; }
    public void setTypeVehicule(TypeVehicule v) { this.typeVehicule = v; }
    public Boolean getEstValideParAdmin() { return estValideParAdmin; }
    public void setEstValideParAdmin(Boolean v) { this.estValideParAdmin = v; }
    public String getStatutService() { return statutService; }
    public void setStatutService(String v) { this.statutService = v; }
    public Float getNoteMoyenne() { return noteMoyenne; }
    public void setNoteMoyenne(Float v) { this.noteMoyenne = v; }
}
