package com.tp.TripApp.course.entity;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;

import com.tp.TripApp.security.entity.ProfilConducteur;

import jakarta.persistence.*;

@Entity
@Table(
    name = "localisations_conducteurs",
    indexes = {
        // Index spatial GiST — clé de la performance PostGIS
        @Index(name = "idx_localisation_position", columnList = "position")
    }
)
public class LocalisationConducteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conducteur_id", nullable = false, unique = true)
    private ProfilConducteur conducteur;

    /**
     * Position géographique du conducteur.
     * Type PostGIS geometry(Point, 4326) — SRID 4326 = WGS84 (GPS standard)
     * X = longitude, Y = latitude (convention JTS/PostGIS)
     */
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point position;

    @Column(nullable = false)
    private LocalDateTime derniere_maj = LocalDateTime.now();

    // ─── Getters & Setters ───────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProfilConducteur getConducteur() { return conducteur; }
    public void setConducteur(ProfilConducteur conducteur) { this.conducteur = conducteur; }

    public Point getPosition() { return position; }
    public void setPosition(Point position) { this.position = position; }

    public LocalDateTime getDerniere_maj() { return derniere_maj; }
    public void setDerniere_maj(LocalDateTime derniere_maj) { this.derniere_maj = derniere_maj; }

    // ─── Helpers ─────────────────────────────────────────────────────────

    /** Latitude (Y en WGS84) */
    public double getLatitude() {
        return position != null ? position.getY() : 0;
    }

    /** Longitude (X en WGS84) */
    public double getLongitude() {
        return position != null ? position.getX() : 0;
    }
}
