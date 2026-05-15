document.addEventListener("DOMContentLoaded", async () => {
    // 1. Buscamos los enlaces de Login, SignIn y la etiqueta de Admin
    const linkLogin = document.querySelector('a[href*="logIn.html"]');
    const linkSignIn = document.querySelector('a[href*="SignIn.html"]');
    const adminBadge = document.querySelector('.admin-badge');

    try {
        // 2. Comprobamos si hay sesión iniciada
        const respuesta = await fetch('/pistaPadel/auth/me', { credentials: 'include' });

        if (respuesta.ok) {
            // === USUARIO CON SESIÓN INICIADA ===
            const perfil = await respuesta.json();

            // Ocultamos Login y SignIn
            if (linkLogin) linkLogin.style.display = 'none';
            if (linkSignIn) linkSignIn.style.display = 'none';

            // Buscamos el grupo derecho de la barra de navegación para inyectar "Cerrar sesión"
            const navGroups = document.querySelectorAll('.nav-grupo');
            const navDerecha = navGroups.length > 1 ? navGroups[1] : navGroups[0];

            if (navDerecha && !document.getElementById('nav-logout')) {
                // Creamos el botón de Cerrar Sesión
                const btnLogout = document.createElement('a');
                btnLogout.href = "#";
                btnLogout.className = "nav-link";
                btnLogout.id = "nav-logout";
                btnLogout.textContent = "Cerrar sesión";
                btnLogout.style.color = "#ff4c4c"; // Para que destaque un poco en rojo
                btnLogout.style.fontWeight = "bold";

                // Le damos la funcionalidad de salir
                btnLogout.addEventListener("click", async (e) => {
                    e.preventDefault();
                    if (!confirm("¿Seguro que quieres cerrar sesión?")) return;

                    await fetch('/pistaPadel/auth/logout', { method: 'POST', credentials: 'include' });
                    sessionStorage.clear();
                    window.location.href = "index.html";
                });

                // Lo añadimos al final de la barra (después de Perfil y Notificaciones)
                navDerecha.appendChild(btnLogout);
            }

            // Mostramos u ocultamos el botón de Admin según su rol
            if (perfil.rol === "ADMIN") {
                if (adminBadge) adminBadge.style.display = 'inline-block';
            } else {
                if (adminBadge) adminBadge.style.display = 'none';
            }

        } else {
            // === USUARIO SIN SESIÓN ===
            // Ocultamos el badge de Admin por seguridad
            if (adminBadge) adminBadge.style.display = 'none';

            // Aseguramos que Login y SignIn se vean (Perfil y Notificaciones no los tocamos, se quedan visibles)
            if (linkLogin) linkLogin.style.display = 'inline-block';
            if (linkSignIn) linkSignIn.style.display = 'inline-block';
        }
    } catch (error) {
        console.error("Error al cargar la barra de navegación:", error);
    }
});