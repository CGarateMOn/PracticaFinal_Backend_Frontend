document.addEventListener("DOMContentLoaded", cargarNotificaciones);

async function cargarNotificaciones(){
    const contenedor = document.querySelector('.lista-reservas');
    contenedor.innerHTML='<p class="subtitulo"> Buscando notificaciones...</p>';
    try{
        const respuesta = await fetch('http://localhost:8080/pistaPadel/reservations',{
            method:'GET',
            credentials:'include'
        });
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

function renderizarNotificaciones(reservas, contenedor){

}