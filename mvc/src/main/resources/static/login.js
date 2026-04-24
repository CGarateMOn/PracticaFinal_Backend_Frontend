//Esperamos a que todo el html esté cargado antes de buscar elementos 
document.addEventListener("DOMContentLoaded", () => {
    //extraemos el formulario de la página
    const form= document.getElementById("loginForm");
    //cuando le demos a submit ejecutamos registrar usuario
    form.addEventListener("submit", logUsuario);
});

async function logUsuario(event){
    event.preventDefault();

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    const datos={
        email: email,
        password: password
    };

    try{
        const respuesta = await fetch("/pistaPadel/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(datos),
            credentials: "include"
        });

        if(respuesta.ok){
            //si hemos tenido exito redirigimos a la página de home
            location.href="perfil.html";
        }else if(respuesta.status==401){
            alert("No estás autorizado");
        }else{
            alert("Error en el login. Código: " + respuesta.status);
        }

        

    } catch(error){
        console.error(error);
    }
}