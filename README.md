# Veterinaria San Marcos · Backend (Parcial 3)

**Curso:** DSY1104 · Desarrollo Fullstack II · Duoc UC · 2026
**Caso:** Veterinaria San Marcos (Forma A)
**Frontend:** https://github.com/eliasgonzalez22/veterinaria-sanmarcos-p2

| Integrante | Correo | GitHub |
|---|---|---|
| Elias Gonzalez Abraham Hernandez | eliaa.gonzalez@duocuc.cl | eliasgonzalez22 |
| Javier Ignacio Alegría Vargas | jav.alegria@duocuc.cl | jav-rgb |
| Sebastian Manuel Leon Rodriguez | sebas.leonr@duocuc.cl | sebastian5180 |

---

## Estado del trabajo (actualizado el martes 17 de noviembre)

**Leyenda:** **Listo** = probado en Swagger y el dato queda guardado en MySQL · **En avance** = ruta y lógica creadas con commits, falta terminar o probar · **Pendiente** = aún no se empieza.

| Operación | Endpoint | Diseñado | Implementado | Probado | Estado |
|---|---|:-:|:-:|:-:|---|
| Tabla de usuarios (MySQL, persistente, clave con hash BCrypt) | `usuarios`, `roles` | ✅ | ✅ | ✅ | **Listo** |
| Inicio de sesión con JWT (error genérico si falla) | `POST /api/v1/auth/login` | ✅ | ✅ | ✅ | **Listo** |
| Registro (rol `DUENO` asignado por el servidor) | `POST /api/v1/auth/registro` | ✅ | ✅ | ✅ | **Listo** |
| Alta de usuario con rol (Administrador) | `POST /api/v1/usuarios` | ✅ | ✅ | ✅ | **Listo** |
| Listar usuarios (Administrador) | `GET /api/v1/usuarios` | ✅ | ✅ | ✅ | **Listo** |
| Ver usuario (el mismo o Administrador) | `GET /api/v1/usuarios/{id}`, `GET /api/v1/usuarios/yo` | ✅ | ✅ | ✅ | **Listo** |
| Modificar usuario (rol/estado solo Administrador) | `PUT /api/v1/usuarios/{id}` | ✅ | ✅ | ✅ | **Listo** |
| Eliminar = desactivar usuario (Administrador) | `DELETE /api/v1/usuarios/{id}` | ✅ | ✅ | ✅ | **Listo** |
| Rutas protegidas 401 / 403 | Filtro JWT + `SecurityConfig` | ✅ | ✅ | ✅ | **Listo** |
| Login y registro del frontend consumiendo la API (carga, éxito y error) | Repo frontend · [PR #3](https://github.com/eliasgonzalez22/veterinaria-sanmarcos-p2/pull/3) | ✅ | ✅ | ✅ | **Listo** |
| Sesión del frontend persistente al recargar y limpia al cerrar o vencer | Repo frontend (`sessionStorage`) | ✅ | ✅ | ✅ | **Listo** |
| Rutas y menús del frontend restringidos por rol | Repo frontend (`RutaProtegida`) | ✅ | ✅ | ✅ | **Listo** |
| Token con `iss` y `aud`, ruta `/auth/me` y límite de intentos de login (Guía 19) | `JwtService`, `AuthController` | ✅ | ⬜ | ⬜ | Pendiente |
| Microservicio de catálogo de servicios | `/api/v1/servicios` | ⬜ | ⬜ | ⬜ | Pendiente (semana 16) |
| Microservicio de citas (regla 409: horario ocupado) | `/api/v1/citas` | ⬜ | ⬜ | ⬜ | Pendiente (semana 16) |

### Pruebas realizadas (16-11-2026, backend en EC2 + MySQL en Docker)

| # | Prueba | Esperado | Obtenido |
|---|---|:-:|:-:|
| 1 | Registro de un usuario nuevo (queda con rol `DUENO`) | 201 | 201 ✅ |
| 2 | Registro con correo repetido | 409 | 409 ✅ |
| 3 | Registro con nombre, correo y clave inválidos (errores por campo) | 400 | 400 ✅ |
| 4 | Login correcto (token con vigencia de 3600 s) | 200 | 200 ✅ |
| 5 | Login con clave incorrecta / correo inexistente (mismo mensaje genérico) | 401 | 401 ✅ |
| 6 | Listar usuarios como Administrador | 200 | 200 ✅ |
| 7 | Listar usuarios como Dueño | 403 | 403 ✅ |
| 8 | Listar usuarios sin token | 401 | 401 ✅ |
| 9 | Token alterado | 401 | 401 ✅ |
| 10 | Dueño consulta la cuenta de otro usuario cambiando el id en la URL | 403 | 403 ✅ |
| 11 | Dueño modifica su propio nombre | 200 | 200 ✅ |
| 12 | Dueño intenta cambiarse el rol a Administrador | 403 | 403 ✅ |
| 13 | Administrador cambia el rol de un usuario | 200 | 200 ✅ |
| 14 | Dueño intenta eliminar un usuario | 403 | 403 ✅ |
| 15 | Administrador desactiva un usuario | 204 | 204 ✅ |
| 16 | Administrador intenta desactivar su propia cuenta | 409 | 409 ✅ |
| 17 | Login de un usuario desactivado | 401 | 401 ✅ |
| 18 | Consultar un usuario inexistente | 404 | 404 ✅ |

### Pruebas del alta de usuarios por el Administrador (17-11-2026)

| # | Prueba | Esperado | Obtenido |
|---|---|:-:|:-:|
| 19 | Administrador crea un usuario con rol `RECEPCIONISTA` | 201 | 201 ✅ |
| 20 | Administrador crea un usuario con correo repetido | 409 | 409 ✅ |
| 21 | Administrador crea un usuario con datos inválidos (errores por campo, incluido el rol vacío) | 400 | 400 ✅ |
| 22 | Administrador crea un usuario con un rol inexistente (`SUPERUSUARIO`) | 404 | 404 ✅ |
| 23 | Recepcionista intenta crear un usuario | 403 | 403 ✅ |
| 24 | Crear usuario sin token | 401 | 401 ✅ |
| 25 | El usuario creado inicia sesión y recibe su rol | 200 | 200 ✅ |
| 26 | Administrador desactiva el usuario de prueba | 204 | 204 ✅ |

### Pruebas de integración con el frontend React (17-11-2026, en el navegador)

| # | Prueba | Esperado | Obtenido |
|---|---|---|---|
| 27 | Login del dueño desde el formulario de React | `POST /auth/login` 200 y redirige a Mis citas | ✅ |
| 28 | Recargar la página con la sesión iniciada | La sesión se mantiene | ✅ |
| 29 | Login con clave incorrecta | "Correo o contraseña incorrectos" en la página, sin `alert()` | ✅ |
| 30 | Registro con un correo existente | "Ya existe una cuenta con ese correo" (409) | ✅ |
| 31 | Entrar a `/admin/usuarios` sin sesión, luego ingresar como Administrador | Va al login y vuelve a `/admin/usuarios` | ✅ |
| 32 | Lista de usuarios como Administrador | `GET /usuarios` con `Authorization: Bearer`, estado "Cargando..." y luego la tabla desde MySQL | ✅ |
| 33 | Cerrar sesión | Se borra el token de `sessionStorage` | ✅ |
| 34 | Recepcionista entra a `/admin/usuarios` | "No tienes permiso para ver esta página" | ✅ |
| 35 | Dueño entra a `/admin/usuarios` | "No tienes permiso para ver esta página" | ✅ |
| 36 | Backend sin respuesta | "No fue posible contactar la API..." | ✅ |

Pruebas unitarias del frontend: `npm run test:run`, 54 pruebas en verde (13 archivos), con mocks de Axios.

Usuarios de prueba que quedaron en la base de datos (desactivados): `prueba.avance@sanmarcos.cl` y `alta.admin@sanmarcos.cl`.

---

## Decisiones técnicas

| Tema | Decisión | Por qué |
|---|---|---|
| Framework | Spring Boot 3.3 (Java 17) | Lo pide el documento del caso (Forma A) y la pauta de la Coordinación. |
| Arquitectura | Un microservicio por responsabilidad: `ms-usuarios` (este), luego `ms-catalogo` y `ms-citas` | El caso exige microservicios independientes, cada uno con su propia BD. |
| Base de datos | MySQL 8, BD `sanmarcos_usuarios` | Relacional en servidor, preferida en el caso. `ddl-auto=update` conserva los datos al reiniciar. |
| Modelo | Tablas `roles` y `usuarios` (FK `rol_id`) | 3FN: el nombre del rol no se repite en cada usuario. |
| Contraseñas | BCrypt (`BCryptPasswordEncoder`) | Hash con sal aleatoria; la clave nunca se guarda ni se devuelve. |
| Autenticación | JWT HS256 (jjwt 0.12), vigencia 60 min | API sin estado: cada petición trae `Authorization: Bearer <token>`. |
| Roles | `ADMINISTRADOR`, `RECEPCIONISTA`, `DUENO` | Roles mínimos del caso. El registro público siempre crea `DUENO`. |
| Eliminar | Borrado lógico (`activo = false`) | No se pierde el historial de citas del usuario. Un usuario inactivo no puede iniciar sesión. |
| Errores | JSON único (`estado`, `error`, `mensaje`, `ruta`, `fecha`, `errores`) | 400 dato inválido, 401 sin token / vencido, 403 rol insuficiente, 404 no existe, 409 conflicto. |
| Documentación | springdoc-openapi (Swagger UI) | Se documenta y prueba la API con el botón **Authorize**. |
| Secretos | Variables de entorno (`DB_PASSWORD`, `JWT_SECRET`) | No hay claves ni archivos `.env` en el repositorio. |

---

## Modelo entidad-relación

```mermaid
erDiagram
    ROLES ||--o{ USUARIOS : "tiene"
    ROLES {
        BIGINT id PK
        VARCHAR nombre UK "ADMINISTRADOR | RECEPCIONISTA | DUENO"
        VARCHAR descripcion
    }
    USUARIOS {
        BIGINT id PK
        VARCHAR nombre
        VARCHAR email UK
        VARCHAR clave_hash "BCrypt"
        BIGINT rol_id FK
        BIT activo
        DATETIME fecha_creacion
        DATETIME fecha_actualizacion
    }
```

Script de referencia: [`ms-usuarios/db/schema.sql`](ms-usuarios/db/schema.sql).

## Diagrama de secuencia: inicio de sesión y ruta protegida

```mermaid
sequenceDiagram
    actor U as Usuario
    participant F as React (frontend)
    participant A as AuthController
    participant S as AuthService
    participant DB as MySQL
    participant J as JwtAuthFilter

    U->>F: correo + contraseña
    F->>A: POST /api/v1/auth/login
    A->>S: login(req)
    S->>DB: SELECT usuario WHERE email = ?
    DB-->>S: usuario + clave_hash
    S->>S: BCrypt.matches(clave, clave_hash)
    alt credenciales correctas
        S-->>F: 200 { token, expiraEnSegundos, usuario }
    else incorrectas o cuenta inactiva
        S-->>F: 401 "Correo o contrasena incorrectos"
    end
    F->>J: GET /api/v1/usuarios (Authorization: Bearer token)
    J->>J: verifica firma y vencimiento
    alt token válido y rol ADMINISTRADOR
        J-->>F: 200 lista de usuarios
    else sin token / alterado / vencido
        J-->>F: 401
    else rol insuficiente
        J-->>F: 403
    end
```

---

## Endpoints (`ms-usuarios`, puerto 8081)

| Método | Ruta | Acceso | Respuestas |
|---|---|---|---|
| POST | `/api/v1/auth/registro` | Público | 201, 400, 409 |
| POST | `/api/v1/auth/login` | Público | 200, 400, 401 |
| GET | `/api/v1/usuarios/yo` | Con token | 200, 401 |
| GET | `/api/v1/usuarios` | ADMINISTRADOR | 200, 401, 403 |
| GET | `/api/v1/usuarios/{id}` | El mismo usuario o ADMINISTRADOR | 200, 401, 403, 404 |
| POST | `/api/v1/usuarios` | ADMINISTRADOR | 201, 400, 401, 403, 409 |
| PUT | `/api/v1/usuarios/{id}` | El mismo usuario (nombre, correo, clave) o ADMINISTRADOR (además rol y activo) | 200, 400, 401, 403, 404, 409 |
| DELETE | `/api/v1/usuarios/{id}` | ADMINISTRADOR (desactiva) | 204, 401, 403, 404, 409 |

**Swagger:** `http://98.80.65.222:8081/swagger-ui.html` (EC2; requiere el puerto 8081 abierto en el grupo de seguridad).

---

## Cómo ejecutarlo

### Requisitos
Java 17+, Maven 3.9+, MySQL 8 (o Docker).

### 1. Base de datos
```bash
export DB_PASSWORD='una_clave_local'
docker compose up -d          # MySQL en el puerto 3306 con volumen persistente
```
(Si ya tienes MySQL instalado, basta con que exista el usuario; la BD se crea sola.)

### 2. Variables de entorno
| Variable | Ejemplo | Obligatoria |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/sanmarcos_usuarios?createDatabaseIfNotExist=true` | No (ese es el valor por defecto) |
| `DB_USER` | `root` | No |
| `DB_PASSWORD` | la misma del paso 1 | Sí |
| `JWT_SECRET` | texto aleatorio de 32+ caracteres | Recomendado (si falta, se genera uno al iniciar) |
| `CORS_ORIGINS` | `http://localhost:5173,http://<IP-EC2>:5173` | No |
| `SEED_CLAVE` | clave de los usuarios de prueba (por defecto `123456`) | No |

Generar un secreto: `openssl rand -base64 48`

### 3. Ejecutar
```bash
cd ms-usuarios
mvn spring-boot:run
```
Abrir `http://localhost:8081/swagger-ui.html`.

### Usuarios de prueba (se crean solos al iniciar)
| Correo | Rol |
|---|---|
| admin@sanmarcos.cl | ADMINISTRADOR |
| recepcion@sanmarcos.cl | RECEPCIONISTA |
| dueno@sanmarcos.cl | DUENO |

Clave: el valor de `SEED_CLAVE`. Son datos de demostración, no cuentas reales.

### Probar en Swagger
1. `POST /api/v1/auth/login` con `{"email":"admin@sanmarcos.cl","clave":"123456"}` → copiar `token`.
2. Botón **Authorize** → pegar el token.
3. `GET /api/v1/usuarios` → 200. Repetir con el token de `dueno@sanmarcos.cl` → 403. Sin token → 401.
4. Reiniciar el backend y volver a listar: los usuarios siguen ahí (persistencia real).

---

## Estructura

```
ms-usuarios/
├── db/schema.sql                      Modelo de referencia
├── pom.xml
└── src/main/java/cl/duoc/sanmarcos/usuarios/
    ├── config/        SecurityConfig, OpenApiConfig, DataInitializer
    ├── controller/    AuthController, UsuarioController
    ├── dto/           Request/Response (validación con @Valid)
    ├── exception/     Errores JSON y GlobalExceptionHandler
    ├── model/         Usuario, Rol, RolNombre
    ├── repository/    UsuarioRepository, RolRepository (Spring Data JPA)
    ├── security/      JwtService, JwtAuthFilter, RespuestasSeguridad
    └── service/       AuthService, UsuarioService (lógica y permisos)
```
