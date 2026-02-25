package com.tp.TripApp.course.enums;

public enum StatutCourse {
    EN_ATTENTE,       // Passager a commandé, recherche conducteur
    ACCEPTEE,         // Conducteur a accepté
    EN_COURS,         // Conducteur est en route vers passager
    ARRIVEE,          // Conducteur arrivé à destination
    TERMINEE,         // Course terminée, paiement effectué
    ANNULEE           // Course annulée
}
