# Practica Final PAT

Sistema de gestión de pistas de pádel desarrollado con Spring Boot.

Autores: Carlos Gárate, Raffaella Boccacci, Martina Iscar, Amalia Varo e Irene Morales.

Este proyecto implementa una API REST completa para la gestión de usuarios, pistas, reservas y disponibilidad, incluyendo seguridad, control de accesos y tareas programadas.

A continuación se hará una breve explicación de cada una de las clases.

---

# Estructura del Proyecto

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
├── service
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
│   ├── EstadoReserva
│   ├── Pista
│   ├── Reserva
│   ├── Token
│   └── Usuario
│
├── modelos
│   ├── LoginRequest
│   ├── RegisterRequest
│   ├── ProfileResponse
│   └── Role
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

---

# Arquitectura General

El proyecto sigue una arquitectura en capas sencilla:

```
Cliente HTTP
      ↓
Controller (api)
      ↓
Service
      ↓
Repositorio (persistencia)
      ↓
Base de datos / almacenamiento
```

* Controllers → Gestionan peticiones HTTP.
* Services → Contienen la lógica de negocio.
* Repositiorios → Encapsulan el acceso a datos (persistencia).
* Entidades → Son las clases que se almacenan en base de datos.
* Modelos → DTOs específicos.
* Records → DTOs funcionales del dominio.
---

# Controllers

## AuthController

Tenemos los siguientes endpoints:

1. **POST /register** donde se autentifica y crea el nuevo usuario y por defecto se crea con el rol de usuario. Si intentas crearte otra cuenta con el mismo correo te salta un error.
2. **POST /login** te permite con el email y la contraseña acceder.
3. **POST /logout** te permite cerrar sesión.
4. **GET /me** te permite obtener tu perfil aunque no hayas hecho el login.

---

## UserController

Es un controlador REST para gestionar los perfiles de usuarios del sistema de pádel, interactuando directamente con el AlmacenDatos.

Sus funciones clave son:

* Gestión de Perfiles: Permite listar todos los usuarios, consultar un perfil específico y actualizar datos mediante PATCH.
* Seguridad Granular: Implementa reglas de acceso inteligentes:

  * listarUsuarios: Solo accesible para el ADMIN.
  * obtener / actualizar: Accesible para el ADMIN o para el propio usuario dueño de la cuenta (authentication.name == #userId).
* Integridad de Datos: Valida que el cuerpo de la petición sea correcto (@Valid) y prohíbe explícitamente modificar el ID del usuario para evitar inconsistencias.
* Persistencia Volátil: Las actualizaciones se guardan directamente en el mapa estático de AlmacenDatos, permitiendo que los cambios se mantengan mientras la aplicación esté ejecutándose.

---

## PistaController

Es un controlador REST para gestionar pistas de pádel con un CRUD completo bajo la ruta /pistaPadel/courts.

Sus funciones clave son:

* Operaciones: Permite listar, ver detalles, crear, modificar (vía PATCH) y eliminar pistas.
* Seguridad: Restringe la creación, edición y borrado solo a usuarios con rol ADMIN.
* Validación: Comprueba que los datos recibidos sean correctos (@Valid) y bloquea cambios accidentales de ID.
* Trazabilidad: Registra en el log tanto las acciones exitosas como los errores de validación.

---

## ReservasController

Es el controlador encargado de gestionar las reservas de los usuarios autenticados.

Sus funciones clave son:

* Creación de reservas con validación de solapamientos horarios.
* Listado de reservas propias con filtros opcionales por rango de fechas.
* Consulta individual de una reserva.
* Cancelación de reservas futuras.
* Modificación parcial (PATCH) con validación de conflictos.
* Seguridad basada en roles USER y ADMIN.
* Control de acceso para que un usuario solo pueda ver o modificar sus propias reservas (salvo ADMIN).

---

## AdminReservasController

Es un controlador REST exclusivo para administradores que permite visualizar todas las reservas del sistema.

Sus funciones clave son:

* Endpoint: **GET /pistaPadel/admin/reservations**
* Permite aplicar filtros opcionales por:

  * date
  * courtId
  * userId
* Seguridad: Solo accesible mediante rol ADMIN (@PreAuthorize).
* Permite una visión global del sistema para tareas de supervisión y gestión.

La diferencia con ReservasController es que este último gestiona únicamente las reservas del usuario autenticado, mientras que AdminReservasController permite una visión completa del sistema.

---

## DisponibilidadController

Es el controlador encargado de exponer la disponibilidad de pistas en función de la fecha solicitada.

Sus funciones clave son:

* Endpoint público para consultar disponibilidad por fecha.
* Endpoint específico para consultar disponibilidad de una pista concreta.
* Validación del formato de fecha.
* Devuelve tramos horarios disponibles calculados dinámicamente.
* Incluye trazas (logs) informativas para seguimiento de consultas.

---

## HealthController

Es un endpoint técnico de verificación del estado de la aplicación.

* Endpoint: **GET /pistaPadel/health**
* Devuelve un 200 OK si la aplicación está funcionando.
* Útil para despliegues en plataformas como Render o CI/CD.
* No contiene lógica de negocio.

---

# Service

## DisponibilidadService

Es la capa de lógica encargada de calcular los tramos horarios disponibles de una pista en una fecha determinada.

Sus funciones clave son:

* Define el horario base del club (9:00 – 22:00).
* Consulta las reservas existentes en memoria.
* Calcula dinámicamente los huecos libres.
* Devuelve únicamente los intervalos disponibles.
* Separa la lógica del controlador, manteniendo una arquitectura limpia.

---

# Seguridad

## ConfiguracionSeguridad

Configura quién puede entrar y qué puede hacer cada usuario.

* Reglas de Acceso: Define qué rutas son públicas (ver pistas, disponibilidad, registro, health) y cuáles requieren estar logueado (reservas, gestión de usuarios).
* Gestión de Sesión: Configura el login mediante autenticación básica (httpBasic) y un proceso de logout personalizado que devuelve un código 204 No Content.
* Usuarios Dinámicos: El método UserDetailsService conecta Spring Security con tu AlmacenDatos. Permite que los usuarios registrados en tu mapa estático puedan loguearse usando su email como nombre de usuario.
* Flexibilidad: Desactiva la protección CSRF para las rutas de la API, facilitando las pruebas desde herramientas como Postman.
* Seguridad por Anotaciones: Al usar @EnableMethodSecurity, permite que funcionen los @PreAuthorize que vimos en los otros controladores.

---

# Data

## AlmacenDatos

Es un almacén de datos en memoria que simula una base de datos mediante mapas estáticos (ConcurrentHashMap).

* Propósito: Centralizar y precargar información de prueba (Semillas/Seeds).
* Contenido: Define roles (Admin/User), usuarios, pistas, horarios disponibles y reservas activas.
* Seguridad: Usa colecciones concurrentes para evitar errores si varios usuarios acceden a la vez.
* Acceso: No se puede instanciar (constructor privado); se accede a los datos de forma directa y global.

---

# Records

Contiene los modelos principales del dominio definidos como `record`:

* Usuario
* Rol
* Pista
* Reserva
* Disponibilidad
* TramosHorarios

Representan la estructura de datos utilizada en toda la aplicación.

---

# Tareas Programadas

Esta clase TareasProgramadas es un servicio de automatización de Spring que ejecuta procesos en segundo plano de forma periódica sin intervención humana.

Es un @Service que utiliza @Scheduled con expresiones Cron para disparar tareas en momentos específicos.

* Tarea 1: Recordatorios Diarios (2:00 AM): Recorre todas las reservas del AlmacenDatos. Si una reserva coincide con la fecha de "hoy", identifica al usuario para enviarle un aviso (actualmente solo deja el rastro en el log).
* Tarea 2: Boletín Mensual (Día 1 a las 9:00 AM): Genera un resumen de la disponibilidad de todas las pistas y lo "envía" (mediante logs) a todos los usuarios que estén marcados como activos en el sistema.
* Dependencia: Se apoya totalmente en los datos estáticos de AlmacenDatos para obtener la información de usuarios, pistas y reservas.

---


