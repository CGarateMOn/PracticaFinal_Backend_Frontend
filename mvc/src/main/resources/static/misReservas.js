window._reservas = [];

async function cargarReservas() {
    const respuesta = await fetch("http://localhost:8080/pistaPadel/reservations", {
        credentials: "include"
    });

    if (respuesta.status === 401) {
        window.location.href = "logIn.html";
        return;
    }

    if (!respuesta.ok) {
        console.error("Error cargando reservas:", respuesta.status);
        return;
    }

    const reservas = await respuesta.json();
    window._reservas = reservas;
    renderizarReservas("todas");
}

function renderizarReservas(filtro) {
    if (!Array.isArray(window._reservas)) {
        console.warn("window._reservas aún no está disponible");
        return;
    }

    const hoy = new Date().toISOString().split("T")[0];
    const contenedor = document.getElementById("listaReservas");
    contenedor.innerHTML = "";

    const reservasFiltradas = window._reservas.filter(r => {
        if (filtro === "anteriores") return r.fechaReserva < hoy;
        if (filtro === "actuales")   return r.fechaReserva >= hoy;
        return true;
    });

    if (reservasFiltradas.length === 0) {
        contenedor.innerHTML = "<p>No tienes reservas en este filtro.</p>";
        return;
    }

    reservasFiltradas.forEach(r => {
        const card = document.createElement("article");
        card.className = `card-reserva ${r.fechaReserva >= hoy ? "tipo-actual" : "tipo-anterior"}`;

        card.innerHTML = `
            <div class="reserva-info">
                <h3>Reserva #${r.idReserva}</h3>
                <span class="fecha">${r.fechaReserva} ${r.horaInicio}</span>
                <span class="pista">${r.pista.nombre}</span>
            </div>
            <div class="reserva-estado">
                <span class="estado ${r.estado === "ACTIVA" ? "finalizado" : "cancelado"}">${r.estado}</span>
                <button type="button" class="btn-cancelar" data-id="${r.idReserva}">Cancelar Reserva</button>
            </div>
        `;
        contenedor.appendChild(card);
    });
}


async function cancelarReserva(idReserva) {
    const confirmar = confirm("¿Seguro que quieres cancelar esta reserva?");
    if (!confirmar) return;

    const respuesta = await fetch(`http://localhost:8080/pistaPadel/reservations/${idReserva}`, {
        method: "DELETE",
        credentials: "include"
    });

    if (respuesta.status === 401) {
        window.location.href = "logIn.html";
        return;
    }

    if (!respuesta.ok) {
        const textoError = await respuesta.text();
        console.error("Error al cancelar:", respuesta.status, textoError);
        alert("No se pudo cancelar la reserva");
        return;
    }

    await cargarReservas();
}

document.getElementById("filtro-todas").addEventListener("change", () => renderizarReservas("todas"));
document.getElementById("filtro-anteriores").addEventListener("change", () => renderizarReservas("anteriores"));
document.getElementById("filtro-actuales").addEventListener("change", () => renderizarReservas("actuales"));


document.getElementById("listaReservas").addEventListener("click", (event) => {
    const boton = event.target.closest(".btn-cancelar");
    if (!boton) return;

    const idReserva = boton.dataset.id;
    cancelarReserva(idReserva);
});

cargarReservas();