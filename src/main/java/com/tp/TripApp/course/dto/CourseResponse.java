package com.tp.TripApp.course.dto;

import com.tp.TripApp.course.entity.Course;
import com.tp.TripApp.course.enums.StatutCourse;
import com.tp.TripApp.course.enums.StatutPaiement;

public class CourseResponse {
    private Long id;
    private StatutCourse statut;
    private StatutPaiement statut_paiement;

    // Départ / Destination
    private Double depart_lat;
    private Double depart_lng;
    private String depart_adresse;
    private Double destination_lat;
    private Double destination_lng;
    private String destination_adresse;

    // Prix
    private Double prix_estime;
    private Double prix_final;
    private String mode_paiement;

    // Conducteur (si appairé)
    private ConducteurInfo conducteur;

    // Distance
    private Double distance_km;

    // Notation
    private Integer note_conducteur;

    public static CourseResponse from(Course c) {
        CourseResponse r = new CourseResponse();
        r.id = c.getId();
        r.statut = c.getStatut();
        r.statut_paiement = c.getStatut_paiement();
        r.depart_lat = c.getDepart_lat();
        r.depart_lng = c.getDepart_lng();
        r.depart_adresse = c.getDepart_adresse();
        r.destination_lat = c.getDestination_lat();
        r.destination_lng = c.getDestination_lng();
        r.destination_adresse = c.getDestination_adresse();
        r.prix_estime = c.getPrix_estime();
        r.prix_final = c.getPrix_final();
        r.mode_paiement = c.getMode_paiement() != null ? c.getMode_paiement().name() : null;
        r.distance_km = c.getDistance_km();
        r.note_conducteur = c.getNote_conducteur();

        if (c.getConducteur() != null) {
            ConducteurInfo info = new ConducteurInfo();
            info.id = c.getConducteur().getPublicId().toString();
            info.nom = c.getConducteur().getCompte().getNom();
            info.prenom = c.getConducteur().getCompte().getPrenom();
            info.photo_profil = c.getConducteur().getCompte().getPhotoProfil();
            info.immatriculation = c.getConducteur().getImmatriculation();
            info.note_moyenne = c.getConducteur().getNote_moyenne();
            info.type_vehicule = c.getConducteur().getType_vehicule() != null
                    ? c.getConducteur().getType_vehicule().name() : null;
            r.conducteur = info;
        }
        return r;
    }

    // ─── Getters ──────────────────────────────────────────────────────────
    public Long getId() { return id; }
    public StatutCourse getStatut() { return statut; }
    public StatutPaiement getStatut_paiement() { return statut_paiement; }
    public Double getDepart_lat() { return depart_lat; }
    public Double getDepart_lng() { return depart_lng; }
    public String getDepart_adresse() { return depart_adresse; }
    public Double getDestination_lat() { return destination_lat; }
    public Double getDestination_lng() { return destination_lng; }
    public String getDestination_adresse() { return destination_adresse; }
    public Double getPrix_estime() { return prix_estime; }
    public Double getPrix_final() { return prix_final; }
    public String getMode_paiement() { return mode_paiement; }
    public ConducteurInfo getConducteur() { return conducteur; }
    public Double getDistance_km() { return distance_km; }
    public Integer getNote_conducteur() { return note_conducteur; }

    // ─── ConducteurInfo nested ────────────────────────────────────────────
    public static class ConducteurInfo {
        public String id;
        public String nom;
        public String prenom;
        public String photo_profil;
        public String immatriculation;
        public Float note_moyenne;
        public String type_vehicule;
    }
}
