package  com.tp.TripApp.security.dto.response;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AuthenticationResponse {
	
		private  UUID id;
	    private String username;
	    private List<? extends GrantedAuthority> roles;

	    @JsonProperty("access_token")
	    private String accessToken;
	    
	    @JsonProperty("refresh_token")
	    private String refreshToken;
	    
	    @JsonProperty("token_type")
	    private String tokenType;

		public AuthenticationResponse(UUID id, String username, List<? extends GrantedAuthority> roles, String accessToken,
				String refreshToken, String tokenType) {
			super();
			this.id = id;
			this.username = username;
			this.roles = roles;
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
			this.tokenType = tokenType;
		}

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public List<? extends GrantedAuthority> getRoles() {
			return roles;
		}

		public void setRoles(List<? extends GrantedAuthority> roles) {
			this.roles = roles;
		}

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
	    
		
	    
	    
}
