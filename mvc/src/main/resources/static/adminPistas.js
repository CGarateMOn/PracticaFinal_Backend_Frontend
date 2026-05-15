

document.addEventListener("DOMContentLoaded", async () => {

    // 1. Verificamos que el usuario sea admin antes de hacer nada más
    const esAdmin = await comprobarAdmin();
    if (!esAdmin) return;

    // 2. Conectamos los radios de filtro: cada cambio recarga la tabla
    document.getElementById("admin-todas")
        .addEventListener("change", cargarPistas);
    document.getElementById("admin-activas")
        .addEventListener("change", cargarPistas);
    document.getElementById("admin-inactivas")
        .addEventListener("change", cargarPistas);

    // 3. Carga inicial de la tabla
    await cargarPistas();
});


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


async function cargarPistas() {

    const cuerpo = document.getElementById("cuerpoTablaPistas");
    const mensaje = document.getElementById("mensajePistas");

    cuerpo.innerHTML = "";
    mensaje.textContent = "Cargando pistas...";

    try {
        let url = "/pistaPadel/courts";

        if (document.getElementById("admin-activas").checked) {
            url += "?activa=true";
        } else if (document.getElementById("admin-inactivas").checked) {
            url += "?activa=false";
        }
        // Si "admin-todas" está marcado, no añadimos query param

        const respuesta = await fetch(url, { credentials: "include" });

        if (respuesta.status === 401) {
            window.location.href = "logIn.html";
            return;
        }
        if (respuesta.status === 403) {
            window.location.href = "index.html";
            return;
        }
        if (!respuesta.ok) {
            mensaje.textContent = `Error cargando pistas (${respuesta.status})`;
            return;
        }

        const pistas = await respuesta.json();
        renderizarPistas(pistas);

    } catch (error) {
        console.error(error);
        mensaje.textContent = "Error de conexión con el servidor.";
    }
}

function renderizarPistas(pistas) {

    const cuerpo = document.getElementById("cuerpoTablaPistas");
    const mensaje = document.getElementById("mensajePistas");

    if (!pistas || pistas.length === 0) {
        mensaje.textContent = "No hay pistas.";
        cuerpo.innerHTML = "";
        return;
    }

    mensaje.textContent = `${pistas.length} pista(s) encontrada(s).`;

    cuerpo.innerHTML = pistas.map(pista => {

        const id = pista.idPista ?? "";
        const nombre = pista.nombre ?? "";
        const ubicacion = pista.ubicacion ?? "";
        const precio = pista.precioHora ?? "";
        const fechaAlta = pista.fechaAlta ?? "";
        const activa = pista.activa === true;

        const textoEstado = activa ? "ACTIVA" : "INACTIVA";
        const claseEstado = activa
            ? "estado-admin estado-activa"
            : "estado-admin estado-inactiva";
        const claseFila = activa
            ? "fila-pista-admin activa"
            : "fila-pista-admin inactiva";

        return `
            <tr class="${claseFila}">
                <td>${id}</td>
                <td>${nombre}</td>
                <td>${ubicacion}</td>
                <td>${precio}€</td>
                <td><span class="${claseEstado}">${textoEstado}</span></td>
                <td>${fechaAlta}</td>
                <td class="acciones-tabla">
                    <a href="adminFormularioPista.html?id=${id}" class="accion-tabla editar">Editar</a>
                    <button class="accion-tabla btn-eliminar" onclick="eliminarPista(${id})">Eliminar</button>
                </td>
            </tr>
        `;
    }).join("");
}


async function eliminarPista(idPista) {

    const confirmar = confirm("¿Seguro que quieres eliminar esta pista?");
    if (!confirmar) return;

    try {
        const respuesta = await fetch(`/pistaPadel/courts/${idPista}`, {
            method: "DELETE",
            credentials: "include"
        });

        if (!respuesta.ok) {
            alert("No se pudo eliminar la pista.");
            return;
        }

        await cargarPistas();

    } catch (error) {
        console.error(error);
        alert("Error de conexión.");
    }
}