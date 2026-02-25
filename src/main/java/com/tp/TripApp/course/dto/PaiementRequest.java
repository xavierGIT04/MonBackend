package com.tp.TripApp.course.dto;

public class PaiementRequest {
    private String mode_paiement; // ESPECES, TMONEY, MOOV_MONEY
    private String code_pin;       // PIN simulé (non vérifié côté serveur en simulation)

    public String getMode_paiement() { return mode_paiement; }
    public void setMode_paiement(String mode_paiement) { this.mode_paiement = mode_paiement; }
    public String getCode_pin() { return code_pin; }
    public void setCode_pin(String code_pin) { this.code_pin = code_pin; }
}
