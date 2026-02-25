package com.tp.TripApp.security.entity;

import java.util.UUID;

import com.tp.TripApp.security.enums.Statut_Service;
import com.tp.TripApp.security.enums.TypeVehicule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class ProfilConducteur {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(unique = true, nullable = false)
	private UUID publicId = UUID.randomUUID();
	
	private String photo_permis;
	private String numero_permis;
    private String photo_cni;
    private String photo_vehicule;
    private String immatriculation;
    private Float note_moyenne = 0.0f;
    private Boolean est_valide_par_admin = false; // Validation Régulateur
    
    @Enumerated(EnumType.STRING)
    private TypeVehicule type_vehicule; // ZEM, TAXI
    
    @Enumerated(EnumType.STRING)
    private Statut_Service statut_service;

    @OneToOne
    @JoinColumn(name = "comptes_utilisateurs_id")
    private CompteUtilisateur compte;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getPublicId() {
		return publicId;
	}

	public void setPublicId(UUID publicId) {
		this.publicId = publicId;
	}

	public String getPhoto_permis() {
		return photo_permis;
	}

	public void setPhoto_permis(String photo_permis) {
		this.photo_permis = photo_permis;
	}

	public String getPhoto_cni() {
		return photo_cni;
	}

	public void setPhoto_cni(String photo_cni) {
		this.photo_cni = photo_cni;
	}

	public String getImmatriculation() {
		return immatriculation;
	}

	public void setImmatriculation(String immatriculation) {
		this.immatriculation = immatriculation;
	}

	public Float getNote_moyenne() {
		return note_moyenne;
	}

	public void setNote_moyenne(Float note_moyenne) {
		this.note_moyenne = note_moyenne;
	}

	public Boolean getEst_valide_par_admin() {
		return est_valide_par_admin;
	}

	public void setEst_valide_par_admin(Boolean est_valide_par_admin) {
		this.est_valide_par_admin = est_valide_par_admin;
	}

	public TypeVehicule getType_vehicule() {
		return type_vehicule;
	}

	public void setType_vehicule(TypeVehicule type_vehicule) {
		this.type_vehicule = type_vehicule;
	}

	public Statut_Service getStatut_service() {
		return statut_service;
	}

	public void setStatut_service(Statut_Service statut_service) {
		this.statut_service = statut_service;
	}

	public CompteUtilisateur getCompte() {
		return compte;
	}

	public void setCompte(CompteUtilisateur compte) {
		this.compte = compte;
	}

	public String getPhoto_vehicule() {
		return photo_vehicule;
	}

	public void setPhoto_vehicule(String photo_vehicule) {
		this.photo_vehicule = photo_vehicule;
	}

	public String getNumero_permis() {
		return numero_permis;
	}

	public void setNumero_permis(String numero_permis) {
		this.numero_permis = numero_permis;
	}
	
	
    
    
    
}
