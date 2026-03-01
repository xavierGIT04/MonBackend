package com.tp.TripApp.profil.controller;

import com.tp.TripApp.profil.dto.ProfilDTO;
import com.tp.TripApp.profil.dto.UpdateProfilRequest;
import com.tp.TripApp.profil.service.ProfilService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profil")
@CrossOrigin(origins = "*")
public class ProfilController {

    private final ProfilService profilService;

    public ProfilController(ProfilService profilService) {
        this.profilService = profilService;
    }

    /**
     * GET /api/v1/profil/me
     * Retourne les informations du compte connecté
     */
    @GetMapping("/me")
    public ResponseEntity<ProfilDTO> getMonProfil() {
        return ResponseEntity.ok(profilService.getMonProfil());
    }

    /**
     * PUT /api/v1/profil/me/update
     * Met à jour le nom et le prénom de l'utilisateur connecté
     * Body JSON: { "nom": "Dupont", "prenom": "Jean" }
     */
    @PutMapping("/me/update")
    public ResponseEntity<ProfilDTO> mettreAJour(@RequestBody UpdateProfilRequest request) {
        return ResponseEntity.ok(profilService.mettreAJour(request));
    }

    /**
     * POST /api/v1/profil/auth/me/photo
     * Upload d'une photo de profil (multipart/form-data, champ "photo")
     */
    @PostMapping("/me/photo")
    public ResponseEntity<?> uploadPhoto(@RequestParam("photo") MultipartFile file) {
        try {
            ProfilDTO dto = profilService.uploadPhoto(file);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Erreur lors de l'upload du fichier"));
        }
    }
}
