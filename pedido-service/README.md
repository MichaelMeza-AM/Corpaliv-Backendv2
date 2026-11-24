# CORPALIV — Servicio de Pedidos

## Contexto y responsabilidad

Registra pedidos para retiro y permite al equipo verlos, añadir observaciones y cambiar su estado. No procesa pagos ni elimina pedidos.

**Puerto:** `8084`. Usa la base `corpaliv_pedidos` en MySQL de XAMPP. Liquibase crea las tablas de pedidos e historial. Al registrar un pedido consulta `producto-service` y conserva una copia del nombre y precio del producto.

## Rutas

| Método y ruta | Acceso | Función |
| --- | --- | --- |
| `POST /api/pedidos` | Público | Registrar un pedido válido. |
| `GET /api/pedidos`, `GET /api/pedidos/{id}`, `GET /api/pedidos/{id}/historial` | Gestor | Listado, detalle e historial. |
| `PATCH /api/pedidos/{id}/estado`, `POST /api/pedidos/{id}/observaciones` | Gestor | Actualizar estado y agregar observaciones. |

Las peticiones de Android y Postman entran por `http://localhost:9090` en el PC, no directamente por este puerto interno.

## Seguridad

Enviar un pedido es público; consultarlo o gestionarlo requiere JWT con rol `GESTOR`.

## Documentación API (Swagger)

`http://localhost:8084/doc/swagger-ui.html` y `http://localhost:8084/v3/api-docs`.

## Ejecución y pruebas

Para ejecutar todo junto: inicia MySQL en XAMPP y Docker Desktop. Desde la raíz `Corpaliv-Backend` ejecuta `docker compose up --build -d`. Docker construye este servicio mediante su propio `Dockerfile`.

Para **compilar y probar este microservicio por separado** en Windows, instala JDK 21, abre PowerShell en esta carpeta y ejecuta:

```powershell
.\mvnw.cmd -s .mvn/settings.xml clean test package
```

El mirror de Maven está en `.mvn/settings.xml`. `target` aparece tras compilar y no se incluye en el ZIP. Consulta el README de la raíz para requisitos, credenciales de desarrollo y pruebas del flujo completo.
