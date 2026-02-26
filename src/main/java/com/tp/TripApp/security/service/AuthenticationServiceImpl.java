package com.tp.TripApp.security.service;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tp.TripApp.security.Repository.ConducteurRepository;
import com.tp.TripApp.security.Repository.PassagerRepository;
import com.tp.TripApp.security.Repository.UserRepository;
import com.tp.TripApp.security.dto.request.AuthenticationRequest;
import com.tp.TripApp.security.dto.request.RegisterRequest;
import com.tp.TripApp.security.dto.response.AuthenticationResponse;
import com.tp.TripApp.security.entity.CompteUtilisateur;
import com.tp.TripApp.security.entity.ProfilConducteur;
import com.tp.TripApp.security.entity.ProfilPassager;
import com.tp.TripApp.security.entity.Regulateur;
import com.tp.TripApp.security.enums.TokenType;
import com.tp.TripApp.service.ImageService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final ImageService imageService;

    public AuthenticationServiceImpl(PasswordEncoder passwordEncoder, JwtService jwtService,
            UserRepository userRepository, AuthenticationManager authenticationManager,
            RefreshTokenService refreshTokenService, ImageService imageService,
            PassagerRepository passagerRepository, ConducteurRepository conducteurRepository) {
        super();
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.imageService = imageService;
    }

    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request, MultipartFile fPermis,
            MultipartFile fProfil, MultipartFile fCni, MultipartFile fVehicule) throws IOException {
        CompteUtilisateur user = new CompteUtilisateur();
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEst_actif(true);

        if (request.getProfil().toUpperCase().equals("PASSAGER")) {
            user.setTelephone(request.getUsername());
            user.setLogin(null);
            ProfilPassager passager = new ProfilPassager();
            passager.setCompte(user);
            user.setProfilPassager(passager);
        } else if (request.getProfil().toUpperCase().equals("CONDUCTEUR")) {
            user.setTelephone(request.getUsername());
            user.setLogin(null);
            ProfilConducteur conducteur = new ProfilConducteur();

            // --- Upload des images vers Cloudinary dans les dossiers spécifiques ---
            if (fPermis != null) {
                conducteur.setPhoto_permis(imageService.uploadImage(fPermis, "permis"));
            }
            if (fCni != null) {
                conducteur.setPhoto_cni(imageService.uploadImage(fCni, "cni"));
            }
            if (fVehicule != null) {
                conducteur.setPhoto_vehicule(imageService.uploadImage(fVehicule, "vehicules"));
            }
            if (fProfil != null && !fProfil.isEmpty()) {
                String urlProfil = imageService.uploadImage(fProfil, "profils");
                user.setPhotoProfil(urlProfil);
            }

            // --- Données texte ---
            conducteur.setNumero_permis(request.getNumero_permis());
            conducteur.setImmatriculation(request.getImmatriculation());
            conducteur.setType_vehicule(request.getType_vehicule());
            conducteur.setEst_valide_par_admin(false);
            // ❌ Ligne supprimée : conducteur.setStatut_service(Statut_Service.LIBRE);
            conducteur.setCompte(user);
            user.setProfilConducteur(conducteur);

        } else if (request.getProfil().toUpperCase().equals("REGULATEUR")) {
            user.setLogin(request.getUsername());
            user.setTelephone(null);
            Regulateur regulateur = new Regulateur();
            regulateur.setCompte(user);
            user.setRegulateur(regulateur);
        }

        user = userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());

        List<? extends GrantedAuthority> roles = user.getAuthorities().stream().toList();

        return new AuthenticationResponse(
                user.getPublicId(),
                user.getUsername(),
                roles,
                jwt,
                refreshToken.getToken(),
                TokenType.BEARER.name());
    }

    @Override
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        CompteUtilisateur user = userRepository.findByTelephoneOrLogin(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or password."));
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        List<? extends GrantedAuthority> roles = user.getAuthorities().stream().toList();

        return new AuthenticationResponse(
                user.getPublicId(),
                user.getUsername(),
                roles,
                jwt,
                refreshToken.getToken(),
                TokenType.BEARER.name());
    }
}
