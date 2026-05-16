const API_BASE = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", async () => {
    const userId = obtenerUserIdDeUrl();

    if (!userId) {
        alert("No se ha indicado el usuario");
        window.location.href = "adminUsuarios.html";
        return;
    }

    const esAdmin = await comprobarAdmin();
    if (!esAdmin) return;

    const formulario = document.querySelector(".admin-formulario-usuario");
    formulario.addEventListener("submit", guardarUsuario);

    await cargarDatosUsuario(userId);
});

function obtenerUserIdDeUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

async function comprobarAdmin() {
    try {
        const respuesta = await fetch(`${API_BASE}/pistaPadel/auth/me`, {
            credentials: "include"
        });

        if (respuesta.status === 401) {
            window.location.href = "logIn.html";
            return false;
        }

        if (!respuesta.ok) {
            window.location.href = "logIn.html";
            return false;
        }

        const perfil = await respuesta.json();

        if (perfil.rol !== "ADMIN") {
            window.location.href = "index.html";
            return false;
        }

        return true;

    } catch (error) {
        console.error("Error comprobando admin:", error);
        alert("No se pudo comprobar la sesión");
        window.location.href = "logIn.html";
        return false;
    }
}

async function cargarDatosUsuario(userId) {
    try {
        const respuesta = await fetch(`${API_BASE}/pistaPadel/users/${userId}`, {
            method: "GET",
            credentials: "include"
        });
        if (respuesta.status === 401) {
            window.location.href = "logIn.html";
            return;
        }
        if (respuesta.status === 403) {
            window.location.href = "index.html";
            return;
        }
        if (respuesta.status === 404) {
            alert("Usuario no encontrado");
            window.location.href = "adminUsuarios.html";
            return;
        }
        if (!respuesta.ok) {
            alert("Error al cargar el usuario. Código: " + respuesta.status);
            return;
        }

        const usuario = await respuesta.json();
        rellenarFormulario(usuario);

    } catch (error) {
        console.error("Error al cargar el usuario:", error);
        alert("No se pudieron cargar los datos del usuario");
    }
}

function rellenarFormulario(usuario) {
    document.getElementById("idUsuario").value = usuario.idUsuario ?? "";
    document.getElementById("nombre").value = usuario.nombre ?? "";
    document.getElementById("apellidos").value = usuario.apellidos ?? "";
    document.getElementById("email").value = usuario.email ?? "";
    document.getElementById("telefono").value = usuario.telefono ?? "";

    document.getElementById("activo").value = usuario.activo ? "true" : "false";
    document.getElementById("rol").value = usuario.rol ?? "USER";

    if (usuario.fechaRegistro) {
        document.getElementById("fechaRegistro").value = usuario.fechaRegistro.split("T")[0];
    }
}

async function guardarUsuario(evento) {
    evento.preventDefault();

    const userId = obtenerUserIdDeUrl();
    const formulario = document.querySelector(".admin-formulario-usuario");
    const botonGuardar = formulario.querySelector(".btn-guardar-pista");
    const textoOriginal = botonGuardar.innerText;

    const datosActualizar = {
        nombre: document.getElementById("nombre").value.trim(),
        apellidos: document.getElementById("apellidos").value.trim(),
        email: document.getElementById("email").value.trim(),
        telefono: document.getElementById("telefono").value.trim(),
        rol: document.getElementById("rol").value,
        activo: document.getElementById("activo").value === "true"
    };

    if (!datosActualizar.nombre || !datosActualizar.apellidos || !datosActualizar.email) {
        alert("Nombre, apellidos y email son obligatorios");
        return;
    }

    botonGuardar.disabled = true;
    botonGuardar.innerText = "Guardando...";

    try {
        const respuesta = await fetch(`${API_BASE}/pistaPadel/users/${userId}`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(datosActualizar),
            credentials: "include"
        });

        if (respuesta.status === 401) {
            window.location.href = "logIn.html";
            return;
        }

        if (respuesta.status === 403) {
            window.location.href = "index.html";
            return;
        }

        if (!respuesta.ok) {
            alert("No se pudo actualizar el usuario. Código: " + respuesta.status);
            return;
        }

        alert("Usuario actualizado correctamente");
        window.location.href = "adminUsuarios.html";

    } catch (error) {
        console.error("Error al actualizar:", error);
        alert("Hubo un error al guardar los cambios");

    } finally {
        botonGuardar.disabled = false;
        botonGuardar.innerText = textoOriginal;
    }
}