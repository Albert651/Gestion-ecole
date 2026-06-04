package com.ecole.gestionecoles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationProvider authProvider)
            throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // --- Routes PUBLIQUES ---
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/etablissements/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/annonces/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/messages").permitAll()

                        // --- Routes reservees a l'ADMIN ---
                        .requestMatchers("/api/messages/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/etablissements/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/etablissements/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/etablissements/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/annonces/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/annonces/**").hasRole("ADMIN")

                        // --- RESERVATIONS ---
                        .requestMatchers(HttpMethod.POST, "/api/reservations").authenticated()      // reserver
                        .requestMatchers(HttpMethod.GET, "/api/reservations/mes").authenticated()   // mes reservations
                        .requestMatchers(HttpMethod.GET, "/api/reservations").hasRole("ADMIN")      // toutes (admin)
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/**").hasRole("ADMIN")   // changer statut (admin)
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/**").authenticated() // annuler (proprietaire/admin)

                        // --- Tout le reste exige d'etre connecte ---
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}