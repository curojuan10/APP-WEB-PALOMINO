# 🎓 Palomino Backend - Sistema de Venta de Cursos

Sistema Web de **Gestión y Venta de Cursos con Integración a Aula Virtual**

---

## 📋 Descripción General

Plataforma digital para:
- 📱 Captar clientes mediante landing page
- 🛒 Gestionar venta de cursos
- 💳 Validar pagos (validación manual)
- 👥 Administrar usuarios y cursos
- 🔐 Otorgar acceso al aula virtual

---

## 🏗️ Arquitectura

```
Frontend (Angular)
    ↓
Backend (Spring Boot REST API) ← TÚ ESTÁS AQUÍ
    ↓
Base de Datos (MySQL)
```

---

## 🧪 Estado del Desarrollo

### ✅ Completado

#### **Fase 1: Entidades Base**
- [x] Entidad `Rol`
  - ID autogenerado
  - Nombre único (ADMIN, CLIENTE)
  - Con Lombok (@Getter, @Setter, @NoArgsConstructor, @AllArgsConstructor)

- [x] Entidad `Usuario`
  - ID, nombre, email (único), password
  - Relación `@ManyToOne` → Rol
  - Fecha de registro automática (@PrePersist)
  - Con Lombok para código limpio

- [x] Relación `@ManyToOne` (Usuario → Rol)
  - Configuración: `fetch = FetchType.EAGER`
  - Foreign Key: `rol_id` en tabla `usuarios`
  - Nullable: `false` (todo usuario debe tener rol)

---

#### **Fase 2: Repositorios**
- [x] `RolRepository`
  - Método: `findByNombre(String nombre)`
  - Proporciona acceso a datos de roles

- [x] `UsuarioRepository`
  - Método: `findByEmail(String email)`
  - Método: `existsByEmail(String email)`
  - Proporciona acceso a datos de usuarios

---

#### **Fase 3: Prueba de Conexión a Base de Datos** ✅
- [x] Configuración de aplicación (`application.properties`)
  - BD: `bd_palomino`
  - Usuario MySQL: `root`
  - Contraseña: `root`
  - Puerto: `3306`
  - `ddl-auto=update` (crea/actualiza tablas automáticamente)
  - `show-sql=true` (muestra SQL en consola)

- [x] Script `data.sql`
  - Inserta roles iniciales (ADMIN, CLIENTE)
  - Se ejecuta automáticamente en startup

- [x] `CommandLineRunner` en `PalominoBackendApplication`
  - Verifica/crea rol CLIENTE automáticamente
  - Crea usuario de prueba si no existe
  - Email: `prueba@palomino.com`
  - Contraseña: `password123` (encriptada con BCrypt)
  - Muestra información en consola al iniciar
  - Manejo robusto de excepciones

- [x] `PasswordEncoderConfig`
  - Bean para encriptación BCrypt
  - Usada en prueba y será usada en servicios

---

### 🔄 En Progreso

#### **Fase 4: DTOs (Data Transfer Objects)**
- [ ] Requests (LoginRequest, RegisterRequest, etc)
- [ ] Responses (AuthResponse, UsuarioResponse, etc)

- [ ] **Fase 5: Servicios**
  - [ ] `AuthService` (registro, login)
  - [ ] `UsuarioService` (CRUD usuarios)

- [ ] **Fase 6: Controladores**
  - [ ] `AuthController`
  - [ ] `UsuarioController`

- [ ] **Fase 7: Seguridad**
  - [ ] Configuración JWT
  - [ ] Encriptación de contraseñas (BCrypt)
  - [ ] Filtro de autenticación

- [ ] **Fase 8: Cursos**
  - [ ] Entidad `Curso`
  - [ ] Servicio y Controlador

- [ ] **Fase 9: Compras**
  - [ ] Entidad `Compra`
  - [ ] Entidad `Pago`
  - [ ] Servicios y Controladores

- [ ] **Fase 10: Panel Admin**
  - [ ] Endpoints de validación de pagos
  - [ ] Reportes

---

## 📊 Modelo de Datos (Actual)

### Tablas Implementadas

#### 1. **roles**
```sql
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);
```

**Atributos Java:**
- `id: Long` (PK)
- `nombre: String` (UNIQUE, 20 chars)

---

#### 2. **usuarios**
```sql
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol_id INT NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);
```

**Atributos Java:**
- `id: Long` (PK)
- `nombre: String` (100 chars)
- `email: String` (UNIQUE, 100 chars)
- `password: String` (encriptado)
- `rol: Rol` (@ManyToOne → rol_id)
- `fechaRegistro: LocalDateTime` (auto)

---

### Tablas Pendientes (Próximas fases)

#### 3. **cursos**
```sql
CREATE TABLE cursos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    imagen_url VARCHAR(255),
    duracion VARCHAR(50),
    estado TINYINT(1) DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 4. **compras**
```sql
CREATE TABLE compras (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    curso_id INT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado_acceso ENUM('PENDIENTE','ACTIVO','BLOQUEADO') DEFAULT 'PENDIENTE',
    UNIQUE (usuario_id, curso_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (curso_id) REFERENCES cursos(id)
);
```

#### 5. **pagos**
```sql
CREATE TABLE pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    compra_id INT NOT NULL,
    comprobante_url VARCHAR(255) NOT NULL,
    monto DECIMAL(10,2),
    metodo_pago VARCHAR(50),
    estado ENUM('PENDIENTE','APROBADO','RECHAZADO') DEFAULT 'PENDIENTE',
    observaciones TEXT,
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (compra_id) REFERENCES compras(id)
);
```

---

## 🔗 Relaciones de Entidades

```
┌──────────────────┐
│     ROL          │
├──────────────────┤
│ id (PK)          │
│ nombre (UNIQUE)  │
└──────────────────┘
         ▲
         │ 1
         │
      MANY
         │
┌──────────────────────────┐
│     USUARIO              │
├──────────────────────────┤
│ id (PK)                  │
│ nombre                   │
│ email (UNIQUE)           │
│ password                 │
│ rol_id (FK) ─────────────┼──→ Rol.id
│ fechaRegistro            │
└──────────────────────────┘
```

**Tipo:** Many-To-One  
**Significado:** Muchos usuarios → 1 rol

---

## 🧭 Flujo del Sistema (Actual)

```
1. Usuario se registra (crea cuenta)
        ↓
2. Sistema asigna rol CLIENTE automáticamente
        ↓
3. Usuario inicia sesión con email y password
        ↓
4. Backend valida credenciales
        ↓
5. Sistema genera JWT token
        ↓
6. Usuario accede al sistema con token válido
```

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología | Versión |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.5.12 |
| **BD** | MySQL | 8.0+ |
| **ORM** | Spring Data JPA | Jakarta |
| **Seguridad** | Spring Security + JWT | JJWT 0.12.6 |
| **Validación** | Spring Validation | 3.5.12 |
| **Utilidades** | Lombok | Latest |
| **Java** | OpenJDK | 17 |
| **Build** | Maven | 3.8+ |

---

## 📁 Estructura del Proyecto

```
palomino-backend/
├── src/main/java/com/palomino/
│   ├── entity/                    ← Entidades JPA
│   │   ├── Rol.java              ✅ CREADO
│   │   ├── Usuario.java          ✅ CREADO
│   │   ├── Curso.java            ⏳ PENDIENTE
│   │   ├── Compra.java           ⏳ PENDIENTE
│   │   └── Pago.java             ⏳ PENDIENTE
│   │
│   ├── repository/               ← Interfaces JPA
│   │   ├── RolRepository.java          ✅ CREADO
│   │   ├── UsuarioRepository.java      ✅ CREADO
│   │   └── ...
│   │
│   ├── service/                  ← Lógica de negocio
│   │   ├── interface/
│   │   └── implementation/
│   │
│   ├── controller/               ← REST Endpoints
│   │   └── ...
│   │
│   ├── dto/                      ← Data Transfer Objects
│   │   ├── request/
│   │   └── response/
│   │
│   ├── security/                 ← JWT, Auth
│   │   ├── jwt/
│   │   └── userdetails/
│   │
│   ├── config/                   ← Configuración
│   │   ├── SecurityConfig.java
│   │   └── ...
│   │
│   ├── exception/                ← Manejo de excepciones
│   │   └── ...
│   │
│   └── PalominoBackendApplication.java
│
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-prod.yml
│   ├── schema.sql               ← DDL para crear tablas
│   └── data.sql                 ← INSERT iniciales
│
├── pom.xml                       ← Dependencias Maven
└── README.md                     ← Este archivo
```

---

## 🚀 Cómo Ejecutar

### Requisitos
- Java 17+
- MySQL 8.0+
- Maven 3.8+

### Pasos

1. **Clonar/Descargar proyecto**
   ```bash
   cd palomino-backend
   ```

2. **Configurar conexión a BD** en `application.yml`
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/palomino_db
       username: root
       password: tu_password
     jpa:
       hibernate:
         ddl-auto: create-drop  # create, update, validate, create-drop
   ```

3. **Compilar proyecto**
   ```bash
   ./mvnw clean compile
   ```

4. **Ejecutar tests**
   ```bash
   ./mvnw test
   ```

5. **Ejecutar aplicación**
   ```bash
   ./mvnw spring-boot:run
   ```

   La API estará disponible en: `http://localhost:8080`

---

## 🔐 Seguridad (Planificado)

- ✅ Estructura preparada
- ⏳ Contraseñas encriptadas con BCrypt
- ⏳ Autenticación con JWT
- ⏳ Roles (ADMIN, CLIENTE)
- ⏳ Protección de rutas

---

## 📚 Endpoints Planeados

### Autenticación (⏳ Pendiente)
```
POST   /api/auth/register    - Registro de usuario
POST   /api/auth/login       - Login
POST   /api/auth/refresh     - Renovar token JWT
```

### Usuarios (⏳ Pendiente)
```
GET    /api/usuarios/{id}    - Obtener usuario
PUT    /api/usuarios/{id}    - Actualizar usuario
DELETE /api/usuarios/{id}    - Eliminar usuario
```

### Cursos (⏳ Pendiente)
```
GET    /api/cursos           - Listar cursos
GET    /api/cursos/{id}      - Obtener curso
POST   /api/cursos           - Crear curso (ADMIN)
PUT    /api/cursos/{id}      - Editar curso (ADMIN)
DELETE /api/cursos/{id}      - Eliminar curso (ADMIN)
```

### Compras (⏳ Pendiente)
```
POST   /api/compras          - Crear compra
GET    /api/compras          - Listar mis compras
GET    /api/compras/{id}     - Obtener compra
```

### Pagos (⏳ Pendiente)
```
POST   /api/pagos            - Subir comprobante
GET    /api/pagos/{id}       - Ver estado pago
```

### Admin (⏳ Pendiente)
```
GET    /api/admin/pagos      - Ver pagos pendientes
PUT    /api/admin/pagos/{id} - Aprobar/Rechazar pago
GET    /api/admin/reportes   - Ver reportes
```

---

## 🧪 Testing (Pendiente)

### Pruebas Unitarias
```bash
./mvnw test
```

### Pruebas con Postman
- Colección: `docs/postman/palomino-api-collection.json`

---

## 📝 Notas Importantes

1. **Entidades creadas son base sólida** para el resto del sistema
2. **Relación ManyToOne correctamente configurada** con FetchType.EAGER
3. **Password se encriptará con BCrypt** antes de persistir
4. **JWT se generará en login** y se validará en cada request
5. **Roles controlarán acceso** a endpoints (ADMIN vs CLIENTE)

---

## 🔄 Próxima Tarea

- [ ] Crear DTOs (Data Transfer Objects)
  - [ ] LoginRequest
  - [ ] RegisterRequest
  - [ ] AuthResponse
  - [ ] UsuarioResponse

---

## 📞 Contacto / Soporte

**Estudiante:** [Tu nombre]  
**Empresa:** Palomino  
**Fecha de Inicio:** 2025

---

## 📜 Licencia

Este proyecto es parte de una experiencia formativa.

---

**Última actualización:** 2026-03-26  
**Estado:** 🟡 En desarrollo (Fase 1-3 completadas, Fase 4 en progreso)

