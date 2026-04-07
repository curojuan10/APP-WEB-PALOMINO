package com.palomino.repository;

import com.palomino.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 * Proporciona métodos CRUD automáticos y consultas personalizadas
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    /**
     * Busca un usuario por su email
     * @param email el email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);
    
    /**
     * Verifica si existe un usuario con un email específico
     * @param email el email a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);
}

