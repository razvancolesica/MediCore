package com.spital.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Dezactivăm CSRF pentru JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
                        .requestMatchers("/api/pacients/register/**").permitAll()
                        .requestMatchers("/api/reservations/**").hasAuthority("ROLE_PACIENT")
                        .requestMatchers("/api/auth/**").permitAll() // Permitem acces la login/register
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN") // Acces pentru admin
                        .requestMatchers("/api/pacients/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_PACIENT") // Acces pentru pacienți și admin
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Fără sesiuni (stateless cu JWT)
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'")) // Protecție XSS prin CSP
                        .frameOptions(frame -> frame.deny()) // Dezactivează iframes pentru protecție împotriva clickjacking
                );

        return http.build();
    }

    // Configurăm conversia JWT pentru a extrage autoritățile din claim-ul "roles"
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Citește claim-ul "role" (un singur string)
            String role = (String) jwt.getClaims().get("role");
            if (role == null) {
                // Dacă nu există, nu acordăm nicio autoritate
                return Collections.emptyList();
            }
            // Întoarcem o listă cu o singură autoritate: "ROLE_<role>"
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
        });
        return converter;
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Definim bean-ul JwtDecoder folosind secretul specificat
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(Keys.hmacShaKeyFor(jwtSecret.getBytes())).build();
    }
}
