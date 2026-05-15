const API_BASE = "http://localhost:8080";
let usuariosGlobal = [];

document.addEventListener("DOMContentLoaded", async () => {
    const esAdmin = await comprobarAdmin();
    if (!esAdmin) return;

    document.getElementById("btnFiltrarUsuarios")
        .addEventListener("click", aplicarFiltrosUsuarios);

    document.getElementById("btnLimpiarFiltrosUsuarios")
        .addEventListener("click", limpiarFiltrosUsuarios);

    await cargarUsuarios();
});

async function comprobarAdmin() {
    const respuesta = await fetch(`${API_BASE}/pistaPadel/auth/me`, {
        credentials: "include"
    });

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
}

async function cargarUsuarios() {
    const tabla = document.getElementById("tablaUsuarios");
    const mensaje = document.getElementById("mensajeUsuarios");

    tabla.innerHTML = "";
    mensaje.textContent = "Cargando usuarios...";

    try {
        const respuesta = await fetch(`${API_BASE}/pistaPadel/users`, {
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

        if (!respuesta.ok) {
            mensaje.textContent = `Error cargando usuarios (${respuesta.status})`;
            return;
        }

        usuariosGlobal = await respuesta.json();
        renderizarUsuarios(usuariosGlobal);

    } catch (error) {
        console.error("Error cargando usuarios:", error);
        mensaje.textContent = "Error de conexión con el servidor.";
    }
}

function aplicarFiltrosUsuarios() {
    const rol = document.getElementById("filtroRol").value;
    const activo = document.getElementById("filtroActivo").value;

    let usuariosFiltrados = [...usuariosGlobal];

    if (rol !== "") {
        usuariosFiltrados = usuariosFiltrados.filter(usuario => usuario.rol === rol);
    }

    if (activo !== "") {
        const valorActivo = activo === "true";
        usuariosFiltrados = usuariosFiltrados.filter(usuario => usuario.activo === valorActivo);
    }

    renderizarUsuarios(usuariosFiltrados);
}

function limpiarFiltrosUsuarios() {
    document.getElementById("filtroRol").value = "";
    document.getElementById("filtroActivo").value = "";
    renderizarUsuarios(usuariosGlobal);
}

function renderizarUsuarios(usuarios) {
    const tabla = document.getElementById("tablaUsuarios");
    const mensaje = document.getElementById("mensajeUsuarios");

    if (!usuarios || usuarios.length === 0) {
        mensaje.textContent = "No hay usuarios con esos filtros.";
        tabla.innerHTML = "";
        return;
    }

    mensaje.textContent = `${usuarios.length} usuario(s) encontrado(s).`;

    tabla.innerHTML = usuarios.map(usuario => {
        const id = usuario.idUsuario ?? "";
        const nombreCompleto = `${usuario.nombre ?? ""} ${usuario.apellidos ?? ""}`.trim();
        const email = usuario.email ?? "";
        const telefono = usuario.telefono ?? "";
        const rol = usuario.rol ?? "";
        const activo = usuario.activo === true;
        const fechaRegistro = usuario.fechaRegistro
            ? usuario.fechaRegistro.substring(0, 10)
            : "";

        const claseRol = rol === "ADMIN"
            ? "rol-admin-badge rol-admin-badge-verde"
            : "rol-admin-badge rol-user-badge";

        const claseEstado = activo
            ? "estado-admin estado-activa"
            : "estado-admin estado-inactiva";

        const textoEstado = activo ? "ACTIVO" : "INACTIVO";

        return `
            <tr>
                <td>#${id}</td>
                <td>${nombreCompleto}</td>
                <td>${email}</td>
                <td>${telefono}</td>
                <td><span class="${claseRol}">${rol}</span></td>
                <td><span class="${claseEstado}">${textoEstado}</span></td>
                <td>${fechaRegistro}</td>
                <td class="acciones-tabla">
                    <button type="button" class="accion-tabla editar" onclick="gestionarUsuario(${id})">
                        Gestionar
                    </button>
                </td>
            </tr>
        `;
    }).join("");
}

function gestionarUsuario(idUsuario) {
    window.location.href = `adminUsuarioDetalle.html?id=${idUsuario}`;
}