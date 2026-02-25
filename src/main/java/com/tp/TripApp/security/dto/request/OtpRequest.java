package com.tp.TripApp.security.dto.request;

public class OtpRequest {
	private String telephone;
	private String codeSaisi;

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getCodeSaisi() {
		return codeSaisi;
	}

	public void setCodeSaisi(String codeSaisi) {
		this.codeSaisi = codeSaisi;
	}
	
	
}
