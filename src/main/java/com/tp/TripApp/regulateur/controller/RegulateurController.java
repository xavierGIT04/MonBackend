package com.tp.TripApp.regulateur.controller;

import com.tp.TripApp.regulateur.dto.*;
import com.tp.TripApp.regulateur.service.RegulateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Dashboard Régulateur — tous les endpoints sont protégés par ROLE_REGULATEUR
 * (le rôle est attribué automatiquement via CompteUtilisateur.getAuthorities()
 *  quand regulateur != null — cf. CompteUtilisateur.java ligne ~66)
 *
 * Base URL : /api/v1/regulateur
 */
@RestController
@RequestMapping("/api/v1/regulateur")
@PreAuthorize("hasRole('REGULATEUR')")
@CrossOrigin(origins = "*") // à restreindre au domaine Angular en production
public class RegulateurController {

    private final RegulateurService service;

    public RegulateurController(RegulateurService service) {
        this.service = service;
    }

    // ═══════════════════════════════════════════════════════════════
    // KYC
    // ═══════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/regulateur/kyc/en-attente
     * Dossiers avec est_valide_par_admin = false
     * → La file d'attente principale du régulateur
     */
    @GetMapping("/kyc/en-attente")
    public ResponseEntity<List<KycDossierDTO>> getDossiersEnAttente() {
        return ResponseEntity.ok(service.getDossiersEnAttente());
    }

    /**
     * GET /api/v1/regulateur/kyc/tous
     * Tous les conducteurs (validés + en attente)
     */
    @GetMapping("/kyc/tous")
    public ResponseEntity<List<KycDossierDTO>> getTousDossiers() {
        return ResponseEntity.ok(service.getTousDossiers());
    }

    /**
     * POST /api/v1/regulateur/kyc/{id}/approuver
     * Valide le conducteur :
     *   - est_valide_par_admin → true
     *   - statut_service → LIBRE
     *   - compte.est_actif → true
     */
    @PostMapping("/kyc/{id}/approuver")
    public ResponseEntity<Map<String, String>> approuver(@PathVariable Long id) {
        service.approuverKyc(id);
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "message", "Conducteur approuvé — il peut maintenant recevoir des courses"
        ));
    }

    /**
     * POST /api/v1/regulateur/kyc/{id}/rejeter
     * Body JSON : { "motifRejet": "Photo de permis illisible" }
     * Désactive le conducteur :
     *   - est_valide_par_admin → false
     *   - compte.est_actif → false
     */
    @PostMapping("/kyc/{id}/rejeter")
    public ResponseEntity<Map<String, String>> rejeter(
            @PathVariable Long id,
            @RequestBody KycDecisionDTO body) {
        service.rejeterKyc(id, body.getMotifRejet());
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "message", "Dossier rejeté"
        ));
    }

    // ═══════════════════════════════════════════════════════════════
    // TRAFIC LIVE
    // ═══════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/regulateur/trafic
     * Stats globales + liste des courses actives
     * → polling toutes les 30s depuis Angular
     */
    @GetMapping("/trafic")
    public ResponseEntity<TrafficStatsDTO> getTrafic() {
        return ResponseEntity.ok(service.getStatsTrafic());
    }

    // ═══════════════════════════════════════════════════════════════
    // GESTION CONDUCTEURS
    // ═══════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/regulateur/conducteurs
     * Liste complète avec stats
     */
    @GetMapping("/conducteurs")
    public ResponseEntity<List<ConducteurListeDTO>> getConducteurs() {
        return ResponseEntity.ok(service.getTousConducteurs());
    }

    /**
     * POST /api/v1/regulateur/conducteurs/{id}/bloquer
     * Body JSON : { "motif": "..." }  (motif informatif, non persisté pour l'instant)
     * Désactive : est_valide_par_admin=false, statut_service=null, compte.est_actif=false
     */
    @PostMapping("/conducteurs/{id}/bloquer")
    public ResponseEntity<Map<String, String>> bloquer(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        service.bloquerConducteur(id);
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "message", "Conducteur bloqué"
        ));
    }

    /**
     * POST /api/v1/regulateur/conducteurs/{id}/debloquer
     * Réactive : est_valide_par_admin=true, statut_service=LIBRE, compte.est_actif=true
     */
    @PostMapping("/conducteurs/{id}/debloquer")
    public ResponseEntity<Map<String, String>> debloquer(@PathVariable Long id) {
        service.debloquerConducteur(id);
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "message", "Conducteur débloqué — il peut à nouveau recevoir des courses"
        ));
    }
}