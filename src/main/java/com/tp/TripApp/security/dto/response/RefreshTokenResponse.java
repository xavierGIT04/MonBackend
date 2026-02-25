package  com.tp.TripApp.security.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import  com.tp.TripApp.security.enums.TokenType;


public class RefreshTokenResponse {

	@JsonProperty("access_token")
    private String accessToken;
	
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("token_type")
    private String tokenType = TokenType.BEARER.name();

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public RefreshTokenResponse(String accessToken, String refreshToken, String tokenType) {
		super();
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.tokenType = tokenType;
	}
    
	
    
}
