package com.tp.TripApp.notification.service;

import com.tp.TripApp.notification.dto.NotificationDTO;
import com.tp.TripApp.notification.entity.Notification;
import com.tp.TripApp.notification.entity.TypeNotification;
import com.tp.TripApp.notification.repository.NotificationRepository;
import com.tp.TripApp.security.entity.CompteUtilisateur;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // ─── Utilitaire auth ──────────────────────────────────────────────────────

    private CompteUtilisateur getUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (CompteUtilisateur) auth.getPrincipal();
    }

    // ─── Créer une notification (appelé depuis CourseService) ─────────────────

    @Transactional
    public void creer(CompteUtilisateur utilisateur, TypeNotification type,
                      String titre, String message, Long courseId) {
        Notification notif = new Notification();
        notif.setUtilisateur(utilisateur);
        notif.setType(type);
        notif.setTitre(titre);
        notif.setMessage(message);
        notif.setCourseId(courseId);
        notificationRepository.save(notif);
    }

    // ─── Endpoints utilisateur ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<NotificationDTO> getMesNotifications() {
        CompteUtilisateur user = getUtilisateurConnecte();
        return notificationRepository
            .findByUtilisateurOrderByDateCreationDesc(user)
            .stream()
            .map(NotificationDTO::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getBadge() {
        CompteUtilisateur user = getUtilisateurConnecte();
        long nonLues = notificationRepository.countByUtilisateurAndLuFalse(user);
        return Map.of("non_lues", nonLues);
    }

    @Transactional
    public void marquerLue(Long id) {
        CompteUtilisateur user = getUtilisateurConnecte();
        notificationRepository.marquerLue(id, user);
    }

    @Transactional
    public void marquerToutesLues() {
        CompteUtilisateur user = getUtilisateurConnecte();
        notificationRepository.marquerToutesLues(user);
    }
}
