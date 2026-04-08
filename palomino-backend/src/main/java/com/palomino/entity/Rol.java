package com.palomino.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad Rol - Catálogo de roles disponibles en el sistema
 * 
 * Roles: ADMIN, CLIENTE, INSTRUCTOR
 * 
 * Sin Lombok - Getters y Setters manuales
 */
@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String nombre;

    // ===== CONSTRUCTORES =====

    /**
     * Constructor sin parámetros (requerido por Hibernate)
     */
    public Rol() {
    }

    /**
     * Constructor con parámetros
     */
    public Rol(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Constructor con id (para testing/seeding)
     */
    public Rol(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // ===== GETTERS =====

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // ===== SETTERS =====

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // ===== MÉTODOS AUXILIARES =====

    @Override
    public String toString() {
        return "Rol{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rol rol = (Rol) o;
        return id != null && id.equals(rol.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

