
document.addEventListener("DOMContentLoaded", () => {

    // 1. PROTECCIÓN DEL PANEL
    const token = sessionStorage.getItem("token");
    const rol = sessionStorage.getItem("rol");

    if (!token || rol !== "EMPLEADO") {
        window.location.replace("index.html");
        return;
    }

    // 2. INICIALIZACIÓN
    mostrarDatosEmpleado();
    mostrarFechaActual();
    configurarCerrarSesion();
    configurarFiltros();
    configurarActualizar();

    cargarMisMovimientos();
});


// 3. DATOS DEL EMPLEADO
function mostrarDatosEmpleado() {

    const nombre = sessionStorage.getItem("nombre") || "Empleado";
    const apellido = sessionStorage.getItem("apellido") || "";

    const nombreCompleto = `${nombre} ${apellido}`.trim();

    const iniciales = nombreCompleto
        .split(" ")
        .filter(Boolean)
        .map(palabra => palabra.charAt(0))
        .join("")
        .substring(0, 2)
        .toUpperCase();

    [
        "employeeName",
        "topbarEmployeeName",
        "welcomeEmployee"
    ].forEach(id => {
        const elemento = document.getElementById(id);
        if (elemento) elemento.textContent = nombreCompleto;
    });

    [
        "employeeInitials",
        "topbarEmployeeInitials"
    ].forEach(id => {
        const elemento = document.getElementById(id);
        if (elemento) elemento.textContent = iniciales;
    });
}


// 4. FECHA ACTUAL
function mostrarFechaActual() {

    const elemento = document.getElementById("currentDate");

    if (!elemento) return;

    elemento.textContent = "TU ACTIVIDAD";
}


// 5. CERRAR SESIÓN
function configurarCerrarSesion() {

    document.getElementById("logoutButton")
        ?.addEventListener("click", () => {

            sessionStorage.clear();
            window.location.replace("index.html");

        });
}


// 6. ACTUALIZAR INFORMACIÓN
function configurarActualizar() {

    document.getElementById("refreshEmployee")
        ?.addEventListener("click", cargarMisMovimientos);

}


// 7. FILTROS DE REPORTES
function configurarFiltros() {

    document.getElementById("applyEmployeeFilters")
        ?.addEventListener("click", () => {

            const desde = document.getElementById("employeeStartDate").value;
            const hasta = document.getElementById("employeeEndDate").value;

            if (desde && hasta && desde > hasta) {
                alert("La fecha inicial no puede ser posterior a la fecha final.");
                return;
            }

            renderMisReportes(window.movimientosEmpleado || []);

        });

    document.getElementById("clearEmployeeFilters")
        ?.addEventListener("click", () => {

            document.getElementById("employeeStartDate").value = "";
            document.getElementById("employeeEndDate").value = "";

            renderMisReportes(window.movimientosEmpleado || []);

        });

}


// 8. CARGAR MOVIMIENTOS
async function cargarMisMovimientos() {

    const tbody = document.getElementById("myMovementsBody");

    if (!tbody) return;

    tbody.innerHTML = `
        <tr>
            <td colspan="5" class="table-loading">
                Cargando actividad...
            </td>
        </tr>
    `;

    try {

        const todosLosMovimientos =
            await apiRequest("/api/movimientos");

        const idUsuario =
            Number(sessionStorage.getItem("idUsuario"));

        // Filtrar por el usuario autenticado
        const misMovimientos = (todosLosMovimientos || []).filter(movimiento => {

            return Number(movimiento.usuario?.idUsuario) === idUsuario;

        });

        // Ordenar del más reciente al más antiguo
        misMovimientos.sort((a, b) =>
            new Date(b.fecha) - new Date(a.fecha)
        );

        // Guardar los movimientos personales
        window.movimientosEmpleado = misMovimientos;

        // Actualizar panel
        mostrarIndicadoresEmpleado(misMovimientos);
        mostrarTablaMovimientos(misMovimientos);
        renderMisReportes(misMovimientos);

    } catch (error) {

        console.error("Error al cargar movimientos:", error);

        tbody.innerHTML = `
            <tr>
                <td colspan="5">
                    No fue posible cargar tus movimientos.
                </td>
            </tr>
        `;

        mostrarErrorReportes();

    }

}


// 9. INDICADORES PRINCIPALES
function mostrarIndicadoresEmpleado(movimientos) {

    const entradas = movimientos.filter(m =>
        String(m.tipoMovimiento).toUpperCase() === "ENTRADA"
    ).length;

    const salidas = movimientos.filter(m =>
        String(m.tipoMovimiento).toUpperCase() === "SALIDA"
    ).length;

    const transferencias = movimientos.filter(m =>
        String(m.tipoMovimiento).toUpperCase() === "TRANSFERENCIA"
    ).length;

    document.getElementById("myMovementCount").textContent =
        movimientos.length.toLocaleString("es-CO");

    document.getElementById("myEntries").textContent =
        entradas.toLocaleString("es-CO");

    document.getElementById("myExits").textContent =
        salidas.toLocaleString("es-CO");

    document.getElementById("myTransfers").textContent =
        transferencias.toLocaleString("es-CO");

}


// 10. TABLA DE MOVIMIENTOS RECIENTES
function mostrarTablaMovimientos(movimientos) {

    const tbody = document.getElementById("myMovementsBody");

    if (!tbody) return;

    if (!movimientos.length) {

        tbody.innerHTML = `
            <tr>
                <td colspan="5">
                    Todavía no tienes movimientos registrados.
                </td>
            </tr>
        `;

        return;
    }

    tbody.innerHTML = movimientos.map(movimiento => {

        const fecha = movimiento.fecha
            ? new Date(movimiento.fecha).toLocaleString("es-CO")
            : "No disponible";

        const origen = movimiento.bodegaOrigen?.nombre || "—";
        const destino = movimiento.bodegaDestino?.nombre || "—";

        return `
            <tr>
                <td>${escaparHTML(movimiento.idMovimiento ?? "—")}</td>

                <td>${escaparHTML(fecha)}</td>

                <td>
                    <span class="report-pill">
                        ${escaparHTML(movimiento.tipoMovimiento || "—")}
                    </span>
                </td>

                <td>${escaparHTML(origen)}</td>

                <td>${escaparHTML(destino)}</td>
            </tr>
        `;

    }).join("");

}


// 11. REPORTES PERSONALES
function renderMisReportes(movimientos) {

    const desde = document.getElementById("employeeStartDate")?.value;
    const hasta = document.getElementById("employeeEndDate")?.value;

    // Filtrar por fechas
    const lista = movimientos.filter(movimiento => {

        const fecha = String(movimiento.fecha || "").slice(0, 10);

        return (!desde || fecha >= desde) &&
               (!hasta || fecha <= hasta);

    });

    const tipos = [
        "ENTRADA",
        "SALIDA",
        "TRANSFERENCIA"
    ];

    const cantidades = tipos.map(tipo =>
        lista.filter(m =>
            String(m.tipoMovimiento).toUpperCase() === tipo
        ).length
    );

    const [entradas, salidas, transferencias] = cantidades;

    // Indicadores de reportes
    document.getElementById("employeeReportMovements").textContent =
        lista.length.toLocaleString("es-CO");

    document.getElementById("employeeReportEntries").textContent =
        entradas.toLocaleString("es-CO");

    document.getElementById("employeeReportExits").textContent =
        salidas.toLocaleString("es-CO");

    document.getElementById("employeeReportTransfers").textContent =
        transferencias.toLocaleString("es-CO");


    // Gráfico de movimientos
    const contenedorGrafico =
        document.getElementById("employeeMovementChart");

    const maximo = Math.max(...cantidades, 1);

    contenedorGrafico.innerHTML = tipos.map((tipo, indice) => {

        const porcentaje = cantidades[indice] / maximo * 100;

        return `
            <div class="report-bar-item">

                <div class="report-bar-label">
                    <span>${tipo}</span>
                    <strong>${cantidades[indice].toLocaleString("es-CO")}</strong>
                </div>

                <div class="report-bar-track">
                    <div class="report-bar-fill tone-${indice}"
                         style="width: ${porcentaje}%">
                    </div>
                </div>

            </div>
        `;

    }).join("");


    // Productos movilizados
    const productos = {};

    lista.forEach(movimiento => {

        (movimiento.detalles || []).forEach(detalle => {

            const nombre =
                detalle.producto?.nombre || "Producto";

            productos[nombre] =
                (productos[nombre] || 0) +
                Number(detalle.cantidad || 0);

        });

    });

    const productosOrdenados = Object.entries(productos)
        .sort((a, b) => b[1] - a[1]);

    const tbodyProductos =
        document.getElementById("employeeTopProducts");

    tbodyProductos.innerHTML = productosOrdenados.length

        ? productosOrdenados.map(([nombre, cantidad]) => `
            <tr>
                <td>
                    <strong>${escaparHTML(nombre)}</strong>
                </td>

                <td>${cantidad.toLocaleString("es-CO")}</td>
            </tr>
        `).join("")

        : `
            <tr>
                <td colspan="2">
                    No hay productos movilizados en este periodo.
                </td>
            </tr>
        `;


    // Tabla detallada de reportes
    const tbodyReportes =
        document.getElementById("employeeReportTable");

    tbodyReportes.innerHTML = lista.length

        ? lista.map(movimiento => {

            const fecha = movimiento.fecha
                ? new Date(movimiento.fecha).toLocaleString("es-CO")
                : "No disponible";

            const nombresProductos = (movimiento.detalles || [])
                .map(d => d.producto?.nombre || "Producto")
                .join(", ");

            const total = (movimiento.detalles || [])
                .reduce((suma, d) =>
                    suma + Number(d.cantidad || 0), 0
                );

            return `
                <tr>

                    <td>#${escaparHTML(movimiento.idMovimiento)}</td>

                    <td>${escaparHTML(fecha)}</td>

                    <td>
                        <span class="report-pill">
                            ${escaparHTML(movimiento.tipoMovimiento)}
                        </span>
                    </td>

                    <td>${escaparHTML(nombresProductos || "—")}</td>

                    <td>
                        <strong>${total.toLocaleString("es-CO")}</strong>
                    </td>

                </tr>
            `;

        }).join("")

        : `
            <tr>
                <td colspan="5">
                    No hay movimientos en este periodo.
                </td>
            </tr>
        `;


    // Contador de resultados
    document.getElementById("employeeReportResults").textContent =
        `${lista.length} movimiento${lista.length === 1 ? "" : "s"}`;

}


// 12. ERROR DE REPORTES
function mostrarErrorReportes() {

    document.getElementById("employeeMovementChart").innerHTML =
        "<p>No fue posible cargar el gráfico.</p>";

    document.getElementById("employeeTopProducts").innerHTML =
        `<tr><td colspan="2">No fue posible cargar los productos.</td></tr>`;

    document.getElementById("employeeReportTable").innerHTML =
        `<tr><td colspan="5">No fue posible cargar los reportes.</td></tr>`;

}


// 13. PROTECCIÓN DE TEXTO HTML
function escaparHTML(valor) {

    return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[caracter]);

}
