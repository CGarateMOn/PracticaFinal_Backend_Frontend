async function cargarPerfil() {
    try {
        const respuesta = await fetch("/pistaPadel/auth/me", { credentials: "include" });

        if (respuesta.status === 401 || respuesta.status === 400) {
            mostrarAlertaLogin(); // La función que creamos antes
            return;
        }

        const perfil = await respuesta.json();

        // Rellenamos los inputs con los datos actuales del usuario
        document.getElementById("saludo").textContent = `Hola, ${perfil.nombre}!!`;
        document.getElementById("inputNombre").value = perfil.nombre;
        document.getElementById("inputApellidos").value = perfil.apellidos;
        document.getElementById("inputEmail").value = perfil.email;
        document.getElementById("inputTelefono").value = perfil.telefono || "";

    } catch (error) {
        console.error("Error al cargar perfil:", error);
    }
}

// Lógica para GUARDAR TODOS LOS CAMBIOS
document.querySelector(".guardar-cambios").addEventListener("click", async () => {
    const nombre = document.getElementById("inputNombre").value.trim();
    const apellidos = document.getElementById("inputApellidos").value.trim();
    const email = document.getElementById("inputEmail").value.trim();
    const telefono = document.getElementById("inputTelefono").value.trim();
    const p1 = document.getElementById("pass1").value;
    const p2 = document.getElementById("pass2").value;

    // Construimos el objeto con los datos básicos
    const datosParaActualizar = {
        nombre: nombre,
        apellidos: apellidos,
        email: email,
        telefono: telefono
    };

    // Si el usuario escribió algo en el campo de contraseña, validamos
    if (p1.length > 0) {
        if (p1 !== p2) {
            alert("Las contraseñas no coinciden.");
            return;
        }
        if (p1.length < 4) {
            alert("La contraseña debe tener al menos 4 caracteres.");
            return;
        }
        datosParaActualizar.password = p1;
    }

    try {
        const respuesta = await fetch(`/pistaPadel/auth/actualizar`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            credentials: "include",
            body: JSON.stringify(datosParaActualizar)
        });

        if (respuesta.ok) {
            alert("¡Perfil actualizado correctamente!");
            // Actualizamos el saludo por si cambió el nombre
            document.getElementById("saludo").textContent = `Hola, ${nombre}!!`;
            // Limpiamos los campos de password por seguridad
            document.getElementById("pass1").value = "";
            document.getElementById("pass2").value = "";
        } else {
            alert("Error al actualizar los datos.");
        }
    } catch (error) {
        console.error("Error en la conexión:", error);
        alert("No se pudo conectar con el servidor.");
    }
});

cargarPerfil();