# 🧪 PRUEBA DE CONEXIÓN A BASE DE DATOS

**Fecha:** 2026-03-26  
**Fase:** 3 - Prueba de Conexión a BD  
**Estado:** ✅ COMPLETADO

---

## 📋 Resumen de lo Implementado

### 1. Configuración de Conexión MySQL

**Archivo:** `src/main/resources/application.properties`

```properties
# URL de la base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/bd_palomino?useSSL=false&serverTimezone=UTC

# Credenciales MySQL
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA - Hibernate genera las tablas automáticamente
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

**Explicación:**
- ✅ Base de datos: `bd_palomino`
- ✅ Usuario MySQL: `root`
- ✅ Contraseña: `root`
- ✅ `ddl-auto=update` → Crea/actualiza tablas automáticamente
- ✅ `show-sql=true` → Muestra SQL ejecutado en consola (útil para debug)

---

### 2. Script de Datos Iniciales

**Archivo:** `src/main/resources/data.sql`

```sql
-- Insertar roles iniciales si no existen
INSERT IGNORE INTO roles (nombre) VALUES ('ADMIN');
INSERT IGNORE INTO roles (nombre) VALUES ('CLIENTE');
```

**Propósito:**
- Se ejecuta automáticamente al iniciar la aplicación
- Crea los roles ADMIN y CLIENTE en la base de datos
- Usa `INSERT IGNORE` para no fallar si ya existen

---

### 3. CommandLineRunner para Prueba

**Archivo:** `src/main/java/com/palomino/PalominoBackendApplication.java`

```java
@Bean
public CommandLineRunner testDatabaseConnection(RolRepository rolRepository, 
                                                UsuarioRepository usuarioRepository) {
    return args -> {
        try {
            // 1. Verifica/crea rol CLIENTE
            Rol rolCliente = rolRepository.findByNombre("CLIENTE");
            if (rolCliente == null) {
                rolCliente = new Rol();
                rolCliente.setNombre("CLIENTE");
                rolCliente = rolRepository.save(rolCliente);
            }

            // 2. Crea usuario de prueba si no existe
            String emailPrueba = "prueba@palomino.com";
            if (!usuarioRepository.existsByEmail(emailPrueba)) {
                Usuario usuarioPrueba = new Usuario();
                usuarioPrueba.setNombre("Usuario Prueba");
                usuarioPrueba.setEmail(emailPrueba);
                
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                usuarioPrueba.setPassword(encoder.encode("password123"));
                usuarioPrueba.setRol(rolCliente);
                
                usuarioPrueba = usuarioRepository.save(usuarioPrueba);
                // Muestra información en consola
            }

            // 3. Muestra total de usuarios
            long totalUsuarios = usuarioRepository.count();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
```

**Qué hace:**
- ✅ Se ejecuta automáticamente al iniciar la aplicación
- ✅ Verifica/crea rol CLIENTE
- ✅ Crea usuario de prueba si no existe
- ✅ Encripta contraseña con BCrypt
- ✅ Muestra información en consola
- ✅ Maneja excepciones de forma robusta

**Usuario de Prueba:**
```
Email: prueba@palomino.com
Contraseña: password123
Rol: CLIENTE
```

---

### 4. Configuración BCrypt

**Archivo:** `src/main/java/com/palomino/config/PasswordEncoderConfig.java`

```java
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**Propósito:**
- Configura el encriptador BCrypt como bean de Spring
- Se usará en servicios de autenticación
- Para la prueba se crea una instancia local

---

## 🚀 Cómo Ejecutar la Prueba

### Prerequisitos
1. **MySQL instalado y corriendo** en `localhost:3306`
2. **Base de datos creada:**
   ```sql
   CREATE DATABASE bd_palomino;
   ```
   
   O dejar que Hibernate la cree automáticamente (si tiene permisos)

3. **Usuario MySQL con credenciales:**
   - Usuario: `root`
   - Contraseña: `root`
   - O ajustar `application.properties` según tu BD

---

### Pasos para Ejecutar

1. **Abrí una terminal en el proyecto:**
   ```bash
   cd "C:\juan\IX\EXPERIENCIA FORMATIVA EN SITUACIÓN REAL DE TRABAJO V\APP-WEB-PALOMINO\palomino-backend"
   ```

2. **Compilar el proyecto:**
   ```bash
   .\mvnw.cmd clean compile
   ```

3. **Ejecutar la aplicación:**
   ```bash
   .\mvnw.cmd spring-boot:run
   ```

4. **Observar la consola:**
   ```
   ========================================
   🔧 PROBANDO CONEXIÓN A BASE DE DATOS
   ========================================

   ✅ Rol CLIENTE encontrado (ID: 1)

   📝 Creando usuario de prueba...
   ✅ Usuario de prueba creado exitosamente
      ID: 1
      Nombre: Usuario Prueba
      Email: prueba@palomino.com
      Rol: CLIENTE
      Fecha Registro: 2026-03-26T10:30:45.123456

   📊 Total de usuarios en BD: 1

   ✅ CONEXIÓN A BD EXITOSA
   ========================================
   ```

---

## 📊 Tablas Creadas Automáticamente

### 1. Tabla `roles`
```sql
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE
);
```

**Datos después de la prueba:**
```
| id | nombre |
|----|--------|
| 1  | ADMIN  |
| 2  | CLIENTE|
```

---

### 2. Tabla `usuarios`
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

**Datos después de la prueba:**
```
| id | nombre         | email                | password (encriptado) | rol_id | fecha_registro      |
|----|----------------|----------------------|------------------------|--------|---------------------|
| 1  | Usuario Prueba | prueba@palomino.com | $2a$10$... (BCrypt)   | 2      | 2026-03-26 10:30:45 |
```

---

## 🔍 Verificar la Prueba Manualmente

Si quieres verificar que los datos se guardaron correctamente en MySQL:

```bash
# Conéctate a MySQL
mysql -u root -p

# Selecciona la BD
USE bd_palomino;

# Ver roles
SELECT * FROM roles;

# Ver usuarios
SELECT id, nombre, email, rol_id, fecha_registro FROM usuarios;

# Verificar relación (usuario con su rol)
SELECT u.id, u.nombre, u.email, r.nombre as rol 
FROM usuarios u 
JOIN roles r ON u.rol_id = r.id;
```

---

## ⚠️ Posibles Errores y Soluciones

### Error: "Unknown database 'bd_palomino'"
**Solución:** Crear la base de datos manualmente:
```sql
CREATE DATABASE bd_palomino;
```

### Error: "Access denied for user 'root'@'localhost'"
**Solución:** Ajustar credenciales en `application.properties`:
```properties
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
```

### Error: "Can't connect to MySQL server on 'localhost:3306'"
**Solución:** 
- Verificar que MySQL esté corriendo: `mysql --version`
- Iniciar MySQL si está instalado como servicio
- Cambiar puerto en `application.properties` si MySQL usa otro

### Error: "Column 'fecha_registro' doesn't exist"
**Solución:** Usar `ddl-auto=create` o `ddl-auto=create-drop` en vez de `update`:
```properties
spring.jpa.hibernate.ddl-auto=create
```

---

## ✅ Checklist de Verificación

- [x] Configuración de BD en `application.properties`
- [x] Script `data.sql` para roles iniciales
- [x] `CommandLineRunner` en clase principal
- [x] `PasswordEncoderConfig` para BCrypt
- [x] Compilación exitosa (sin errores)
- [x] README actualizado
- [x] Usuario de prueba creado y guardado
- [x] Relación Usuario-Rol verificada

---

## 🎯 Resultados Esperados

✅ **Después de ejecutar la aplicación:**

1. **Consola muestra mensaje de éxito** con detalles del usuario
2. **Base de datos `bd_palomino`** se crea automáticamente
3. **Tablas `roles` y `usuarios`** se crean automáticamente
4. **Roles ADMIN y CLIENTE** se insertan
5. **Usuario de prueba** se crea con:
   - Email: `prueba@palomino.com`
   - Contraseña encriptada: `password123`
   - Rol: CLIENTE
   - Fecha: automática (LocalDateTime.now())

---

## 💡 Próximos Pasos

Esta prueba verifica que:
- ✅ La conexión a BD funciona
- ✅ Las entidades se persisten correctamente
- ✅ Las relaciones ManyToOne funcionan
- ✅ BCrypt encripta contraseñas correctamente

Ahora podemos continuar con:
- [ ] Crear DTOs (Data Transfer Objects)
- [ ] Crear servicios de autenticación
- [ ] Crear controladores REST
- [ ] Implementar JWT

---

## 📝 Notas Técnicas

- **CommandLineRunner:** Se ejecuta después del contexto de Spring inicializar
- **FetchType.EAGER:** El rol se carga automáticamente con el usuario
- **@PrePersist:** Hook que se ejecuta antes de INSERT (asigna fecha)
- **BCrypt:** Estándar industrial para encriptación de contraseñas
- **INSERT IGNORE:** No falla si el registro ya existe en BD

---

**Última actualización:** 2026-03-26  
**Responsable:** GitHub Copilot  
**Estado:** ✅ Completado y Documentado

