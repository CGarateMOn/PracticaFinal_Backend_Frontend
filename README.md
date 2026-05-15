# Practica Final PAT
 
Sistema de gestión de pistas de pádel desarrollado con Spring Boot y frontend en HTML/CSS/JavaScript.
 
Autores: Carlos Gárate, Raffaella Boccacci, Martina Iscar, Amalia Varo e Irene Morales.
 
---
 
# Acceso a la Aplicación
 
## Pasos para ejecutar
 
1. Ejecutar `MvcApplication` (botón Run en IntelliJ).
2. Abrir el navegador en: [http://localhost:8080/index.html](http://localhost:8080/index.html)
---
 
## Usuarios disponibles
 
Todos los usuarios tienen la misma contraseña: **1234**
 
| Nombre | Email | Rol |
|---|---|---|
| Admin Premium | admin@premium.com | **ADMIN** |
| Carlos III Martín | carlos@premium.com | Cliente |
| María García | maria@premium.com | Cliente |
| Lady Amalia IV de Inglaterra | amalia@premium.com | Cliente |
| Asistenta Limpiadora Martina | martina@premium.com | Cliente |
| Raffaella Peruana Acevichada | raffaella@premium.com | Cliente |
| Irene Paseadora de Perros Dueña de Tres Chihuahuas | irene@premium.com | Cliente |
 
> Al iniciar sesión con `admin@premium.com` se redirige automáticamente al panel de administración. Los usuarios con rol Cliente no pueden acceder a las rutas de administración.
 
---
 
# Estructura del Proyecto
 
## Backend (`edu.comillas.icai.gitt.pat.spring.mvc`)
 
```
edu.comillas.icai.gitt.pat.spring.mvc
│
├── api
│   ├── AdminReservasController
│   ├── AuthController
│   ├── DisponibilidadController
│   ├── HealthController
│   ├── PistaController
│   ├── ReservasController
│   └── UsuarioController
│
├── service (Lógica de Negocio)
│   ├── AuthService
│   ├── DisponibilidadService
│   ├── PistaService
│   ├── ReservaService
│   └── UsuarioService
│
├── repositorios
│   ├── RepoPistas
│   ├── RepoReserva
│   ├── RepoToken
│   └── RepoUsuarios
│
├── entidades
│   ├── EstadoReserva (enum)
│   ├── Pista
│   ├── Reserva
│   ├── Token
│   └── Usuario
│
├── modelos
│   ├── LoginRequest
│   ├── RegisterRequest
│   ├── ProfileResponse
│   └── Rol (enum)
│
├── records
│   ├── Disponibilidad
│   ├── PistaPatchForm
│   ├── TramosHorarios
│   └── UpdateUsuarioRequest
│
├── util
│   └── Hashing
│
├── TareasProgramadas
└── MvcApplication
```
 
## Frontend (`src/main/resources/static`)
 
El frontend se ha desarrollado íntegramente en HTML, CSS y JavaScript vanilla. Cada página HTML tiene su propio archivo JavaScript asociado. La hoja de estilos `styles.css` es compartida por todos los HTMLs.
 
```
static/
│
├── admin.html                    / admin.js
├── adminFormularioPista.html     / adminFormularioPista.js
├── adminPistas.html              / adminPistas.js
├── adminReservas.html            / adminReservas.js
├── adminUsuarioDetalle.html      / adminUsuarioDetalle.js
├── adminUsuarios.html            / adminUsuarios.js
├── index.html                    / index.js (pendiente)
├── logIn.html                    / login.js
├── misReservas.html              / misReservas.js
├── notificaciones.html           / notificaciones.js
├── perfil.html                   / perfil.js
├── reservas.html                 / reservas.js
├── SignIn.html                   / signin.js
│
├── navbar.js                     (componente compartido de navegación)
└── styles.css                    (hoja de estilos compartida)
```
 
> Todos los HTMLs están implementados. Algunos JavaScripts están aún en desarrollo; el patrón de nomenclatura es `nombrePagina.js` para cada `nombrePagina.html`.
 
---
 
# Arquitectura General
 
El proyecto sigue una arquitectura en capas:
 
```
Cliente HTTP (Frontend HTML/JS)
      ↓
Controller (api)
      ↓
Service
      ↓
Repositorio (persistencia)
      ↓
Base de datos / almacenamiento
```
 
- **Controllers** → Gestionan peticiones HTTP y validan los datos de entrada.
- **Services** → Contienen la lógica de negocio.
- **Repositorios** → Encapsulan el acceso a datos (persistencia).
- **Entidades** → Clases que representan las tablas de la base de datos.
- **Modelos** → DTOs específicos.
- **Records** → DTOs funcionales del dominio.
---
 
# Controllers
 
## AuthController
Gestiona el ciclo de vida de la sesión del usuario:
 
1. **POST /register** — Crea un nuevo usuario con rol USER por defecto. No permite duplicados por email.
2. **POST /login** — Permite acceder con email y contraseña. Si el usuario es admin, redirige al panel de administración.
3. **POST /logout** — Cierra la sesión.
4. **GET /me** — Devuelve el perfil del usuario autenticado.
---
 
## UsuarioController
 
Gestión administrativa y personal de usuarios:
 
- Permite listar todos los usuarios (solo ADMIN).
- Permite consultar y actualizar (PATCH) datos de perfil.
- Implementa seguridad para que un usuario solo pueda editar su propio perfil o lo haga un administrador.
---
 
## PistaController
 
CRUD completo de las instalaciones:
- Público: Listar pistas y ver detalles.
- Privado (ADMIN): Crear, modificar (precio, nombre) y eliminar pistas.
---
 
## ReservasController
 
El núcleo operativo para el usuario:
- Permite crear reservas validando que no existan solapamientos.
- Listado de reservas propias con filtros de fecha.
- Cancelación de reservas existentes.
---
 
## AdminReservasController
 
Panel de control para administradores:
- Visualización global de todas las reservas del sistema.
- Filtros avanzados por fecha, ID de pista o ID de usuario para labores de supervisión.
---
 
## DisponibilidadController
 
Expone la disponibilidad de pistas en función de la fecha solicitada:
 
- Endpoint público para consultar disponibilidad por fecha.
- Endpoint específico para consultar disponibilidad de una pista concreta.
- Validación del formato de fecha.
- Devuelve tramos horarios disponibles calculados dinámicamente.
---
 
## HealthController
 
Endpoint técnico de verificación del estado de la aplicación.
 
- **GET /pistaPadel/health** → Devuelve 200 OK si la aplicación está funcionando.
- Útil para despliegues en plataformas como Render o CI/CD.
---
 
# Service
 
## DisponibilidadService
 
Calcula los tramos horarios disponibles de una pista en una fecha determinada:
 
- Define el horario base del club (9:00 – 22:00).
- Consulta las reservas existentes en memoria.
- Calcula dinámicamente los huecos libres.
- Devuelve únicamente los intervalos disponibles.
## AuthService & Hashing
 
El `AuthService` gestiona la lógica de autenticación, apoyándose en la utilidad `Hashing` para asegurar que las contraseñas nunca se guarden en texto plano.
 
---
 
## Persistencia (Datos)
 
- **Entidades**: Clases como `Usuario` y `Reserva` están mapeadas a una base de datos relacional.
- **Repositorios**: Permiten realizar consultas complejas (como buscar reservas por rango de fechas) de forma eficiente.
- **Estado**: Se utiliza el enum `EstadoReserva` para gestionar el ciclo de vida de una reserva (ACTIVA, CANCELADA).
---
 
# Seguridad
 
## ConfiguracionSeguridad
 
- **Reglas de Acceso**: Define qué rutas son públicas (ver pistas, disponibilidad, registro, health) y cuáles requieren autenticación (reservas, gestión de usuarios).
- **Gestión de Sesión**: Login mediante autenticación básica (`httpBasic`) y logout personalizado que devuelve 204 No Content.
- **Usuarios Dinámicos**: Conecta Spring Security con el repositorio de usuarios para que el email actúe como nombre de usuario.
- **Flexibilidad**: Desactiva la protección CSRF para las rutas de la API.
- **Seguridad por Anotaciones**: Mediante `@EnableMethodSecurity` permite el uso de `@PreAuthorize` en los controladores.
---
 
# Records
 
Contiene los modelos principales del dominio definidos como `record`:
 
- Usuario, Rol, Pista, Reserva, Disponibilidad, TramosHorarios
---
 
# Tareas Programadas
 
La clase `TareasProgramadas` actúa como orquestador temporal del sistema:
 
- **Recordatorios Diarios** (`0 0 2 * * *`): Cada día a las 2:00 AM envía recordatorios a usuarios con reservas para ese día.
- **Boletín Mensual** (`0 0 9 1 * *`): El día 1 de cada mes a las 9:00 AM genera un resumen de disponibilidad de pistas para el nuevo mes.
---
 
# Pruebas (Testing)
 
El proyecto cuenta con una suite de tests en `src/test/java`:
 
- **Unitarios**: Pruebas de servicios (`UsuarioServiceTest`, `DisponibilidadServiceTest`) aislados de la base de datos.
- **Integración/Repositorios**: Validación de consultas en `RepoUsuariosTest`.
- **E2E (End-to-End)**: Pruebas completas de flujo de usuario en `UsuarioE2ETest`.


