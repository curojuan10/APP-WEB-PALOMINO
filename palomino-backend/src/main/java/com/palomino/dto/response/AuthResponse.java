package com.palomino.dto.response;

/**
 * DTO para respuesta de autenticación (login/register)
 * Contiene el token JWT y información básica del usuario (sin password)
 * 
 * Sin Lombok - Getters y Setters manuales para claridad
 */
public class AuthResponse {

    private String token;
    private Long userId;
    private String nombre;
    private String email;
    private String rol;
    private String mensaje;

    /**
     * Constructor sin parámetros (requerido para Jackson)
     */
    public AuthResponse() {
    }

    /**
     * Constructor con parámetros
     */
    public AuthResponse(String token, Long userId, String nombre, String email, String rol, String mensaje) {
        this.token = token;
        this.userId = userId;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
        this.mensaje = mensaje;
    }

    // ===== GETTERS =====

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getRol() {
        return rol;
    }

    public String getMensaje() {
        return mensaje;
    }

    // ===== SETTERS =====

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    // ===== MÉTODOS AUXILIARES =====

    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null") + '\'' +
                ", userId=" + userId +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol='" + rol + '\'' +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}

