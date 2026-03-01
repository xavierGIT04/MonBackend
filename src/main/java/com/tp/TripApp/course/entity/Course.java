package com.tp.TripApp.course.entity;

import java.time.LocalDateTime;

import org.locationtech.jts.geom.Point;

import com.tp.TripApp.course.enums.ModePaiement;
import com.tp.TripApp.course.enums.StatutCourse;
import com.tp.TripApp.course.enums.StatutPaiement;
import com.tp.TripApp.security.entity.CompteUtilisateur;
import com.tp.TripApp.security.entity.ProfilConducteur;

import jakarta.persistence.*;

@Entity
@Table(
    name = "courses",
    indexes = {
        // Index spatial sur les points de départ pour retrouver les courses proches
        @Index(name = "idx_course_depart_position", columnList = "depart_position"),
        @Index(name = "idx_course_statut", columnList = "statut")
    }
)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── Participants ────────────────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passager_id", nullable = false)
    private CompteUtilisateur passager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conducteur_id")
    private ProfilConducteur conducteur;

    // ─── Localisation (PostGIS) ───────────────────────────────────────────────
    /**
     * Point de départ — geometry(Point, 4326)
     * X = longitude, Y = latitude
     */
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point depart_position;

    private String depart_adresse;

    /**
     * Destination — geometry(Point, 4326)
     */
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point destination_position;

    private String destination_adresse;

    // ─── Prix & Paiement ─────────────────────────────────────────────────────
    private Double prix_estime;
    private Double prix_final;

    @Enumerated(EnumType.STRING)
    private ModePaiement mode_paiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPaiement statut_paiement = StatutPaiement.EN_ATTENTE;

    // ─── Statut & Timing ─────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCourse statut = StatutCourse.EN_ATTENTE;

    private LocalDateTime date_commande;
    private LocalDateTime date_acceptation;
    private LocalDateTime date_debut;
    private LocalDateTime date_fin;

    // ─── Notation ────────────────────────────────────────────────────────────
    private Integer note_conducteur;
    private String commentaire;

    // ─── Distance calculée par PostGIS ───────────────────────────────────────
    private Double distance_km;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type_vehicule_demande")
    private com.tp.TripApp.security.enums.TypeVehicule type_vehicule_demande;

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CompteUtilisateur getPassager() { return passager; }
    public void setPassager(CompteUtilisateur passager) { this.passager = passager; }

    public ProfilConducteur getConducteur() { return conducteur; }
    public void setConducteur(ProfilConducteur conducteur) { this.conducteur = conducteur; }

    public Point getDepart_position() { return depart_position; }
    public void setDepart_position(Point depart_position) { this.depart_position = depart_position; }

    public String getDepart_adresse() { return depart_adresse; }
    public void setDepart_adresse(String depart_adresse) { this.depart_adresse = depart_adresse; }

    public Point getDestination_position() { return destination_position; }
    public void setDestination_position(Point destination_position) { this.destination_position = destination_position; }

    public String getDestination_adresse() { return destination_adresse; }
    public void setDestination_adresse(String destination_adresse) { this.destination_adresse = destination_adresse; }

    // Helpers coordonnées départ
    public double getDepart_lat() { return depart_position != null ? depart_position.getY() : 0; }
    public double getDepart_lng() { return depart_position != null ? depart_position.getX() : 0; }

    // Helpers coordonnées destination
    public double getDestination_lat() { return destination_position != null ? destination_position.getY() : 0; }
    public double getDestination_lng() { return destination_position != null ? destination_position.getX() : 0; }

    public Double getPrix_estime() { return prix_estime; }
    public void setPrix_estime(Double prix_estime) { this.prix_estime = prix_estime; }

    public Double getPrix_final() { return prix_final; }
    public void setPrix_final(Double prix_final) { this.prix_final = prix_final; }

    public ModePaiement getMode_paiement() { return mode_paiement; }
    public void setMode_paiement(ModePaiement mode_paiement) { this.mode_paiement = mode_paiement; }

    public StatutPaiement getStatut_paiement() { return statut_paiement; }
    public void setStatut_paiement(StatutPaiement statut_paiement) { this.statut_paiement = statut_paiement; }

    public StatutCourse getStatut() { return statut; }
    public void setStatut(StatutCourse statut) { this.statut = statut; }

    public LocalDateTime getDate_commande() { return date_commande; }
    public void setDate_commande(LocalDateTime date_commande) { this.date_commande = date_commande; }

    public LocalDateTime getDate_acceptation() { return date_acceptation; }
    public void setDate_acceptation(LocalDateTime date_acceptation) { this.date_acceptation = date_acceptation; }

    public LocalDateTime getDate_debut() { return date_debut; }
    public void setDate_debut(LocalDateTime date_debut) { this.date_debut = date_debut; }

    public LocalDateTime getDate_fin() { return date_fin; }
    public void setDate_fin(LocalDateTime date_fin) { this.date_fin = date_fin; }

    public Integer getNote_conducteur() { return note_conducteur; }
    public void setNote_conducteur(Integer note_conducteur) { this.note_conducteur = note_conducteur; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Double getDistance_km() { return distance_km; }
    public void setDistance_km(Double distance_km) { this.distance_km = distance_km; }
    
    public com.tp.TripApp.security.enums.TypeVehicule getType_vehicule_demande() {
        return type_vehicule_demande;
    }
    public void setType_vehicule_demande(com.tp.TripApp.security.enums.TypeVehicule type_vehicule_demande) {
        this.type_vehicule_demande = type_vehicule_demande;
    }
}
