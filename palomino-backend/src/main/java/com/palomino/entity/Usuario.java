package com.palomino.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.ManyToOne;

import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Usuario - Representa un usuario del sistema
 * 
 * Sin Lombok - Getters y Setters manuales con validación
 * Constructor controlado sin @AllArgsConstructor
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;



    // ===== CONSTRUCTORES =====

    /**
     * Constructor sin parámetros (requerido por Hibernate)
     */
    public Usuario() {
    }

    /**
     * Constructor controlado para crear usuario
     * NO incluye ID (generado por BD)
     * NO incluye relaciones complejas
     * 
     * @param nombre del usuario
     * @param email único del usuario
     * @param password (debe venir encriptado desde AuthService)
     * @param rol del usuario
     */
    public Usuario(String nombre, String email, String password, Rol rol) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email no válido");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password no puede estar vacío");
        }
        if (rol == null) {
            throw new IllegalArgumentException("Rol no puede ser nulo");
        }
        
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // ===== GETTERS =====

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Rol getRol() {
        return rol;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    // ===== SETTERS CONTROLADOS =====

    /**
     * Setter para nombre con validación
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre no puede estar vacío");
        }
        if (nombre.length() > 100) {
            throw new IllegalArgumentException("Nombre no puede exceder 100 caracteres");
        }
        this.nombre = nombre;
    }

    /**
     * Setter para email con validación
     */
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email no puede estar vacío");
        }
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email no es válido");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("Email no puede exceder 100 caracteres");
        }
        this.email = email;
    }

    /**
     * Setter para password con validación
     * 
     * IMPORTANTE: El password debe venir ENCRIPTADO desde AuthServiceImpl
     * Este setter solo valida que no sea nulo/vacío
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password no puede estar vacío");
        }
        // Se asume que ya viene encriptado desde AuthServiceImpl
        this.password = password;
    }

    /**
     * Setter para rol con validación
     */
    public void setRol(Rol rol) {
        if (rol == null) {
            throw new IllegalArgumentException("Rol no puede ser nulo");
        }
        this.rol = rol;
    }

    /**
     * NO hay setter para id (es @GeneratedValue)
     * NO hay setter para fechaRegistro (es auto-generada)
     * NO hay setters públicos para relaciones (se manejan via repositorio)
     */

    // ===== MÉTODOS DE CICLO DE VIDA JPA =====

    /**
     * Hook JPA: Se ejecuta antes de guardar la entidad por primera vez
     * Establece fechaRegistro
     */
    @PrePersist
    protected void onCreate() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDateTime.now();
        }
    }

    // ===== MÉTODOS AUXILIARES =====

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + (rol != null ? rol.getNombre() : "null") +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id != null && id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
