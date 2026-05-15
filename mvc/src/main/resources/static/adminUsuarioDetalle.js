const urlParams = new URLSearchParams(window.location.search);
const userId = urlParams.get('id');

const formulario = document.querySelector('.admin-formulario-usuario');

document.addEventListener("DOMContentLoaded", () => {
    if(!userId){
        alert("No se ha indicado el usuario");
        window.location.href = "adminUsuarios.html";
        return;
    }
    cargarDatosUsuario();
});

async  function cargarDatosUsuario(){
    try{
        const respuesta = await  fetch(`http://localhost:8080/pistaPadel/users/${userId}`,{
            method: 'GET',
            credentials: 'include'
        });

        if(respuesta.status === 401 || respuesta.status === 403 || respuesta.status === 500){
            alert("No tienes permisos de administrador");
            window.location.href = 'logIn.html';
            return;
        }

        if(!respuesta.ok){
            throw new Error(`Error: ${respuesta.status}`);

        }

        const usuario = await respuesta.json();
        rellenarFormulario(usuario);
    }catch(error){
        console.error("Error al cargar el usuario:", error);
        alert("No se pudieron cargar los datos del usuario");
    }


    function  rellenarFormulario(usuario){
        document.getElementById('idUsuario').value = usuario.idUsuario;
        document.getElementById('nombre').value = usuario.nombre;
        document.getElementById('apellidos').value = usuario.apellidos;
        document.getElementById('email').value = usuario.email;
        document.getElementById('telefono').value = usuario.telefono;

        document.getElementById('activo').value = usuario.activo ? "true" : "false";
        document.getElementById('rol').value = usuario.rol;

        if(usuario.fechaRegistro){
            document.getElementById('fechaRegistro').value = usuario.fechaRegistro.split('T')[0];
        }
    }

    formulario.addEventListener('submit', async(evento) => {
        evento.preventDefault();
        const botonGuardar = formulario.querySelector('.btn-guardar-pista');
        const textoOrignal = botonGuardar.innerText;

        botonGuardar.disabled = true;
        botonGuardar.innerText = "Guardando";

        const datosActualizar ={
            nombre: document.getElementById('nombre').value,
            apellidos: document.getElementById('apellidos').value,
            email: document.getElementById('email').value,
            telefono: document.getElementById('telefono').value,
            rol: document.getElementById('rol').value,
            active: document.getElementById('activo').value
        };

        try {
            const respuesta = await fetch(`http://localhost:8080/pistaPadel/users/${userId}`,{
                method: 'PATCH',
                headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'},
                body: JSON.stringify(datosActualizar),
                credentials: 'include'
            });

            if (respuesta.status === 401 || respuesta.status === 403 || respuesta.status === 500) {
                alert("Sesión inválida");
                window.location.href = 'logIn.html';
                return;
            }
            if (!respuesta.ok) {
                throw new Error(`Error: ${respuesta.status}`);
            }

            alert("Usuario actualizado correctamente.");
            window.location.href = 'adminUsuarios.html';
        }catch(error){
            console.error("Error al actualizar: ", error);
            alert("Hubo un eerror al guardar los cambios");
            botonGuardar.disabled = false;
            botonGuardar.innerText = textoOrignal;
        }

    });
}