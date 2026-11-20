
/* =========================================
   LOGITRACK - SISTEMA GLOBAL DE MENSAJES
========================================= */

const LogiTrackUI = {

    mostrarMensaje({
        titulo = "Operación realizada",
        mensaje = "",
        tipo = "success",
        duracion = 3500
    } = {}) {

        let contenedor = document.getElementById("uiToastContainer");

        if (!contenedor) {
            contenedor = document.createElement("div");
            contenedor.id = "uiToastContainer";
            contenedor.className = "ui-toast-container";
            document.body.appendChild(contenedor);
        }

        const iconos = {
            success: "fa-circle-check",
            error: "fa-circle-xmark",
            warning: "fa-triangle-exclamation",
            info: "fa-circle-info"
        };

        const toast = document.createElement("div");
        toast.className = `ui-toast ${tipo}`;

        const icono = document.createElement("i");
        icono.className = `fa-solid ${iconos[tipo] || iconos.info}`;

        const contenido = document.createElement("div");
        contenido.className = "ui-toast-content";

        const encabezado = document.createElement("strong");
        encabezado.textContent = titulo;

        const texto = document.createElement("span");
        texto.textContent = mensaje;

        const cerrar = document.createElement("button");
        cerrar.type = "button";
        cerrar.className = "ui-toast-close";
        cerrar.innerHTML = "&times;";
        cerrar.setAttribute("aria-label", "Cerrar notificación");

        contenido.append(encabezado, texto);
        toast.append(icono, contenido, cerrar);
        contenedor.appendChild(toast);

        const retirar = () => {
            toast.classList.add("hide");
            setTimeout(() => toast.remove(), 250);
        };

        cerrar.addEventListener("click", retirar);

        if (duracion > 0) {
            setTimeout(retirar, duracion);
        }
    },


    confirmar({
        titulo = "¿Deseas continuar?",
        mensaje = "Confirma si deseas realizar esta acción.",
        textoConfirmar = "Aceptar",
        textoCancelar = "Cancelar",
        tipo = "warning"
    } = {}) {

        return new Promise(resolve => {

            const overlay = document.createElement("div");
            overlay.className = "ui-confirm-overlay";

            const iconos = {
                warning: "fa-triangle-exclamation",
                danger: "fa-trash-can",
                info: "fa-circle-info"
            };

            overlay.innerHTML = `
                <div class="ui-confirm-modal" role="dialog"
                     aria-modal="true" aria-labelledby="uiConfirmTitle">

                    <div class="ui-confirm-icon ${tipo}">
                        <i class="fa-solid ${iconos[tipo] || iconos.warning}"></i>
                    </div>

                    <h2 id="uiConfirmTitle"></h2>
                    <p class="ui-confirm-message"></p>

                    <div class="ui-confirm-actions">
                        <button type="button" class="ui-button-cancel">
                            ${textoCancelar}
                        </button>

                        <button type="button" class="ui-button-confirm ${tipo}">
                            ${textoConfirmar}
                        </button>
                    </div>
                </div>
            `;

            overlay.querySelector("h2").textContent = titulo;
            overlay.querySelector(".ui-confirm-message").textContent = mensaje;

            document.body.appendChild(overlay);

            const botonCancelar = overlay.querySelector(".ui-button-cancel");
            const botonConfirmar = overlay.querySelector(".ui-button-confirm");

            let resuelto = false;

            const finalizar = resultado => {
                if (resuelto) return;

                resuelto = true;
                document.removeEventListener("keydown", manejarTecla);
                overlay.remove();
                resolve(resultado);
            };

            const manejarTecla = evento => {
                if (evento.key === "Escape") {
                    finalizar(false);
                }
            };

            botonCancelar.addEventListener("click", () => finalizar(false));
            botonConfirmar.addEventListener("click", () => finalizar(true));

            overlay.addEventListener("click", evento => {
                if (evento.target === overlay) {
                    finalizar(false);
                }
            });

            document.addEventListener("keydown", manejarTecla);

            botonConfirmar.focus();
        });
    }

};
