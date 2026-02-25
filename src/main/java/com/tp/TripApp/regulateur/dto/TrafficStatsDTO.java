package com.tp.TripApp.regulateur.dto;

import java.util.List;

//═══════════════════════════════════════════════════════════════
//Trafic — stats globales
//═══════════════════════════════════════════════════════════════

public class TrafficStatsDTO {
	
	 private long coursesEnCours;
	 private long coursesEnAttente;
	 private long conducteursDispo;         // statut_service = LIBRE + est_valide_par_admin = true
	 private long coursesTermineesAujourdhui;
	 private double gainsTotauxAujourdhui;
	 private List<CourseResumeeDTO> coursesActives;
	
	 public long getCoursesEnCours() { return coursesEnCours; }
	 public void setCoursesEnCours(long v) { this.coursesEnCours = v; }
	 public long getCoursesEnAttente() { return coursesEnAttente; }
	 public void setCoursesEnAttente(long v) { this.coursesEnAttente = v; }
	 public long getConducteursDispo() { return conducteursDispo; }
	 public void setConducteursDispo(long v) { this.conducteursDispo = v; }
	 public long getCoursesTermineesAujourdhui() { return coursesTermineesAujourdhui; }
	 public void setCoursesTermineesAujourdhui(long v) { this.coursesTermineesAujourdhui = v; }
	 public double getGainsTotauxAujourdhui() { return gainsTotauxAujourdhui; }
	 public void setGainsTotauxAujourdhui(double v) { this.gainsTotauxAujourdhui = v; }
	 public List<CourseResumeeDTO> getCoursesActives() { return coursesActives; }
	 public void setCoursesActives(List<CourseResumeeDTO> v) { this.coursesActives = v; }
}
