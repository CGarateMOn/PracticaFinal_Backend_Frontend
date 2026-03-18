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

* Controllers → Gestionan peticiones HTTP y validan los datos de entrada
* Services → Contienen la lógica de negocio.
* Repositiorios → Encapsulan el acceso a datos (persistencia).
* Entidades → Son las clases que srepresentan las tablas de la base de datos.
* Modelos → DTOs específicos.
* Records → DTOs funcionales del dominio.
---

# Controllers

## AuthController
Gestiona el ciclo de vida de la sesión del usuario:
Tenemos los siguientes endpoints:

1. **POST /register** donde se autentifica y crea el nuevo usuario y por defecto se crea con el rol de usuario. Si intentas crearte otra cuenta con el mismo correo te salta un error.
2. **POST /login** te permite con el email y la contraseña acceder.
3. **POST /logout** te permite cerrar sesión.
4. **GET /me** te permite obtener tu perfil aunque no hayas hecho el login.

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

## AuthService & Hashing
El AuthService gestiona la lógica de autenticación, apoyándose en la utilidad Hashing para asegurar que las contraseñas nunca se guarden en texto plano, utilizando algoritmos de cifrado seguros.

---
## Persistencia (Datos)
- Entidades: Clases como Usuario y Reserva están mapeadas a una base de datos relacional.
- Repositorios: permiten realizar consultas complejas (como buscar reservas por rango de fechas) de forma eficiente.
- Estado: Se utiliza el enum EstadoReserva para gestionar el ciclo de vida de una reserva (Confirmada, Pendiente, Cancelada).

# Seguridad

## ConfiguracionSeguridad

Configura quién puede entrar y qué puede hacer cada usuario.

* Reglas de Acceso: Define qué rutas son públicas (ver pistas, disponibilidad, registro, health) y cuáles requieren estar logueado (reservas, gestión de usuarios).
* Gestión de Sesión: Configura el login mediante autenticación básica (httpBasic) y un proceso de logout personalizado que devuelve un código 204 No Content.
* Usuarios Dinámicos: El método UserDetailsService conecta Spring Security con tu AlmacenDatos. Permite que los usuarios registrados en tu mapa estático puedan loguearse usando su email como nombre de usuario.
* Flexibilidad: Desactiva la protección CSRF para las rutas de la API, facilitando las pruebas desde herramientas como Postman.
* Seguridad por Anotaciones: Al usar @EnableMethodSecurity, permite que funcionen los @PreAuthorize que vimos en los otros controladores.

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

Esta clase TareasProgramadas actua como el reloj interno y orquestador temporal del sistema. Su función principal es disparar procesos automáticos en momentos específicos del tiempo, garantizando que el sistema realice tareas de mantenimiento y comunicación

- Recordatorios Diarios de Reservas: * Ejecución: Cada día a las 2:00 AM (0 0 2 * * *).

* Acción: Ordena a ReservaService que procese y envíe recordatorios a los usuarios que tienen una reserva para el día que comienza.

* Propósito: Reducir el absentismo y mejorar la experiencia del usuario.

- Boletín Mensual de Disponibilidad: * Ejecución: El día 1 de cada mes a las 9:00 AM (0 0 9 1 * *).

* Acción: Invoca a PistaService para generar y distribuir un resumen informativo sobre la disponibilidad de las pistas para el nuevo mes.
* Propósito: Fomentar la reserva anticipada y mantener informada a la comunidad de jugadores.

---

# Pruebas (Testing)
El proyecto cuenta con una robusta suite de tests en src/test/java:

- Unitarios: Pruebas de servicios (UsuarioServiceTest, DisponibilidadServiceTest) aislados de la base de datos.

- Integración/Repositorios: Validación de consultas en RepoUsuariosTest.

- E2E (End-to-End): Pruebas completas de flujo de usuario en UsuarioE2ETest para asegurar que los endpoints responden correctamente.


