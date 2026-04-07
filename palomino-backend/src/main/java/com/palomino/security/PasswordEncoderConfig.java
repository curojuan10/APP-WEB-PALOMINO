package com.palomino.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuración de PasswordEncoder para encriptación de contraseñas con BCrypt
 * Proporciona un bean reutilizable en toda la aplicación
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Bean para encriptar contraseñas con BCrypt
     * BCryptPasswordEncoder incluye automáticamente salt y es resistente a ataques
     * 
     * @return BCryptPasswordEncoder configurado
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

