package com.tp.TripApp.security.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tp.TripApp.security.Repository.UserRepository;

@Configuration
public class ApplicationSecurityConfig {
	
    private final  UserRepository userRepository;

    

    public ApplicationSecurityConfig(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByTelephoneOrLogin(username,username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Provider d'authentification DAO
     * Utilise le UserDetailsService et l'encodeur de mot de passe
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Manager d'authentification
     * Utilisé pour authentifier les utilisateurs
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Encodeur de mot de passe BCrypt
     * Utilisé pour hasher et vérifier les mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}