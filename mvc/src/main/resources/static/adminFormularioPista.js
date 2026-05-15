document.addEventListener("DOMContentLoaded", async () => {
    const esAdmin = await comprobarAdmin();
    if (!esAdmin) return;

    const form = document.querySelector(".admin-formulario-pista");
    const btnEliminar = document.querySelector(".btn-cancelar");

    form.addEventListener("submit", guardarPista);

    btnEliminar.addEventListener("click", eliminarPista);

    const idPista = obtenerIdPistaDeUrl();

    if (idPista) {
        await cargarPista(idPista);
    } else {
        prepararModoCrear();
    }
});

function obtenerIdPistaDeUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

async function comprobarAdmin() {
    const respuesta = await fetch("/pistaPadel/auth/me", {
        credentials: "include"
    });

    if (!respuesta.ok) {
        window.location.href = "logIn.html";
        return false;
    }

    const perfil = await respuesta.json();

    if (perfil.rol !== "ADMIN") {
        window.location.href = "index.html";
        return false;
    }

    return true;
}

function prepararModoCrear() {
    document.querySelector("h2").textContent = "Nueva pista";
    document.getElementById("idPista").value = "";
    document.getElementById("idPista").placeholder = "Se asignará automáticamente";
    document.getElementById("fechaAlta").value = "";
    document.getElementById("fechaAlta").readOnly = true;

    const btnEliminar = document.querySelector(".btn-cancelar");
    btnEliminar.style.display = "none";
}

async function cargarPista(idPista) {
    try {
        const respuesta = await fetch(`/pistaPadel/courts/${idPista}`, {
            credentials: "include"
        });

        if (respuesta.status === 401) {
            window.location.href = "logIn.html";
            return;
        }

        if (respuesta.status === 403) {
            window.location.href = "index.html";
            return;
        }

        if (respuesta.status === 404) {
            alert("Pista no encontrada");
            window.location.href = "adminPistas.html";
            return;
        }

        if (!respuesta.ok) {
            alert("Error al cargar la pista");
            return;
        }

        const pista = await respuesta.json();

        document.getElementById("idPista").value = pista.idPista ?? "";
        document.getElementById("nombre").value = pista.nombre ?? "";
        document.getElementById("ubicacion").value = pista.ubicacion ?? "";
        document.getElementById("precioHora").value = pista.precioHora ?? "";
        document.getElementById("fechaAlta").value = pista.fechaAlta ?? "";
        document.getElementById("activa").value = String(pista.activa);

    } catch (error) {
        console.error("Error cargando pista:", error);
        alert("No se pudo conectar con el servidor");
    }
}

async function guardarPista(event) {
    event.preventDefault();

    const idPista = obtenerIdPistaDeUrl();

    const datos = {
        nombre: document.getElementById("nombre").value.trim(),
        ubicacion: document.getElementById("ubicacion").value.trim(),
        precioHora: Number(document.getElementById("precioHora").value),
        activa: document.getElementById("activa").value === "true"
    };

    if (!datos.nombre || !datos.ubicacion || datos.precioHora <= 0) {
        alert("Revisa los datos de la pista");
        return;
    }

    try {
        const url = idPista
            ? `/pistaPadel/courts/${idPista}`
            : "/pistaPadel/courts";

        const metodo = idPista ? "PATCH" : "POST";

        const respuesta = await fetch(url, {
            method: metodo,
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "include",
            body: JSON.stringify(datos)
        });

        if (respuesta.status === 401) {
            window.location.href = "logIn.html";
            return;
        }

        if (respuesta.status === 403) {
            window.location.href = "index.html";
            return;
        }

        if (!respuesta.ok) {
            alert("No se pudo guardar la pista. Código: " + respuesta.status);
            return;
        }

        alert("Pista guardada correctamente");
        window.location.href = "adminPistas.html";

    } catch (error) {
        console.error("Error guardando pista:", error);
        alert("Error de conexión con el servidor");
    }
}

async function eliminarPista() {
    const idPista = obtenerIdPistaDeUrl();

    if (!idPista) {
        alert("No puedes eliminar una pista que todavía no existe");
        return;
    }

    const confirmar = confirm("¿Seguro que quieres eliminar esta pista?");
    if (!confirmar) return;

    try {
        const respuesta = await fetch(`/pistaPadel/courts/${idPista}`, {
            method: "DELETE",
            credentials: "include"
        });

        if (!respuesta.ok) {
            alert("No se pudo eliminar la pista. Código: " + respuesta.status);
            return;
        }

        alert("Pista eliminada correctamente");
        window.location.href = "adminPistas.html";

    } catch (error) {
        console.error("Error eliminando pista:", error);
        alert("Error de conexión con el servidor");
    }
}