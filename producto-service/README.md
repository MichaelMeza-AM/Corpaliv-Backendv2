# CORPALIV — Servicio de Productos

## Contexto y responsabilidad

Administra el catálogo que consulta el público y los precios que `pedido-service` usa al registrar pedidos. Retirar un producto lo oculta sin borrar los datos ya guardados en pedidos.

**Puerto:** `8081`. Usa la base `corpaliv_productos` en MySQL de XAMPP. Liquibase crea la tabla y carga cuatro productos de ejemplo.

## Rutas

| Método y ruta | Acceso | Función |
| --- | --- | --- |
| `GET /api/productos`, `GET /api/productos/{id}` | Público | Lista y detalle de productos activos. |
| `POST /api/productos`, `PUT /api/productos/{id}` | Gestor | Crear y editar productos. |
| `DELETE /api/productos/{id}` | Gestor | Retiro lógico; conserva el historial. |

Las peticiones de Android y Postman entran por `http://localhost:9090` en el PC, no directamente por este puerto interno.

## Seguridad

Las escrituras requieren JWT con rol `GESTOR`; la consulta pública no necesita login.

## Documentación API (Swagger)

`http://localhost:8081/doc/swagger-ui.html` y `http://localhost:8081/v3/api-docs`.

## Ejecución y pruebas

Para ejecutar todo junto: inicia MySQL en XAMPP y Docker Desktop. Desde la raíz `Corpaliv-Backend` ejecuta `docker compose up --build -d`. Docker construye este servicio mediante su propio `Dockerfile`.

Para **compilar y probar este microservicio por separado** en Windows, instala JDK 21, abre PowerShell en esta carpeta y ejecuta:

```powershell
.\mvnw.cmd -s .mvn/settings.xml clean test package
```

El mirror de Maven está en `.mvn/settings.xml`. `target` aparece tras compilar y no se incluye en el ZIP. Consulta el README de la raíz para requisitos, credenciales de desarrollo y pruebas del flujo completo.
