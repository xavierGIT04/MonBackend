package  com.tp.TripApp.security.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import  com.tp.TripApp.security.dto.request.AuthenticationRequest;
import  com.tp.TripApp.security.dto.request.RegisterRequest;
import  com.tp.TripApp.security.dto.response.AuthenticationResponse;

public interface AuthenticationService {
	AuthenticationResponse register(RegisterRequest request, MultipartFile fPermis, 
															 MultipartFile fProfil,
													         MultipartFile fCni, 
													         MultipartFile fVehicule) throws IOException;
	AuthenticationResponse authenticate(AuthenticationRequest request);
}
