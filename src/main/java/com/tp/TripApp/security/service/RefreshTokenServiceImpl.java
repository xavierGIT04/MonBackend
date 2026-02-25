package com.tp.TripApp.security.service;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tp.TripApp.security.Repository.RefreshTokenRepository;
import com.tp.TripApp.security.Repository.UserRepository;
import com.tp.TripApp.security.dto.request.RefreshTokenRequest;
import com.tp.TripApp.security.dto.response.RefreshTokenResponse;
import com.tp.TripApp.security.entity.RefreshToken;
import com.tp.TripApp.security.entity.CompteUtilisateur;
import com.tp.TripApp.security.enums.TokenType;
import com.tp.TripApp.security.exception.TokenException;



@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{
	
	private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshExpiration;
    
   
	public RefreshTokenServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository,
			JwtService jwtService) {
		super();
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.jwtService = jwtService;
		
	}

	@Override
    public RefreshToken createRefreshToken(Long userId) {
        CompteUtilisateur user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        RefreshToken refreshToken =new  RefreshToken(
               user,
               Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()),
               Instant.now().plusMillis(refreshExpiration),
               false);
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token == null){
           
            throw new TokenException(null, "Token is null");
        }
        if(token.getExpiryDate().compareTo(Instant.now()) < 0 ){
            refreshTokenRepository.delete(token);
            throw new TokenException(token.getToken(), "Refresh token was expired. Please make a new authentication request");
        }
        return token;
    }
    
    @Override
    public RefreshTokenResponse generateNewToken(RefreshTokenRequest request) {
        CompteUtilisateur user = refreshTokenRepository.findByToken(request.getRefreshToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .orElseThrow(() -> new TokenException(request.getRefreshToken(),"Refresh token does not exist"));

        String token = jwtService.generateToken(user);
        return new RefreshTokenResponse(
                token,
                request.getRefreshToken(),
                TokenType.BEARER.name());
                
    }
}
