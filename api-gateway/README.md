# CORPALIV — API Gateway

## Contexto y responsabilidad

Punto de entrada de la app Android y de Postman. Distribuye las peticiones entre los cuatro microservicios y valida los JWT antes de pasar a las rutas privadas.

**Puerto:** `9090`. No guarda datos ni se conecta a MySQL. El mapa de rutas se encuentra en `src/main/resources/application.yml`.

## Rutas

| Ruta recibida | Destino dentro de Docker |
| --- | --- |
| `/api/productos` | `producto-service:8081` |
| `/api/solicitudes` | `colaboracion-service:8082` |
| `/api/auth`, `/api/usuarios` | `auth-service:8083` |
| `/api/pedidos` | `pedido-service:8084` |

Desde el PC, Android o Postman acceden al backend por `http://localhost:9090`; las peticiones a los otros servicios pasan por este gateway.

## Seguridad

La consulta de productos, el login y el envío de nuevas solicitudes o pedidos son públicos. Para las demás rutas exige un JWT válido. Cada servicio comprueba después el rol de gestor en sus propias rutas privadas.

## Documentación API (Swagger)

Este gateway no tiene una página Swagger propia; la documentación está en los servicios y el resumen de rutas en el README de la raíz.

## Ejecución y pruebas

Para ejecutar todo junto: inicia MySQL en XAMPP y Docker Desktop. Desde la raíz `Corpaliv-Backend` ejecuta `docker compose up --build -d`. Docker construye este servicio mediante su propio `Dockerfile`.

Para **compilar y probar este microservicio por separado** en Windows, instala JDK 21, abre PowerShell en esta carpeta y ejecuta:

```powershell
.\mvnw.cmd -s .mvn/settings.xml clean test package
```

El mirror de Maven está en `.mvn/settings.xml`. `target` aparece tras compilar y no se incluye en el ZIP. Consulta el README de la raíz para requisitos, credenciales de desarrollo y pruebas del flujo completo.
