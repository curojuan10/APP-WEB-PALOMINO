package com.palomino.service;

import com.palomino.dto.request.LoginRequest;
import com.palomino.dto.request.RegisterRequest;
import com.palomino.dto.response.AuthResponse;
import com.palomino.entity.Rol;
import com.palomino.entity.Usuario;
import com.palomino.repository.RolRepository;
import com.palomino.repository.UsuarioRepository;
import com.palomino.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de autenticación
 * Maneja registro y login de usuarios con generación de JWT
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                         RolRepository rolRepository,
                         BCryptPasswordEncoder passwordEncoder,
                         AuthenticationManager authenticationManager,
                         JwtProvider jwtProvider) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    /**
     * Registra un nuevo usuario en el sistema
     * Validaciones:
     * - Email debe ser único
     * - Password se encripta con BCrypt
     * - Se asigna rol CLIENTE por defecto
     * 
     * @param registerRequest datos del nuevo usuario
     * @return AuthResponse con token JWT y datos del usuario
     * @throws IllegalArgumentException si el email ya existe
     */
    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        // 1. Validar que el email no existe
        if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado: " + registerRequest.getEmail());
        }

        // 2. Obtener el rol CLIENTE (por defecto para nuevos usuarios)
        Rol rolCliente = rolRepository.findByNombre("CLIENTE");
        if (rolCliente == null) {
            throw new IllegalArgumentException("El rol CLIENTE no existe en la base de datos");
        }

        // 3. Crear la entidad Usuario con password encriptado
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registerRequest.getNombre());
        nuevoUsuario.setEmail(registerRequest.getEmail());
        // IMPORTANTE: Encriptar password ANTES de guardar
        nuevoUsuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        nuevoUsuario.setRol(rolCliente);

        // 4. Guardar el usuario en la base de datos
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 5. Generar token JWT para el nuevo usuario
        String token = jwtProvider.generateToken(usuarioGuardado.getEmail());

        // 6. Retornar respuesta con token y datos del usuario
        return construirAuthResponse(usuarioGuardado, token, "Usuario registrado exitosamente");
    }

    /**
     * Autentica un usuario y genera un token JWT
     * Validaciones:
     * - Email debe estar registrado
     * - Password debe ser correcto (validado con BCrypt)
     * 
     * @param loginRequest credenciales del usuario
     * @return AuthResponse con token JWT y datos del usuario
     * @throws IllegalArgumentException si las credenciales son inválidas
     */
    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            // 1. Intentar autenticar con AuthenticationManager
            // Esto carga el usuario con CustomUserDetailsService y valida la contraseña
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // 2. Si la autenticación fue exitosa, obtener el usuario de la BD
            Usuario usuarioAutenticado = usuarioRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // 3. Generar token JWT con la autenticación
            String token = jwtProvider.generateTokenFromAuthentication(authentication);

            // 4. Retornar respuesta con token y datos del usuario
            return construirAuthResponse(usuarioAutenticado, token, "Login exitoso");

        } catch (Exception e) {
            // Si la autenticación falla, lanzar excepción
            throw new IllegalArgumentException("Email o contraseña inválidos");
        }
    }

    /**
     * Método auxiliar para construir la respuesta de autenticación
     * 
     * @param usuario el usuario autenticado/registrado
     * @param token el JWT generado
     * @param mensaje mensaje de confirmación
     * @return AuthResponse completo
     */
    private AuthResponse construirAuthResponse(Usuario usuario, String token, String mensaje) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setUserId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol().getNombre());
        response.setMensaje(mensaje);
        return response;
    }
}

