package com.tp.TripApp.security.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class OtpValidation {
	
	@Id
    private String telephone;
    private String code;
    private LocalDateTime expiration;

    public boolean estValide() {
        return LocalDateTime.now().isBefore(expiration);
    }

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LocalDateTime getExpiration() {
		return expiration;
	}

	public void setExpiration(LocalDateTime expiration) {
		this.expiration = expiration;
	}
    

}
