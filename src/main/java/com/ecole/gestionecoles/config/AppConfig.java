package com.ecole.gestionecoles.config;

import com.ecole.gestionecoles.repository.UtilisateurRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Petite configuration a part : elle fournit le UserDetailsService
 * (qui charge un utilisateur depuis la base par son email).
 *
 * On le sort de SecurityConfig pour eviter une reference circulaire
 * entre SecurityConfig et JwtAuthFilter.
 */
@Configuration
public class AppConfig {

    @Bean
    public UserDetailsService userDetailsService(UtilisateurRepository repository) {
        return email -> repository.findByEmail(email)
                .map(u -> User.withUsername(u.getEmail())
                        .password(u.getMotDePasse())
                        .roles(u.getRole())   // "ADMIN" -> autorite "ROLE_ADMIN"
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + email));
    }
}