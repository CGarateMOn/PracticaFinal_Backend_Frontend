const INDEX_API_BASE = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {
    cargarInicio();
});

async function cargarInicio() {
    const mensajeInicio = document.getElementById("mensaje-inicio");
    const accesoAdmin = document.getElementById("acceso-admin");

    try {
        const respuesta = await fetch(`${INDEX_API_BASE}/pistaPadel/auth/me`, {
            credentials: "include"
        });

        if (!respuesta.ok) {
            mostrarInicioSinSesion(mensajeInicio, accesoAdmin);
            return;
        }

        const usuario = await respuesta.json();
        mostrarInicioConSesion(usuario, mensajeInicio, accesoAdmin);
    } catch (error) {
        console.error("Error al cargar la informacion de inicio:", error);
        mostrarInicioSinSesion(mensajeInicio, accesoAdmin);
    }
}

function mostrarInicioConSesion(usuario, mensajeInicio, accesoAdmin) {
    const nombre = usuario.nombre || "usuario";
    const rol = (usuario.rol || "").toUpperCase();

    if (mensajeInicio) {
        mensajeInicio.textContent = `Hola, ${nombre}. Desde aqui puedes reservar pista y revisar tus reservas.`;
    }

    if (accesoAdmin) {
        accesoAdmin.hidden = rol !== "ADMIN";
    }

    mostrarEnlacesSinSesion(false);
}

function mostrarInicioSinSesion(mensajeInicio, accesoAdmin) {
    if (mensajeInicio) {
        mensajeInicio.textContent = "Bienvenido a Premium Padel. Puedes consultar la pagina de inicio, iniciar sesion o crear una cuenta.";
    }

    if (accesoAdmin) {
        accesoAdmin.hidden = true;
    }

    mostrarEnlacesSinSesion(true);
}

function mostrarEnlacesSinSesion(mostrar) {
    const enlacesSinSesion = document.querySelectorAll("main .enlace-sin-sesion");

    enlacesSinSesion.forEach(enlace => {
        enlace.hidden = !mostrar;
    });
}
