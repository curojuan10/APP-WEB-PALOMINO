# 🤖 AGENTS.md - Guía para Agentes IA

## 📦 Stack Técnico
- **Framework**: Spring Boot 3.5.12 (Java 17)
- **ORM**: Spring Data JPA + Hibernate
- **Base de Datos**: MySQL 8
- **Seguridad**: Spring Security + JWT (JJWT 0.12.6)
- **Validación**: Spring Validation
- **Utilidades**: Lombok (anotaciones @Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor)

## 🏗️ Arquitectura del Proyecto

```
com.palomino/
├── entity/           → Entidades JPA (Rol, Usuario)
├── repository/       → Interfaces Spring Data JPA
├── security/         → Seguridad (SecurityConfig, UserDetailsService, PasswordEncoder)
├── controller/       → (Vacío) - Será para REST endpoints
├── dto/             → Data Transfer Objects (request/response)
├── config/           → (Vacío) - Reservado para otras configuraciones
└── PalominoBackendApplication.java → Punto de entrada + CommandLineRunner
```

## 🔑 Conceptos Clave del Dominio

### Entidades Base
- **Rol** (`entity/Rol.java`): Catálogo de roles (ADMIN, CLIENTE)
  - ID autogenerado, nombre único
  - Relación 1:N con Usuario
  
- **Usuario** (`entity/Usuario.java`): Usuarios del sistema
  - Email único, password (encriptado con BCrypt)
  - Relación N:1 con Rol (fetch=EAGER)
  - Timestamp automático de registro (PrePersist)

### Patrón de Seguridad
1. **SecurityConfig**: Configuración de Spring Security con rutas públicas/protegidas + JwtFilter
2. **CustomUserDetailsService**: Implementa UserDetailsService, carga usuario desde BD
3. **PasswordEncoderConfig**: Bean singleton que proporciona BCryptPasswordEncoder
4. **JwtProvider**: Genera tokens JWT a partir de Authentication o UserDetails
5. **JwtUtils**: Utilidades para validar, extraer claims y verificar expiración de tokens
6. **JwtFilter**: Filtro OncePerRequestFilter que procesa tokens JWT en cada request
7. **data.sql**: Inserta roles iniciales (ADMIN, CLIENTE) con `INSERT IGNORE`
8. **CommandLineRunner** en main app: Verifica conexión BD y crea usuario de prueba en cada inicio

## 📋 Convenciones y Patrones Específicos

### Relaciones JPA
- Usar `fetch = FetchType.EAGER` para Rol en Usuario (evita lazy loading issues)
- `@JoinColumn(name = "rol_id", nullable = false)` - Integridad referencial obligatoria

### Encriptación
- **SIEMPRE** usar `passwordEncoder.encode()` antes de guardar passwords
- BCryptPasswordEncoder incluye salt automático - nunca guardar plain text

### Inicialización de Datos
- `data.sql` se ejecuta automáticamente (solo SQL, no Java)
- `CommandLineRunner` en PalominoBackendApplication se ejecuta después de iniciar contexto Spring
- Usar `INSERT IGNORE` para evitar duplicados en cada reinicio

### DTOs (En Desarrollo)
- Crear en `dto/request/` para entrada (LoginRequest, RegisterRequest)
- Crear en `dto/response/` para salida (UserResponse, TokenResponse)
- Nunca exponer entidades directamente en APIs

## 🔄 Flujos Críticos

### Flow de Prueba de BD (Startup)
```
App startup → CommandLineRunner.run()
→ Verifica rol CLIENTE existe (si no, lo crea)
→ Busca usuario prueba@palomino.com
→ Si no existe, crea con password encriptado
→ Output en logs confirma éxito/fallo
```

### Flow Esperado de Autenticación (Completo)
```
1. POST /auth/login (email + password)
   → LoginRequest validado en Controller
   → AuthenticationManager autentica con CustomUserDetailsService
   → JwtProvider genera token JWT
   → AuthResponse retorna token + info usuario

2. GET /api/usuario/profile (Authorization: Bearer <token>)
   → JwtFilter extrae token del header
   → JwtUtils valida token (no expirado, username coincide)
   → Cargar usuario desde BD con CustomUserDetailsService
   → SecurityContext establece autenticación
   → Controller accede a usuario autenticado
```

## 🚀 Comandos Esenciales

### Build & Run
```bash
# Build del proyecto (Maven)
mvnw clean package

# Ejecutar aplicación
mvnw spring-boot:run

# En Windows usar mvnw.cmd
mvnw.cmd spring-boot:run
```

### Test de BD
- Ver logs al iniciar: buscar "PROBANDO CONEXIÓN A BASE DE DATOS"
- La aplicación automáticamente:
  1. Verifica rol CLIENTE
  2. Intenta crear usuario prueba@palomino.com
  3. Imprime resultado en consola

## ⚙️ Configuración Base

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

