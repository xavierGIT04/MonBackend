package com.tp.TripApp.profil.dto;

import com.tp.TripApp.security.entity.CompteUtilisateur;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO retourné par GET /api/v1/auth/me
 */
public class ProfilDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String username;    // numéro de téléphone (ou login pour régulateur)
    private String photoProfil; // URL Cloudinary (déjà présent dans CompteUtilisateur)
    private List<String> roles;

    public static ProfilDTO from(CompteUtilisateur user) {
        ProfilDTO dto = new ProfilDTO();
        dto.id = user.getId();
        dto.nom = user.getNom();
        dto.prenom = user.getPrenom();
        dto.username = user.getUsername();
        dto.photoProfil = user.getPhotoProfil();
        dto.roles = user.getAuthorities()
                .stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList());
        return dto;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getUsername() { return username; }
    public String getPhotoProfil() { return photoProfil; }
    public List<String> getRoles() { return roles; }
}
