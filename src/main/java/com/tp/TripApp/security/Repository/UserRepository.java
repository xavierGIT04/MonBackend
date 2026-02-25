package  com.tp.TripApp.security.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import  com.tp.TripApp.security.entity.CompteUtilisateur;

public interface UserRepository extends JpaRepository<CompteUtilisateur, Long>{
    Optional<CompteUtilisateur> findByTelephoneOrLogin(String telephone, String login);

}
