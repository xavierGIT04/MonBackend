package com.tp.TripApp.notification.controller;

import com.tp.TripApp.notification.dto.NotificationDTO;
import com.tp.TripApp.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * GET /api/v1/notifications
     * Liste toutes les notifications de l'utilisateur connecté
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMesNotifications() {
        return ResponseEntity.ok(notificationService.getMesNotifications());
    }

    /**
     * GET /api/v1/notifications/badge
     * Nombre de notifications non lues (pour le badge)
     */
    @GetMapping("/badge")
    public ResponseEntity<Map<String, Object>> getBadge() {
        return ResponseEntity.ok(notificationService.getBadge());
    }

    /**
     * PUT /api/v1/notifications/{id}/lire
     * Marque une notification comme lue
     */
    @PutMapping("/{id}/lire")
    public ResponseEntity<Void> marquerLue(@PathVariable Long id) {
        notificationService.marquerLue(id);
        return ResponseEntity.ok().build();
    }

    /**
     * PUT /api/v1/notifications/tout-lire
     * Marque toutes les notifications comme lues
     */
    @PutMapping("/tout-lire")
    public ResponseEntity<Void> marquerToutesLues() {
        notificationService.marquerToutesLues();
        return ResponseEntity.ok().build();
    }
}
