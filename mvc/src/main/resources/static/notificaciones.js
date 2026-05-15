document.addEventListener("DOMContentLoaded", cargarNotificaciones);

async function cargarNotificaciones(){
    const contenedor = document.querySelector('.lista-reservas');
    contenedor.innerHTML = '<p class="subtitulo">Buscando notificaciones...</p>';

    try {
        const respuesta = await fetch('/pistaPadel/reservations', {
            method: 'GET',
            credentials: 'include'
        });

        if (respuesta.status === 400 || respuesta.status === 401) {
            mostrarAlertaLogin();
            return; // Salimos para no ejecutar el resto
        }

        if (!respuesta.ok) {
            throw new Error(`Error del servidor: ${respuesta.status}`);
        }

        const reservas = await respuesta.json();
        renderizarNotificaciones(reservas, contenedor);

    } catch (error) {
        // ¡CORREGIDO! Ya no hay paréntesis vacíos () rompiendo el código
        console.error('Error al cargar notificaciones:', error);
        mostrarAlertaLogin();
    }
}

// Función que oculta las notificaciones y muestra el aviso de login
function mostrarAlertaLogin() {
    const contenedorLista = document.querySelector(".lista-reservas");
    // Inyectamos el aviso de login manteniendo el diseño limpio
    contenedorLista.innerHTML = `
        <div style="text-align: center; padding: 50px 20px; background-color: #f8f9fa; border-radius: 10px; border: 1px solid #ddd; margin-top: 20px;">
            <h3 style="margin-bottom: 15px; color: #333;">Acceso Restringido</h3>
            <p style="margin-bottom: 25px; color: #666;">Necesitas iniciar sesión para ver tus notificaciones.</p>
            <a href="logIn.html" class="btn-login" style="text-decoration: none; display: inline-block; padding: 10px 30px;">Ir a Iniciar Sesión</a>
        </div>
    `;
}

function renderizarNotificaciones(reservas, contenedor) {
    // 1. Limpiamos el texto de "Buscando notificaciones..."
    contenedor.innerHTML = '';

    // 2. Comprobamos si el usuario no tiene ninguna reserva
    if (!reservas || reservas.length === 0) {
        contenedor.innerHTML = `
            <div style="text-align: center; padding: 40px;">
                <p class="subtitulo">No tienes notificaciones recientes ni reservas activas.</p>
                <a href="reservas.html" class="btn-login" style="text-decoration: none; display: inline-block; margin-top: 15px; padding: 10px 20px;">Reservar una pista</a>
            </div>
        `;
        return;
    }

    // 3. Recorremos el array de reservas y generamos el HTML para cada una
    const htmlTarjetas = reservas.map(reserva => {
        // Formateamos la hora para quitarle los segundos (de "10:30:00" a "10:30")
        const horaFormateada = reserva.horaInicio.slice(0, 5);

        // Decidimos los colores según el estado (aprovechando las clases que vi en tu HTML)
        const claseEstado = reserva.estado === 'ACTIVA' ? 'estado pendiente' : 'estado finalizado';
        const textoEstado = reserva.estado === 'ACTIVA' ? 'Confirmada' : 'Cancelada';

        return `
            <article class="card-notificacion">
                <div class="notificacion-info">
                    <h3>Recordatorio de Reserva #${reserva.idReserva}</h3>
                    <span class="fecha">${reserva.fechaReserva} | ${horaFormateada}</span>
                    <span class="pista">${reserva.pista.nombre} - ${reserva.pista.ubicacion}</span>
                </div>
                <div class="notificacion-estado">
                    <span class="${claseEstado}">${textoEstado}</span>
                    ${reserva.estado === 'ACTIVA' ?
            `<button type="button" class="btn-cancelar" onclick="window.location.href='misReservas.html'">Gestionar</button>`
            : ''
        }
                </div>
            </article>
        `;
    }).join(''); // Juntamos todas las tarjetas en un solo texto HTML

    // 4. Inyectamos todas las tarjetas generadas en el contenedor principal
    contenedor.innerHTML = htmlTarjetas;
}