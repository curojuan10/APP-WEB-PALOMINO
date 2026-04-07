package com.palomino.repository;

import com.palomino.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad Rol
 * Proporciona métodos CRUD automáticos para gestionar roles
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
    /**
     * Busca un rol por su nombre
     * @param nombre el nombre del rol (ej: "ADMIN", "CLIENTE")
     * @return el rol encontrado, null si no existe
     */
    Rol findByNombre(String nombre);
}

