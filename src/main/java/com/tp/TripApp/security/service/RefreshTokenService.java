package com.tp.TripApp.security.service;

import com.tp.TripApp.security.dto.request.RefreshTokenRequest;
import com.tp.TripApp.security.dto.response.RefreshTokenResponse;
import com.tp.TripApp.security.entity.RefreshToken;

public interface RefreshTokenService {
	RefreshToken createRefreshToken(Long userId);
	RefreshToken verifyExpiration(RefreshToken token);
	RefreshTokenResponse generateNewToken(RefreshTokenRequest request);
}
