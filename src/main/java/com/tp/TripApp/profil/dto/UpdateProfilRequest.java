package com.tp.TripApp.profil.dto;

/**
 * Corps de la requête PUT /api/v1/auth/me/update
 */
public class UpdateProfilRequest {

    private String nom;
    private String prenom;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
}
