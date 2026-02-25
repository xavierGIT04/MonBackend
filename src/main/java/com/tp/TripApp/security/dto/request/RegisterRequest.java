package com.tp.TripApp.security.dto.request;


import com.tp.TripApp.security.enums.TypeVehicule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public class RegisterRequest {
	
	 	@NotBlank(message = "le prenom est requis")
	    private String prenom;
	 	
	    @NotBlank(message = "le nom est requis")
	    private String nom;
	    
	    @NotBlank(message = "le username est requis")
	    private String username;
	    
	    @NotBlank(message = "password is required")
	    private String password;
	    
	    @NotNull
	    private String profil;
	    private String photoProfil;
	    
	    private String photo_permis;
	    private String numero_permis;
	    private String photo_cni;
	    private String photo_vehicule;
	    private String immatriculation;
	    private TypeVehicule type_vehicule;

		public String getPrenom() {
			return prenom;
		}

		public void setPrenom(String prenom) {
			this.prenom = prenom;
		}

		public String getNom() {
			return nom;
		}

		public void setNom(String nom) {
			this.nom = nom;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getProfil() {
			return profil;
		}

		public void setProfil(String profil) {
			this.profil = profil;
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

		public String getPhoto_vehicule() {
			return photo_vehicule;
		}

		public void setPhoto_vehicule(String photo_vehicule) {
			this.photo_vehicule = photo_vehicule;
		}

		public String getImmatriculation() {
			return immatriculation;
		}

		public void setImmatriculation(String immatriculation) {
			this.immatriculation = immatriculation;
		}

		public TypeVehicule getType_vehicule() {
			return type_vehicule;
		}

		public void setType_vehicule(TypeVehicule type_vehicule) {
			this.type_vehicule = type_vehicule;
		}

		public String getNumero_permis() {
			return numero_permis;
		}

		public void setNumero_permis(String numero_permis) {
			this.numero_permis = numero_permis;
		}

		public String getPhotoProfil() {
			return photoProfil;
		}

		public void setPhotoProfil(String photoProfil) {
			this.photoProfil = photoProfil;
		}
		
	    
	    
	    
}

