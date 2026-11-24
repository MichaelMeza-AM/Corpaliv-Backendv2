# CORPALIV — Servicio de Autenticación

## Contexto y responsabilidad

Comprueba el correo y la contraseña del equipo de CORPALIV y emite JWT. También permite gestionar cuentas del equipo; por ahora no existe un perfil separado que justifique otro microservicio.

**Puerto:** `8083`. Usa la base `corpaliv_auth` en MySQL de XAMPP. Liquibase crea la tabla `usuarios` y las contraseñas se guardan como hashes BCrypt. Las credenciales iniciales y la clave JWT están en el `docker-compose.yml` de la raíz.

## Rutas

| Método y ruta | Acceso | Función |
| --- | --- | --- |
| `POST /api/auth/login` | Público | Iniciar sesión y recibir un JWT de ocho horas. |
| `GET /api/auth/me` | Gestor | Consultar el perfil autenticado. |
| `GET /api/usuarios`, `POST /api/usuarios` | Gestor | Listar y crear cuentas del equipo. |
| `PATCH /api/usuarios/{id}/activo?valor=false` | Gestor | Activar o desactivar una cuenta. |

Las peticiones de Android y Postman entran por `http://localhost:9090` en el PC, no directamente por este puerto interno.

## Seguridad

El login es público. Las otras rutas exigen JWT con rol `GESTOR`. Las credenciales de demostración local son `gestor@corpaliv.cl` / `albalab2026`; cámbialas antes de publicar.

## Documentación API (Swagger)

`http://localhost:8083/doc/swagger-ui.html` y `http://localhost:8083/v3/api-docs`.

## Ejecución y pruebas

Para ejecutar todo junto: inicia MySQL en XAMPP y Docker Desktop. Desde la raíz `Corpaliv-Backend` ejecuta `docker compose up --build -d`. Docker construye este servicio mediante su propio `Dockerfile`.

Para **compilar y probar este microservicio por separado** en Windows, instala JDK 21, abre PowerShell en esta carpeta y ejecuta:

```powershell
.\mvnw.cmd -s .mvn/settings.xml clean test package
```

El mirror de Maven está en `.mvn/settings.xml`. `target` aparece tras compilar y no se incluye en el ZIP. Consulta el README de la raíz para requisitos, credenciales de desarrollo y pruebas del flujo completo.
