package  com.tp.TripApp.security.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tp.TripApp.security.dto.request.AuthenticationRequest;
import com.tp.TripApp.security.dto.request.OtpRequest;
import com.tp.TripApp.security.dto.request.RefreshTokenRequest;
import com.tp.TripApp.security.dto.request.RegisterRequest;
import com.tp.TripApp.security.dto.response.AuthenticationResponse;
import com.tp.TripApp.security.dto.response.RefreshTokenResponse;
import com.tp.TripApp.security.service.AuthenticationService;
import com.tp.TripApp.security.service.OtpService;
import com.tp.TripApp.security.service.RefreshTokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthenticationController {
	@Autowired
    private  AuthenticationService authenticationService;
	
	@Autowired
    private OtpService otpService;
	
	@Autowired
    private  RefreshTokenService refreshTokenService;

	
	@PostMapping("/demander-otp")
	public ResponseEntity<Map<String, String>> demanderOtp(@RequestBody Map<String, String> body) {
	    String telephone = body.get("telephone");
	    String code = otpService.genererEtEnvoyerCode(telephone);
	    
	    return ResponseEntity.ok(Map.of(
	        "status", "CODE_SENT",
	        "code", code,
	        "message", "Le code de simulation est : " + code
	    ));
	}
	
	@PostMapping("/verifier-otp")
	public ResponseEntity<?> verifierOtp(@RequestBody OtpRequest request) {
	    String telephoneValide = otpService.verifierCode(request.getTelephone(), request.getCodeSaisi());

	    if (telephoneValide != null) {
	        // Succès : On renvoie le numéro validé pour que le mobile le stocke
	        return ResponseEntity.ok(Map.of(
	            "status", "SUCCESS",
	            "telephone", telephoneValide,
	            "message", "Numéro vérifié avec succès"
	        ));
	    } else {
	        // Échec : Code incorrect ou expiré
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Code invalide ou expiré");
	    }
	}

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(
	        @RequestPart("data") @Valid RegisterRequest request,
	        @RequestPart(value = "fileProfil", required = false) MultipartFile fileProfil, 
	        @RequestPart(value = "filePermis", required = false) MultipartFile filePermis,
	        @RequestPart(value = "fileCni", required = false) MultipartFile fileCni,
	        @RequestPart(value = "fileVehicule", required = false) MultipartFile fileVehicule
	) throws IOException {
		
	    return ResponseEntity.ok(authenticationService.register(request, filePermis, fileProfil,  fileCni, fileVehicule));
	}

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.generateNewToken(request));
    }



}
