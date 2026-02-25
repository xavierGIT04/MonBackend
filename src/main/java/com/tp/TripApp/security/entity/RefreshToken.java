package com.tp.TripApp.security.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;



@Entity
public class RefreshToken {
		@Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    private long id;

	    @ManyToOne
	    @JoinColumn(name = "user_id", referencedColumnName = "id")
	    private CompteUtilisateur user;

	    @Column(nullable = false, unique = true)
	    private String token;

	    @Column(nullable = false)
	    private Instant expiryDate;

	    public boolean revoked;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public CompteUtilisateur getUser() {
			return user;
		}

		public void setUser(CompteUtilisateur user) {
			this.user = user;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public Instant getExpiryDate() {
			return expiryDate;
		}

		public void setExpiryDate(Instant expiryDate) {
			this.expiryDate = expiryDate;
		}

		public boolean isRevoked() {
			return revoked;
		}

		public void setRevoked(boolean revoked) {
			this.revoked = revoked;
		}

		public RefreshToken(CompteUtilisateur user, String token, Instant expiryDate, boolean revoked) {
			super();
			
			this.user = user;
			this.token = token;
			this.expiryDate = expiryDate;
			this.revoked = revoked;
		}
	    
	    

}
