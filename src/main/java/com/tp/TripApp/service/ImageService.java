package com.tp.TripApp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageService {

    private final Cloudinary cloudinary;

    // Injection des identifiants depuis application.properties
    public ImageService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    /**
     * Upload un fichier vers Cloudinary dans un dossier spécifique
     * @param file Le fichier binaire reçu du mobile
     * @param folderName Le sous-dossier (ex: "permis", "cni", "vehicules")
     * @return L'URL sécurisée de l'image stockée
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    public String uploadImage(MultipartFile file, String folderName) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Configuration de l'upload avec le dossier de destination
        Map<?, ?> uploadParams = ObjectUtils.asMap(
                "folder", "trip_db/" + folderName,
                "resource_type", "auto" // Détecte automatiquement si c'est image ou pdf
        );

        // Exécution de l'upload
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

        // On retourne l'URL publique sécurisée (https)
        return uploadResult.get("secure_url").toString();
    }

    /**
     * Méthode optionnelle pour supprimer une image si besoin (ex: changement de photo)
     */
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}