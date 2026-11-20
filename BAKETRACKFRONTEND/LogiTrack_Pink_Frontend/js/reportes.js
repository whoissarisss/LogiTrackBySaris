document.addEventListener("DOMContentLoaded", () => {
    const $ = id => document.getElementById(id);
    const state = {
        movimientos: [],
        top: [],
        bodegas: []
    };

    const escapeHTML = value => String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    }[character]));

    const num = value => Number(value || 0).toLocaleString("es-CO", {
        maximumFractionDigits: 2
    });

    // MENÚ HAMBURGUESA
    function configureMobileMenu() {
        const toggle = $("menuToggle");
        const sidebar = document.querySelector(".sidebar");
        const backdrop = document.querySelector(".sidebar-backdrop");

        if (!toggle || !sidebar || !backdrop) return;

        const closeMenu = () => {
            document.body.classList.remove("sidebar-open");
            toggle.setAttribute("aria-expanded", "false");
            toggle.setAttribute("aria-label", "Abrir menú");
            toggle.innerHTML = '<i class="fa-solid fa-bars"></i>';
        };

        const openMenu = () => {
            document.body.classList.add("sidebar-open");
            toggle.setAttribute("aria-expanded", "true");
            toggle.setAttribute("aria-label", "Cerrar menú");
            toggle.innerHTML = '<i class="fa-solid fa-xmark"></i>';
        };

        toggle.addEventListener("click", () => {
            document.body.classList.contains("sidebar-open")
                ? closeMenu()
                : openMenu();
        });

        backdrop.addEventListener("click", closeMenu);

        sidebar.querySelectorAll(".menu-item").forEach(link => {
            link.addEventListener("click", closeMenu);
        });

        document.addEventListener("keydown", event => {
            if (event.key === "Escape") closeMenu();
        });

        window.addEventListener("resize", () => {
            if (window.innerWidth > 768) closeMenu();
        });
    }

    // PERFIL DEL ADMINISTRADOR
    function profile() {
        const name = sessionStorage.getItem("nombre") || "Administrador";
        const surname = sessionStorage.getItem("apellido") || "";
        const fullName = `${name} ${surname}`.trim();
        const initials = (name[0] || "A") + (surname[0] || "");

        $("sidebarAdminName").textContent = fullName;
        $("topbarAdminName").textContent = fullName;
        $("sidebarAdminInitials").textContent = initials.toUpperCase();
        $("topbarAdminInitials").textContent = initials.toUpperCase();

        $("currentDate").textContent = "BAKE TRACK · ANÁLISIS";
    }

    // FILTRAR MOVIMIENTOS POR FECHA
    function filteredMovements() {
        return state.movimientos.filter(movement => {
            const date = String(movement.fecha || "").slice(0, 10);

            return (
                (!$("startDate").value || date >= $("startDate").value) &&
                (!$("endDate").value || date <= $("endDate").value)
            );
        });
    }

    // CALCULAR CANTIDAD TOTAL
    function total(movement) {
        return (movement.detalles || []).reduce(
            (sum, detail) => sum + Number(detail.cantidad || 0),
            0
        );
    }

    // MOSTRAR REPORTES
    function render() {
        const list = filteredMovements();
        const types = ["ENTRADA", "SALIDA", "TRANSFERENCIA"];

        const counts = types.map(type =>
            list.filter(movement => movement.tipoMovimiento === type).length
        );

        const max = Math.max(...counts, 1);

        $("totalMovements").textContent = num(list.length);
        $("totalEntries").textContent = num(counts[0]);
        $("totalExits").textContent = num(counts[1]);
        $("totalTransfers").textContent = num(counts[2]);

        // GRÁFICO
        $("movementChart").innerHTML = types.map((type, index) => `
            <div class="report-bar-item">
                <div class="report-bar-label">
                    <span>${type}</span>
                    <strong>${num(counts[index])}</strong>
                </div>

                <div class="report-bar-track">
                    <div
                        class="report-bar-fill tone-${index}"
                        style="width:${counts[index] / max * 100}%">
                    </div>
                </div>
            </div>
        `).join("");

        // TABLA DE MOVIMIENTOS
        const rows = [...list].sort(
            (a, b) => new Date(b.fecha) - new Date(a.fecha)
        );

        $("reportMovementsBody").innerHTML = rows.length
            ? rows.map(movement => `
                <tr>
                    <td>#${escapeHTML(movement.idMovimiento)}</td>

                    <td>
                        ${escapeHTML(
                            new Date(movement.fecha).toLocaleString("es-CO")
                        )}
                    </td>

                    <td>
                        <span class="report-pill">
                            ${escapeHTML(movement.tipoMovimiento)}
                        </span>
                    </td>

                    <td>
                        ${escapeHTML([
                            movement.usuario?.nombre,
                            movement.usuario?.apellido
                        ].filter(Boolean).join(" ") || "—")}
                    </td>

                    <td>
                        ${escapeHTML(
                            (movement.detalles || [])
                                .map(detail => detail.producto?.nombre || "Producto")
                                .join(", ") || "—"
                        )}
                    </td>

                    <td>
                        <strong>${num(total(movement))}</strong>
                    </td>
                </tr>
            `).join("")
            : `
                <tr>
                    <td colspan="6" class="table-empty">
                        No hay movimientos en este periodo.
                    </td>
                </tr>
            `;

        $("movementResults").textContent =
            `${list.length} movimiento${list.length === 1 ? "" : "s"}`;
    }

    // PRODUCTOS MÁS MOVILIZADOS
    function renderTop() {
        const products = state.top;

        $("topProductsBody").innerHTML = products.length
            ? products.map(product => `
                <tr>
                    <td>
                        <strong>${escapeHTML(product.nombreProducto)}</strong>
                    </td>
                    <td>${num(product.totalMovilizado)}</td>
                </tr>
            `).join("")
            : `
                <tr>
                    <td colspan="2" class="table-empty">
                        Sin datos de productos movilizados.
                    </td>
                </tr>
            `;
    }

    // INVENTARIO POR BODEGA
    function renderWarehouses() {
        const warehouses = state.bodegas;

        $("warehouseSummaryBody").innerHTML = warehouses.length
            ? warehouses.map(warehouse => `
                <tr>
                    <td>
                        <strong>${escapeHTML(warehouse.nombreBodega)}</strong>
                    </td>
                    <td>${num(warehouse.cantidadProductos)}</td>
                    <td>${num(warehouse.totalStock)}</td>
                    <td>
                        ${warehouse.capacidad == null
                            ? "—"
                            : num(warehouse.capacidad)}
                    </td>
                </tr>
            `).join("")
            : `
                <tr>
                    <td colspan="4" class="table-empty">
                        No hay resumen de bodegas.
                    </td>
                </tr>
            `;
    }

    // CARGAR INFORMACIÓN DESDE EL BACKEND
    async function load() {
        $("reportMovementsBody").innerHTML = `
            <tr>
                <td colspan="6" class="table-loading">
                    Cargando información...
                </td>
            </tr>
        `;

        try {
            const [movements, topProducts, warehouses] = await Promise.all([
                apiRequest("/api/movimientos"),
                apiRequest("/api/movimientos/productos-mayor-movimiento"),
                apiRequest("/api/inventarios/resumen-por-bodega")
            ]);

            state.movimientos = Array.isArray(movements) ? movements : [];
            state.top = Array.isArray(topProducts) ? topProducts : [];
            state.bodegas = Array.isArray(warehouses) ? warehouses : [];

            render();
            renderTop();
            renderWarehouses();

        } catch (error) {
            console.error(error);

            $("reportMovementsBody").innerHTML = `
                <tr>
                    <td colspan="6" class="table-error">
                        ${escapeHTML(error.message)}
                    </td>
                </tr>
            `;

            if (window.LogiTrackUI?.mostrarMensaje) {
                window.LogiTrackUI.mostrarMensaje({
                    tipo: "error",
                    titulo: "No se pudieron cargar los reportes",
                    mensaje: error.message
                });
            }
        }
    }

    // APLICAR FILTROS
    $("applyReportFilters").addEventListener("click", () => {
        const start = $("startDate").value;
        const end = $("endDate").value;

        if (start && end && start > end) {
            if (window.LogiTrackUI?.mostrarMensaje) {
                window.LogiTrackUI.mostrarMensaje({
                    tipo: "warning",
                    mensaje: "La fecha inicial no puede ser posterior a la fecha final."
                });
            } else {
                alert("La fecha inicial no puede ser posterior a la fecha final.");
            }

            return;
        }

        render();
    });

    // LIMPIAR FILTROS
    $("clearReportFilters").addEventListener("click", () => {
        $("startDate").value = "";
        $("endDate").value = "";
        render();
    });

    // ACTUALIZAR REPORTES
    $("refreshReports").addEventListener("click", load);

    // CERRAR SESIÓN
    $("logoutButton").addEventListener("click", () => {
        sessionStorage.clear();
        location.href = "index.html";
    });

    // INICIALIZACIÓN
    configureMobileMenu();
    profile();
    load();
});