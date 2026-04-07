package com.palomino.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Utilidades para manipulación y validación de JWT
 * Proporciona métodos para:
 * - Extraer información de tokens
 * - Validar tokens
 * - Verificar expiración
 */
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /**
     * Obtiene la clave secreta para firmar/verificar JWT
     * 
     * @return SecretKey para operaciones criptográficas
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extrae todos los claims (información) del token JWT
     * 
     * @param token el token JWT
     * @return Claims con la información del token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrae un claim específico del token
     * 
     * @param token el token JWT
     * @param claimsResolver función que extrae el claim deseado
     * @return el claim extraído
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae el subject (username/email) del token
     * 
     * @param token el token JWT
     * @return el subject (email del usuario)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token
     * 
     * @param token el token JWT
     * @return la fecha de expiración
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Verifica si el token ha expirado
     * 
     * @param token el token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida si un token es válido para un usuario específico
     * 
     * @param token el token JWT
     * @param username el username (email) esperado
     * @return true si el token es válido para el usuario y no ha expirado
     */
    public boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username)) && !isTokenExpired(token);
    }
}

