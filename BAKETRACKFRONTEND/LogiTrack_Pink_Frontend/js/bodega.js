
document.addEventListener("DOMContentLoaded", () => {

    // ==========================================
    // ELEMENTOS DEL DOM
    // ==========================================

    const warehouseTableBody = document.getElementById("warehouseTableBody");
    const warehouseCount = document.getElementById("warehouseCount");
    const warehouseStock = document.getElementById("warehouseStock");
    const warehouseCities = document.getElementById("warehouseCities");

    const warehouseSearch = document.getElementById("warehouseSearch");
    const refreshWarehouses = document.getElementById("refreshWarehouses");

    const warehouseModal = document.getElementById("warehouseModal");
    const warehouseModalTitle = document.getElementById("warehouseModalTitle");
    const warehouseForm = document.getElementById("warehouseForm");

    const warehouseId = document.getElementById("warehouseId");
    const warehouseName = document.getElementById("warehouseName");
    const warehouseCapacity = document.getElementById("warehouseCapacity");
    const warehouseCity = document.getElementById("warehouseCity");
    const warehouseManager = document.getElementById("warehouseManager");

    const warehouseFormMessage = document.getElementById("warehouseFormMessage");
    const saveWarehouse = document.getElementById("saveWarehouse");

    const openWarehouseModal = document.getElementById("openWarehouseModal");
    const closeWarehouseModal = document.getElementById("closeWarehouseModal");
    const cancelWarehouse = document.getElementById("cancelWarehouse");
    const logoutButton = document.getElementById("logoutButton");


    // ==========================================
    // VARIABLES
    // ==========================================

    let bodegas = [];
    let ciudades = [];
    let encargados = [];


    // ==========================================
    // VALIDAR SESIÓN
    // ==========================================

    const token = sessionStorage.getItem("token");
    const rol = sessionStorage.getItem("rol");

    if (!token || rol !== "ADMIN") {
        window.location.href = "index.html";
        return;
    }


    // ==========================================
    // MENÚ HAMBURGUESA PARA MÓVILES
    // ==========================================

    function configurarMenuMovil() {

        const sidebar = document.querySelector(".sidebar");
        const topbar = document.querySelector(".topbar");

        if (!sidebar || !topbar) return;

        let botonMenu = document.querySelector(".menu-toggle");
        let fondoMenu = document.querySelector(".sidebar-backdrop");

        if (!botonMenu) {
            botonMenu = document.createElement("button");
            botonMenu.type = "button";
            botonMenu.className = "menu-toggle";
            botonMenu.setAttribute("aria-label", "Abrir menú");
            botonMenu.setAttribute("aria-expanded", "false");
            botonMenu.innerHTML = '<i class="fa-solid fa-bars"></i>';

            topbar.prepend(botonMenu);
        }

        if (!fondoMenu) {
            fondoMenu = document.createElement("div");
            fondoMenu.className = "sidebar-backdrop";
            document.body.appendChild(fondoMenu);
        }

        function abrirMenu() {
            document.body.classList.add("sidebar-open");

            botonMenu.setAttribute("aria-expanded", "true");
            botonMenu.setAttribute("aria-label", "Cerrar menú");
            botonMenu.innerHTML = '<i class="fa-solid fa-xmark"></i>';
        }

        function cerrarMenu() {
            document.body.classList.remove("sidebar-open");

            botonMenu.setAttribute("aria-expanded", "false");
            botonMenu.setAttribute("aria-label", "Abrir menú");
            botonMenu.innerHTML = '<i class="fa-solid fa-bars"></i>';
        }

        botonMenu.addEventListener("click", () => {
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

        window.matchMedia("(min-width: 761px)").addEventListener("change", event => {
            if (event.matches) {
                cerrarMenu();
            }
        });
    }


    // ==========================================
    // INFORMACIÓN DEL ADMINISTRADOR
    // ==========================================

    function cargarPerfilAdmin() {

        const nombre = sessionStorage.getItem("nombre") || "Administrador";
        const apellido = sessionStorage.getItem("apellido") || "";

        const nombreCompleto = `${nombre} ${apellido}`.trim();
        const iniciales = `${nombre.charAt(0)}${apellido.charAt(0)}`.toUpperCase();

        const sidebarName = document.getElementById("sidebarAdminName");
        const topbarName = document.getElementById("topbarAdminName");
        const sidebarInitials = document.getElementById("sidebarAdminInitials");
        const topbarInitials = document.getElementById("topbarAdminInitials");

        if (sidebarName) sidebarName.textContent = nombreCompleto;
        if (topbarName) topbarName.textContent = nombreCompleto;

        if (sidebarInitials) sidebarInitials.textContent = iniciales;
        if (topbarInitials) topbarInitials.textContent = iniciales;
    }


    // ==========================================
    // FECHA ACTUAL
    // ==========================================

    function mostrarFecha() {

        const elementoFecha = document.getElementById("currentDate");

        if (!elementoFecha) return;

        elementoFecha.textContent = "GESTIÓN DE BODEGAS";
    }


    // ==========================================
    // CARGAR BODEGAS
    // ==========================================

    async function cargarBodegas() {

        warehouseTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="table-loading">
                    <i class="fa-solid fa-spinner fa-spin"></i>
                    Cargando bodegas...
                </td>
            </tr>
        `;

        try {

            const respuesta = await apiRequest("/api/bodegas");

            bodegas = Array.isArray(respuesta) ? respuesta : [];

            mostrarBodegas(bodegas);
            actualizarIndicadores();

        } catch (error) {

            console.error("Error al cargar bodegas:", error);

            warehouseTableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="table-loading">
                        No fue posible cargar las bodegas.
                    </td>
                </tr>
            `;

            LogiTrackUI.mostrarMensaje({
                titulo: "Error al cargar bodegas",
                mensaje: "No fue posible consultar la información de las bodegas.",
                tipo: "error"
            });
        }
    }


    // ==========================================
    // MOSTRAR BODEGAS EN LA TABLA
    // ==========================================

    function mostrarBodegas(lista) {

        if (!lista || lista.length === 0) {

            warehouseTableBody.innerHTML = `
                <tr>
                    <td colspan="5" class="table-loading">
                        No hay bodegas registradas.
                    </td>
                </tr>
            `;

            return;
        }

        warehouseTableBody.innerHTML = lista.map(bodega => {

            const nombreCiudad = bodega.ciudad?.nombre || "Sin ciudad";

            const nombreEncargado = bodega.encargado
                ? `${bodega.encargado.nombre} ${bodega.encargado.apellido}`
                : "Sin encargado";

            return `
                <tr>
                    <td>
                        <div class="warehouse-name-cell">
                            <div class="warehouse-table-icon">
                                <img src="assets/Bodegas.png" alt="Bodega">
                            </div>
                            <strong>${escaparHTML(bodega.nombre)}</strong>
                        </div>
                    </td>

                    <td>${escaparHTML(nombreCiudad)}</td>

                    <td>
                        <span class="capacity-badge">
                            ${formatearNumero(bodega.capacidad)}
                        </span>
                    </td>

                    <td>${escaparHTML(nombreEncargado)}</td>

                    <td>
                        <div class="warehouse-actions">

                            <button
                                type="button"
                                class="action-button edit"
                                data-action="edit"
                                data-id="${escaparHTML(bodega.idBodega)}"
                                title="Editar bodega"
                            >
                                <i class="fa-solid fa-pen"></i>
                            </button>

                            <button
                                type="button"
                                class="action-button delete"
                                data-action="delete"
                                data-id="${escaparHTML(bodega.idBodega)}"
                                title="Eliminar bodega"
                            >
                                <i class="fa-solid fa-trash"></i>
                            </button>

                        </div>
                    </td>
                </tr>
            `;

        }).join("");
    }


    // ==========================================
    // ACTUALIZAR INDICADORES
    // ==========================================

    async function actualizarIndicadores() {

        warehouseCount.textContent = bodegas.length;

        const ciudadesUnicas = new Set(
            bodegas
                .map(bodega => bodega.ciudad?.idCiudad)
                .filter(id => id !== undefined && id !== null)
        );

        warehouseCities.textContent = ciudadesUnicas.size;

        try {

            const resumen = await apiRequest(
                "/api/inventarios/resumen-por-bodega"
            );

            const total = Array.isArray(resumen)
                ? resumen.reduce(
                    (suma, item) => suma + Number(item.totalStock || 0),
                    0
                )
                : 0;

            warehouseStock.textContent = formatearNumero(total);

        } catch (error) {

            console.error("Error al consultar el stock:", error);
            warehouseStock.textContent = "--";
        }
    }


    // ==========================================
    // CARGAR CIUDADES
    // ==========================================

    async function cargarCiudades() {

        try {

            const respuesta = await apiRequest("/api/ciudades");

            ciudades = Array.isArray(respuesta) ? respuesta : [];

            warehouseCity.innerHTML = `
                <option value="">Selecciona una ciudad</option>
            `;

            ciudades.forEach(ciudad => {

                const option = document.createElement("option");

                option.value = ciudad.idCiudad;
                option.textContent = `${ciudad.nombre} - ${ciudad.departamento}`;

                warehouseCity.appendChild(option);
            });

        } catch (error) {

            console.error("Error al cargar ciudades:", error);

            LogiTrackUI.mostrarMensaje({
                titulo: "Error al cargar ciudades",
                mensaje: "No fue posible consultar las ciudades disponibles.",
                tipo: "error"
            });
        }
    }


    // ==========================================
    // CARGAR EMPLEADOS APROBADOS
    // ==========================================

    async function cargarEncargados() {

        try {

            const respuesta = await apiRequest(
                "/api/usuarios/empleados-aprobados"
            );

            encargados = Array.isArray(respuesta) ? respuesta : [];

            warehouseManager.innerHTML = `
                <option value="">Selecciona un encargado</option>
            `;

            encargados.forEach(usuario => {

                const option = document.createElement("option");

                option.value = usuario.idUsuario;
                option.textContent = `${usuario.nombre} ${usuario.apellido}`;

                warehouseManager.appendChild(option);
            });

        } catch (error) {

            console.error("Error al cargar encargados:", error);

            LogiTrackUI.mostrarMensaje({
                titulo: "Error al cargar encargados",
                mensaje: "No fue posible consultar los empleados aprobados.",
                tipo: "error"
            });
        }
    }


    // ==========================================
    // ABRIR MODAL
    // ==========================================

    function abrirModal() {

        warehouseForm.reset();

        warehouseId.value = "";
        warehouseModalTitle.textContent = "Registrar nueva bodega";
        saveWarehouse.textContent = "Guardar bodega";

        limpiarMensaje();

        warehouseModal.style.display = "flex";
    }


    // ==========================================
    // CERRAR MODAL
    // ==========================================

    function cerrarModal() {

        warehouseModal.style.display = "none";
        warehouseForm.reset();

        warehouseId.value = "";

        limpiarMensaje();
    }


    // ==========================================
    // EDITAR BODEGA
    // ==========================================

    async function editarBodega(id) {

        const bodega = bodegas.find(
            item => String(item.idBodega) === String(id)
        );

        if (!bodega) {

            LogiTrackUI.mostrarMensaje({
                titulo: "Bodega no encontrada",
                mensaje: "No encontramos la bodega que deseas editar.",
                tipo: "error"
            });

            return;
        }

        try {

            await Promise.all([
                cargarCiudades(),
                cargarEncargados()
            ]);

            warehouseId.value = bodega.idBodega;
            warehouseName.value = bodega.nombre;
            warehouseCapacity.value = bodega.capacidad;

            warehouseCity.value = bodega.ciudad?.idCiudad || "";
            warehouseManager.value = bodega.encargado?.idUsuario || "";

            warehouseModalTitle.textContent = "Editar bodega";
            saveWarehouse.textContent = "Actualizar bodega";

            limpiarMensaje();

            warehouseModal.style.display = "flex";

        } catch (error) {

            console.error("Error al preparar edición:", error);

            LogiTrackUI.mostrarMensaje({
                titulo: "No se pudo abrir la edición",
                mensaje: "Ocurrió un problema al preparar la información de la bodega.",
                tipo: "error"
            });
        }
    }


    // ==========================================
    // GUARDAR O ACTUALIZAR BODEGA
    // ==========================================

    async function guardarBodega(event) {

        event.preventDefault();

        const id = warehouseId.value;

        const datosBodega = {
            nombre: warehouseName.value.trim(),
            capacidad: Number(warehouseCapacity.value),
            idCiudad: Number(warehouseCity.value),
            idEncargado: Number(warehouseManager.value)
        };

        if (
            !datosBodega.nombre ||
            !Number.isInteger(datosBodega.capacidad) ||
            datosBodega.capacidad <= 0 ||
            !datosBodega.idCiudad ||
            !datosBodega.idEncargado
        ) {

            mostrarMensaje(
                "Completa todos los campos correctamente.",
                "error"
            );

            return;
        }

        saveWarehouse.disabled = true;
        saveWarehouse.textContent = id ? "Actualizando..." : "Guardando...";

        try {

            if (id) {

                await apiRequest(`/api/bodegas/${id}`, {
                    method: "PUT",
                    body: JSON.stringify(datosBodega)
                });

            } else {

                await apiRequest("/api/bodegas", {
                    method: "POST",
                    body: JSON.stringify(datosBodega)
                });
            }

            cerrarModal();

            await cargarBodegas();

            LogiTrackUI.mostrarMensaje({
                titulo: id
                    ? "¡Bodega actualizada!"
                    : "¡Nueva bodega registrada!",
                mensaje: id
                    ? "Los cambios se guardaron correctamente."
                    : "La bodega se agregó correctamente a LogiTrack.",
                tipo: "success"
            });

        } catch (error) {

            console.error("Error al guardar bodega:", error);

            LogiTrackUI.mostrarMensaje({
                titulo: "No se pudo guardar",
                mensaje: error.message || "No fue posible guardar la bodega.",
                tipo: "error"
            });

        } finally {

            saveWarehouse.disabled = false;

            saveWarehouse.textContent = id
                ? "Actualizar bodega"
                : "Guardar bodega";
        }
    }


    // ==========================================
    // ELIMINAR BODEGA
    // ==========================================

    async function eliminarBodega(id) {

        const bodega = bodegas.find(
            item => String(item.idBodega) === String(id)
        );

        if (!bodega) {

            LogiTrackUI.mostrarMensaje({
                titulo: "Bodega no encontrada",
                mensaje: "No encontramos la bodega seleccionada.",
                tipo: "error"
            });

            return;
        }

        const confirmar = await LogiTrackUI.confirmar({
            titulo: "¿Eliminar esta bodega?",
            mensaje: `Estás a punto de eliminar "${bodega.nombre}". ¿Deseas continuar?`,
            textoConfirmar: "Sí, eliminar",
            textoCancelar: "No, conservar",
            tipo: "danger"
        });

        if (!confirmar) return;

        try {

            await apiRequest(`/api/bodegas/${id}`, {
                method: "DELETE"
            });

            await cargarBodegas();

            LogiTrackUI.mostrarMensaje({
                titulo: "¡Bodega eliminada!",
                mensaje: "La bodega se eliminó correctamente.",
                tipo: "success"
            });

        } catch (error) {

            console.error("Error al eliminar bodega:", error);

            LogiTrackUI.mostrarMensaje({
                titulo: "No se pudo eliminar",
                mensaje: error.message || "No fue posible eliminar la bodega.",
                tipo: "error"
            });
        }
    }


    // ==========================================
    // BUSCAR BODEGAS
    // ==========================================

    function buscarBodegas() {

        const termino = warehouseSearch.value
            .trim()
            .toLowerCase();

        const resultado = bodegas.filter(bodega => {

            const nombre = (bodega.nombre || "").toLowerCase();

            const ciudad = (
                bodega.ciudad?.nombre || ""
            ).toLowerCase();

            return nombre.includes(termino) ||
                ciudad.includes(termino);
        });

        mostrarBodegas(resultado);
    }


    // ==========================================
    // MENSAJES DEL FORMULARIO
    // ==========================================

    function mostrarMensaje(mensaje, tipo) {

        warehouseFormMessage.textContent = mensaje;
        warehouseFormMessage.className = `form-message ${tipo}`;
    }


    function limpiarMensaje() {

        warehouseFormMessage.textContent = "";
        warehouseFormMessage.className = "form-message";
    }


    // ==========================================
    // FUNCIONES AUXILIARES
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


    function formatearNumero(valor) {

        return Number(valor || 0).toLocaleString("es-CO");
    }


    // ==========================================
    // EVENTOS
    // ==========================================

    openWarehouseModal.addEventListener("click", async () => {

        abrirModal();

        await Promise.all([
            cargarCiudades(),
            cargarEncargados()
        ]);
    });


    closeWarehouseModal.addEventListener("click", cerrarModal);
    cancelWarehouse.addEventListener("click", cerrarModal);

    warehouseForm.addEventListener("submit", guardarBodega);

    warehouseSearch.addEventListener("input", buscarBodegas);


    refreshWarehouses.addEventListener("click", async () => {

        await cargarBodegas();

        await Promise.all([
            cargarCiudades(),
            cargarEncargados()
        ]);

        LogiTrackUI.mostrarMensaje({
            titulo: "Información actualizada",
            mensaje: "Los datos de las bodegas se han actualizado.",
            tipo: "success"
        });
    });


    warehouseTableBody.addEventListener("click", event => {

        const boton = event.target.closest("button[data-action]");

        if (!boton) return;

        const id = boton.dataset.id;
        const accion = boton.dataset.action;

        if (accion === "edit") {
            editarBodega(id);
        }

        if (accion === "delete") {
            eliminarBodega(id);
        }
    });


    warehouseModal.addEventListener("click", event => {

        if (event.target === warehouseModal) {
            cerrarModal();
        }
    });


    // ==========================================
    // CERRAR SESIÓN PERSONALIZADO
    // ==========================================

    logoutButton.addEventListener("click", async () => {

        const confirmar = await LogiTrackUI.confirmar({
            titulo: "¿Cerrar sesión?",
            mensaje: "Tu sesión de LogiTrack se cerrará. ¿Deseas continuar?",
            textoConfirmar: "Sí, cerrar sesión",
            textoCancelar: "Seguir aquí",
            tipo: "warning"
        });

        if (confirmar) {

            sessionStorage.clear();
            window.location.href = "index.html";
        }
    });


    // ==========================================
    // INICIALIZACIÓN
    // ==========================================

    configurarMenuMovil();

    cargarPerfilAdmin();
    mostrarFecha();
    cargarBodegas();

});
