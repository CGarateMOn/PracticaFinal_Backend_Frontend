async function cargarPerfil() {
    try {
        const respuesta = await fetch("/pistaPadel/auth/me", { credentials: "include" });

        // Si no hay sesión iniciada (o falta la cookie), bloqueamos la vista
        if (respuesta.status === 401 || respuesta.status === 400) {
            mostrarAlertaLogin();
            return;
        }

        if (!respuesta.ok) {
            throw new Error(`Error: ${respuesta.status}`);
        }

        const perfil = await respuesta.json();

        // Rellenamos los inputs con los datos actuales del usuario
        document.getElementById("saludo").textContent = `Hola, ${perfil.nombre}!!`;
        document.getElementById("inputNombre").value = perfil.nombre;
        document.getElementById("inputApellidos").value = perfil.apellidos;
        document.getElementById("inputEmail").value = perfil.email;
        document.getElementById("inputTelefono").value = perfil.telefono || "";
        // Si el usuario es admin, mostramos el enlace al panel de administración
        if (perfil.rol === "ADMIN") {
            const enlaceAdmin = document.getElementById("enlaceAdmin");
            if (enlaceAdmin) {
                enlaceAdmin.style.display = "inline-block";
            }
        }

    } catch (error) {
        console.error("Error al cargar perfil:", error);
        // Si no hay conexión con el servidor, también mostramos la alerta de login por seguridad
        mostrarAlertaLogin();
    }
}

// Función que oculta los datos privados y muestra el aviso de login
function mostrarAlertaLogin() {
    // 1. Seleccionamos la sección donde está la tabla y los botones
    const seccionInfo = document.querySelector(".informacion-personal");

    // 2. Cambiamos el saludo para que sea genérico
    const saludo = document.getElementById("saludo");
    if (saludo) saludo.textContent = "¡Bienvenido!";

    // 3. Opcional: difuminamos la foto de perfil para dar efecto de "desconectado"
    const fotoPerfil = document.querySelector(".foto-perfil");
    if (fotoPerfil) fotoPerfil.style.opacity = "0.3";

    // 4. Borramos la tabla y ponemos el mensaje de Acceso Restringido
    if (seccionInfo) {
        seccionInfo.innerHTML = `
        <div style="text-align: center; padding: 40px; background-color: #f8f9fa; border-radius: 10px; border: 1px solid #ddd; margin-top: 20px;">
            <h3 style="margin-bottom: 15px; color: #333;">Acceso Restringido</h3>
            <p style="margin-bottom: 25px; color: #666;">Debes iniciar sesión para ver y editar tus datos personales.</p>
            <a href="logIn.html" class="btn-login" style="text-decoration: none; display: inline-block; width: max-content; margin: 0 auto; padding: 10px 30px;">Ir a Iniciar Sesión</a>
        </div>
    `;
    }
}

// Lógica para GUARDAR TODOS LOS CAMBIOS
// Comprobamos primero que el botón existe (por si mostrarAlertaLogin lo ha borrado)
const btnGuardar = document.querySelector(".guardar-cambios");
if (btnGuardar) {
    btnGuardar.addEventListener("click", async () => {
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
}

// Ejecutamos la carga inicial
cargarPerfil();