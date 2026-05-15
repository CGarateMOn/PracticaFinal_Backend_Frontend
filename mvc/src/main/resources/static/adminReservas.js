const API_BASE = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", async () => {
    const esAdmin = await comprobarAdmin();
    if (!esAdmin) return;

    await cargarFiltros();

    document.getElementById("btnFiltrarReservas").addEventListener("click", cargarReservas);

    document.getElementById("btnLimpiarFiltros").addEventListener("click", () => {
        document.getElementById("inputFecha").value = "";
        document.getElementById("inputPista").value = "";
        document.getElementById("inputUsuario").value = "";
        cargarReservas();
    });

    await cargarReservas();
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

async function cargarReservas() {
    const mensaje = document.getElementById("mensajeReservas");
    const tabla = document.getElementById("tablaReservas");

    mensaje.textContent = "Cargando reservas...";
    tabla.innerHTML = "";

    const date = document.getElementById("inputFecha").value;
    const courtId = document.getElementById("inputPista").value;
    const userId = document.getElementById("inputUsuario").value;

    const params = new URLSearchParams();

    if (date) params.set("date", date);
    if (courtId) params.set("courtId", courtId);
    if (userId) params.set("userId", userId);

    try {
        const respuesta = await fetch(`${API_BASE}/pistaPadel/admin/reservations?${params.toString()}`, {
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
            mensaje.textContent = `Error al cargar reservas. Código: ${respuesta.status}`;
            return;
        }

        const reservas = await respuesta.json();
        renderizarReservas(reservas);

    } catch (error) {
        console.error("Error cargando reservas admin:", error);
        mensaje.textContent = "No se pudo conectar con el servidor.";
    }
}

function renderizarReservas(reservas) {
    const mensaje = document.getElementById("mensajeReservas");
    const tabla = document.getElementById("tablaReservas");

    if (!reservas || reservas.length === 0) {
        mensaje.textContent = "No hay reservas con esos filtros.";
        tabla.innerHTML = "";
        return;
    }

    mensaje.textContent = `${reservas.length} reserva(s) encontrada(s).`;

    tabla.innerHTML = reservas.map(reserva => {
        const idReserva = reserva.idReserva ?? "";
        const usuario = reserva.usuario
            ? `${reserva.usuario.nombre ?? ""} ${reserva.usuario.apellidos ?? ""}`.trim()
            : "Sin usuario";

        const pista = reserva.pista
            ? reserva.pista.nombre
            : "Sin pista";

        const fecha = reserva.fechaReserva ?? "";
        const hora = reserva.horaInicio ? reserva.horaInicio.slice(0, 5) : "";
        const duracion = reserva.duracionMinutos ? `${reserva.duracionMinutos} min` : "";
        const estado = reserva.estado ?? "";

        const claseEstado = estado === "ACTIVA"
            ? "estado-admin estado-activa"
            : "estado-admin estado-inactiva";

        return `
            <tr>
                <td>#${idReserva}</td>
                <td>${usuario}</td>
                <td>${pista}</td>
                <td>${fecha}</td>
                <td>${hora}</td>
                <td>${duracion}</td>
                <td><span class="${claseEstado}">${estado}</span></td>
            </tr>
        `;
    }).join("");
}
async function cargarFiltros() {
    try {
        const [respPistas, respUsuarios] = await Promise.all([
            fetch(`${API_BASE}/pistaPadel/courts`, { credentials: "include" }),
            fetch(`${API_BASE}/pistaPadel/users`, { credentials: "include" })
        ]);

        if (respPistas.ok) {
            const pistas = await respPistas.json();
            const selectPista = document.getElementById("inputPista");
            pistas.forEach(pista => {
                const option = document.createElement("option");
                option.value = pista.idPista;
                option.textContent = pista.nombre;
                selectPista.appendChild(option);
            });
        }

        if (respUsuarios.ok) {
            const usuarios = await respUsuarios.json();
            const selectUsuario = document.getElementById("inputUsuario");
            usuarios.forEach(usuario => {
                const option = document.createElement("option");
                option.value = usuario.idUsuario;
                option.textContent = `${usuario.nombre} ${usuario.apellidos}`;
                selectUsuario.appendChild(option);
            });
        }
    } catch (error) {
        console.error("Error cargando filtros:", error);
    }
}