const inputFecha = document.getElementById("reserva");
const contenedorPistas=document.getElementById("contenedor-pistas");

inputFecha.addEventListener("change", cargarDisponibilidad);

//vamos definiendo las funciones necesarias para el funcionamiento de la página web
async function cargarDisponibilidad(){

    const fecha=inputFecha.value;

    if(!fecha){
        return;
    }

    contenedorPistas.innerHTML='<p class="subtitulo">Cargando disponibilidad...</p>';

    try{
        //ahora le pedimos al servidor que nos enseñe las pistas disponibles
        const respuesta = await fetch(`http://localhost:8080/pistaPadel/availability?date=${fecha}`,{
            method: "GET",
            credentials: "include"
        });

        if(!respuesta.ok){
            throw new Error(`Error del servidor: ${respuesta.status}`);
        }

        const disponiblilidades = await respuesta.json();
        renderizarPistas(disponiblilidades);
    
    } catch(error){
        console.error('Error al cargar disponibilidad:', error);
        contenedorPistas.innerHTML = '<p class="subtitulo">Error al cargar la disponibilidad. Inténtalo de nuevo.</p>';
    }
}

function renderizarPistas(disponiblilidades){
    if(disponiblilidades.length===0){
        contenedorPistas.innerHTML='<p class="subtitulo">No hay pistas disponibles para esta fecha.</p>';
        return;
    }

    contenedorPistas.innerHTML="";

    disponiblilidades.forEach(disponibilidad=>{
        //vamos creado los elementos del html
        const card=document.createElement('article');
        card.classList.add('card');

        const botonesHoras = generarBotonesHoras(disponibilidad.tramosHorariosDisponibles, disponibilidad.idPista);

        //inner HTML construye el contenido interno de cada carta
        card.innerHTML=`
            <div class="info-pist">
                <h3>Pista ${disponibilidad.idPista}</h3>
            </div>
            <div class="horarios">
                ${botonesHoras}
            </div>
        `;

        //añadimos la carta finalizada al contenedor DOM
        contenedorPistas.appendChild(card);
    });
}

function generarBotonesHoras(tramos, idPista){
    if(!tramos || tramos.length===0){
        return '<p class="subtitulo">Sin horarios disponibles</p>';
    }

    // .map() recorre el array y transforma cada tramo en un string HTML
    // .join('') une todos esos strings en uno solo (sin separadores)
    return tramos.map(tramo=>{
        const horaLegible = tramo.inicio.slice(0, 5);

        return `
            <button 
                class="hora-disponible" 
                data-pista="${idPista}" 
                data-hora="${tramo.inicio}"
                data-fin="${tramo.fin}"
                onclick="seleccionarHora(this)">
                ${horaLegible}
            </button>
        `;
    }).join('');
}

 async function seleccionarHora(boton){
    // Leemos los datos que guardamos en los atributos data-* del botón
    const idPista = boton.dataset.pista;   // ej: "3"
    const hora    = boton.dataset.hora;    // ej: "09:00:00"
    const horaFin = boton.dataset.fin;     // ej: "10:30:00"
    const fecha   = inputFecha.value;      // ej: "2026-04-24"

    // Confirmamos con el usuario antes de redirigir
    const confirmar = confirm(
        `¿Reservar Pista ${idPista} el ${fecha} de ${hora.slice(0,5)} a ${horaFin.slice(0,5)}?`
    );

    const textoOriginal = boton.innerText;
    boton.disabled = true;
    boton.innerText = "Reservando";

    if(!confirmar){
        return;
    }

    const datosReserva = {
        idPista: parseInt(idPista),
        fecha: fecha,
        inicio: hora,
        fin: horaFin
    }

    try{
        const respuesta = await fetch(`http://localhost:8080/pistaPadel/reservations`,{
            method: 'POST',
            headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
            body: JSON.stringify(datosReserva),
            credentials: 'include'
        });

        if(respuesta.status === 401 || respuesta.status === 400 || respuesta.status === 500){
            alert("Tu sesión ha caducado. Por favor, inicia sesión.");
            window.location.href = 'logIn.html';
            return;
        }

        if(!respuesta.ok){
            throw  new Error(`Error del servidor : ${respuesta.status}`);
        }

        alert("¡Reserva realizada con éxito");
        window.location.href = 'misReservas.html';
    } catch (error){
        console.error("Error al realizar la reserva: ", error);
        alert("Intentalo de nuevo.");
        boton.disabled = false;
        boton.innerText = textoOriginal;
    }
}