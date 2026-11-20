document.addEventListener("DOMContentLoaded", () => {

    // =========================================
    // MENÚ HAMBURGUESA PARA MÓVILES
    // =========================================

    function configurarMenuMovil() {
        const sidebar = document.querySelector(".sidebar");
        const topbar = document.querySelector(".topbar");

        if (!sidebar || !topbar) return;
        if (document.querySelector(".menu-toggle")) return;

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

        function cerrarMenu() {
            document.body.classList.remove("sidebar-open");
            menuToggle.setAttribute("aria-expanded", "false");
            menuToggle.setAttribute("aria-label", "Abrir menú");
            menuToggle.innerHTML = '<i class="fa-solid fa-bars"></i>';
            fondoMenu.setAttribute("aria-hidden", "true");
        }

        function abrirMenu() {
            document.body.classList.add("sidebar-open");
            menuToggle.setAttribute("aria-expanded", "true");
            menuToggle.setAttribute("aria-label", "Cerrar menú");
            menuToggle.innerHTML = '<i class="fa-solid fa-xmark"></i>';
            fondoMenu.setAttribute("aria-hidden", "false");
        }

        menuToggle.addEventListener("click", () => {
            document.body.classList.contains("sidebar-open")
                ? cerrarMenu()
                : abrirMenu();
        });

        fondoMenu.addEventListener("click", cerrarMenu);

        sidebar.querySelectorAll(".menu-item").forEach(enlace => {
            enlace.addEventListener("click", cerrarMenu);
        });

        document.addEventListener("keydown", evento => {
            if (evento.key === "Escape") cerrarMenu();
        });

        window.matchMedia("(min-width: 769px)").addEventListener("change", evento => {
            if (evento.matches) cerrarMenu();
        });
    }

    // =========================================
    // ELEMENTOS DEL DOM
    // =========================================

    const movementForm = document.getElementById("movementForm");
    const movementType = document.getElementById("movementType");

    const originWarehouse = document.getElementById("originWarehouse");
    const destinationWarehouse = document.getElementById("destinationWarehouse");

    const originWarehouseGroup = document.getElementById("originWarehouseGroup");
    const destinationWarehouseGroup = document.getElementById("destinationWarehouseGroup");

    const movementDetails = document.getElementById("movementDetails");
    const addMovementDetail = document.getElementById("addMovementDetail");

    const movementTableBody = document.getElementById("movementTableBody");
    const movementSearch = document.getElementById("movementSearch");

    const filterMovementType = document.getElementById("filterMovementType");
    const filterStartDate = document.getElementById("filterStartDate");
    const filterEndDate = document.getElementById("filterEndDate");

    const filterMovementsButton = document.getElementById("filterMovementsButton");
    const clearMovementFilters = document.getElementById("clearMovementFilters");
    const refreshMovements = document.getElementById("refreshMovements");

    const saveMovementButton = document.getElementById("saveMovementButton");

    const movementDetailModal = document.getElementById("movementDetailModal");
    const movementDetailContent = document.getElementById("movementDetailContent");
    const movementDetailSubtitle = document.getElementById("movementDetailSubtitle");
    const closeMovementDetail = document.getElementById("closeMovementDetail");

    let bodegas = [];
    let productos = [];
    let movimientos = [];
    let movimientosFiltrados = [];

    // =========================================
    // UTILIDADES
    // =========================================

    function escaparHTML(valor) {
        return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        })[caracter]);
    }

    function formatearCantidad(valor) {
        return Number(valor || 0).toLocaleString("es-CO", {
            minimumFractionDigits: 0,
            maximumFractionDigits: 3
        });
    }

    function formatearFecha(fecha) {
        if (!fecha) return "Sin fecha";

        const fechaConvertida = new Date(fecha);

        if (Number.isNaN(fechaConvertida.getTime())) {
            return escaparHTML(fecha);
        }

        return fechaConvertida.toLocaleString("es-CO", {
            dateStyle: "short",
            timeStyle: "short"
        });
    }

    function obtenerNombreUsuario(usuario) {
        if (!usuario) return "No disponible";

        if (typeof usuario === "string") return usuario;

        return usuario.nombreCompleto ||
            [usuario.nombre, usuario.apellido].filter(Boolean).join(" ") ||
            usuario.username ||
            usuario.correo ||
            "No disponible";
    }

    function notificar(mensaje, tipo = "success") {
        if (window.LogiTrackUI?.mostrarMensaje) {
            LogiTrackUI.mostrarMensaje({ tipo, mensaje });
        } else {
            alert(mensaje);
        }
    }

    // =========================================
    // PERFIL Y FECHA
    // =========================================

    function cargarPerfilAdmin() {
        const nombre = sessionStorage.getItem("nombre") || "Administrador";
        const apellido = sessionStorage.getItem("apellido") || "";

        const nombreCompleto = `${nombre} ${apellido}`.trim();
        const iniciales = (nombre.charAt(0) + apellido.charAt(0)).toUpperCase();

        document.getElementById("sidebarAdminName").textContent = nombreCompleto;
        document.getElementById("topbarAdminName").textContent = nombreCompleto;

        document.getElementById("sidebarAdminInitials").textContent = iniciales || "AD";
        document.getElementById("topbarAdminInitials").textContent = iniciales || "AD";
    }

    function cargarFechaActual() {
        document.getElementById("currentDate").textContent = "CONTROL DE MOVIMIENTOS";
    }

    // =========================================
    // CARGAR BODEGAS
    // =========================================

    async function cargarBodegas() {
        try {
            bodegas = await apiRequest("/api/bodegas");

            if (!Array.isArray(bodegas)) {
                bodegas = [];
            }

            llenarSelectBodegas();

        } catch (error) {
            console.error("Error al cargar bodegas:", error);
            notificar("No fue posible cargar las bodegas.", "error");
        }
    }

    function llenarSelectBodegas() {
        const opciones = bodegas.map(bodega => `
            <option value="${escaparHTML(bodega.idBodega)}">
                ${escaparHTML(bodega.nombre)}
            </option>
        `).join("");

        originWarehouse.innerHTML =
            '<option value="">Selecciona una bodega</option>' + opciones;

        destinationWarehouse.innerHTML =
            '<option value="">Selecciona una bodega</option>' + opciones;
    }

    // =========================================
    // CARGAR PRODUCTOS
    // =========================================

    async function cargarProductos() {
        try {
            productos = await apiRequest("/api/productos");

            if (!Array.isArray(productos)) {
                productos = [];
            }

            llenarSelectProductos();

        } catch (error) {
            console.error("Error al cargar productos:", error);
            notificar("No fue posible cargar los productos.", "error");
        }
    }

    function llenarSelectProductos(select = null) {
        const selects = select
            ? [select]
            : [...document.querySelectorAll(".detail-product")];

        selects.forEach(elemento => {
            const opciones = productos.map(producto => `
                <option value="${escaparHTML(producto.idProducto)}">
                    ${escaparHTML(producto.nombre)}
                </option>
            `).join("");

            elemento.innerHTML =
                '<option value="">Selecciona un producto</option>' + opciones;
        });
    }

    // =========================================
    // MOSTRAR CAMPOS SEGÚN TIPO
    // =========================================

    function actualizarCamposMovimiento() {
        const tipo = movementType.value;

        originWarehouseGroup.style.display =
            tipo === "ENTRADA" ? "none" : "flex";

        destinationWarehouseGroup.style.display =
            tipo === "SALIDA" ? "none" : "flex";

        originWarehouse.required =
            tipo === "SALIDA" || tipo === "TRANSFERENCIA";

        destinationWarehouse.required =
            tipo === "ENTRADA" || tipo === "TRANSFERENCIA";

        if (tipo !== "SALIDA" && tipo !== "TRANSFERENCIA") {
            originWarehouse.value = "";
        }

        if (tipo !== "ENTRADA" && tipo !== "TRANSFERENCIA") {
            destinationWarehouse.value = "";
        }
    }

    movementType.addEventListener("change", actualizarCamposMovimiento);

    // =========================================
    // AGREGAR Y ELIMINAR PRODUCTOS
    // =========================================

    function actualizarBotonesEliminar() {
        const filas = movementDetails.querySelectorAll(".movement-detail-row");

        filas.forEach(fila => {
            const boton = fila.querySelector(".btn-remove-detail");
            boton.disabled = filas.length === 1;
        });
    }

    function agregarDetalle() {
        const fila = document.createElement("div");

        fila.className = "movement-detail-row";

        fila.innerHTML = `
            <div class="form-group">
                <label>Producto *</label>
                <select class="detail-product" required>
                    <option value="">Selecciona un producto</option>
                </select>
            </div>

            <div class="form-group">
                <label>Cantidad *</label>
                <input
                    type="number"
                    class="detail-quantity"
                    min="0.001"
                    step="0.001"
                    placeholder="Ej. 10"
                    required
                >
            </div>

            <button
                type="button"
                class="btn-remove-detail"
                title="Eliminar producto"
            >
                <i class="fa-solid fa-trash"></i>
            </button>
        `;

        movementDetails.appendChild(fila);

        llenarSelectProductos(fila.querySelector(".detail-product"));
        actualizarBotonesEliminar();
    }

    addMovementDetail.addEventListener("click", agregarDetalle);

    movementDetails.addEventListener("click", event => {
        const boton = event.target.closest(".btn-remove-detail");

        if (!boton || boton.disabled) return;

        boton.closest(".movement-detail-row").remove();
        actualizarBotonesEliminar();
    });

    // =========================================
    // VALIDAR FORMULARIO
    // =========================================

    function obtenerDetallesFormulario() {
        const filas = movementDetails.querySelectorAll(".movement-detail-row");
        const detalles = [];
        const productosAgregados = new Set();

        for (const fila of filas) {
            const idProducto = Number(fila.querySelector(".detail-product").value);
            const cantidad = Number(fila.querySelector(".detail-quantity").value);

            if (!idProducto || !Number.isFinite(cantidad) || cantidad < 0.001) {
                throw new Error("Selecciona productos e ingresa cantidades válidas.");
            }

            if (productosAgregados.has(idProducto)) {
                throw new Error("No puedes agregar el mismo producto más de una vez.");
            }

            productosAgregados.add(idProducto);

            detalles.push({
                idProducto,
                cantidad
            });
        }

        if (!detalles.length) {
            throw new Error("Debes agregar al menos un producto.");
        }

        return detalles;
    }

    function validarMovimiento() {
        const tipo = movementType.value;

        if (!tipo) {
            throw new Error("Selecciona el tipo de movimiento.");
        }

        if (
            (tipo === "SALIDA" || tipo === "TRANSFERENCIA") &&
            !originWarehouse.value
        ) {
            throw new Error("Selecciona la bodega de origen.");
        }

        if (
            (tipo === "ENTRADA" || tipo === "TRANSFERENCIA") &&
            !destinationWarehouse.value
        ) {
            throw new Error("Selecciona la bodega de destino.");
        }

        if (
            tipo === "TRANSFERENCIA" &&
            originWarehouse.value === destinationWarehouse.value
        ) {
            throw new Error("La bodega de origen y destino deben ser diferentes.");
        }

        return obtenerDetallesFormulario();
    }

    // =========================================
    // REGISTRAR MOVIMIENTO
    // =========================================

    movementForm.addEventListener("submit", async event => {
        event.preventDefault();

        let detalles;

        try {
            detalles = validarMovimiento();
        } catch (error) {
            notificar(error.message, "error");
            return;
        }

        const tipo = movementType.value;

        const movimiento = {
            tipoMovimiento: tipo,
            idBodegaOrigen:
                tipo === "SALIDA" || tipo === "TRANSFERENCIA"
                    ? Number(originWarehouse.value)
                    : null,
            idBodegaDestino:
                tipo === "ENTRADA" || tipo === "TRANSFERENCIA"
                    ? Number(destinationWarehouse.value)
                    : null,
            detalles
        };

        try {
            saveMovementButton.disabled = true;
            saveMovementButton.innerHTML =
                '<i class="fa-solid fa-spinner fa-spin"></i> Registrando...';

            await apiRequest("/api/movimientos", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(movimiento)
            });

            notificar("Movimiento registrado correctamente.");

            movementForm.reset();
            actualizarCamposMovimiento();

            const filas = movementDetails.querySelectorAll(".movement-detail-row");

            filas.forEach((fila, indice) => {
                if (indice > 0) fila.remove();
            });

            actualizarBotonesEliminar();

            await cargarMovimientos();

        } catch (error) {
            console.error("Error al registrar movimiento:", error);
            notificar(
                error.message || "No fue posible registrar el movimiento.",
                "error"
            );
        } finally {
            saveMovementButton.disabled = false;
            saveMovementButton.innerHTML =
                '<i class="fa-solid fa-floppy-disk"></i> Registrar movimiento';
        }
    });

    // =========================================
    // CARGAR HISTORIAL
    // =========================================

    async function cargarMovimientos() {
        movementTableBody.innerHTML = `
            <tr>
                <td colspan="8" class="table-loading">
                    <i class="fa-solid fa-spinner fa-spin"></i>
                    Cargando movimientos...
                </td>
            </tr>
        `;

        try {
            movimientos = await apiRequest("/api/movimientos");

            if (!Array.isArray(movimientos)) {
                movimientos = [];
            }

            movimientosFiltrados = [...movimientos];

            actualizarEstadisticas();
            aplicarFiltros();

        } catch (error) {
            console.error("Error al cargar movimientos:", error);

            movementTableBody.innerHTML = `
                <tr>
                    <td colspan="8" class="table-loading">
                        No fue posible cargar los movimientos.
                    </td>
                </tr>
            `;

            notificar("Error al consultar el historial de movimientos.", "error");
        }
    }

    // =========================================
    // ESTADÍSTICAS
    // =========================================

    function actualizarEstadisticas() {
        document.getElementById("totalMovements").textContent =
            movimientos.length;

        document.getElementById("totalEntries").textContent =
            movimientos.filter(m => m.tipoMovimiento === "ENTRADA").length;

        document.getElementById("totalExits").textContent =
            movimientos.filter(m => m.tipoMovimiento === "SALIDA").length;

        document.getElementById("totalTransfers").textContent =
            movimientos.filter(m => m.tipoMovimiento === "TRANSFERENCIA").length;
    }

    // =========================================
    // FILTROS
    // =========================================

    function aplicarFiltros() {
        const busqueda = movementSearch.value.trim().toLowerCase();
        const tipo = filterMovementType.value;
        const fechaInicio = filterStartDate.value;
        const fechaFin = filterEndDate.value;

        movimientosFiltrados = movimientos.filter(movimiento => {
            const origen = movimiento.bodegaOrigen?.nombre || "";
            const destino = movimiento.bodegaDestino?.nombre || "";
            const usuario = obtenerNombreUsuario(movimiento.usuario);

            const texto = [
                movimiento.idMovimiento,
                movimiento.id,
                origen,
                destino,
                usuario
            ].join(" ").toLowerCase();

            const coincideBusqueda = !busqueda || texto.includes(busqueda);
            const coincideTipo = !tipo || movimiento.tipoMovimiento === tipo;

            const fecha = movimiento.fecha
                ? String(movimiento.fecha).substring(0, 10)
                : "";

            const coincideInicio = !fechaInicio || (fecha && fecha >= fechaInicio);
            const coincideFin = !fechaFin || (fecha && fecha <= fechaFin);

            return coincideBusqueda &&
                coincideTipo &&
                coincideInicio &&
                coincideFin;
        });

        mostrarMovimientos(movimientosFiltrados);
    }

    movementSearch.addEventListener("input", aplicarFiltros);
    filterMovementsButton.addEventListener("click", aplicarFiltros);

    clearMovementFilters.addEventListener("click", () => {
        movementSearch.value = "";
        filterMovementType.value = "";
        filterStartDate.value = "";
        filterEndDate.value = "";

        aplicarFiltros();
    });

    refreshMovements.addEventListener("click", cargarMovimientos);

    // =========================================
    // MOSTRAR HISTORIAL
    // =========================================

    function obtenerClaseTipo(tipo) {
        switch (tipo) {
            case "ENTRADA":
                return "entry";
            case "SALIDA":
                return "exit";
            case "TRANSFERENCIA":
                return "transfer";
            default:
                return "";
        }
    }

    function mostrarMovimientos(lista) {
        if (!lista.length) {
            movementTableBody.innerHTML = `
                <tr>
                    <td colspan="8" class="table-loading">
                        No se encontraron movimientos.
                    </td>
                </tr>
            `;

            document.getElementById("movementResults").textContent =
                "Mostrando 0 movimientos";

            return;
        }

        movementTableBody.innerHTML = lista.map(movimiento => {
            const id = movimiento.idMovimiento ?? movimiento.id;
            const origen = movimiento.bodegaOrigen?.nombre || "—";
            const destino = movimiento.bodegaDestino?.nombre || "—";
            const usuario = obtenerNombreUsuario(movimiento.usuario);
            const detalles = movimiento.detalles || [];

            return `
                <tr>
                    <td>#${escaparHTML(id)}</td>

                    <td>${escaparHTML(formatearFecha(movimiento.fecha))}</td>

                    <td>
                        <span class="movement-type ${obtenerClaseTipo(movimiento.tipoMovimiento)}">
                            ${escaparHTML(movimiento.tipoMovimiento)}
                        </span>
                    </td>

                    <td>${escaparHTML(origen)}</td>
                    <td>${escaparHTML(destino)}</td>
                    <td>${escaparHTML(usuario)}</td>

                    <td>
                        <span class="movement-products-count">
                            ${detalles.length} producto(s)
                        </span>
                    </td>

                    <td>
                        <div class="movement-actions">
                            <button
                                type="button"
                                class="btn-view-movement"
                                data-id="${escaparHTML(id)}"
                                title="Ver detalle"
                            >
                                <i class="fa-solid fa-eye"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join("");

        document.getElementById("movementResults").textContent =
            `Mostrando ${lista.length} de ${movimientos.length} movimientos`;
    }

    // =========================================
    // VER DETALLE
    // =========================================

    movementTableBody.addEventListener("click", async event => {
        const boton = event.target.closest(".btn-view-movement");

        if (!boton) return;

        const id = boton.dataset.id;

        try {
            const movimiento = await apiRequest(`/api/movimientos/${id}`);
            mostrarDetalleMovimiento(movimiento);

        } catch (error) {
            console.error("Error al consultar detalle:", error);
            notificar("No fue posible consultar el detalle.", "error");
        }
    });

    function mostrarDetalleMovimiento(movimiento) {
        const id = movimiento.idMovimiento ?? movimiento.id;
        const detalles = movimiento.detalles || [];

        movementDetailSubtitle.textContent = `Movimiento #${id}`;

        const productosHTML = detalles.map(detalle => `
            <div class="movement-detail-product">
                <span>${escaparHTML(detalle.producto?.nombre || "Producto")}</span>
                <strong>${formatearCantidad(detalle.cantidad)}</strong>
            </div>
        `).join("");

        movementDetailContent.innerHTML = `
            <div class="movement-detail-info">
                <div>
                    <span>Identificador</span>
                    <strong>#${escaparHTML(id)}</strong>
                </div>

                <div>
                    <span>Fecha</span>
                    <strong>${escaparHTML(formatearFecha(movimiento.fecha))}</strong>
                </div>

                <div>
                    <span>Tipo</span>
                    <strong>${escaparHTML(movimiento.tipoMovimiento)}</strong>
                </div>

                <div>
                    <span>Usuario</span>
                    <strong>${escaparHTML(obtenerNombreUsuario(movimiento.usuario))}</strong>
                </div>

                <div>
                    <span>Bodega de origen</span>
                    <strong>${escaparHTML(movimiento.bodegaOrigen?.nombre || "—")}</strong>
                </div>

                <div>
                    <span>Bodega de destino</span>
                    <strong>${escaparHTML(movimiento.bodegaDestino?.nombre || "—")}</strong>
                </div>
            </div>

            <div class="movement-detail-products">
                <h3>Productos registrados</h3>
                ${productosHTML || "<p>No hay productos registrados.</p>"}
            </div>
        `;

        movementDetailModal.classList.add("active");
    }

    closeMovementDetail.addEventListener("click", () => {
        movementDetailModal.classList.remove("active");
    });

    movementDetailModal.addEventListener("click", event => {
        if (event.target === movementDetailModal) {
            movementDetailModal.classList.remove("active");
        }
    });

    // =========================================
    // CERRAR SESIÓN
    // =========================================

    document.getElementById("logoutButton").addEventListener("click", () => {
        sessionStorage.clear();
        window.location.href = "index.html";
    });

    // =========================================
    // INICIALIZACIÓN
    // =========================================

    async function inicializar() {
        configurarMenuMovil();

        cargarPerfilAdmin();
        cargarFechaActual();

        actualizarCamposMovimiento();
        actualizarBotonesEliminar();

        await Promise.all([
            cargarBodegas(),
            cargarProductos(),
            cargarMovimientos()
        ]);
    }

    inicializar();

});