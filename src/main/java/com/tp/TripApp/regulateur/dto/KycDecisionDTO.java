package com.tp.TripApp.regulateur.dto;

public class KycDecisionDTO {
    private String motifRejet; // renseigné seulement si rejet

    public String getMotifRejet() { return motifRejet; }
    public void setMotifRejet(String v) { this.motifRejet = v; }
}
