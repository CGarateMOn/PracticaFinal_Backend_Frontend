async function cargarPerfil(){
    const respuesta= await fetch("http://localhost:8080/pistaPadel/auth/me", {
        credentials: "include"
    });

    console.log("Status recibido:", respuesta.status);

    if(respuesta.status===401){
        mostrarAlertaLogin();
        return;
    }

    const perfil = await respuesta.json();
    console.log("Perfil recibido:", perfil);

    document.getElementById("saludo").textContent = `Hola, ${perfil.nombre}!!`;
    document.getElementById("tdNombre").textContent=perfil.nombre;
    document.getElementById("tdApellidos").textContent=perfil.apellidos;
    document.getElementById("tdEmail").textContent=perfil.email;
    document.getElementById("tdRol").textContent=perfil.rol;
    if (perfil.telefono !== null && perfil.telefono !== undefined) {
        document.getElementById("tdTelefono").textContent = perfil.telefono;
    } else {
        document.getElementById("tdTelefono").textContent = "No disponible";
    }

}

function mostrarAlertaLogin() {
    const contenedorPrincipal = document.querySelector("main");
    contenedorPrincipal.innerHTML = `
        <div class="intro" style="text-align: center; padding: 50px 20px;">
            <h2>Acceso Restringido</h2>
            <p>Necesitas iniciar sesión para ver tu perfil y gestionar tus datos.</p>
            <div class="espacio"></div>
            <a href="logIn.html" class="btn-login" style="text-decoration: none; display: inline-block; padding: 10px 30px;">Ir a Iniciar Sesión</a>
        </div>
    `;
}

// 2. LÓGICA PARA GUARDAR CAMBIOS
document.querySelector(".guardar-cambios").addEventListener("click", async () => {
    const p1 = document.getElementById("pass1").value;
    const p2 = document.getElementById("pass2").value;

    // Objeto que enviaremos al backend
    const datosParaActualizar = {};

    // Solo validamos y añadimos la contraseña si el usuario escribió algo
    if (p1.length > 0 || p2.length > 0) {
        if (p1 !== p2) {
            alert("Las contraseñas no coinciden.");
            return;
        }
        if (p1.length < 4) {
            alert("La contraseña debe tener al menos 4 caracteres.");
            return;
        }
        // Añadimos la nueva contraseña al objeto
        datosParaActualizar.password = p1;
    } else {
        alert("No has introducido ninguna nueva contraseña.");
        return;
    }

    try {
        const respuesta = await fetch("pistaPadel/auth/actualizar", {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: "include", // Vital para enviar la cookie de sesión
            body: JSON.stringify(datosParaActualizar)
        });

        if (respuesta.ok) {
            alert("¡Contraseña actualizada correctamente!");
            // Limpiamos los inputs por seguridad
            document.getElementById("pass1").value = "";
            document.getElementById("pass2").value = "";
        } else {
            const errorData = await respuesta.json().catch(() => ({}));
            alert("Error al actualizar: " + (errorData.message || "Consulte al administrador"));
        }
    } catch (error) {
        console.error("Error en la conexión:", error);
        alert("No se pudo conectar con el servidor.");
    }
});

cargarPerfil();