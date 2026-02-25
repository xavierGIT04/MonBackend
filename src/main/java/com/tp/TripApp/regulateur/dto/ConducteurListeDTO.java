package com.tp.TripApp.regulateur.dto;

import com.tp.TripApp.security.enums.TypeVehicule;

//═══════════════════════════════════════════════════════════════
//Conducteur — liste gestion
//═══════════════════════════════════════════════════════════════

public class ConducteurListeDTO {
	
	 private Long conducteurId;
	 private String nom;
	 private String prenom;
	 private String telephone;
	 private String photoProfil;
	 private Boolean estValideParAdmin;   // true = approuvé, false = en attente / bloqué
	 private String statutService;        // LIBRE | OCCUPE
	 private Float noteMoyenne;
	 private Long totalCourses;
	 private String immatriculation;
	 private TypeVehicule typeVehicule;
	
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
	 public Boolean getEstValideParAdmin() { return estValideParAdmin; }
	 public void setEstValideParAdmin(Boolean v) { this.estValideParAdmin = v; }
	 public String getStatutService() { return statutService; }
	 public void setStatutService(String v) { this.statutService = v; }
	 public Float getNoteMoyenne() { return noteMoyenne; }
	 public void setNoteMoyenne(Float v) { this.noteMoyenne = v; }
	 public Long getTotalCourses() { return totalCourses; }
	 public void setTotalCourses(Long v) { this.totalCourses = v; }
	 public String getImmatriculation() { return immatriculation; }
	 public void setImmatriculation(String v) { this.immatriculation = v; }
	 public TypeVehicule getTypeVehicule() { return typeVehicule; }
	 public void setTypeVehicule(TypeVehicule v) { this.typeVehicule = v; }
}

