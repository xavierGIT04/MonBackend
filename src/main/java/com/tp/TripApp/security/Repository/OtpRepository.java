package com.tp.TripApp.security.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tp.TripApp.security.entity.OtpValidation;

public interface OtpRepository extends JpaRepository<OtpValidation, String>{
	Optional<OtpValidation> findByTelephone(String telephone);
}
