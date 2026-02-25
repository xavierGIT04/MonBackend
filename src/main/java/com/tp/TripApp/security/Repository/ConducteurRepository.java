package com.tp.TripApp.security.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tp.TripApp.security.entity.ProfilConducteur;

public interface ConducteurRepository extends JpaRepository<ProfilConducteur, Long>{

}
