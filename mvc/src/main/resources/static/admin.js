document.addEventListener("DOMContentLoaded", () =>{
    cargarDashboard();
});

async function cargarDashboard(){
    try{
        const respuesta = await fetch('http://localhost:8080/pistaPadel/users',{
            method: 'GET',
            credentials: 'include'
        });

        if(respuesta.status === 401 || respuesta.status === 403 || respuesta.status === 500){
            alert("Acceso denegado");
            window.location.href = "logIn.html";
            return;
        }

        const tarjetas = document.querySelectorAll('.dato-admin');

        if(respuesta.ok){
        const  usuarios = await  respuesta.json();
        tarjetas[0].innerText = usuarios.length;
        }

        const r = await  fetch('http://localhost:8080/pistaPadel/courts', {
            method: 'GET',
            credentials: 'include'
        });

        if(r.ok){
            const pistas = await  r.json();

            let pistaActivas = 0;
            for(let i = 0; i<pistas.length; i++){
                if(pistas[i].activa === true){
                    pistaActivas++;
                }
            }
            tarjetas[1].innerText = pistaActivas;
        }

        const res = await fetch('http://localhost:8080/pistaPadel/reservations', {
            method: 'GET',
            credentials: 'include'
        });

        if(res.ok){
            const pistas = await  res.json();
        }
    }catch (error){
        console.error("Error al cargar el panel:", error);
    }

    function  actualizTablas(reservas){
        const body = document.querySelector('.tabla-admin tbody');
        if(!body){
            return;
        }

        body.innerHTML = '';

        let limite = 3;
        if(reservas.length < 3){
            limite = reservas.length;
        }

        for(let i = reservas.length -1; i>= reservas.length -limite; i--){
            let r = reservas[i];
            let estado = '';
            if(r.estado === 'ACTIVA'){
                estado = 'estado-activa';
            }else{
                estado = 'estado-cancelado';
            }

            const tr = document.createElement('tr');
            tr.innerHTML = `
            <td>${r.idReserva}</td>
<td> Usuario ${r.usuario ?  r.usuario.idUsuario : 'N/A'}</td>
<td>Pista ${r.pista ? r.pista.id_pista: r.idPista}</td>
<td>${r.fechaReserva}</td>
<td>${r.horaInicio}</td>
<td><span class = "estado-admin" ${estado}"> ${r.estado}</span></td>`;
            body.appendChild(tr);
        }
    }
}