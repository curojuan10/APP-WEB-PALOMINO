# 🤖 AGENTS.md - Guía para Agentes IA

**Última actualización:** Fase 4 (Autenticación con JWT completada)  
**Stack:** Spring Boot 3.5.12 (Java 17) + JWT + MySQL 8  
**Propósito:** Plataforma de venta y gestión de cursos con aula virtual integrada

---

## 📦 Stack Técnico
- **Framework**: Spring Boot 3.5.12 (Java 17)
- **ORM**: Spring Data JPA + Hibernate
- **Base de Datos**: MySQL 8
- **Seguridad**: Spring Security + JWT (JJWT 0.12.6)
- **Validación**: Spring Validation
- **Utilidades**: Lombok (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor)
- **Herramienta Build**: Maven 3.x

---

## 🏗️ Arquitectura General

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (Angular)                        │
└────────────────────────┬────────────────────────────────────┘
                         │ REST API
┌────────────────────────▼────────────────────────────────────┐
│          BACKEND (Spring Boot 3.5.12 + JWT)                 │
│  ├─ /auth              → Autenticación y generación JWT     │
│  ├─ /api/usuario       → Gestión de perfil de usuario       │
│  ├─ /api/cursos        → PRÓXIMA: Gestión de cursos         │
│  ├─ /api/compras       → PRÓXIMA: Gestión de compras        │
│  └─ /api/admin         → PRÓXIMA: Panel administrativo      │
└────────────────────────┬────────────────────────────────────┘
                         │ JDBC + Hibernate
┌────────────────────────▼────────────────────────────────────┐
│        BASE DE DATOS (MySQL 8 - bd_palomino)               │
│  ├─ usuarios   → Cuentas de usuarios del sistema            │
│  ├─ roles      → Catálogo (ADMIN, CLIENTE, INSTRUCTOR)     │
│  ├─ cursos     → PRÓXIMA: Catálogo de cursos               │
│  ├─ compras    → PRÓXIMA: Pedidos de usuarios              │
│  └─ pagos      → PRÓXIMA: Registro de transacciones        │
└─────────────────────────────────────────────────────────────┘
```

---

## 📂 Estructura del Proyecto

```
com.palomino/
├── entity/               → Entidades JPA (Rol, Usuario, Curso, Compra, etc)
├── repository/           → Interfaces Spring Data JPA
├── security/             → JWT, Spring Security, UserDetailsService
├── controller/           → REST endpoints (@RestController)
├── service/              → Lógica de negocio (@Service)
├── dto/                  → Data Transfer Objects
│   ├── request/          → DTOs de entrada (LoginRequest, RegisterRequest, etc)
│   └── response/         → DTOs de salida (AuthResponse, UsuarioResponse, etc)
├── config/               → Configuraciones adicionales
├── exception/            → Excepciones personalizadas (EN DESARROLLO)
└── PalominoBackendApplication.java → Punto de entrada + CommandLineRunner
```

## 🔐 Sistema de Autenticación (Completado - Fase 4)

### Flujo de Autenticación

```
┌─────────────────────────────────────────────────────────────┐
│ 1. REGISTRO (POST /auth/register)                           │
│    { nombre, email, password } → validar → guardar usuario  │
│    → generar JWT → retornar token                           │
└────────────┬────────────────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────────────────┐
│ 2. LOGIN (POST /auth/login)                                 │
│    { email, password } → validar → autenticar con Spring    │
│    → generar JWT → retornar token                           │
└────────────┬────────────────────────────────────────────────┘
             │
┌────────────▼────────────────────────────────────────────────┐
│ 3. SOLICITUDES PROTEGIDAS (GET /api/usuario/profile)       │
│    Authorization: Bearer <token> → JwtFilter procesa        │
│    → valida token → carga usuario → SecurityContext         │
│    → controller accede a usuario autenticado                │
└─────────────────────────────────────────────────────────────┘
```

### Componentes Clave

**1. SecurityConfig** (`security/SecurityConfig.java`)
- Configura rutas públicas vs protegidas
- Define política de sesiones: **STATELESS** (sin HttpSession)
- Agrega JwtFilter antes del filtro de autenticación
- Rutas públicas: `/auth/login`, `/auth/register`, `/api/public/**`
- Rutas protegidas: `/api/usuario/**` (require autenticación)
- Rutas admin: `/api/admin/**` (require rol ADMIN)

**2. JwtProvider** (`security/JwtProvider.java`)
- Genera tokens JWT a partir del email del usuario
- Se usa en AuthServiceImpl tras registro/login exitoso

**3. JwtFilter** (`security/JwtFilter.java`)
- Filtra TODOS los requests HTTP
- Extrae token de header: `Authorization: Bearer <token>`
- Valida token (expiración, firma)
- Carga usuario desde BD usando CustomUserDetailsService
- Establece Authentication en SecurityContext

**4. JwtUtils** (`security/JwtUtils.java`)
- Valida firma y expiración de tokens
- Extrae claims (email, claims personalizados)

**5. CustomUserDetailsService** (`security/CustomUserDetailsService.java`)
- Implementa `UserDetailsService`
- Busca usuario por email (no ID) en BD
- Usado por AuthenticationManager y JwtFilter

**6. PasswordEncoderConfig** (`security/PasswordEncoderConfig.java`)
- Proporciona bean BCryptPasswordEncoder
- **SIEMPRE** usar para encriptar passwords


---

## 🔑 Entidades Base (Fase 1-2)

### Usuario (`entity/Usuario.java`)
```java
@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String email;          // Único, usado como username
    
    private String password;       // SIEMPRE encriptado con BCrypt
    
    @ManyToOne(fetch = FetchType.EAGER)  // IMPORTANTE: EAGER
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;
    
    private LocalDateTime fechaRegistro;  // Auto-generada con @PrePersist
}
```

### Rol (`entity/Rol.java`)
```java
@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String nombre;        // ADMIN, CLIENTE, INSTRUCTOR (futuro)
}
```

---

## 📬 DTOs y Servicios (Fase 3-4)

### DTOs de Entrada
- **LoginRequest**: email, password
- **RegisterRequest**: nombre, email, password

### DTOs de Salida
- **AuthResponse**: token, usuarioId, email, nombre, rol, mensaje, exitoso

### AuthService (Interface)
```java
public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
}
```

### AuthServiceImpl
- Registra nuevos usuarios (valida email único)
- Login (autentica con Spring Security)
- Encripta passwords con BCrypt
- Genera JWT tokens
- Usa `@Transactional` para atomicidad

---

## 📋 Convenciones y Patrones Específicos

### ✅ DO's (Hacer)
- Usar `@Transactional` en servicios que modifican datos
- Encriptar passwords con `BCryptPasswordEncoder.encode()`
- Usar email como identificador único (username)
- Usar `FetchType.EAGER` en relaciones @ManyToOne de Rol
- Validar DTOs con anotaciones `@NotBlank`, `@Email`, `@Size`
- Inyectar repositorios en servicios (no en controllers)
- Usar `ResponseEntity<T>` en controllers
- Capturar excepciones en controllers y retornar DTOs

### ❌ DON'Ts (No hacer)
- **NUNCA** guardar passwords en plain text
- **NUNCA** exponer entidades JPA directamente en APIs (usar DTOs)
- **NUNCA** usar `FetchType.LAZY` en Usuario.rol
- **NUNCA** inyectar `HttpSession` (es STATELESS)
- **NUNCA** pasar password en respuestas HTTP

---

## 🚀 Comandos Esenciales

### Build & Run
```bash
# Build del proyecto
mvnw clean package

# Ejecutar aplicación
mvnw spring-boot:run

# En Windows
mvnw.cmd clean package
mvnw.cmd spring-boot:run
```

### Limpiar Caché
```bash
# Limpiar compilados
mvnw clean

# Limpiar sin tests
mvnw clean package -DskipTests
```

### Verificación de BD
- Buscar en logs: "✅ CONEXIÓN A BD EXITOSA"
- Usuario de prueba: `prueba@palomino.com` / `password123`

**application.properties** debe tener:
- `spring.datasource.url=jdbc:mysql://localhost:3306/bd_palomino`
- `spring.datasource.username=root` / `password=root` (desarrollo local)
- `spring.jpa.hibernate.ddl-auto=update` (auto-schema)
- `app.jwt.secret` y `app.jwt.expiration-ms` (JWT)

## 📌 Archivos Clave a Conocer

1. **PalominoBackendApplication.java** - Punto de entrada + test inicial
2. **entity/Usuario.java** - Modelo principal con PrePersist hook
3. **entity/Rol.java** - Catálogo de roles
4. **security/SecurityConfig.java** - Configuración de Spring Security
5. **security/CustomUserDetailsService.java** - Carga de usuarios desde BD
6. **security/JwtProvider.java** - Generación de tokens JWT
7. **security/JwtUtils.java** - Validación y extracción de claims
8. **security/JwtFilter.java** - Filtro de procesamiento JWT
9. **security/PasswordEncoderConfig.java** - Configuración de BCrypt
10. **service/AuthService.java** - Interfaz de autenticación
11. **service/AuthServiceImpl.java** - Implementación de registro y login
12. **dto/request/LoginRequest.java** - DTO de entrada para login
13. **dto/request/RegisterRequest.java** - DTO de entrada para registro
14. **dto/response/AuthResponse.java** - DTO de respuesta con token
15. **repository/** - Interfaces de acceso a datos
16. **pom.xml** - Dependencias y configuración Maven

## ⚠️ Notas para Agentes

- **No usar JPA lazy loading**: Rol siempre es EAGER
- **Email es único**: Validado en AuthServiceImpl
- **Timestamps**: Usuario tiene fechaRegistro auto-generada
- **Contraseñas**: Encriptadas con BCrypt SIEMPRE, nunca en logs/respuestas
- **UserDetailsService**: Implementado con CustomUserDetailsService (email como username)
- **SecurityConfig**: Rutas públicas (auth/*), protegidas (/api/usuario/*), admin (/api/admin/*)
- **Sesiones**: STATELESS (sin HttpSession) - preparado para JWT
- **JWT Header**: Espera "Authorization: Bearer <token>"
- **AuthServiceImpl**: Tiene @Transactional, maneja BCrypt, AuthenticationManager y JwtProvider
- **AuthController**: Endpoints POST /auth/register y /auth/login con manejo de errores
- **Próximas tareas**: Global Exception Handler, CORS config, UsuarioController

