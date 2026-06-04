package com.ecole.gestionecoles.config;

import com.ecole.gestionecoles.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * S'execute a CHAQUE requete : si un jeton "Bearer ..." est present et valide,
 * il identifie l'utilisateur pour la suite du traitement.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String entete = request.getHeader("Authorization");

        // Pas de jeton -> on laisse passer (la route decidera si elle exige une connexion)
        if (entete == null || !entete.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jeton = entete.substring(7);
            String email = jwtService.extraireEmail(jeton);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails utilisateur = userDetailsService.loadUserByUsername(email);
                if (jwtService.estValide(jeton, utilisateur)) {
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    utilisateur, null, utilisateur.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) {
            // Jeton invalide/expire : on continue sans identifier l'utilisateur
        }

        filterChain.doFilter(request, response);
    }
}