package com.tp.TripApp.security.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;



@Entity
@Table(name = "comptes_utilisateurs")
public class CompteUtilisateur implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(unique = true, nullable = false)
	private UUID publicId = UUID.randomUUID();
	
	@Column(unique = true)
	private String telephone; // pour conducteur et passager
	
	@Column(unique = true)
	private String login;  // pour regulateur
	
	private String password;
    private String nom;
    private String prenom;
    private boolean est_actif;
    
    @Column(nullable = true)
    private String photoProfil;
    
    @OneToOne(mappedBy = "compte", cascade = CascadeType.ALL)
    private ProfilPassager profilPassager;
    
    @OneToOne(mappedBy = "compte", cascade = CascadeType.ALL)
    private ProfilConducteur profilConducteur;
    
    @OneToOne(mappedBy = "compte", cascade = CascadeType.ALL)
    private Regulateur regulateur;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	List<GrantedAuthority> authorities = new ArrayList<>();
    	if(profilConducteur != null) {authorities.add(new SimpleGrantedAuthority("ROLE_CONDUCTEUR"));}
    	if(profilPassager != null) {authorities.add(new SimpleGrantedAuthority("ROLE_PASSAGER"));}
    	if(regulateur != null) {authorities.add(new SimpleGrantedAuthority("ROLE_REGULATEUR"));}
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

 
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return est_actif;
    }

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

	public void setPassword(String password) {
		this.password = password;
	}


	public void setEst_actif(boolean est_actif) {
		this.est_actif = est_actif;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public ProfilPassager getProfilPassager() {
		return profilPassager;
	}

	public void setProfilPassager(ProfilPassager profilPassager) {
		this.profilPassager = profilPassager;
	}

	public ProfilConducteur getProfilConducteur() {
		return profilConducteur;
	}

	public void setProfilConducteur(ProfilConducteur profilConducteur) {
		this.profilConducteur = profilConducteur;
	}

	public Regulateur getRegulateur() {
		return regulateur;
	}

	public void setRegulateur(Regulateur regulateur) {
		this.regulateur = regulateur;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public String getUsername() {
		return (login!=null) ? login: telephone;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPhotoProfil() {
		return photoProfil;
	}

	public void setPhotoProfil(String photoProfil) {
		this.photoProfil = photoProfil;
	}
	
	
    
    
}
