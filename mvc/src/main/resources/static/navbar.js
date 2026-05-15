document.addEventListener("DOMContentLoaded", async () => {
    marcarPaginaActual();

    const linkLogin = document.querySelector('a[href*="logIn.html"]');
    const linkSignIn = document.querySelector('a[href*="SignIn.html"]');
    const adminBadge = document.querySelector(".admin-badge");

    try {
        const respuesta = await fetch("/pistaPadel/auth/me", {
            credentials: "include"
        });

        if (respuesta.ok) {
            const perfil = await respuesta.json();

            // Si hay sesión, ocultamos SOLO login y registro.
            // No ocultamos Reservar, Mis reservas, Perfil, Notificaciones, etc.
            if (linkLogin) linkLogin.style.display = "none";
            if (linkSignIn) linkSignIn.style.display = "none";

            insertarBotonLogout();

            if (adminBadge) {
                adminBadge.style.display = perfil.rol === "ADMIN" ? "inline-block" : "none";
            }

        } else {
            // Si no hay sesión, mostramos login/signin y ocultamos badge admin.
            if (linkLogin) linkLogin.style.display = "inline-block";
            if (linkSignIn) linkSignIn.style.display = "inline-block";

            if (adminBadge) {
                adminBadge.style.display = "none";
            }
        }

    } catch (error) {
        console.error("Error al cargar la barra de navegación:", error);

        // En caso de error, no redirigimos ni rompemos la cabecera.
        // Simplemente dejamos la navegación visible.
        if (linkLogin) linkLogin.style.display = "inline-block";
        if (linkSignIn) linkSignIn.style.display = "inline-block";
        if (adminBadge) adminBadge.style.display = "none";
    }
});

function marcarPaginaActual() {
    const paginaActual = window.location.pathname.split("/").pop() || "index.html";
    const enlaces = document.querySelectorAll(".nav-link, .logo");

    enlaces.forEach(enlace => {
        const href = enlace.getAttribute("href");

        if (!href) return;

        const paginaEnlace = href.split("/").pop();

        if (paginaEnlace === paginaActual) {
            enlace.classList.add("nav-activo");
        } else {
            enlace.classList.remove("nav-activo");
        }
    });
}

function insertarBotonLogout() {
    if (document.getElementById("nav-logout")) return;

    const navGroups = document.querySelectorAll(".nav-grupo");

    if (navGroups.length === 0) return;

    const navDerecha = navGroups.length > 1
        ? navGroups[1]
        : navGroups[0];

    const btnLogout = document.createElement("a");
    btnLogout.href = "#";
    btnLogout.className = "nav-link";
    btnLogout.id = "nav-logout";
    btnLogout.textContent = "Cerrar sesión";

    btnLogout.addEventListener("click", async (event) => {
        event.preventDefault();

        const confirmar = confirm("¿Seguro que quieres cerrar sesión?");
        if (!confirmar) return;

        try {
            await fetch("/pistaPadel/auth/logout", {
                method: "POST",
                credentials: "include"
            });
        } catch (error) {
            console.error("Error cerrando sesión:", error);
        }

        sessionStorage.clear();
        window.location.href = "index.html";
    });

    navDerecha.appendChild(btnLogout);
}