document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("loginForm");
    form.addEventListener("submit", logUsuario);
});

async function logUsuario(event){
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    const datos = {
        email: email,
        password: password
    };

    try {
        const respuesta = await fetch("/pistaPadel/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(datos),
            credentials: "include"
        });

        if(respuesta.ok){
            const usuario = await respuesta.json();
            if(usuario.rol === "ADMIN"){
                location.href = "adminReservas.html";
            } else {
                location.href = "perfil.html";
            }
        } else if(respuesta.status === 401){
            alert("No estás autorizado");
        } else {
            alert("Error en el login. Código: " + respuesta.status);
        }

    } catch(error){
        console.error(error);
    }
}