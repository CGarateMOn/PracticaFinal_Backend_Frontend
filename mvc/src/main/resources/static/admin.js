const API_BASE = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", async () => {
    const esAdmin = await comprobarAdmin();
    if (!esAdmin) return;

    await cargarDashboard();
});

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
        console.error("Error comprobando sesión admin:", error);
        alert("No se pudo comprobar la sesión");
        window.location.href = "logIn.html";
        return false;
    }
}

async function cargarDashboard() {
    const tarjetas = document.querySelectorAll(".dato-admin");

    try {
        const [respUsuarios, respPistas, respReservas] = await Promise.all([
            fetch(`${API_BASE}/pistaPadel/users`, {
                method: "GET",
                credentials: "include"
            }),
            fetch(`${API_BASE}/pistaPadel/courts`, {
                method: "GET",
                credentials: "include"
            }),
            fetch(`${API_BASE}/pistaPadel/admin/reservations`, {
                method: "GET",
                credentials: "include"
            })
        ]);

        if (respUsuarios.status === 401 || respPistas.status === 401 || respReservas.status === 401) {
            window.location.href = "logIn.html";
            return;
        }

        if (respUsuarios.status === 403 || respPistas.status === 403 || respReservas.status === 403) {
            window.location.href = "index.html";
            return;
        }

        if (!respUsuarios.ok || !respPistas.ok || !respReservas.ok) {
            throw new Error("Error cargando datos del panel");
        }

        const usuarios = await respUsuarios.json();
        const pistas = await respPistas.json();
        const reservas = await respReservas.json();

        actualizarTarjetas(tarjetas, usuarios, pistas, reservas);
        actualizarActividadReciente(reservas);
        actualizarTablaReservas(reservas);

    } catch (error) {
        console.error("Error al cargar el panel:", error);
        alert("No se pudo cargar el panel de administración");
    }
}

function actualizarTarjetas(tarjetas, usuarios, pistas, reservas) {
    const hoy = new Date().toISOString().substring(0, 10);

    const usuariosRegistrados = usuarios.length;

    const pistasActivas = pistas.filter(pista => pista.activa === true).length;

    const reservasHoy = reservas.filter(reserva =>
        reserva.fechaReserva === hoy && reserva.estado === "ACTIVA"
    ).length;

    const cancelacionesRecientes = reservas.filter(reserva =>
        reserva.estado === "CANCELADA"
    ).length;

    if (tarjetas[0]) tarjetas[0].textContent = usuariosRegistrados;
    if (tarjetas[1]) tarjetas[1].textContent = pistasActivas;
    if (tarjetas[2]) tarjetas[2].textContent = reservasHoy;
    if (tarjetas[3]) tarjetas[3].textContent = cancelacionesRecientes;
}

function actualizarActividadReciente(reservas) {
    const contenedor = document.querySelector(".admin-actividad");

    if (!contenedor) return;

    if (!reservas || reservas.length === 0) {
        contenedor.innerHTML = "<p>No hay actividad reciente.</p>";
        return;
    }

    const reservasOrdenadas = [...reservas].sort((a, b) => {
        const fechaA = `${a.fechaReserva ?? ""}T${a.horaInicio ?? "00:00"}`;
        const fechaB = `${b.fechaReserva ?? ""}T${b.horaInicio ?? "00:00"}`;
        return fechaB.localeCompare(fechaA);
    });

    const ultimas = reservasOrdenadas.slice(0, 4);

    contenedor.innerHTML = ultimas.map(reserva => {
        const id = reserva.idReserva ?? "";
        const usuario = reserva.usuario
            ? `${reserva.usuario.nombre ?? ""} ${reserva.usuario.apellidos ?? ""}`.trim()
            : "Usuario no disponible";

        const pista = reserva.pista
            ? reserva.pista.nombre
            : "Pista no disponible";

        const fecha = reserva.fechaReserva ?? "";
        const hora = reserva.horaInicio ? reserva.horaInicio.slice(0, 5) : "";
        const estado = reserva.estado ?? "";

        return `
            <p>
                <strong>Reserva #${id}</strong> - ${usuario}, ${pista}, ${fecha} ${hora} (${estado})
            </p>
        `;
    }).join("");
}

function actualizarTablaReservas(reservas) {
    const cuerpoTabla = document.querySelector(".tabla-admin tbody");

    if (!cuerpoTabla) return;

    cuerpoTabla.innerHTML = "";

    if (!reservas || reservas.length === 0) {
        cuerpoTabla.innerHTML = `
            <tr>
                <td colspan="6">No hay reservas registradas.</td>
            </tr>
        `;
        return;
    }

    const reservasOrdenadas = [...reservas].sort((a, b) => {
        const fechaA = `${a.fechaReserva ?? ""}T${a.horaInicio ?? "00:00"}`;
        const fechaB = `${b.fechaReserva ?? ""}T${b.horaInicio ?? "00:00"}`;
        return fechaB.localeCompare(fechaA);
    });

    const ultimasReservas = reservasOrdenadas.slice(0, 3);

    cuerpoTabla.innerHTML = ultimasReservas.map(reserva => {
        const id = reserva.idReserva ?? "";

        const usuario = reserva.usuario
            ? `${reserva.usuario.nombre ?? ""} ${reserva.usuario.apellidos ?? ""}`.trim()
            : "Sin usuario";

        const pista = reserva.pista
            ? reserva.pista.nombre
            : "Sin pista";

        const fecha = reserva.fechaReserva ?? "";
        const hora = reserva.horaInicio ? reserva.horaInicio.slice(0, 5) : "";
        const estado = reserva.estado ?? "";

        const claseEstado = estado === "ACTIVA"
            ? "estado-admin estado-activa"
            : "estado-admin estado-cancelada";

        return `
            <tr>
                <td>#${id}</td>
                <td>${usuario}</td>
                <td>${pista}</td>
                <td>${fecha}</td>
                <td>${hora}</td>
                <td><span class="${claseEstado}">${estado}</span></td>
            </tr>
        `;
    }).join("");
}