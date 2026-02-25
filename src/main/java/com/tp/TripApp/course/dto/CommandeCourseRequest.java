package com.tp.TripApp.course.dto;

// ─── Requête de commande de course ────────────────────────────────────────
public class CommandeCourseRequest {
    private Double depart_lat;
    private Double depart_lng;
    private String depart_adresse;
    private Double destination_lat;
    private Double destination_lng;
    private String destination_adresse;
    private String mode_paiement; // ESPECES, TMONEY, MOOV_MONEY

    public Double getDepart_lat() { return depart_lat; }
    public void setDepart_lat(Double depart_lat) { this.depart_lat = depart_lat; }
    public Double getDepart_lng() { return depart_lng; }
    public void setDepart_lng(Double depart_lng) { this.depart_lng = depart_lng; }
    public String getDepart_adresse() { return depart_adresse; }
    public void setDepart_adresse(String depart_adresse) { this.depart_adresse = depart_adresse; }
    public Double getDestination_lat() { return destination_lat; }
    public void setDestination_lat(Double destination_lat) { this.destination_lat = destination_lat; }
    public Double getDestination_lng() { return destination_lng; }
    public void setDestination_lng(Double destination_lng) { this.destination_lng = destination_lng; }
    public String getDestination_adresse() { return destination_adresse; }
    public void setDestination_adresse(String destination_adresse) { this.destination_adresse = destination_adresse; }
    public String getMode_paiement() { return mode_paiement; }
    public void setMode_paiement(String mode_paiement) { this.mode_paiement = mode_paiement; }
}
