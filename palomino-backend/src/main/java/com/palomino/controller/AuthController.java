package com.palomino.controller;

import com.palomino.dto.request.LoginRequest;
import com.palomino.dto.request.RegisterRequest;
import com.palomino.dto.response.AuthResponse;
import com.palomino.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para autenticación
 * Expone endpoints para registro y login de usuarios
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Endpoint para registrar un nuevo usuario
     * 
     * @param registerRequest datos del nuevo usuario (nombre, email, password)
     * @return ResponseEntity con AuthResponse (token + datos usuario) o error
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            AuthResponse response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMensaje("Error en el registro: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint para autenticar un usuario existente
     * 
     * @param loginRequest credenciales del usuario (email, password)
     * @return ResponseEntity con AuthResponse (token + datos usuario) o error
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMensaje(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setMensaje("Error en el login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

