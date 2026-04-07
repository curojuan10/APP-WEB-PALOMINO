package com.palomino.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security para el proyecto Palomino
 * Define:
 * - Rutas públicas vs protegidas
 * - Política de sesiones (stateless para JWT)
 * - Autenticación y autorización
 * - CORS y CSRF
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtFilter jwtFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService, 
                         BCryptPasswordEncoder passwordEncoder,
                         JwtFilter jwtFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtFilter = jwtFilter;
    }

    /**
     * Configura el AuthenticationManager para autenticación con email/password
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
        
        return authenticationManagerBuilder.build();
    }

    /**
     * Configura la cadena de filtros de seguridad
     * Define rutas públicas, protegidas y políticas de sesión
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilitar CSRF para API REST (stateless)
                .csrf(csrf -> csrf.disable())
                
                // Configurar CORS (será expandido en producción)
                .cors(cors -> {})
                
                // Usar sesiones stateless (JWT no requiere sesión)
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Agregar el filtro JWT antes del filtro de autenticación por username/password
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                
                // Configurar autorización de rutas
                .authorizeHttpRequests(authz -> authz
                        // Rutas públicas (sin autenticación)
                        .requestMatchers("/auth/login", "/auth/register").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        
                        // Rutas protegidas (requieren autenticación)
                        .requestMatchers("/api/usuario/**").authenticated()
                        
                        // Rutas solo para ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        
                        // Todas las demás requieren autenticación
                        .anyRequest().authenticated()
                )
                
                // Configurar manejo de excepciones (será expandido con entry point customizado)
                .exceptionHandling(exception -> {
                    // Placeholder para manejar excepciones de autenticación/autorización
                    // Se implementará en siguiente fase con AuthenticationEntryPoint
                });
        
        return http.build();
    }
}

