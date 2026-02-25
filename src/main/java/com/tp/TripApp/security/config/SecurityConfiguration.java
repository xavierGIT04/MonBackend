package com.tp.TripApp.security.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final Http401UnauthorizedEntryPoint unauthorizedEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    
    
    
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter,
			AuthenticationProvider authenticationProvider, Http401UnauthorizedEntryPoint unauthorizedEntryPoint,
			CustomAccessDeniedHandler accessDeniedHandler) {
		super();
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.authenticationProvider = authenticationProvider;
		this.unauthorizedEntryPoint = unauthorizedEntryPoint;
		this.accessDeniedHandler = accessDeniedHandler;
	}


	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(
                                        "/api/v1/auth/**",
                                        "/error",
                                        "/csrf",
                                        "/resources/**",
                                        // Swagger et OpenAPI
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/webjars/**"
                                ).permitAll()
                               //.requestMatchers("/api/v1/admin/resource").hasRole("ADMIN") replaced with annotation in AuthorizationController      
                                .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}