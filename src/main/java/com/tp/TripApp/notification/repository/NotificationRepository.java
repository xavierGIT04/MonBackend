package com.tp.TripApp.notification.repository;

import com.tp.TripApp.notification.entity.Notification;
import com.tp.TripApp.security.entity.CompteUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Toutes les notifications d'un utilisateur, plus récentes en premier
    List<Notification> findByUtilisateurOrderByDateCreationDesc(CompteUtilisateur utilisateur);

    // Nombre de notifications non lues
    long countByUtilisateurAndLuFalse(CompteUtilisateur utilisateur);

    // Marquer toutes comme lues
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.lu = true WHERE n.utilisateur = :utilisateur AND n.lu = false")
    void marquerToutesLues(@Param("utilisateur") CompteUtilisateur utilisateur);

    // Marquer une notification comme lue
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.lu = true WHERE n.id = :id AND n.utilisateur = :utilisateur")
    void marquerLue(@Param("id") Long id, @Param("utilisateur") CompteUtilisateur utilisateur);
}
