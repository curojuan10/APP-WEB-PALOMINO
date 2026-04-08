package com.palomino.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitud de login
 * Contiene credenciales de usuario (email y password)
 * 
 * Sin Lombok - Getters y Setters manuales para claridad
 */
public class LoginRequest {

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    /**
     * Constructor sin parámetros (requerido para Jackson)
     */
    public LoginRequest() {
    }

    /**
     * Constructor con parámetros
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // ===== GETTERS =====

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // ===== SETTERS =====

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // ===== MÉTODOS AUXILIARES =====

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                '}';
    }
}

