//para este tipo siempre esperamos a que el dom este listo
document.addEventListener("DOMContentLoaded", async()=>{
    //hay que optener la cookie de admin
    const respuesta = await fetch("/pistaPadel/auth/me", {
        credentials: "include"
    });

    if(!respuesta.ok){
        window.location.href="login.html";
        return;
    }

    //ahora compruebo si es admin
    const perfil = await respuesta.json();
    if(perfil.rol!="ADMIN"){
        window.location.href="index.html";
        return;
    }

    await cargarReservas();
});

async function cargarReservas(){
    //leemos los valores que introduce el ususario en el html
    const date=document.getElementById("inputFecha").value;
    const courtId=document.getElementById("inputPista").value;
    const userId=document.getElementById("inputUsuario").value;

    const params = new URLSearchParams();
    if(date) params.set("date", date);
    if(courtId) params.set("courtId", courtId);
    if(userId) params.set("userId", userId);

    const res= await fetch(`/pistaPadel/admin/reservations?${params}`, {
        credentials: "include"
    });

    if(!res.ok){
        mostrarError("Error al cargar reservas: " + res.status);
        return;
    }

    const reservas=await res.json();
    renderizarTabla(reservas);
}

function renderizarTabla(reservas){
    const tbody = document.getElementById("tablaReservas");
    tbody.innerHTML = reservas.map(r=>`
        <tr>
            <td>${r.idReserva}</td>
            <td>${r.usuario?.nombre ?? r.idUsuario}</td>
            <td>${r.pista?.nombre ?? r.idPista}</td>
            <td>${r.fechaReserva}</td>
            <td>${r.horaInicio}</td>
            <td>${r.duracionMinutos} min</td>
            <td>${r.estado}</td>
            <td><button onclick="cancelar(${r.idReserva})">Cancelar</button></td>
        </tr>
    `).join("");
}