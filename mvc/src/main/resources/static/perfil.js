async function cargarPerfil(){
    const respuesta= await fetch("http://localhost:8080/pistaPadel/auth/me", {
        credentials: "include"
    });

    console.log("Status recibido:", respuesta.status);

    if(respuesta.status===401){
        window.location.href="logIn.html";
        return;
    }

    const perfil = await respuesta.json();
    console.log("Perfil recibido:", perfil);

    document.getElementById("saludo").textContent = `Hola, ${perfil.nombre}!!`;
    document.getElementById("tdNombre").textContent=perfil.nombre;
    document.getElementById("tdApellidos").textContent=perfil.apellidos;
    document.getElementById("tdEmail").textContent=perfil.email;
    document.getElementById("tdRol").textContent=perfil.rol;
    document.getElementById("tdTelefono").textContent = perfil.telefono ?? "No disponible";
}

cargarPerfil();