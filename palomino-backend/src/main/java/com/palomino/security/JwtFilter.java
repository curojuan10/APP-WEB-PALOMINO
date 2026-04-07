package com.palomino.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que se ejecuta una vez por request
 * Responsable de:
 * - Extraer el token del header Authorization
 * - Validar el token
 * - Cargar el usuario desde la BD
 * - Establecer la autenticación en el contexto de Spring Security
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public JwtFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Método invocado por Spring para filtrar cada request
     * Solo procesa requests que tengan un token JWT válido
     * 
     * @param request el request HTTP
     * @param response la respuesta HTTP
     * @param filterChain la cadena de filtros
     */
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, 
                                    @Nonnull HttpServletResponse response, 
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // 1. Extraer el JWT del header Authorization
            String jwt = extractTokenFromRequest(request);

            // 2. Validar que el token existe y no es nulo
            if (jwt != null) {
                // 3. Extraer el username (email) del token
                String username = jwtUtils.extractUsername(jwt);

                // 4. Validar que el token es válido para este usuario
                if (jwtUtils.isTokenValid(jwt, username)) {
                    // 5. Cargar los detalles del usuario desde la BD
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 6. Crear el objeto de autenticación
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    
                    // 7. Agregar detalles de la solicitud web
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 8. Establecer la autenticación en el contexto de Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // Log de error pero continúa el flujo (puede ser una ruta pública)
            logger.error("No se pudo procesar el token JWT: " + e.getMessage());
        }

        // 9. Continuar con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el JWT del header Authorization
     * El formato esperado es: "Bearer <token>"
     * 
     * @param request el request HTTP
     * @return el token JWT o null si no existe
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Remover "Bearer "
        }

        return null;
    }
}


