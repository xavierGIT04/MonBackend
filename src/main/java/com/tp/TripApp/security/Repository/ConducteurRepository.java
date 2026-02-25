package com.tp.TripApp.security.Repository;

import com.tp.TripApp.security.entity.ProfilConducteur;
import com.tp.TripApp.security.enums.Statut_Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ConducteurRepository extends JpaRepository<ProfilConducteur, Long> {

    // ─── KYC : conducteurs non encore validés ────────────────────────────────
    @Query("SELECT c FROM ProfilConducteur c WHERE c.est_valide_par_admin = false")
    List<ProfilConducteur> findPendingConducteurs();

    // ─── KYC : conducteurs validés ───────────────────────────────────────────
    @Query("SELECT c FROM ProfilConducteur c WHERE c.est_valide_par_admin = true")
    List<ProfilConducteur> findValidatedConducteurs();

    // ─── Conducteurs disponibles et validés (compteur trafic live) ───────────
    // Note : J'utilise c.statut_service car j'imagine qu'il suit la même convention
    @Query("SELECT COUNT(c) FROM ProfilConducteur c WHERE c.statut_service = :statut AND c.est_valide_par_admin = true")
    long countAvailableAndValidated(@Param("statut") Statut_Service statut);

    // ─── Conducteur par compte ───────────────────────────────────────────────
    @Query("SELECT c FROM ProfilConducteur c WHERE c.compte.id = :compteId")
    java.util.Optional<ProfilConducteur> findByCompteId(@Param("compteId") Long compteId);
}