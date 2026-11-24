# CORPALIV — Backend de la aplicación móvil

## Contexto del sistema

CORPALIV permite consultar productos, registrar pedidos para retiro y enviar solicitudes de aporte, alianza o capacitación. El equipo gestor puede revisar esas solicitudes y pedidos, añadir observaciones y cambiar estados. Este backend adapta la estructura de microservicios del proyecto Festival a esas funciones reales.

**Estado actual:** la versión inicial se ejecutó con Docker y MySQL de XAMPP en el PC de desarrollo: funcionaron la consulta de productos, el inicio de sesión JWT, la consulta de solicitudes y pedidos, y la creación y eliminación de una alianza de prueba. Esta revisión separa DTO, conversión de respuestas y errores como en el proyecto Festival. Los cinco proyectos compilaron y pasaron las 18 pruebas existentes y nuevas mediante una configuración temporal de Maven Central, porque el mirror configurado no respondió desde el entorno de verificación. El mirror del proyecto se conserva sin cambios; hay que comprobar el despliegue de esta revisión en el PC. La app Android sigue guardando sus datos en Room: **aún no está conectada a esta API**.

## Arquitectura de microservicios

| Servicio | Puerto local | Base MySQL | Responsabilidad |
| --- | ---: | --- | --- |
| `api-gateway` | **9090** | Ninguna | Entrada única para Android y Postman; rutas y primera validación del JWT. |
| `producto-service` | 8081 | `corpaliv_productos` | Catálogo, precios y retiro lógico de productos. |
| `colaboracion-service` | 8082 | `corpaliv_colaboraciones` | Aportes, alianzas, capacitaciones e historial. |
| `auth-service` | 8083 | `corpaliv_auth` | Inicio de sesión JWT y cuentas del equipo. |
| `pedido-service` | 8084 | `corpaliv_pedidos` | Pedidos para retiro, estado, observaciones e historial. |

El teléfono solo llama al **gateway**. Los servicios internos se comunican entre sí dentro de Docker; `pedido-service` consulta a `producto-service` para obtener el nombre y precio al registrar un pedido. Cada servicio con datos tiene su propia base; Liquibase crea sus tablas. En este proyecto MySQL funciona en **XAMPP, en el PC**, no dentro de Docker.

Cada carpeta es un proyecto Spring Boot independiente: contiene su propio `pom.xml`, `Dockerfile`, `README.md`, `mvnw`, `mvnw.cmd`, `.mvn` con wrapper y mirror, y `src/main` / `src/test`. Las pruebas de controlador y de servicio se ubican bajo sus carpetas correspondientes cuando existen. `docker-compose.yml`, `build-all.bat` y `clean-all.bat` están en la raíz. `.gitattributes` mantiene los saltos de línea adecuados al trabajar con Git en Windows y Linux. `target` se genera al compilar y no viene en el ZIP.

Los servicios de autenticación, productos, colaboraciones y pedidos tienen `dto` para las entradas y salidas, `assemblers` para convertir entidades a respuestas y `exception` para errores de negocio y validación. Los DTO son clases Java con Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`); las respuestas de entidades tienen `fromModel`, como en Festival. Los *assemblers* delegan en esa conversión sin añadir enlaces HATEOAS, para mantener los mismos campos JSON que usaba la API inicial. Las fechas de pedidos, solicitudes e historiales siguen siendo milisegundos Unix (`long`), como en la app Android; no se cambian a texto con `@JsonFormat`.

## Requisitos para ejecutarlo

- **Docker Desktop** iniciado.
- **MySQL de XAMPP** iniciado en el puerto **3306**. El ejemplo local usa `root` sin contraseña; si tu instalación difiere, cambia `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` en los cuatro servicios de `docker-compose.yml`.
- **Java JDK 21** únicamente si deseas compilar directamente con `mvnw.cmd` fuera de Docker. Docker ya incluye Java y Maven para construir sus imágenes.

Apache de XAMPP solo hace falta para abrir phpMyAdmin y ver las bases de datos; **Postman** se usa para llamar a la API, no para administrar MySQL.

## Despliegue local con XAMPP y Docker

1. Descomprime el ZIP y abre la carpeta `Corpaliv-Backend` en VS Code. Inicia MySQL en XAMPP y Docker Desktop.
2. En PowerShell, situado en **la raíz `Corpaliv-Backend`**, ejecuta:

   ```powershell
   docker compose up --build -d
   docker compose ps
   ```

3. Verifica que aparezcan **cinco contenedores**. Si alguno falla, lee sus mensajes:

   ```powershell
   docker compose logs --tail=80 auth-service producto-service colaboracion-service pedido-service api-gateway
   ```

4. Al terminar, detén los contenedores con `docker compose down`. Los datos siguen en el MySQL de XAMPP. No borres sus bases si quieres conservar las solicitudes.

Los contenedores se conectan a XAMPP mediante `host.docker.internal:3306`. El gateway se abre en `http://localhost:9090` desde el PC; en un emulador Android sería `http://10.0.2.2:9090`, y en un teléfono de la misma red, `http://IP_DEL_PC:9090` (permitiendo ese puerto en el firewall del PC).

## Autenticación y permisos

La cuenta inicial de demostración se define en `docker-compose.yml`:

- **Correo:** `gestor@corpaliv.cl`
- **Contraseña:** `albalab2026`

`POST /api/auth/login` comprueba esas credenciales en `auth-service` y devuelve un JWT válido durante **ocho horas**. La contraseña se guarda como hash BCrypt en MySQL, no como texto plano. El cliente envía `Authorization: Bearer TOKEN` al consultar datos privados. El gateway valida el token y cada servicio exige el rol `GESTOR` donde corresponda.

Si ya se había iniciado la versión anterior con la cuenta de prueba `gestor@ejemplo.cl`, `auth-service` la cambia a las credenciales actuales sin borrar otros usuarios. Si ya existe `gestor@corpaliv.cl`, volver a iniciar el servicio no restablece su contraseña. Estas credenciales, la clave JWT compartida y el usuario MySQL son **valores de desarrollo local** visibles en Compose; hay que cambiarlos antes de publicar el sistema.

Prueba de inicio de sesión y consulta desde PowerShell:

```powershell
$login = Invoke-RestMethod -Method Post -Uri 'http://localhost:9090/api/auth/login' -ContentType 'application/json' -Body '{"email":"gestor@corpaliv.cl","password":"albalab2026"}'
Invoke-RestMethod -Uri 'http://localhost:9090/api/solicitudes' -Headers @{Authorization="Bearer $($login.token)"}
```

## Rutas del API Gateway

Todas estas rutas se llaman a través de `http://localhost:9090` desde el PC:

| Método y ruta | Acceso | Función |
| --- | --- | --- |
| `GET /api/productos`, `GET /api/productos/{id}` | Público | Consultar productos activos. |
| `POST /api/productos`, `PUT /api/productos/{id}`, `DELETE /api/productos/{id}` | Gestor | Crear, editar o retirar un producto. El retiro es lógico para conservar los pedidos antiguos. |
| `POST /api/solicitudes` | Público | Registrar aporte, alianza o capacitación. |
| `GET /api/solicitudes`, `GET /api/solicitudes/{id}`, `GET /api/solicitudes/{id}/historial` | Gestor | Revisar solicitudes e historial. |
| `PATCH /api/solicitudes/{id}/estado`, `POST /api/solicitudes/{id}/observaciones` | Gestor | Cambiar estado o agregar observaciones. |
| `DELETE /api/solicitudes/{id}` | Gestor | Eliminar solo **alianzas o capacitaciones en estado `RECIBIDA`**. |
| `POST /api/pedidos` | Público | Registrar un pedido para retiro consultando el catálogo. |
| `GET /api/pedidos`, `GET /api/pedidos/{id}`, `GET /api/pedidos/{id}/historial` | Gestor | Revisar pedidos e historial. |
| `PATCH /api/pedidos/{id}/estado`, `POST /api/pedidos/{id}/observaciones` | Gestor | Cambiar estado o agregar observaciones. Los pedidos no se eliminan. |
| `POST /api/auth/login` | Público | Recibir el JWT. |
| `GET /api/auth/me`, `GET /api/usuarios`, `POST /api/usuarios`, `PATCH /api/usuarios/{id}/activo?valor=false` | Gestor | Perfil y cuentas del equipo. |

Los estados son `RECIBIDA`, `EN_REVISION`, `CONTACTADO`, `CONCRETADO` y `DESCARTADO`. El gestor obtiene dos listas distintas, solicitudes y pedidos; un pedido no se guarda también como solicitud.

Los errores de validación y de negocio devuelven `timestamp`, `status`, `error`, `message` y `path`. Por ejemplo, `status: 400` y `message: "Tipo no permitido"`. Los errores de autenticación del filtro JWT pueden tener un cuerpo distinto.

## Documentación API (Swagger)

Swagger se abre **directamente** en el puerto de cada servicio desde el PC; el gateway no tiene una página Swagger propia:

| Servicio | Swagger UI | OpenAPI JSON |
| --- | --- | --- |
| Productos | `http://localhost:8081/doc/swagger-ui.html` | `http://localhost:8081/v3/api-docs` |
| Colaboraciones | `http://localhost:8082/doc/swagger-ui.html` | `http://localhost:8082/v3/api-docs` |
| Autenticación | `http://localhost:8083/doc/swagger-ui.html` | `http://localhost:8083/v3/api-docs` |
| Pedidos | `http://localhost:8084/doc/swagger-ui.html` | `http://localhost:8084/v3/api-docs` |

Abrir la documentación Swagger no requiere token. Para ejecutar una ruta del gestor en Swagger, obtén primero el token mediante `POST http://localhost:9090/api/auth/login` y pégalo en **Authorize**. Las rutas públicas no requieren token.

## Ejemplos de solicitudes JSON

Alianza (`POST /api/solicitudes`):

```json
{"tipo":"ALIANZA","nombre":"Camila Rojas","correo":"camila@ejemplo.cl","organizacion":"Empresa SpA","cargo":"RRHH","detalleColaboracion":"Queremos colaborar en inclusión laboral.","aceptaPrivacidad":true}
```

Aporte (`POST /api/solicitudes`):

```json
{"tipo":"DONACION","nombre":"Ana Pérez","correo":"ana@ejemplo.cl","donanteTipo":"PERSONA","montoCLP":50000,"anonimo":false,"aceptaPrivacidad":true}
```

Pedido (`POST /api/pedidos`):

```json
{"productoId":2,"nombreRetira":"Ana Pérez","cantidad":2,"fechaRetiro":1795564800000,"envolturaRegalo":false,"correo":"ana@ejemplo.cl","notas":"Retiro por la tarde","aceptaCondiciones":true}
```

`fechaRetiro` usa milisegundos Unix, igual que Android. El servidor toma nombre y precio desde `producto-service`, sin confiar en un precio enviado por el teléfono. El backend todavía no recibe las fotos guardadas como rutas locales en Android. La donación es una intención de aporte; no realiza un cobro electrónico.

## Pruebas unitarias y compilación

Con JDK 21 instalado en Windows, desde la raíz ejecuta `.\build-all.bat` para compilar y probar los cinco proyectos. Para probar solo uno, entra en su carpeta y ejecuta su propio wrapper, por ejemplo:

```powershell
cd pedido-service
.\mvnw.cmd -s .mvn/settings.xml clean test package
```

El mirror de Maven está en `.mvn/settings.xml` dentro de **cada** servicio y no se modificó. `clean-all.bat` elimina los resultados `target` de los cinco servicios; no borra contenedores ni bases de datos. Las 18 pruebas pasaron con Maven y una configuración temporal de Maven Central en el entorno de verificación; al descomprimir el ZIP en el PC, ejecuta `build-all.bat` para comprobar el mirror proporcionado y el proyecto. También se comprobó en el PC de desarrollo la ejecución inicial con Docker y MySQL de XAMPP para las operaciones indicadas arriba; esta revisión se debe probar allí nuevamente. Como en el proyecto Festival, colaboraciones y pedidos tienen pruebas separadas en `src/test/java/.../controller` y `src/test/java/.../service`. Autenticación prueba su controlador y configuración; productos prueba su controlador. No se agregaron clases de servicio ni pruebas de arranque vacías solo para repetir carpetas.

## Pendiente para integrar Android

Actualmente la app de Jetpack Compose consulta y guarda datos en **Room, en el teléfono**; por eso crear algo desde el móvil no lo envía a MySQL. Para conectarla hay que incorporar Retrofit, definir peticiones y respuestas compatibles con esta API, sustituir o coordinar los repositorios locales, enviar el JWT en las rutas privadas y probar los flujos reales con el gateway iniciado. Los detalles de esa etapa se trabajarán en el proyecto Android, sin modificarlo desde este ZIP.

También quedan por verificar el flujo completo en un teléfono, el requisito académico de consumir una API externa, las pruebas pertinentes en Android, la documentación de la solución integrada y el APK release firmado. Esas tareas no están incluidas ni se consideran terminadas por tener este backend compilado.
