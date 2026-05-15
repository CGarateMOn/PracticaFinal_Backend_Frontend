const API_BASE = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", async () => {

    const esAdmin = await comprobarAdmin();
    if (!esAdmin) return;

    document.getElementById("btnFiltrarPistas")
        .addEventListener("click", cargarPistas);

    document.getElementById("btnLimpiarFiltroPistas")
        .addEventListener("click", limpiarFiltros);

    document.getElementById("btnNuevaPista")
        .addEventListener("click", () => {
            window.location.href = "adminFormularioPista.html";
        });

    await cargarPistas();
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

function limpiarFiltros() {
    document.getElementById("filtroEstado").value = "";
    cargarPistas();
}

async function cargarPistas() {

    const tabla = document.getElementById("tablaPistas");
    const mensaje = document.getElementById("mensajePistas");

    tabla.innerHTML = "";
    mensaje.textContent = "Cargando pistas...";

    try {

        const estado = document.getElementById("filtroEstado").value;

        let url = "/pistaPadel/courts";

        if (estado !== "") {
            url += `?activa=${estado}`;
        }

        const respuesta = await fetch(url, {
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
            mensaje.textContent = `Error cargando pistas (${respuesta.status})`;
            return;
        }

        const pistas = await respuesta.json();

        renderizarPistas(pistas);

    } catch (error) {

        console.error(error);
        mensaje.textContent = "Error de conexión con el servidor.";
    }
}

function renderizarPistas(pistas) {

    const tabla = document.getElementById("tablaPistas");
    const mensaje = document.getElementById("mensajePistas");

    if (!pistas || pistas.length === 0) {
        mensaje.textContent = "No hay pistas.";
        tabla.innerHTML = "";
        return;
    }

    mensaje.textContent = `${pistas.length} pista(s) encontrada(s).`;

    tabla.innerHTML = pistas.map(pista => {

        const id = pista.idPista ?? "";
        const nombre = pista.nombre ?? "";
        const ubicacion = pista.ubicacion ?? "";
        const precio = pista.precioHora ?? "";
        const activa = pista.activa === true;

        const textoEstado = activa ? "Activa" : "Inactiva";

        const claseEstado = activa
            ? "estado-admin estado-activa"
            : "estado-admin estado-cancelada";

        return `
            <tr>
                <td>#${id}</td>
                <td>${nombre}</td>
                <td>${ubicacion}</td>
                <td>${precio} €</td>
                <td>
                    <span class="${claseEstado}">
                        ${textoEstado}
                    </span>
                </td>

                <td>
                    <button
                        class="btn-accion btn-editar"
                        onclick="editarPista(${id})"
                    >
                        Editar
                    </button>
                    <button
                        class="btn-accion btn-eliminar"
                        onclick="eliminarPista(${id})"
                    >
                        Eliminar
                    </button>
                </td>
            </tr>
        `;
    }).join("");
}

function editarPista(idPista) {
    window.location.href = `adminFormularioPista.html?id=${idPista}`;
}

async function eliminarPista(idPista) {
    const confirmar = confirm("¿Seguro que quieres eliminar esta pista?");
    if (!confirmar) return;

    try {
        const respuesta = await fetch(`/pistaPadel/courts/${idPista}`, {
            method: "DELETE",
            credentials: "include"
        });
        if (!respuesta.ok) {
            alert("No se pudo eliminar la pista.");
            return;
        }
        await cargarPistas();

    } catch (error) {
        console.error(error);
        alert("Error de conexión.");
    }
}