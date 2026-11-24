# CORPALIV — Servicio de Colaboraciones

## Contexto y responsabilidad

Registra aportes, alianzas y solicitudes de capacitación. Guarda el estado, las observaciones y el historial que revisa el equipo gestor.

**Puerto:** `8082`. Usa la base `corpaliv_colaboraciones` en MySQL de XAMPP. Liquibase crea las tablas de solicitudes e historial.

## Rutas

| Método y ruta | Acceso | Función |
| --- | --- | --- |
| `POST /api/solicitudes` | Público | Registrar una solicitud. |
| `GET /api/solicitudes`, `GET /api/solicitudes/{id}`, `GET /api/solicitudes/{id}/historial` | Gestor | Listado, detalle e historial. |
| `PATCH /api/solicitudes/{id}/estado`, `POST /api/solicitudes/{id}/observaciones` | Gestor | Actualizar estado y agregar observaciones. |
| `DELETE /api/solicitudes/{id}` | Gestor | Solo alianzas o capacitaciones en `RECIBIDA`. |

Las peticiones de Android y Postman entran por `http://localhost:9090` en el PC, no directamente por este puerto interno.

## Seguridad

Enviar una solicitud es público; consultarla o gestionarla requiere JWT con rol `GESTOR`. No se eliminan aportes.

## Documentación API (Swagger)

`http://localhost:8082/doc/swagger-ui.html` y `http://localhost:8082/v3/api-docs`.

## Ejecución y pruebas

Para ejecutar todo junto: inicia MySQL en XAMPP y Docker Desktop. Desde la raíz `Corpaliv-Backend` ejecuta `docker compose up --build -d`. Docker construye este servicio mediante su propio `Dockerfile`.

Para **compilar y probar este microservicio por separado** en Windows, instala JDK 21, abre PowerShell en esta carpeta y ejecuta:

```powershell
.\mvnw.cmd -s .mvn/settings.xml clean test package
```

El mirror de Maven está en `.mvn/settings.xml`. `target` aparece tras compilar y no se incluye en el ZIP. Consulta el README de la raíz para requisitos, credenciales de desarrollo y pruebas del flujo completo.
