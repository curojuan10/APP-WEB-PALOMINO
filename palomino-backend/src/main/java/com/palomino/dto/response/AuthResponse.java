package com.palomino.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para respuesta de autenticación (login/register)
 * Contiene el token JWT y información básica del usuario (sin password)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private Long userId;
    private String nombre;
    private String email;
    private String rol;
    private String mensaje;
}

