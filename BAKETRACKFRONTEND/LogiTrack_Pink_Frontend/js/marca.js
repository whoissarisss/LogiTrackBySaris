
// BAKE TRACK - MÓDULO DE MARCAS

document.addEventListener("DOMContentLoaded", () => {

    const brandTableBody = document.getElementById("brandTableBody");
    const brandCount = document.getElementById("brandCount");
    const brandResults = document.getElementById("brandResults");
    const brandSearch = document.getElementById("brandSearch");

    const brandModal = document.getElementById("brandModal");
    const brandForm = document.getElementById("brandForm");
    const brandId = document.getElementById("brandId");
    const brandName = document.getElementById("brandName");
    const brandModalTitle = document.getElementById("brandModalTitle");
    const brandFormMessage = document.getElementById("brandFormMessage");
    const saveBrand = document.getElementById("saveBrand");

    let marcas = [];

    // ==========================================
    // MENÚ RESPONSIVE PARA DISPOSITIVOS MÓVILES
    // ==========================================

    function configurarMenuMovil() {

        const sidebar = document.querySelector(".sidebar");
        const topbar = document.querySelector(".topbar");

        if (!sidebar || !topbar) return;

        const menuToggle = document.createElement("button");

        menuToggle.type = "button";
        menuToggle.className = "menu-toggle";
        menuToggle.setAttribute("aria-label", "Abrir menú");
        menuToggle.setAttribute("aria-expanded", "false");
        menuToggle.innerHTML = '<i class="fa-solid fa-bars"></i>';

        topbar.insertBefore(menuToggle, topbar.firstChild);

        const fondoMenu = document.createElement("div");
        fondoMenu.className = "sidebar-backdrop";
        fondoMenu.setAttribute("aria-hidden", "true");

        document.body.appendChild(fondoMenu);

        function abrirMenu() {
            document.body.classList.add("sidebar-open");
            menuToggle.setAttribute("aria-expanded", "true");
            menuToggle.setAttribute("aria-label", "Cerrar menú");
            menuToggle.innerHTML = '<i class="fa-solid fa-xmark"></i>';
            fondoMenu.setAttribute("aria-hidden", "false");
        }

        function cerrarMenu() {
            document.body.classList.remove("sidebar-open");
            menuToggle.setAttribute("aria-expanded", "false");
            menuToggle.setAttribute("aria-label", "Abrir menú");
            menuToggle.innerHTML = '<i class="fa-solid fa-bars"></i>';
            fondoMenu.setAttribute("aria-hidden", "true");
        }

        menuToggle.addEventListener("click", () => {
            if (document.body.classList.contains("sidebar-open")) {
                cerrarMenu();
            } else {
                abrirMenu();
            }
        });

        fondoMenu.addEventListener("click", cerrarMenu);

        sidebar.querySelectorAll(".menu-item").forEach(enlace => {
            enlace.addEventListener("click", cerrarMenu);
        });

        document.addEventListener("keydown", event => {
            if (event.key === "Escape") {
                cerrarMenu();
            }
        });

        const mediaQuery = window.matchMedia("(min-width: 761px)");

        mediaQuery.addEventListener("change", event => {
            if (event.matches) {
                cerrarMenu();
            }
        });
    }

    // ==========================================
    // PERFIL DEL ADMINISTRADOR
    // ==========================================

    function cargarPerfilAdmin() {

        const nombre = sessionStorage.getItem("nombre") || "Administrador";
        const apellido = sessionStorage.getItem("apellido") || "";
        const completo = `${nombre} ${apellido}`.trim();
        const iniciales = `${nombre.charAt(0)}${apellido.charAt(0) || ""}`.toUpperCase();

        document.getElementById("sidebarAdminName").textContent = completo;
        document.getElementById("topbarAdminName").textContent = completo;
        document.getElementById("sidebarAdminInitials").textContent = iniciales;
        document.getElementById("topbarAdminInitials").textContent = iniciales;
    }

    // ==========================================
    // EVITAR INYECCIÓN DE HTML
    // ==========================================

    function escaparHTML(valor) {

        return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        })[caracter]);
    }

    // ==========================================
    // MOSTRAR MENSAJES DEL FORMULARIO
    // ==========================================

    function mostrarMensaje(mensaje, tipo = "error") {

        brandFormMessage.textContent = mensaje;
        brandFormMessage.className = `form-message ${tipo}`;
    }

    function limpiarMensaje() {

        brandFormMessage.textContent = "";
        brandFormMessage.className = "form-message";
    }

    // ==========================================
    // CARGAR MARCAS DESDE EL BACKEND
    // ==========================================

    async function cargarMarcas() {

        brandTableBody.innerHTML = `
            <tr>
                <td colspan="3" class="table-loading">
                    <i class="fa-solid fa-spinner fa-spin"></i>
                    Cargando marcas...
                </td>
            </tr>`;

        try {

            const respuesta = await apiRequest("/api/marcas");

            marcas = Array.isArray(respuesta) ? respuesta : [];

            brandCount.textContent =
                marcas.length.toLocaleString("es-CO");

            filtrarMarcas();

        } catch (error) {

            console.error("Error al cargar marcas:", error);

            brandTableBody.innerHTML = `
                <tr>
                    <td colspan="3" class="table-empty">
                        No fue posible cargar las marcas.
                        Verifica la conexión e inténtalo nuevamente.
                    </td>
                </tr>`;

            brandCount.textContent = "--";
            brandResults.textContent =
                "No se pudieron cargar los resultados";
        }
    }

    // ==========================================
    // MOSTRAR MARCAS EN LA TABLA
    // ==========================================

    function mostrarMarcas(lista) {

        if (!lista.length) {

            brandTableBody.innerHTML = `
                <tr>
                    <td colspan="3" class="table-empty">
                        No se encontraron marcas registradas.
                    </td>
                </tr>`;

        } else {

            brandTableBody.innerHTML = lista.map(marca => `
                <tr>

                    <td class="brand-id">
                        #${escaparHTML(marca.idMarca)}
                    </td>

                    <td class="brand-name">
                        ${escaparHTML(marca.nombre)}
                    </td>

                    <td>
                        <div class="brand-actions">

                            <button
                                class="brand-action"
                                type="button"
                                data-action="edit"
                                data-id="${escaparHTML(marca.idMarca)}"
                                title="Editar marca"
                                aria-label="Editar marca">

                                <i class="fa-solid fa-pen"></i>

                            </button>

                            <button
                                class="brand-action delete"
                                type="button"
                                data-action="delete"
                                data-id="${escaparHTML(marca.idMarca)}"
                                title="Eliminar marca"
                                aria-label="Eliminar marca">

                                <i class="fa-solid fa-trash"></i>

                            </button>

                        </div>
                    </td>

                </tr>
            `).join("");
        }

        brandResults.textContent =
            `Mostrando ${lista.length} de ${marcas.length} marcas`;
    }

    // ==========================================
    // FILTRAR MARCAS
    // ==========================================

    function filtrarMarcas() {

        const termino = brandSearch.value
            .trim()
            .toLocaleLowerCase("es");

        const resultado = marcas.filter(marca =>
            String(marca.nombre || "")
                .toLocaleLowerCase("es")
                .includes(termino)
        );

        mostrarMarcas(resultado);
    }

    // ==========================================
    // ABRIR MODAL
    // ==========================================

    function abrirModal(marca = null) {

        brandForm.reset();
        limpiarMensaje();

        brandId.value = marca ? marca.idMarca : "";
        brandName.value = marca ? marca.nombre : "";

        brandModalTitle.textContent = marca
            ? "Editar marca"
            : "Registrar nueva marca";

        saveBrand.textContent = marca
            ? "Actualizar marca"
            : "Guardar marca";

        brandModal.style.display = "flex";
        brandName.focus();
    }

    // ==========================================
    // CERRAR MODAL
    // ==========================================

    function cerrarModal() {

        brandModal.style.display = "none";
        brandForm.reset();
        brandId.value = "";
        limpiarMensaje();
    }

    // ==========================================
    // REGISTRAR O ACTUALIZAR MARCA
    // ==========================================

    async function guardarMarca(event) {

        event.preventDefault();

        const id = brandId.value;

        const datos = {
            nombre: brandName.value.trim()
        };

        if (!datos.nombre) {

            mostrarMensaje("El nombre de la marca es obligatorio.");
            brandName.focus();
            return;
        }

        saveBrand.disabled = true;

        saveBrand.textContent = id
            ? "Actualizando..."
            : "Guardando...";

        try {

            if (id) {

                await apiRequest(`/api/marcas/${id}`, {
                    method: "PUT",
                    body: JSON.stringify(datos)
                });

            } else {

                await apiRequest("/api/marcas", {
                    method: "POST",
                    body: JSON.stringify(datos)
                });
            }

            cerrarModal();
            await cargarMarcas();

            if (
                typeof LogiTrackUI !== "undefined" &&
                LogiTrackUI.mostrarMensaje
            ) {

                LogiTrackUI.mostrarMensaje({
                    tipo: "success",
                    mensaje: id
                        ? "Marca actualizada correctamente."
                        : "Marca registrada correctamente."
                });

            } else {

                alert(
                    id
                        ? "Marca actualizada correctamente."
                        : "Marca registrada correctamente."
                );
            }

        } catch (error) {

            console.error("Error al guardar marca:", error);

            mostrarMensaje(
                error.message || "No fue posible guardar la marca."
            );

        } finally {

            saveBrand.disabled = false;

            saveBrand.textContent = id
                ? "Actualizar marca"
                : "Guardar marca";
        }
    }

    // ==========================================
    // ELIMINAR MARCA
    // ==========================================

    async function eliminarMarca(id) {

        const marca = marcas.find(
            item => String(item.idMarca) === String(id)
        );

        if (!marca) return;

        const confirmado = confirm(
            `¿Deseas eliminar la marca "${marca.nombre}"?`
        );

        if (!confirmado) return;

        try {

            await apiRequest(`/api/marcas/${id}`, {
                method: "DELETE"
            });

            await cargarMarcas();

            if (
                typeof LogiTrackUI !== "undefined" &&
                LogiTrackUI.mostrarMensaje
            ) {

                LogiTrackUI.mostrarMensaje({
                    tipo: "success",
                    mensaje: "Marca eliminada correctamente."
                });

            } else {

                alert("Marca eliminada correctamente.");
            }

        } catch (error) {

            console.error("Error al eliminar marca:", error);

            alert(
                error.message ||
                "No fue posible eliminar la marca. Puede estar asociada a productos."
            );
        }
    }

    // ==========================================
    // EVENTOS
    // ==========================================

    document
        .getElementById("openBrandModal")
        .addEventListener("click", () => abrirModal());

    document
        .getElementById("closeBrandModal")
        .addEventListener("click", cerrarModal);

    document
        .getElementById("cancelBrand")
        .addEventListener("click", cerrarModal);

    document
        .getElementById("refreshBrands")
        .addEventListener("click", cargarMarcas);

    brandSearch.addEventListener("input", filtrarMarcas);

    brandForm.addEventListener("submit", guardarMarca);

    brandTableBody.addEventListener("click", event => {

        const boton = event.target.closest("button[data-action]");

        if (!boton) return;

        const id = boton.dataset.id;

        if (boton.dataset.action === "edit") {

            const marca = marcas.find(
                item => String(item.idMarca) === String(id)
            );

            if (marca) abrirModal(marca);

        } else if (boton.dataset.action === "delete") {

            eliminarMarca(id);
        }
    });

    brandModal.addEventListener("click", event => {

        if (event.target === brandModal) {
            cerrarModal();
        }
    });

    document
        .getElementById("logoutButton")
        .addEventListener("click", () => {

            sessionStorage.clear();
            window.location.href = "index.html";
        });

    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    configurarMenuMovil();
    cargarPerfilAdmin();
    cargarMarcas();

});
