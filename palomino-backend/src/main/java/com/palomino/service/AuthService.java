package com.palomino.service;

import com.palomino.dto.request.LoginRequest;
import com.palomino.dto.request.RegisterRequest;
import com.palomino.dto.response.AuthResponse;

/**
 * Interfaz de servicio para autenticación
 * Define operaciones de login y registro de usuarios
 */
public interface AuthService {

    /**
     * Registra un nuevo usuario en el sistema
     * 
     * @param registerRequest datos del nuevo usuario (nombre, email, password)
     * @return AuthResponse con token JWT y datos del usuario registrado
     * @throws IllegalArgumentException si el email ya existe
     */
    AuthResponse register(RegisterRequest registerRequest);

    /**
     * Autentica un usuario y genera un token JWT
     * 
     * @param loginRequest credenciales del usuario (email, password)
     * @return AuthResponse con token JWT y datos del usuario autenticado
     * @throws IllegalArgumentException si las credenciales son inválidas
     */
    AuthResponse login(LoginRequest loginRequest);
}

