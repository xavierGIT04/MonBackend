package com.tp.TripApp.security.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tp.TripApp.security.Repository.OtpRepository;
import com.tp.TripApp.security.entity.OtpValidation;

import jakarta.transaction.Transactional;

@Service
public class OtpService {
	
	@Autowired
    private OtpRepository otpRepository;
	
	@Transactional
	public String genererEtEnvoyerCode(String telephone) {
        // 1. Générer un code aléatoire de 4 chiffres
		String code = String.format("%04d", new Random().nextInt(10000));
		
        // 2. Enregistrer en base avec une validité de 5 minutes
        OtpValidation otp = new OtpValidation();
        otp.setTelephone(telephone);
        otp.setCode(code);
        otp.setExpiration(LocalDateTime.now().plusMinutes(5));
        otpRepository.save(otp);
        
        return code;
    }
	
	@Transactional
    public String verifierCode(String telephone, String codeSaisi) {
        Optional<OtpValidation> otpOpt = otpRepository.findByTelephone(telephone);

        if (otpOpt.isPresent()) {
            OtpValidation otp = otpOpt.get();

            // 1. Vérifier si le code est le bon ET s'il n'est pas expiré
            if (otp.getCode().equals(codeSaisi) && otp.estValide()) {
                // 2. Supprimer le code après usage pour qu'il ne soit plus réutilisable
                otpRepository.delete(otp);
                return telephone;
            }
        }
        return null; // Code faux ou expiré
    }
}
