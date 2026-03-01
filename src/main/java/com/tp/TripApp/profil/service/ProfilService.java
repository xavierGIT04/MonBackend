package com.tp.TripApp.profil.service;

import com.tp.TripApp.profil.dto.ProfilDTO;
import com.tp.TripApp.profil.dto.UpdateProfilRequest;
import com.tp.TripApp.security.Repository.UserRepository;
import com.tp.TripApp.security.entity.CompteUtilisateur;
import com.tp.TripApp.service.ImageService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ProfilService {

    private final UserRepository userRepository;
    private final ImageService imageService; // ✅ Cloudinary déjà configuré dans votre projet

    public ProfilService(UserRepository userRepository, ImageService imageService) {
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    // ─── Utilitaire auth ──────────────────────────────────────────────────────

    private CompteUtilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CompteUtilisateur) auth.getPrincipal();
    }

    // ─── Récupérer le profil ──────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ProfilDTO getMonProfil() {
        CompteUtilisateur user = getUtilisateurConnecte();
        // Recharger depuis la DB pour avoir les données fraîches
        CompteUtilisateur fresh = userRepository.findById(user.getId()).orElse(user);
        return ProfilDTO.from(fresh);
    }

    // ─── Mettre à jour nom et prénom ──────────────────────────────────────────

    @Transactional
    public ProfilDTO mettreAJour(UpdateProfilRequest request) {
        CompteUtilisateur user = getUtilisateurConnecte();
        CompteUtilisateur entity = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (request.getNom() != null && !request.getNom().isBlank()) {
            entity.setNom(request.getNom().trim());
        }
        if (request.getPrenom() != null && !request.getPrenom().isBlank()) {
            entity.setPrenom(request.getPrenom().trim());
        }

        userRepository.save(entity);
        return ProfilDTO.from(entity);
    }

    // ─── Upload photo de profil → Cloudinary ─────────────────────────────────

    @Transactional
    public ProfilDTO uploadPhoto(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Fichier vide ou manquant");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Le fichier doit être une image");
        }

        CompteUtilisateur user = getUtilisateurConnecte();
        CompteUtilisateur entity = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // ✅ Upload vers Cloudinary dans le dossier "profils" (comme à l'inscription)
        String urlCloudinary = imageService.uploadImage(file, "profils");
        entity.setPhotoProfil(urlCloudinary);
        userRepository.save(entity);

        return ProfilDTO.from(entity);
    }
}
