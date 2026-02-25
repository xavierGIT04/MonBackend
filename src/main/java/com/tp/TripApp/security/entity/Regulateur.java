package com.tp.TripApp.security.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Regulateur {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(unique = true, nullable = false)
	private UUID publicId = UUID.randomUUID();
	
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

	public CompteUtilisateur getCompte() {
		return compte;
	}

	public void setCompte(CompteUtilisateur compte) {
		this.compte = compte;
	}
	
	

}
