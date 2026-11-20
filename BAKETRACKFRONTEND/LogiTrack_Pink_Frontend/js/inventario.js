
// ================================================
// LOGITRACK - MÓDULO DE INVENTARIO
// ================================================

document.addEventListener("DOMContentLoaded", () => {

    // ELEMENTOS DEL DOM
    const inventoryTableBody = document.getElementById("inventoryTableBody");
    const inventoryCount = document.getElementById("inventoryCount");
    const totalStock = document.getElementById("totalStock");
    const lowStockCount = document.getElementById("lowStockCount");
    const inventoryResults = document.getElementById("inventoryResults");

    const inventorySearch = document.getElementById("inventorySearch");
    const filterWarehouse = document.getElementById("filterWarehouse");
    const filterProduct = document.getElementById("filterProduct");

    const stockLimit = document.getElementById("stockLimit");
    const searchLowStock = document.getElementById("searchLowStock");
    const showAllInventory = document.getElementById("showAllInventory");

    const warehouseSummaryBody = document.getElementById("warehouseSummaryBody");
    const warehouseSummaryResults = document.getElementById("warehouseSummaryResults");

    const refreshInventory = document.getElementById("refreshInventory");
    const refreshWarehouseSummary = document.getElementById("refreshWarehouseSummary");

    // VARIABLES
    let inventarios = [];
    let inventariosMostrados = [];
    let resumenBodegas = [];
    let modoStockBajo = false;
    let limiteStockActual = 10;


    // ================================================
    // MENÚ RESPONSIVE PARA DISPOSITIVOS MÓVILES
    // ================================================

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

    // ================================================
    // PERFIL DEL ADMINISTRADOR
    // ================================================

    function cargarPerfilAdmin() {

        const nombre = sessionStorage.getItem("nombre") || "Administrador";
        const apellido = sessionStorage.getItem("apellido") || "";

        const completo = `${nombre} ${apellido}`.trim();

        const iniciales = (
            nombre.charAt(0) +
            (apellido.charAt(0) || "")
        ).toUpperCase();

        document.getElementById("sidebarAdminName").textContent = completo;
        document.getElementById("topbarAdminName").textContent = completo;

        document.getElementById("sidebarAdminInitials").textContent = iniciales;
        document.getElementById("topbarAdminInitials").textContent = iniciales;
    }

    // ================================================
    // FECHA ACTUAL
    // ================================================

    function cargarFechaActual() {

        document.getElementById("currentDate").textContent = "CONTROL DE INVENTARIO";
    }

    // ================================================
    // ESCAPAR HTML
    // ================================================

    function escaparHTML(valor) {

        return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        })[caracter]);
    }

    // ================================================
    // FORMATO DE CANTIDADES
    // ================================================

    function formatearCantidad(valor) {

        const numero = Number(valor ?? 0);

        if (!Number.isFinite(numero)) {
            return "0,000";
        }

        return numero.toLocaleString("es-CO", {
            minimumFractionDigits: 0,
            maximumFractionDigits: 3
        });
    }

    // ================================================
    // NOTIFICACIONES
    // ================================================

    function mostrarNotificacion(mensaje, tipo = "error") {

        if (
            typeof LogiTrackUI !== "undefined" &&
            typeof LogiTrackUI.mostrarMensaje === "function"
        ) {
            LogiTrackUI.mostrarMensaje({
                tipo,
                mensaje
            });
        } else {
            alert(mensaje);
        }
    }

    // ================================================
    // CARGAR INVENTARIOS
    // ================================================

    async function cargarInventarios() {

        inventoryTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="table-loading">
                    <i class="fa-solid fa-spinner fa-spin"></i>
                    Cargando inventario...
                </td>
            </tr>
        `;

        try {

            const respuesta = await apiRequest("/api/inventarios");

            inventarios = Array.isArray(respuesta) ? respuesta : [];

            inventoryCount.textContent =
                inventarios.length.toLocaleString("es-CO");

            const sumaStock = inventarios.reduce((total, item) => {
                return total + Number(item.stockActual || 0);
            }, 0);

            totalStock.textContent = formatearCantidad(sumaStock);

            cargarFiltros();

            modoStockBajo = false;

            inventariosMostrados = [...inventarios];

            aplicarFiltros();

            await actualizarIndicadorStockBajo();

        } catch (error) {

            console.error("Error al cargar inventario:", error);

            inventoryTableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="table-empty">
                        No fue posible cargar el inventario.
                        Verifica la conexión e inténtalo nuevamente.
                    </td>
                </tr>
            `;

            inventoryCount.textContent = "--";
            totalStock.textContent = "--";
            lowStockCount.textContent = "--";

            inventoryResults.textContent =
                "No se pudieron cargar los resultados";
        }
    }

    // ================================================
    // CARGAR FILTROS
    // ================================================

    function cargarFiltros() {

        const bodegas = new Map();
        const productos = new Map();

        inventarios.forEach(item => {

            if (item.bodega) {

                bodegas.set(
                    String(item.bodega.idBodega),
                    item.bodega.nombre
                );
            }

            if (item.producto) {

                productos.set(
                    String(item.producto.idProducto),
                    item.producto.nombre
                );
            }
        });

        const bodegaSeleccionada = filterWarehouse.value;
        const productoSeleccionado = filterProduct.value;

        filterWarehouse.innerHTML =
            `<option value="">Todas las bodegas</option>`;

        [...bodegas.entries()]
            .sort((a, b) => String(a[1]).localeCompare(String(b[1]), "es"))
            .forEach(([id, nombre]) => {

                filterWarehouse.innerHTML += `
                    <option value="${escaparHTML(id)}">
                        ${escaparHTML(nombre)}
                    </option>
                `;
            });

        filterProduct.innerHTML =
            `<option value="">Todos los productos</option>`;

        [...productos.entries()]
            .sort((a, b) => String(a[1]).localeCompare(String(b[1]), "es"))
            .forEach(([id, nombre]) => {

                filterProduct.innerHTML += `
                    <option value="${escaparHTML(id)}">
                        ${escaparHTML(nombre)}
                    </option>
                `;
            });

        if (bodegas.has(bodegaSeleccionada)) {
            filterWarehouse.value = bodegaSeleccionada;
        }

        if (productos.has(productoSeleccionado)) {
            filterProduct.value = productoSeleccionado;
        }
    }

    // ================================================
    // MOSTRAR INVENTARIOS
    // ================================================

    function mostrarInventarios(lista) {

        if (!lista.length) {

            inventoryTableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="inventory-empty">
                        <i class="fa-solid fa-box-open"></i>
                        No se encontraron registros de inventario.
                    </td>
                </tr>
            `;

        } else {

            inventoryTableBody.innerHTML = lista.map(item => {

                const bodega = item.bodega || {};
                const producto = item.producto || {};

                const marca = producto.marca?.nombre || "Sin marca";
                const categoria = producto.categoria?.nombre || "Sin categoría";

                const stock = Number(item.stockActual || 0);

                let claseStock = "";

                if (stock === 0) {
                    claseStock = "zero";
                } else if (stock <= limiteStockActual) {
                    claseStock = "low";
                }

                return `
                    <tr>

                        <td>
                            #${escaparHTML(item.idInventario)}
                        </td>

                        <td>
                            <span class="inventory-product-name">
                                ${escaparHTML(bodega.nombre || "Sin bodega")}
                            </span>
                        </td>

                        <td>
                            <span class="inventory-product-name">
                                ${escaparHTML(producto.nombre || "Sin producto")}
                            </span>
                        </td>

                        <td>
                            ${escaparHTML(marca)}
                        </td>

                        <td>
                            ${escaparHTML(categoria)}
                        </td>

                        <td>
                            <span class="inventory-stock ${claseStock}">
                                ${formatearCantidad(stock)}
                            </span>
                        </td>

                    </tr>
                `;

            }).join("");
        }

        inventoryResults.textContent =
            `Mostrando ${lista.length} de ${inventariosMostrados.length} registros`;
    }

    // ================================================
    // FILTROS DE INVENTARIO
    // ================================================

    function aplicarFiltros() {

        const termino = inventorySearch.value
            .trim()
            .toLocaleLowerCase("es");

        const idBodega = filterWarehouse.value;
        const idProducto = filterProduct.value;

        const resultado = inventariosMostrados.filter(item => {

            const bodega = item.bodega || {};
            const producto = item.producto || {};

            const coincideTexto =
                String(bodega.nombre || "").toLocaleLowerCase("es").includes(termino) ||
                String(producto.nombre || "").toLocaleLowerCase("es").includes(termino) ||
                String(producto.marca?.nombre || "").toLocaleLowerCase("es").includes(termino) ||
                String(producto.categoria?.nombre || "").toLocaleLowerCase("es").includes(termino);

            const coincideBodega =
                !idBodega ||
                String(bodega.idBodega) === String(idBodega);

            const coincideProducto =
                !idProducto ||
                String(producto.idProducto) === String(idProducto);

            return coincideTexto && coincideBodega && coincideProducto;
        });

        mostrarInventarios(resultado);
    }

    // ================================================
    // CONSULTAR INDICADOR DE STOCK BAJO
    // ================================================

    async function actualizarIndicadorStockBajo() {

        try {

            const respuesta = await apiRequest(
                `/api/inventarios/stock-bajo?limite=${encodeURIComponent(limiteStockActual)}`
            );

            const lista = Array.isArray(respuesta) ? respuesta : [];

            lowStockCount.textContent =
                lista.length.toLocaleString("es-CO");

        } catch (error) {

            console.error("Error al consultar indicador de stock bajo:", error);

            lowStockCount.textContent = "--";
        }
    }

    // ================================================
    // CONSULTAR STOCK BAJO
    // ================================================

    async function consultarStockBajo() {

        const limite = stockLimit.value.trim();

        if (limite === "" || !Number.isFinite(Number(limite)) || Number(limite) < 0) {

            mostrarNotificacion(
                "Ingresa un límite de stock válido. Debe ser mayor o igual a cero."
            );

            stockLimit.focus();
            return;
        }

        limiteStockActual = Number(limite);

        searchLowStock.disabled = true;
        searchLowStock.innerHTML = `
            <i class="fa-solid fa-spinner fa-spin"></i>
            Consultando...
        `;

        try {

            const respuesta = await apiRequest(
                `/api/inventarios/stock-bajo?limite=${encodeURIComponent(limiteStockActual)}`
            );

            const lista = Array.isArray(respuesta) ? respuesta : [];

            modoStockBajo = true;

            inventariosMostrados = lista;

            inventorySearch.value = "";
            filterWarehouse.value = "";
            filterProduct.value = "";

            lowStockCount.textContent =
                lista.length.toLocaleString("es-CO");

            aplicarFiltros();

            mostrarNotificacion(
                `Se encontraron ${lista.length} registros con stock igual o inferior a ${formatearCantidad(limiteStockActual)}.`,
                "success"
            );

        } catch (error) {

            console.error("Error al consultar stock bajo:", error);

            mostrarNotificacion(
                error.message || "No fue posible consultar el stock bajo."
            );

        } finally {

            searchLowStock.disabled = false;

            searchLowStock.innerHTML = `
                <i class="fa-solid fa-magnifying-glass"></i>
                Consultar stock bajo
            `;
        }
    }

    // ================================================
    // MOSTRAR TODO EL INVENTARIO
    // ================================================

    function mostrarTodoInventario() {

        modoStockBajo = false;

        inventariosMostrados = [...inventarios];

        inventorySearch.value = "";
        filterWarehouse.value = "";
        filterProduct.value = "";

        limiteStockActual = Number(stockLimit.value) >= 0
            ? Number(stockLimit.value)
            : 10;

        aplicarFiltros();

        actualizarIndicadorStockBajo();
    }

    // ================================================
    // CARGAR RESUMEN POR BODEGA
    // ================================================

    async function cargarResumenBodegas() {

        warehouseSummaryBody.innerHTML = `
            <tr>
                <td colspan="5" class="table-loading">
                    <i class="fa-solid fa-spinner fa-spin"></i>
                    Cargando resumen por bodega...
                </td>
            </tr>
        `;

        try {

            const respuesta = await apiRequest(
                "/api/inventarios/resumen-por-bodega"
            );

            resumenBodegas = Array.isArray(respuesta) ? respuesta : [];

            mostrarResumenBodegas(resumenBodegas);

        } catch (error) {

            console.error("Error al cargar resumen por bodega:", error);

            warehouseSummaryBody.innerHTML = `
                <tr>
                    <td colspan="5" class="table-empty">
                        No fue posible cargar el resumen por bodega.
                    </td>
                </tr>
            `;

            warehouseSummaryResults.textContent =
                "No se pudo cargar el resumen";
        }
    }

    // ================================================
    // MOSTRAR RESUMEN POR BODEGA
    // ================================================

    function mostrarResumenBodegas(lista) {

        if (!lista.length) {

            warehouseSummaryBody.innerHTML = `
                <tr>
                    <td colspan="5" class="inventory-empty">
                        <i class="fa-solid fa-warehouse"></i>
                        No hay información de existencias por bodega.
                    </td>
                </tr>
            `;

        } else {

            warehouseSummaryBody.innerHTML = lista.map(item => `
                <tr>

                    <td>
                        #${escaparHTML(item.idBodega)}
                    </td>

                    <td>
                        <span class="inventory-product-name">
                            ${escaparHTML(item.nombreBodega)}
                        </span>
                    </td>

                    <td>
                        ${escaparHTML(item.capacidad)}
                    </td>

                    <td>
                        <span class="inventory-stock">
                            ${formatearCantidad(item.totalStock)}
                        </span>
                    </td>

                    <td>
                        ${Number(item.cantidadProductos || 0).toLocaleString("es-CO")}
                    </td>

                </tr>
            `).join("");
        }

        warehouseSummaryResults.textContent =
            `Mostrando ${lista.length} bodegas`;
    }

    // ================================================
    // EVENTOS
    // ================================================

    inventorySearch.addEventListener("input", aplicarFiltros);

    filterWarehouse.addEventListener("change", aplicarFiltros);

    filterProduct.addEventListener("change", aplicarFiltros);

    stockLimit.addEventListener("input", () => {

        const valor = stockLimit.value.trim();

        if (
            valor !== "" &&
            Number.isFinite(Number(valor)) &&
            Number(valor) >= 0
        ) {
            limiteStockActual = Number(valor);
        }
    });

    searchLowStock.addEventListener("click", consultarStockBajo);

    showAllInventory.addEventListener("click", mostrarTodoInventario);

    refreshInventory.addEventListener("click", async () => {

        await cargarInventarios();

        mostrarNotificacion(
            "Inventario actualizado correctamente.",
            "success"
        );
    });

    refreshWarehouseSummary.addEventListener(
        "click",
        cargarResumenBodegas
    );

    // ================================================
    // CERRAR SESIÓN
    // ================================================

    document.getElementById("logoutButton").addEventListener("click", () => {

        sessionStorage.clear();

        window.location.href = "index.html";
    });

    // ================================================
    // INICIALIZAR MÓDULO
    // ================================================

    configurarMenuMovil();
    
    cargarPerfilAdmin();
    cargarFechaActual();

    cargarInventarios();
    cargarResumenBodegas();

});
