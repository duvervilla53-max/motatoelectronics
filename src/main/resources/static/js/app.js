/**
 * ============================================================
 * PROYECTO: MOTATO ELECTRONICS
 * MÓDULO: GESTIÓN DE USUARIOS
 * ARCHIVO: app.js
 * ============================================================
 *
 * Este archivo contiene toda la lógica JavaScript encargada de:
 *
 * ✔ Registrar usuarios
 * ✔ Iniciar sesión
 * ✔ Mostrar mensajes al usuario
 * ✔ Conectarse con Spring Boot mediante Fetch API
 *
 * Autor:
 * Duver Villada
 * SENA
 */


/*=============================================================
        CAMBIO ENTRE LOGIN Y REGISTRO
=============================================================*/

// Caja del Login
const loginBox = document.getElementById("loginBox");

// Caja del Registro
const registroBox = document.getElementById("registroBox");

// Enlace "Registrarse"
const mostrarRegistro = document.getElementById("mostrarRegistro");

// Enlace "Volver al Login"
const mostrarLogin = document.getElementById("mostrarLogin");


// Mostrar formulario de registro
if(mostrarRegistro){

    mostrarRegistro.addEventListener("click",function(e){

        e.preventDefault();

        loginBox.classList.add("oculto");

        registroBox.classList.remove("oculto");

    });

}


// Volver al Login
if(mostrarLogin){

    mostrarLogin.addEventListener("click",function(e){

        e.preventDefault();

        registroBox.classList.add("oculto");

        loginBox.classList.remove("oculto");

    });

}


/*=============================================================
        REGISTRAR USUARIO
=============================================================*/

// Captura el formulario
const formularioRegistro = document.getElementById("registerForm");


// Verifica que exista
if(formularioRegistro){

formularioRegistro.addEventListener("submit", async function(e){

    e.preventDefault();


    // ===================================
    // Captura los datos del formulario
    // ===================================

    const usuario={

        nombre:document.getElementById("nombre").value,

        cedula:document.getElementById("cedula").value,

        correo:document.getElementById("correo").value,

        telefono:document.getElementById("telefono").value,

        direccion:document.getElementById("direccion").value,

        rol:document.getElementById("rol").value,

        password:document.getElementById("password").value

    };


    try{


        // =====================================
        // ENVÍA LOS DATOS A SPRING BOOT
        // =====================================

        const respuesta=await fetch("http://localhost:8080/api/usuarios",{

            method:"POST",

            headers:{

                "Content-Type":"application/json"

            },

            body:JSON.stringify(usuario)

        });


        // Obtiene el mensaje devuelto por Spring
        const mensaje=await respuesta.text();

        // Si el registro fue exitoso
        if(respuesta.ok){

            mostrarMensaje(

                "mensajeRegistro",

                "✅ Usuario registrado correctamente.",

                "green"

            );

            formularioRegistro.reset();

        }

        // Si hubo un error
        else{

            mostrarMensaje(

                "mensajeRegistro",

                mensaje,

                "red"

            );

        }

    }

    catch(error){

        mostrarMensaje(

            "mensajeRegistro",

            "❌ No fue posible conectar con el servidor.",

            "red"

        );

    }

});

}


/*=============================================================
        FUNCIÓN PARA MOSTRAR MENSAJES
=============================================================*/

function mostrarMensaje(id,mensaje,color){

    const caja=document.getElementById(id);

    caja.innerHTML=mensaje;

    caja.style.color=color;

}

/*=============================================================
        INICIAR SESIÓN
=============================================================*/

// Obtiene el formulario del Login
const formularioLogin = document.getElementById("loginForm");

// Verifica que exista
if(formularioLogin){

    formularioLogin.addEventListener("submit", async function(e){

        e.preventDefault();

        // Captura los datos ingresados

        const usuario={

            correo:document.getElementById("correoLogin").value,

            password:document.getElementById("passwordLogin").value

        };

        try{

            // Envía la información al backend

            const respuesta = await fetch("http://localhost:8080/api/login",{

                method:"POST",

                headers:{

                    "Content-Type":"application/json"

                },

                body:JSON.stringify(usuario)

            });

            const mensaje = await respuesta.text();

            // Si las credenciales son correctas

            if(respuesta.ok){

                mostrarMensaje(

                    "mensajeLogin",

                    "<strong>Autenticación exitosa.</strong><br>Bienvenido a Motato Electronics.",

                    "mensaje-exito"

                );

            }

            // Si las credenciales son incorrectas

            else{

                mostrarMensaje(

                    "mensajeLogin",

                    "<strong>Error de autenticación.</strong><br>Correo electrónico o contraseña incorrectos. Verifique la información e intente nuevamente.",

                    "mensaje-error"

                );

            }

        }

        catch(error){

            mostrarMensaje(

                "mensajeLogin",

                "⚠ No fue posible establecer conexión con el servidor.",

                "#f59e0b"

            );

        }

    });

}

/*=========================================================
        MOSTRAR MENSAJES DEL SISTEMA
==========================================================*/

function mostrarMensaje(id, texto, tipo){

    const caja=document.getElementById(id);

    caja.style.display="block";

    caja.innerHTML=texto;

    caja.className="mensaje-login";

    caja.classList.add(tipo);

    // Ocultar automáticamente después de 5 segundos

    setTimeout(()=>{

        caja.style.display="none";

    },5000);

}