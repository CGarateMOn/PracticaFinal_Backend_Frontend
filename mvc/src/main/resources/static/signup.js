//Esperamos a que todo el html esté cargado antes de buscar elementos 
document.addEventListener("DOMContentLoaded", () => {
    //extraemos el formulario de la página
    const form= document.getElementById("SigninForm");
    //cuando le demos a submit ejecutamos registrar usuario
    form.addEventListener("submit", registrarUsuario);
});

    //definimos la funcion que se ejecutará al realizar el submit
    async function registrarUsuario(event){
        event.preventDefault();

        //leemos los valores de los campos del html
        const nombre=document.getElementById("Nombre").value.trim();
        const apellido=document.getElementById("Apellidos").value.trim();
        const email= document.getElementById("email").value.trim();
        const telefono = document.getElementById("telefono").value.trim();
        const password=document.getElementById("password").value.trim();
        const password2=document.getElementById("password2").value.trim();

        //ahora operamos con los campos enviados por el usuario. Antes de llamar al backend
        //debemos realizar distintas comprobaciones como comprobar que las contraseñas coinciden

        if(password!=password2){
            alert("las contraseñas no coinciden");
            //return para salir de la función, no podemos continuar
            return;
        }

        //ahora construimos el json que tenemos que enviar al backend
        const datos={
            nombre: nombre,
            apellidos: apellido,
            email: email,
            password: password,
            telefono: telefono
        };

        //ahora hacemos la peticion fetch al backend
        try{
            const respuesta= await fetch("/pistaPadel/auth/register", {
                method: "POST",
                headers: {
                    "Content-type": "application/json"
                },
                body:JSON.stringify(datos),
                //necesario para que el navegador entregue y reciba cookies
                credentials:"include"
            });

            //ahora procesamos la respuesta del backend
            if(respuesta.ok){
                //si se crea bien mandamos a la página de login
                alert("Cuenta creada correctamente");
                window.location.href="logIn.html";
            } else if (respuesta.status==400){
                alert("Datos inválidos");
            } else if (respuesta.status==409){
                alert("Usuario ya creado");
            } else {
                alert("Error en el registro. Código: " + respuesta.status);
            }
        } catch(error){
            console.error(error);
        }
    }
