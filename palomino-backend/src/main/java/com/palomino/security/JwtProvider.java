package com.palomino.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Proveedor de JWT - Responsable de generar nuevos tokens JWT
 * Crea tokens con información del usuario autenticado
 */
@Component
public class JwtProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Obtiene la clave secreta para firmar JWT
     * 
     * @return SecretKey para operaciones criptográficas
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un JWT a partir de la autenticación del usuario
     * El subject del token es el email del usuario
     * 
     * @param authentication objeto de autenticación de Spring Security
     * @return JWT token como string
     */
    public String generateTokenFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        
        // Crear claims adicionales con información del usuario
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", authentication.getAuthorities());
        
        return buildToken(claims, email);
    }

    /**
     * Genera un JWT para un usuario específico (por email)
     * 
     * @param email el email del usuario
     * @return JWT token como string
     */
    public String generateToken(String email) {
        return buildToken(new HashMap<>(), email);
    }

    /**
     * Genera un JWT para UserDetails
     * 
     * @param userDetails objeto con detalles del usuario
     * @return JWT token como string
     */
    public String generateTokenFromUserDetails(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities());
        
        return buildToken(claims, userDetails.getUsername());
    }

    /**
     * Construye el JWT con claims, subject y tiempos de validez
     * 
     * @param claims mapa con información adicional del usuario
     * @param subject el subject del token (email)
     * @return JWT token firmado como string
     */
    private String buildToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expirationDate)
                .claims(claims)
                .signWith(getSigningKey())
                .compact();
    }
}

