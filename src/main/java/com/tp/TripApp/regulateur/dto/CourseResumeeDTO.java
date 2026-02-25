package com.tp.TripApp.regulateur.dto;

import java.time.LocalDateTime;

//═══════════════════════════════════════════════════════════════
//Course résumée (pour la liste trafic live)
//═══════════════════════════════════════════════════════════════

public class CourseResumeeDTO {
	
	 private Long courseId;
	 private String statut;
	 private String passagerNom;
	 private String conducteurNom;
	 private String departAdresse;      // Course.depart_adresse
	 private String destinationAdresse; // Course.destination_adresse
	 private LocalDateTime dateCommande; // Course.date_commande
	 private Double prixFinal;           // Course.prix_final
	 private Double prixEstime;          // Course.prix_estime
	 private Double distanceKm;          // Course.distance_km
	
	 public Long getCourseId() { return courseId; }
	 public void setCourseId(Long v) { this.courseId = v; }
	 public String getStatut() { return statut; }
	 public void setStatut(String v) { this.statut = v; }
	 public String getPassagerNom() { return passagerNom; }
	 public void setPassagerNom(String v) { this.passagerNom = v; }
	 public String getConducteurNom() { return conducteurNom; }
	 public void setConducteurNom(String v) { this.conducteurNom = v; }
	 public String getDepartAdresse() { return departAdresse; }
	 public void setDepartAdresse(String v) { this.departAdresse = v; }
	 public String getDestinationAdresse() { return destinationAdresse; }
	 public void setDestinationAdresse(String v) { this.destinationAdresse = v; }
	 public LocalDateTime getDateCommande() { return dateCommande; }
	 public void setDateCommande(LocalDateTime v) { this.dateCommande = v; }
	 public Double getPrixFinal() { return prixFinal; }
	 public void setPrixFinal(Double v) { this.prixFinal = v; }
	 public Double getPrixEstime() { return prixEstime; }
	 public void setPrixEstime(Double v) { this.prixEstime = v; }
	 public Double getDistanceKm() { return distanceKm; }
	 public void setDistanceKm(Double v) { this.distanceKm = v; }
}
