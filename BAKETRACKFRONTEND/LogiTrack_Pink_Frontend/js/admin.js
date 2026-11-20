
document.addEventListener("DOMContentLoaded", () => {

    // PROTECCIÓN DEL PANEL
    const token = sessionStorage.getItem("token");
    const rol = sessionStorage.getItem("rol");

    if (!token || rol !== "ADMIN") {
        window.location.replace("index.html");
        return;
    }

    mostrarFechaActual();
    mostrarDatosAdministrador();
    configurarCerrarSesion();
    configurarMenuMovil();
    cargarDashboard();
});


function mostrarFechaActual() {
    const elemento = document.getElementById("currentDate");
    if (!elemento) return;
    elemento.textContent = "PANEL DE CONTROL";
}


function mostrarDatosAdministrador() {
    const nombre = sessionStorage.getItem("nombre") || "Administrador";
    const apellido = sessionStorage.getItem("apellido") || "";
    const nombreCompleto = `${nombre} ${apellido}`.trim();

    const iniciales = nombreCompleto
        .split(" ")
        .filter(Boolean)
        .map(palabra => palabra.charAt(0))
        .join("")
        .substring(0, 2)
        .toUpperCase();

    const elementos = {
        sidebarName: document.getElementById("sidebarAdminName"),
        topbarName: document.getElementById("topbarAdminName"),
        sidebarInitials: document.getElementById("sidebarAdminInitials"),
        topbarInitials: document.getElementById("topbarAdminInitials"),
        topbarRole: document.getElementById("topbarAdminRole")
    };

    if (elementos.sidebarName) elementos.sidebarName.textContent = nombreCompleto;
    if (elementos.topbarName) elementos.topbarName.textContent = nombreCompleto;
    if (elementos.sidebarInitials) elementos.sidebarInitials.textContent = iniciales;
    if (elementos.topbarInitials) elementos.topbarInitials.textContent = iniciales;
    if (elementos.topbarRole) elementos.topbarRole.textContent = "ADMIN";
}


function configurarCerrarSesion() {
    document.getElementById("logoutButton")?.addEventListener("click", () => {
        sessionStorage.clear();
        window.location.replace("index.html");
    });
}


// =========================================
// MENÚ RESPONSIVE PARA CELULARES
// =========================================

function configurarMenuMovil() {

    const sidebar = document.querySelector(".sidebar");
    const topbar = document.querySelector(".topbar");

    if (!sidebar || !topbar) return;

    // Crear botón hamburguesa
    const botonMenu = document.createElement("button");

    botonMenu.type = "button";
    botonMenu.className = "menu-toggle";
    botonMenu.id = "mobileMenuToggle";
    botonMenu.innerHTML = "☰";
    botonMenu.setAttribute("aria-label", "Abrir menú");
    botonMenu.setAttribute("aria-expanded", "false");
    botonMenu.setAttribute("aria-controls", "mobileSidebar");

    sidebar.id = "mobileSidebar";

    // Insertar botón en el encabezado
    topbar.insertBefore(botonMenu, topbar.firstChild);

    // Crear fondo oscuro para cerrar el menú
    const fondoMenu = document.createElement("button");

    fondoMenu.type = "button";
    fondoMenu.className = "menu-backdrop";
    fondoMenu.setAttribute("aria-label", "Cerrar menú");

    document.body.appendChild(fondoMenu);

    function abrirMenu() {
        document.body.classList.add("menu-open");

        botonMenu.setAttribute("aria-expanded", "true");
        botonMenu.setAttribute("aria-label", "Cerrar menú");

        fondoMenu.setAttribute("aria-hidden", "false");
    }

    function cerrarMenu() {
        document.body.classList.remove("menu-open");

        botonMenu.setAttribute("aria-expanded", "false");
        botonMenu.setAttribute("aria-label", "Abrir menú");

        fondoMenu.setAttribute("aria-hidden", "true");
    }

    function alternarMenu() {
        const menuAbierto = document.body.classList.contains("menu-open");

        if (menuAbierto) {
            cerrarMenu();
        } else {
            abrirMenu();
        }
    }

    botonMenu.addEventListener("click", alternarMenu);

    fondoMenu.addEventListener("click", cerrarMenu);

    // Cerrar al seleccionar una opción
    sidebar.querySelectorAll(".menu-item").forEach(enlace => {
        enlace.addEventListener("click", cerrarMenu);
    });

    // Cerrar con Escape
    document.addEventListener("keydown", evento => {
        if (evento.key === "Escape") {
            cerrarMenu();
        }
    });

    // Cerrar al regresar a escritorio
    window.matchMedia("(min-width: 761px)").addEventListener("change", evento => {
        if (evento.matches) {
            cerrarMenu();
        }
    });
}


// =========================================
// CARGAR INFORMACIÓN DEL DASHBOARD
// =========================================

async function cargarDashboard() {

    const consultas = [
        apiRequest("/api/productos"),
        apiRequest("/api/bodegas"),
        apiRequest("/api/inventarios"),
        apiRequest("/api/movimientos"),
        apiRequest("/api/inventarios/resumen-por-bodega"),
        apiRequest("/api/inventarios/stock-bajo?limite=10")
    ];

    const resultados = await Promise.allSettled(consultas);

    const obtener = (indice) => {

        if (resultados[indice].status === "fulfilled") {
            return resultados[indice].value || [];
        }

        console.error(
            "Error en consulta del dashboard:",
            resultados[indice].reason
        );

        return [];
    };

    const productos = obtener(0);
    const bodegas = obtener(1);
    const inventarios = obtener(2);
    const movimientos = obtener(3);
    const resumenBodegas = obtener(4);
    const stockBajo = obtener(5);

    mostrarIndicadores(productos, bodegas, inventarios, movimientos);
    mostrarResumenBodegas(resumenBodegas);
    mostrarActividadReciente(movimientos);
    mostrarStockBajo(stockBajo);
}


function mostrarIndicadores(productos, bodegas, inventarios, movimientos) {

    const totalStock = inventarios.reduce(
        (total, item) => total + Number(item.stockActual || 0),
        0
    );

    document.getElementById("totalProductos").textContent =
        productos.length.toLocaleString("es-CO");

    document.getElementById("totalBodegas").textContent =
        bodegas.length.toLocaleString("es-CO");

    document.getElementById("stockDisponible").textContent =
        totalStock.toLocaleString("es-CO");

    document.getElementById("totalMovimientos").textContent =
        movimientos.length.toLocaleString("es-CO");
}


function mostrarResumenBodegas(resumen) {

    const contenedor = document.getElementById("warehouseSummary");

    if (!contenedor) return;

    if (!resumen.length) {
        contenedor.innerHTML = "<p>No hay datos de inventario por bodega.</p>";
        return;
    }

    const maxStock = Math.max(
        ...resumen.map(item => Number(item.totalStock || 0)),
        1
    );

    contenedor.innerHTML = resumen.map(bodega => {

        const stock = Number(bodega.totalStock || 0);
        const porcentaje = Math.min((stock / maxStock) * 100, 100);

        return `
            <div class="warehouse-item">
                <div class="warehouse-info">
                    <strong>${escaparHTML(bodega.nombreBodega || "Bodega")}</strong>
                    <span>${stock.toLocaleString("es-CO")} unidades</span>
                </div>

                <div class="warehouse-progress">
                    <div class="warehouse-progress-fill" style="width:${porcentaje}%"></div>
                </div>

                <small>${Number(bodega.cantidadProductos || 0)} productos diferentes</small>
            </div>
        `;

    }).join("");
}


function mostrarActividadReciente(movimientos) {

    const contenedor = document.getElementById("recentActivity");

    if (!contenedor) return;

    if (!movimientos.length) {
        contenedor.innerHTML = "<p>No hay movimientos registrados.</p>";
        return;
    }

    const recientes = [...movimientos]
        .sort((a, b) => new Date(b.fecha) - new Date(a.fecha))
        .slice(0, 5);

    contenedor.innerHTML = recientes.map(movimiento => {

        const fecha = movimiento.fecha
            ? new Date(movimiento.fecha).toLocaleString("es-CO")
            : "Fecha no disponible";

        const usuario = movimiento.usuario
            ? `${movimiento.usuario.nombre || ""} ${movimiento.usuario.apellido || ""}`.trim()
            : "Usuario";

        const productos = (movimiento.detalles || [])
            .map(detalle =>
                `${escaparHTML(detalle.producto?.nombre || "Producto")} (${Number(detalle.cantidad || 0)})`
            ).join(", ");

        return `
            <div class="activity-item">
                <strong>${escaparHTML(movimiento.tipoMovimiento || "Movimiento")}</strong>
                <p>${productos || "Sin detalles"}</p>
                <small>${escaparHTML(usuario)} · ${escaparHTML(fecha)}</small>
            </div>
        `;

    }).join("");
}


function mostrarStockBajo(productos) {

    const contenedor = document.getElementById("lowStockProducts");

    if (!contenedor) return;

    if (!productos.length) {

        contenedor.innerHTML = `
            <div style="text-align:center;padding:30px;color:#6E5890">
                <p>¡Excelente! No hay productos con stock bajo.</p>
            </div>
        `;

        return;
    }

    contenedor.innerHTML = productos.map(item => {

        const nombre = escaparHTML(item.producto?.nombre || "Producto");
        const bodega = escaparHTML(item.bodega?.nombre || "Bodega");
        const stock = Number(item.stockActual || 0);

        return `
            <article style="background:#F8F7FC;border:1px solid #E9E4F1;border-radius:16px;padding:20px;">
                <span>${stock} ${stock === 1 ? "unidad" : "unidades"}</span>
                <h3>${nombre}</h3>
                <p>Bodega: ${bodega}</p>
                <strong style="color:#A87529;">Requiere atención</strong>
            </article>
        `;

    }).join("");
}


// =========================================
// SEGURIDAD: ESCAPAR HTML
// =========================================

function escaparHTML(valor) {

    return String(valor).replace(/[&<>"']/g, caracter => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[caracter]);
}
