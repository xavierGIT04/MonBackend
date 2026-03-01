package com.tp.TripApp.notification.entity;

public enum TypeNotification {
    COURSE_ACCEPTEE,       // Conducteur a accepté la course
    COURSE_DEMARREE,       // Course démarrée
    COURSE_ARRIVEE,        // Conducteur arrivé à destination
    COURSE_TERMINEE,       // Course terminée
    COURSE_ANNULEE,        // Course annulée
    PAIEMENT_CONFIRME,     // Paiement confirmé
    NOUVELLE_COURSE,       // Nouveau passager disponible (conducteur)
    SYSTEME               // Notification système générique
}
