document.addEventListener("DOMContentLoaded", () => {

    const loginForm = document.getElementById("loginForm");
    const registerForm = document.getElementById("registerForm");

    const loginView = document.getElementById("loginView");
    const registerView = document.getElementById("registerView");

    const loginTab = document.getElementById("showLogin");
    const registerTab = document.getElementById("showRegister");

    // CAMBIAR ENTRE INICIO DE SESIÓN Y REGISTRO

    function cambiarVista(vista) {

        const login = vista === "login";

        loginView.hidden = !login;
        registerView.hidden = login;

        loginTab.classList.toggle("active", login);
        registerTab.classList.toggle("active", !login);

        loginTab.setAttribute("aria-selected", String(login));
        registerTab.setAttribute("aria-selected", String(!login));

    }

    loginTab?.addEventListener("click", () => cambiarVista("login"));

    registerTab?.addEventListener("click", () => cambiarVista("registro"));


    // INICIO DE SESIÓN

    loginForm?.addEventListener("submit", async event => {

        event.preventDefault();

        const mensaje = document.getElementById("loginMessage");
        const boton = loginForm.querySelector('button[type="submit"]');
        const textoBoton = boton.querySelector("span");

        mensaje.textContent = "";
        mensaje.style.color = "";

        boton.disabled = true;
        textoBoton.textContent = "Validando acceso...";

        try {

            const correo = document.getElementById("correo").value.trim();
            const password = document.getElementById("password").value;

            const respuesta = await apiRequest("/api/auth/login", {
                method: "POST",
                body: JSON.stringify({ correo, password })
            });

            if (!respuesta?.token) {
                throw new Error("El servidor no devolvió un token válido.");
            }

            if (!["ADMIN", "EMPLEADO"].includes(respuesta.rol)) {
                throw new Error("El rol recibido no corresponde a un usuario habilitado.");
            }

            // GUARDAR SESIÓN

            sessionStorage.setItem("token", respuesta.token);
            sessionStorage.setItem("rol", respuesta.rol);
            sessionStorage.setItem("idUsuario", respuesta.idUsuario);
            sessionStorage.setItem("nombre", respuesta.nombre || "");
            sessionStorage.setItem("apellido", respuesta.apellido || "");
            sessionStorage.setItem("correo", respuesta.correo || correo);

            // REDIRECCIONAR SEGÚN EL ROL

            if (respuesta.rol === "ADMIN") {

                window.location.href = "admin.html";

            } else {

                window.location.href = "empleado.html";

            }

        } catch (error) {

            mensaje.style.color = "#B34F70";

            mensaje.textContent =
                error.message || "No fue posible iniciar sesión.";

        } finally {

            boton.disabled = false;
            textoBoton.textContent = "Entrar a mi espacio";

        }

    });


    // REGISTRO DE USUARIOS

    registerForm?.addEventListener("submit", async event => {

        event.preventDefault();

        const mensaje = document.getElementById("registerMessage");
        const boton = registerForm.querySelector('button[type="submit"]');
        const textoBoton = boton.querySelector("span");

        mensaje.textContent = "";
        mensaje.style.color = "";

        const nombre = document.getElementById("regNombre").value.trim();
        const apellido = document.getElementById("regApellido").value.trim();
        const correo = document.getElementById("regCorreo").value.trim();
        const password = document.getElementById("regPassword").value;

        if (password.length < 8) {

            mensaje.style.color = "#B34F70";

            mensaje.textContent =
                "La contraseña debe tener mínimo 8 caracteres.";

            return;

        }

        boton.disabled = true;
        textoBoton.textContent = "Enviando solicitud...";

        try {

            const respuesta = await apiRequest("/api/usuarios/registro", {
                method: "POST",
                body: JSON.stringify({
                    nombre,
                    apellido,
                    correo,
                    password
                })
            });

            mensaje.style.color = "#8C4965";

            mensaje.textContent =
                `¡Solicitud enviada! Tu registro quedó ${
                    respuesta?.estadoSolicitud || "PENDIENTE"
                }. El administrador debe aprobarlo antes de que puedas ingresar.`;

            registerForm.reset();

        } catch (error) {

            mensaje.style.color = "#B34F70";

            mensaje.textContent =
                error.message || "No fue posible enviar la solicitud.";

        } finally {

            boton.disabled = false;
            textoBoton.textContent = "Enviar solicitud";

        }

    });

});