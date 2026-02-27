package com.tp.TripApp.course.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tp.TripApp.course.dto.*;
import com.tp.TripApp.course.service.CourseService;

@RestController
@RequestMapping("/api/v1/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ENDPOINTS PASSAGER
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/courses/estimation?dLat=&dLng=&aLat=&aLng=
     * Estimation du prix avant commande
     */
    @GetMapping("/estimation")
    @PreAuthorize("hasRole('PASSAGER')")
    public ResponseEntity<Map<String, Object>> estimer(
            @RequestParam Double dLat, @RequestParam Double dLng,
            @RequestParam Double aLat, @RequestParam Double aLng) {
        return ResponseEntity.ok(courseService.estimerCourse(dLat, dLng, aLat, aLng));
    }

    /**
     * POST /api/v1/courses/commander
     * Passager commande une course
     */
    @PostMapping("/commander")
    @PreAuthorize("hasRole('PASSAGER')")
    public ResponseEntity<CourseResponse> commander(
            @RequestBody CommandeCourseRequest req) {
        return ResponseEntity.ok(courseService.commanderCourse(req));
    }

    /**
     * GET /api/v1/courses/active
     * Passager récupère sa course active (polling toutes les 3s)
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('PASSAGER')")
    public ResponseEntity<CourseResponse> getCourseActivePassager() {
        CourseResponse course = courseService.getCourseActivePassager();
        if (course == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(course);
    }

    /**
     * DELETE /api/v1/courses/{id}/annuler
     * Passager annule sa course
     */
    @DeleteMapping("/{id}/annuler")
    @PreAuthorize("hasRole('PASSAGER')")
    public ResponseEntity<CourseResponse> annuler(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.annulerCourse(id));
    }

    /**
     * POST /api/v1/courses/{id}/payer
     * Passager confirme le paiement (simulé)
     */
    @PostMapping("/{id}/payer")
    @PreAuthorize("hasRole('PASSAGER')")
    public ResponseEntity<CourseResponse> payer(
            @PathVariable Long id,
            @RequestBody PaiementRequest req) {
        return ResponseEntity.ok(courseService.confirmerPaiement(id, req));
    }

    /**
     * POST /api/v1/courses/{id}/noter
     * Passager note le conducteur
     */
    @PostMapping("/{id}/noter")
    @PreAuthorize("hasRole('PASSAGER')")
    public ResponseEntity<CourseResponse> noter(
            @PathVariable Long id,
            @RequestBody NotationRequest req) {
        return ResponseEntity.ok(courseService.noterConducteur(id, req));
    }

    /**
     * GET /api/v1/courses/historique
     * Historique du passager
     */
    @GetMapping("/historique")
    @PreAuthorize("hasRole('PASSAGER')")
    public ResponseEntity<List<CourseResponse>> historiquePassager() {
        return ResponseEntity.ok(courseService.getHistoriquePassager());
    }

    /**
     * GET /api/v1/courses/conducteurs-actifs?lat=&lng=
     * Positions des conducteurs actifs (pour la carte)
     */
    @GetMapping("/conducteurs-actifs")
    @PreAuthorize("hasRole('PASSAGER')")
    public ResponseEntity<List<Map<String, Object>>> conducteurActifs(
            @RequestParam Double lat, @RequestParam Double lng) {
        return ResponseEntity.ok(courseService.getConducteursActifs(lat, lng));
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  ENDPOINTS CONDUCTEUR
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * PUT /api/v1/courses/localisation
     * Conducteur met à jour sa position GPS
     */
    @PutMapping("/localisation")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    public ResponseEntity<Void> updateLocalisation(@RequestBody LocalisationRequest req) {
        courseService.mettreAJourLocalisation(req);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /api/v1/courses/proches
     * Courses EN_ATTENTE proches du conducteur
     */
    @GetMapping("/proches")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    public ResponseEntity<List<CourseResponse>> getCoursesProches() {
        return ResponseEntity.ok(courseService.getCoursesProches());
    }

    /**
     * POST /api/v1/courses/{id}/accepter
     * Conducteur accepte une course
     */
    @PostMapping("/{id}/accepter")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    public ResponseEntity<CourseResponse> accepter(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.accepterCourse(id));
    }

    /**
     * POST /api/v1/courses/{id}/demarrer
     * Conducteur démarre la course
     */
    @PostMapping("/{id}/demarrer")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    public ResponseEntity<CourseResponse> demarrer(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.demarrerCourse(id));
    }

    /**
     * POST /api/v1/courses/{id}/arrivee
     * Conducteur signale l'arrivée → déclenche paiement côté passager
     */
    @PostMapping("/{id}/arrivee")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    public ResponseEntity<CourseResponse> arrivee(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.signalerArrivee(id));
    }

    /**
     * GET /api/v1/courses/conducteur/active
     * Course active du conducteur (polling)
     */
    @GetMapping("/conducteur/active")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    public ResponseEntity<CourseResponse> getCourseActiveConducteur() {
        return ResponseEntity.ok(courseService.getCourseActiveConducteur());
    }

    /**
     * GET /api/v1/courses/conducteur/historique
     * Historique du conducteur
     */
    @GetMapping("/conducteur/historique")
    @PreAuthorize("hasRole('CONDUCTEUR')")
    public ResponseEntity<Map<String, Object>> historiqueConducteur() {
        return ResponseEntity.ok(courseService.getStatsConducteur());
    }
}
