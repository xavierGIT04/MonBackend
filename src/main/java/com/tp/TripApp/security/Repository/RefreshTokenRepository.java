package  com.tp.TripApp.security.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import  com.tp.TripApp.security.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);
}
