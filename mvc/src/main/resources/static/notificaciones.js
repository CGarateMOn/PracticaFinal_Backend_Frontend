document.addEventListener("DOMContentLoaded", cargarNotificaciones);

async function cargarNotificaciones(){
    const contenedor = document.querySelector('.lista-reservas');
    contenedor.innerHTML='<p class="subtitulo"> Buscando notificaciones...</p>';
    try{
        const respuesta = await fetch('http://localhost:8080/pistaPadel/reservations',{
            method:'GET',
            credentials:'include'
        });

        if(respuesta.status === 401){
            mostrarAlertaLogin();
            return; // Salimos para no ejecutar el resto
        }
        if(!respuesta.ok){
            throw new Error(`Error del servidor: ${respuesta.status}`);
        }
        const reservas = await respuesta.json();
        renderizarNotificaciones(reservas, contenedor);
    } catch (error){
        console.error()('Error al cargar notificaciones', error);
        contenedor.innerHTML='<p class="subtitulo">No se han podido cargar las notificaicones.</p>';
    }
}

// Función que oculta las notificaciones y muestra el aviso de login
function mostrarAlertaLogin() {
    const contenedorPrincipal = document.querySelector("main");
    contenedorPrincipal.innerHTML = `
        <div class="intro" style="text-align: center; padding: 50px 20px;">
            <h2>Acceso Restringido</h2>
            <p>Necesitas iniciar sesión para ver tus notificaciones.</p>
            <div class="espacio"></div>
            <a href="logIn.html" class="btn-login" style="text-decoration: none; display: inline-block; padding: 10px 30px;">Ir a Iniciar Sesión</a>
        </div>
    `;
}

function renderizarNotificaciones(reservas, contenedor){

}