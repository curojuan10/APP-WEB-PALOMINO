# 🧪 GUÍA PRÁCTICA: Testing en Postman

## 📸 Paso a Paso Visual

---

## PASO 1: Iniciar el Backend

### Terminal:
```bash
cd C:\juan\IX\EXPERIENCIA FORMATIVA EN SITUACIÓN REAL DE TRABAJO V\APP-WEB-PALOMINO\palomino-backend

mvnw.cmd spring-boot:run
```

### Logs Esperados:
```
[main] INFO org.springframework.boot.StartupInfoLogger - Started PalominoBackendApplication in X.XXX seconds
[main] INFO org.springframework.boot.web.embedded.tomcat.TomcatWebServer - Tomcat started on port(s): 8080
```

✅ **APLICACIÓN LISTA EN:** http://localhost:8080

---

## PASO 2: Importar Collection en Postman

### 2.1 Abrir Postman
- Click en icono de Postman o busca en "Postman" en inicio

### 2.2 Click en "Import"
- Ubicación: Esquina superior izquierda
- Busca el icono de carpeta o escrito "Import"

### 2.3 Seleccionar Archivo
- Navega hasta: `palomino-backend/Palomino_API_Postman_Collection.json`
- Click en archivo
- Click "Open"

### 2.4 Confirmar Import
- Click en "Import"
- Verifica que aparezca "Palomino API - Authentication" en colecciones

---

## PASO 3: Ejecutar Caso 1 - Registro Exitoso

### 3.1 Seleccionar Request
```
Collections 
  └─ Palomino API - Authentication
     └─ Register User - Success
```

### 3.2 Verificar Headers
```
Content-Type: application/json
```
✅ Ya está configurado en la collection

### 3.3 Verificar Body
```json
{
  "nombre": "Juan Pérez García",
  "email": "juan.perez@example.com",
  "password": "MiPassword123!"
}
```
✅ Ya está listo

### 3.4 Click en "Send"
- Botón azul lado derecho: **"Send"**
- Espera respuesta

### 3.5 Verificar Respuesta
```
Status: 201 Created
Body:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "nombre": "Juan Pérez García",
  "email": "juan.perez@example.com",
  "rol": "CLIENTE",
  "mensaje": "Usuario registrado exitosamente"
}
```

✅ **REGISTRO EXITOSO**

**Guarda el token para paso siguiente**

---

## PASO 4: Ejecutar Caso 2 - Email Duplicado

### 4.1 Seleccionar Request
```
Collections 
  └─ Palomino API - Authentication
     └─ Register User - Duplicate Email
```

### 4.2 Click en "Send"

### 4.3 Verificar Respuesta
```
Status: 400 Bad Request
Body:
{
  "mensaje": "El email ya está registrado: juan.perez@example.com"
}
```

✅ **VALIDACIÓN CORRECTA - Email no permite duplicados**

---

## PASO 5: Ejecutar Caso 5 - Login Exitoso

### 5.1 Seleccionar Request
```
Collections 
  └─ Palomino API - Authentication
     └─ Login User - Success
```

### 5.2 Verificar Body
```json
{
  "email": "juan.perez@example.com",
  "password": "MiPassword123!"
}
```
✅ Mismo email y password del registro

### 5.3 Click en "Send"

### 5.4 Verificar Respuesta
```
Status: 200 OK
Body:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "nombre": "Juan Pérez García",
  "email": "juan.perez@example.com",
  "rol": "CLIENTE",
  "mensaje": "Login exitoso"
}
```

✅ **LOGIN EXITOSO**

**Nota:** El token será diferente al de registro (ambos son válidos)

---

## PASO 6: Probar Casos de Error

### Caso 3: Email Inválido
```
Request: Register User - Invalid Email
Email: "email-invalido" ← Sin @
Status: 400 Bad Request ✅
```

### Caso 4: Password Corto
```
Request: Register User - Password Too Short
Password: "123" ← Menos de 6 caracteres
Status: 400 Bad Request ✅
```

### Caso 6: Credenciales Inválidas
```
Request: Login User - Invalid Credentials
Email: juan.perez@example.com
Password: "PasswordIncorrecto123!"
Status: 400 Bad Request
Mensaje: "Email o contraseña inválidos" ✅
```

### Caso 7: Usuario No Existe
```
Request: Login User - Non-existent User
Email: "noexiste@example.com"
Password: "AnyPassword123!"
Status: 400 Bad Request
Mensaje: "Email o contraseña inválidos" ✅
```

---

## 💾 Guardar Token para Requests Futuros

### En Postman Environment Variables:

1. Click en **"Environments"** (izquierda)
2. Click en **"Create New Environment"**
3. Nombre: **"Palomino Dev"**
4. Añadir Variable:
   - **Name:** `token`
   - **Initial value:** (dejar vacío)
   - **Current value:** (dejar vacío)

### Al recibir token en response:

1. Copia el valor de `token` de la respuesta
2. En Environments → Palomino Dev
3. Pega en "Current value" de la variable `token`
4. Click "Save"

### Usar en Headers de Requests Futuros:

```
Authorization: Bearer {{token}}
```

Postman automáticamente reemplazará `{{token}}` con el valor guardado.

---

## ✅ Checklist de Testing Completo

### Antes de Empezar:
- [ ] Backend corriendo en terminal
- [ ] Postman abierto
- [ ] Collection importada
- [ ] MySQL corriendo

### Ejecución de Casos:
- [ ] Caso 1: Register Success → 201 ✅
- [ ] Caso 2: Duplicate Email → 400 ✅
- [ ] Caso 3: Invalid Email → 400 ✅
- [ ] Caso 4: Short Password → 400 ✅
- [ ] Caso 5: Login Success → 200 ✅
- [ ] Caso 6: Invalid Credentials → 400 ✅
- [ ] Caso 7: Non-existent User → 400 ✅

### Validaciones:
- [ ] Token es JWT (empieza con "eyJ")
- [ ] Token contiene email en payload
- [ ] Usuario guardado en BD
- [ ] Password encriptado en BD (no igual al original)
- [ ] Rol es "CLIENTE"

---

## 🔍 Ver Detalles del JWT

### En Postman Response:

1. Copia el valor completo de `token`
2. Abre: https://jwt.io/
3. Pega en "Encoded" (izquierda)
4. Verifica en "Decoded" (derecha):

```json
HEADER:
{
  "alg": "HS256",
  "typ": "JWT"
}

PAYLOAD:
{
  "sub": "juan.perez@example.com",
  "iat": 1711838400,
  "exp": 1711924800,
  "authorities": [...]
}
```

✅ Verifica que:
- Algoritmo es HS256
- Subject es el email
- Expiration es futuro (24 horas por defecto)

---

## 🆘 Troubleshooting

### Error: "Connection refused"
```
❌ Backend no está corriendo
✅ Solución: Ejecuta mvnw.cmd spring-boot:run
```

### Error: "Email ya está registrado"
```
❌ Intentas registrar mismo email dos veces
✅ Solución: Usa otro email o limpia BD
```

### Error: "Email o contraseña inválidos"
```
❌ Credenciales incorrectas
✅ Solución: Verifica que coincidir exactamente
```

### Error: "La aplicación no responde"
```
❌ Postman espera respuesta
✅ Solución: Espera más, o verifica backend en terminal
```

### Token no funciona en requests futuros
```
❌ Token incorrecto o formato inválido
✅ Solución: Verifica Authorization: Bearer [token]
```

---

## 📊 Resumen de Respuestas

### Registro Exitoso (201):
```json
{
  "token": "JWT válido",
  "userId": 1,
  "nombre": "...",
  "email": "...",
  "rol": "CLIENTE",
  "mensaje": "Usuario registrado exitosamente"
}
```

### Login Exitoso (200):
```json
{
  "token": "JWT válido",
  "userId": 1,
  "nombre": "...",
  "email": "...",
  "rol": "CLIENTE",
  "mensaje": "Login exitoso"
}
```

### Error Validación (400):
```json
{
  "mensaje": "Descripción del error"
}
```

### Error Servidor (500):
```json
{
  "mensaje": "Error en el [registro/login]: ..."
}
```

---

## 🎯 Próximas Pruebas

Después de validar auth, puedes probar:
```
[ ] GET /api/usuario/profile (requiere token)
[ ] POST /api/admin/usuarios (solo ADMIN)
[ ] GET /api/usuario/list (solo ADMIN)
```

Estas requieren:
- Crear AuthController para usuario
- Crear UsuarioController
- Proteger con JWT + roles

---

## 📞 Resumen Rápido

```bash
# Terminal - Iniciar backend
mvnw.cmd spring-boot:run

# Postman - Importar collection
Archivo → Palomino_API_Postman_Collection.json

# Pruebas
1. Register User - Success
2. Register User - Duplicate Email
3. Login User - Success
4. Probar otros casos

# Verificar
Status codes correctos ✅
JWT válido ✅
Usuario en BD ✅
Password encriptado ✅
```

**¡Todo listo para empezar!** 🚀

