package com.ecole.gestionecoles.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Genere et verifie les jetons JWT (le "badge" signe d'un utilisateur connecte).
 */
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long dureeValiditeMs;

    // La cle de signature, derivee du secret
    private SecretKey cle() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Cree un jeton pour un utilisateur (email + role a l'interieur)
    public String genererJeton(String email, String role) {
        Date maintenant = new Date();
        Date expiration = new Date(maintenant.getTime() + dureeValiditeMs);
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(maintenant)
                .expiration(expiration)
                .signWith(cle())
                .compact();
    }

    // Extrait l'email contenu dans le jeton
    public String extraireEmail(String jeton) {
        return lireContenu(jeton).getSubject();
    }

    // Verifie que le jeton est valide (bonne signature, non expire, bon utilisateur)
    public boolean estValide(String jeton, UserDetails utilisateur) {
        try {
            Claims contenu = lireContenu(jeton);
            return contenu.getSubject().equals(utilisateur.getUsername())
                    && contenu.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims lireContenu(String jeton) {
        return Jwts.parser()
                .verifyWith(cle())
                .build()
                .parseSignedClaims(jeton)
                .getPayload();
    }
}