// =========================================
// BAKE TRACK - MÓDULO DE PRODUCTOS
// =========================================

let productos = [];
let marcas = [];
let categorias = [];

let modoEdicion = false;


// =========================================
// ELEMENTOS DEL DOM
// =========================================

const tablaProductos = document.getElementById("tablaProductos");
const modalProducto = document.getElementById("modalProducto");
const formProducto = document.getElementById("formProducto");

const buscarProducto = document.getElementById("buscarProducto");
const filtroMarca = document.getElementById("filtroMarca");
const filtroCategoria = document.getElementById("filtroCategoria");
const filtroEstado = document.getElementById("filtroEstado");

const selectMarca = document.getElementById("marcaProducto");
const selectCategoria = document.getElementById("categoriaProducto");


// =========================================
// VALIDACIÓN DE SESIÓN
// =========================================

function validarSesion() {

    const token = sessionStorage.getItem("token");
    const rol = sessionStorage.getItem("rol");

    if (!token || rol !== "ADMIN") {
        window.location.href = "index.html";
        return false;
    }

    return true;
}


// =========================================
// INFORMACIÓN DEL ADMINISTRADOR
// =========================================

function cargarPerfilAdministrador() {

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


// =========================================
// MENÚ RESPONSIVE PARA MÓVILES
// =========================================

function configurarMenuMovil() {

    const sidebar = document.querySelector(".sidebar");
    const topbar = document.querySelector(".topbar");

    if (!sidebar || !topbar) return;

    // Evitar duplicar el botón
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

    document.addEventListener("keydown", evento => {

        if (evento.key === "Escape") {
            cerrarMenu();
        }

    });

    window.matchMedia("(min-width: 761px)").addEventListener("change", evento => {

        if (evento.matches) {
            cerrarMenu();
        }

    });
}


// =========================================
// CERRAR SESIÓN
// =========================================

async function cerrarSesion() {

    const confirmar = await LogiTrackUI.confirmar({

        titulo: "¿Cerrar sesión?",
        mensaje: "Tu sesión de Bake Track se cerrará. ¿Deseas continuar?",
        textoConfirmar: "Sí, cerrar sesión",
        textoCancelar: "Seguir aquí",
        tipo: "warning"

    });

    if (!confirmar) return;

    sessionStorage.removeItem("token");
    sessionStorage.removeItem("rol");
    sessionStorage.removeItem("idUsuario");
    sessionStorage.removeItem("nombre");
    sessionStorage.removeItem("apellido");
    sessionStorage.removeItem("correo");

    window.location.href = "index.html";
}


// =========================================
// NOTIFICACIONES PERSONALIZADAS
// =========================================

function mostrarNotificacion(mensaje, tipo = "success") {

    const configuracion = {

        success: {
            titulo: "Operación realizada"
        },

        error: {
            titulo: "Ha ocurrido un problema"
        },

        warning: {
            titulo: "Atención"
        },

        info: {
            titulo: "Información"
        }

    };

    LogiTrackUI.mostrarMensaje({

        titulo: configuracion[tipo]?.titulo || "Notificación",
        mensaje: mensaje,
        tipo: tipo

    });
}


// =========================================
// ESCAPAR TEXTO HTML
// =========================================

function escaparHTML(valor) {

    return String(valor ?? "").replace(/[&<>"']/g, caracter => {

        const caracteres = {

            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"

        };

        return caracteres[caracter];

    });

}


// =========================================
// CARGAR MARCAS
// =========================================

async function cargarMarcas() {

    try {

        marcas = await apiRequest("/api/marcas");

        if (!Array.isArray(marcas)) {
            marcas = [];
        }

        llenarSelectMarcas();

    } catch (error) {

        console.error("Error al cargar marcas:", error);

        mostrarNotificacion(
            "No fue posible cargar las marcas.",
            "error"
        );

    }

}


function llenarSelectMarcas() {

    const opciones = marcas.map(marca => `

        <option value="${marca.idMarca}">
            ${escaparHTML(marca.nombre)}
        </option>

    `).join("");

    selectMarca.innerHTML =
        '<option value="">Selecciona una marca</option>' + opciones;

    filtroMarca.innerHTML =
        '<option value="">Todas las marcas</option>' + opciones;

}


// =========================================
// CARGAR CATEGORÍAS
// =========================================

async function cargarCategorias() {

    try {

        categorias = await apiRequest("/api/categorias");

        if (!Array.isArray(categorias)) {
            categorias = [];
        }

        llenarSelectCategorias();

    } catch (error) {

        console.error("Error al cargar categorías:", error);

        mostrarNotificacion(
            "No fue posible cargar las categorías.",
            "error"
        );

    }

}


function llenarSelectCategorias() {

    const opciones = categorias.map(categoria => `

        <option value="${categoria.idCategoria}">
            ${escaparHTML(categoria.nombre)}
        </option>

    `).join("");

    selectCategoria.innerHTML =
        '<option value="">Selecciona una categoría</option>' + opciones;

    filtroCategoria.innerHTML =
        '<option value="">Todas las categorías</option>' + opciones;

}


// =========================================
// CARGAR PRODUCTOS
// =========================================

async function cargarProductos() {

    tablaProductos.innerHTML = `

        <tr>
            <td colspan="7" class="table-message">
                Cargando productos...
            </td>
        </tr>

    `;

    try {

        const respuesta = await apiRequest("/api/productos");

        productos = Array.isArray(respuesta) ? respuesta : [];

        actualizarIndicadores();
        aplicarFiltros();

    } catch (error) {

        console.error("Error al cargar productos:", error);

        tablaProductos.innerHTML = `

            <tr>
                <td colspan="7" class="table-message">
                    No fue posible cargar los productos.
                </td>
            </tr>

        `;

        mostrarNotificacion(
            "Error al consultar los productos.",
            "error"
        );

    }

}


// =========================================
// INDICADORES
// =========================================

function actualizarIndicadores() {

    const total = productos.length;

    const activos = productos.filter(producto =>
        producto.estado === true
    ).length;

    const inactivos = productos.filter(producto =>
        producto.estado === false
    ).length;

    document.getElementById("totalProductos").textContent = total;
    document.getElementById("productosActivos").textContent = activos;
    document.getElementById("productosInactivos").textContent = inactivos;

}


// =========================================
// FILTROS
// =========================================

function aplicarFiltros() {

    const texto = buscarProducto.value.trim().toLowerCase();

    const marcaSeleccionada = filtroMarca.value;
    const categoriaSeleccionada = filtroCategoria.value;
    const estadoSeleccionado = filtroEstado.value;

    const filtrados = productos.filter(producto => {

        const coincideNombre =
            (producto.nombre || "").toLowerCase().includes(texto);

        const coincideMarca =
            !marcaSeleccionada ||
            String(producto.marca?.idMarca) === marcaSeleccionada;

        const coincideCategoria =
            !categoriaSeleccionada ||
            String(producto.categoria?.idCategoria) === categoriaSeleccionada;

        const coincideEstado =
            estadoSeleccionado === "" ||
            String(producto.estado) === estadoSeleccionado;

        return coincideNombre &&
            coincideMarca &&
            coincideCategoria &&
            coincideEstado;

    });

    renderizarProductos(filtrados);

}


// =========================================
// MOSTRAR PRODUCTOS EN LA TABLA
// =========================================

function renderizarProductos(lista) {

    if (lista.length === 0) {

        tablaProductos.innerHTML = `

            <tr>
                <td colspan="7" class="table-message">
                    No se encontraron productos.
                </td>
            </tr>

        `;

        document.getElementById("contadorProductos").textContent =
            "Mostrando 0 productos";

        return;
    }

    tablaProductos.innerHTML = lista.map(producto => {

        const estadoActivo = producto.estado === true;

        return `

            <tr>

                <td>${escaparHTML(producto.idProducto)}</td>

                <td>
                    <span class="product-name">
                        ${escaparHTML(producto.nombre)}
                    </span>
                </td>

                <td>
                    <span class="product-description">
                        ${escaparHTML(producto.descripcion || "Sin descripción")}
                    </span>
                </td>

                <td>
                    ${escaparHTML(producto.marca?.nombre || "Sin marca")}
                </td>

                <td>
                    ${escaparHTML(producto.categoria?.nombre || "Sin categoría")}
                </td>

                <td>
                    <span class="status-badge ${
                        estadoActivo ? "status-active" : "status-inactive"
                    }">
                        ${estadoActivo ? "Activo" : "Inactivo"}
                    </span>
                </td>

                <td>

                    <div class="product-actions">

                        <button
                            type="button"
                            class="btn-action btn-edit"
                            data-action="editar"
                            data-id="${escaparHTML(producto.idProducto)}"
                            title="Editar producto">
                            ✎
                        </button>

                        <button
                            type="button"
                            class="btn-action btn-delete"
                            data-action="eliminar"
                            data-id="${escaparHTML(producto.idProducto)}"
                            title="Eliminar producto">
                            🗑
                        </button>

                    </div>

                </td>

            </tr>

        `;

    }).join("");

    document.getElementById("contadorProductos").textContent =
        `Mostrando ${lista.length} de ${productos.length} productos`;

}


// =========================================
// ABRIR MODAL
// =========================================

function abrirModalProducto() {

    modoEdicion = false;

    formProducto.reset();

    document.getElementById("idProducto").value = "";

    document.getElementById("tituloModalProducto").textContent =
        "Nuevo producto";

    document.getElementById("btnGuardarProducto").textContent =
        "Guardar producto";

    modalProducto.classList.add("active");

}


// =========================================
// CERRAR MODAL
// =========================================

function cerrarModalProducto() {

    modalProducto.classList.remove("active");

    formProducto.reset();

}


// =========================================
// EDITAR PRODUCTO
// =========================================

function editarProducto(idProducto) {

    const producto = productos.find(item =>
        String(item.idProducto) === String(idProducto)
    );

    if (!producto) {

        mostrarNotificacion(
            "No se encontró el producto.",
            "error"
        );

        return;
    }

    modoEdicion = true;

    document.getElementById("idProducto").value = producto.idProducto;

    document.getElementById("nombreProducto").value =
        producto.nombre || "";

    document.getElementById("descripcionProducto").value =
        producto.descripcion || "";

    selectMarca.value = producto.marca?.idMarca || "";
    selectCategoria.value = producto.categoria?.idCategoria || "";

    document.getElementById("tituloModalProducto").textContent =
        "Editar producto";

    document.getElementById("btnGuardarProducto").textContent =
        "Actualizar producto";

    modalProducto.classList.add("active");

}


// =========================================
// REGISTRAR / ACTUALIZAR PRODUCTO
// =========================================

async function guardarProducto(evento) {

    evento.preventDefault();

    const idProducto = document.getElementById("idProducto").value;

    const datosProducto = {

        nombre: document.getElementById("nombreProducto").value.trim(),

        descripcion: document.getElementById("descripcionProducto").value.trim(),

        idMarca: Number(selectMarca.value),

        idCategoria: Number(selectCategoria.value)

    };

    if (
        !datosProducto.nombre ||
        !datosProducto.idMarca ||
        !datosProducto.idCategoria
    ) {

        mostrarNotificacion(
            "Completa los campos obligatorios.",
            "warning"
        );

        return;
    }

    const botonGuardar = document.getElementById("btnGuardarProducto");

    botonGuardar.disabled = true;

    botonGuardar.textContent = modoEdicion
        ? "Actualizando..."
        : "Guardando...";

    try {

        if (modoEdicion) {

            await apiRequest(`/api/productos/${idProducto}`, {

                method: "PUT",

                body: JSON.stringify(datosProducto)

            });

        } else {

            await apiRequest("/api/productos", {

                method: "POST",

                body: JSON.stringify(datosProducto)

            });

        }

        cerrarModalProducto();

        await cargarProductos();

        mostrarNotificacion(

            modoEdicion
                ? "Producto actualizado correctamente."
                : "Producto registrado correctamente.",

            "success"

        );

    } catch (error) {

        console.error("Error al guardar producto:", error);

        mostrarNotificacion(
            error.message || "No fue posible guardar el producto.",
            "error"
        );

    } finally {

        botonGuardar.disabled = false;

        botonGuardar.textContent = modoEdicion
            ? "Actualizar producto"
            : "Guardar producto";

    }

}


// =========================================
// ELIMINAR PRODUCTO
// =========================================

async function eliminarProducto(idProducto) {

    const producto = productos.find(item =>
        String(item.idProducto) === String(idProducto)
    );

    if (!producto) {

        mostrarNotificacion(
            "No se encontró el producto.",
            "error"
        );

        return;
    }

    const confirmar = await LogiTrackUI.confirmar({

        titulo: "¿Eliminar este producto?",

        mensaje: `Estás a punto de eliminar "${producto.nombre}". ¿Deseas continuar?`,

        textoConfirmar: "Sí, eliminar",

        textoCancelar: "No, conservar",

        tipo: "danger"

    });

    if (!confirmar) return;

    try {

        await apiRequest(`/api/productos/${idProducto}`, {
            method: "DELETE"
        });

        await cargarProductos();

        mostrarNotificacion(
            "Producto eliminado correctamente.",
            "success"
        );

    } catch (error) {

        console.error("Error al eliminar producto:", error);

        mostrarNotificacion(

            error.message ||
            "No fue posible eliminar el producto. Verifica si tiene movimientos o inventario asociado.",

            "error"

        );

    }

}


// =========================================
// EVENTOS DE LA TABLA
// =========================================

tablaProductos.addEventListener("click", evento => {

    const boton = evento.target.closest("button[data-action]");

    if (!boton) return;

    const idProducto = boton.dataset.id;

    if (boton.dataset.action === "editar") {
        editarProducto(idProducto);
    }

    if (boton.dataset.action === "eliminar") {
        eliminarProducto(idProducto);
    }

});


// =========================================
// INICIALIZAR EVENTOS
// =========================================

function inicializarEventos() {

    document.getElementById("btnNuevoProducto")
        .addEventListener("click", abrirModalProducto);

    document.getElementById("btnCerrarModal")
        .addEventListener("click", cerrarModalProducto);

    document.getElementById("btnCancelarProducto")
        .addEventListener("click", cerrarModalProducto);

    document.getElementById("btnCerrarSesion")
        .addEventListener("click", cerrarSesion);

    formProducto.addEventListener("submit", guardarProducto);

    buscarProducto.addEventListener("input", aplicarFiltros);

    filtroMarca.addEventListener("change", aplicarFiltros);

    filtroCategoria.addEventListener("change", aplicarFiltros);

    filtroEstado.addEventListener("change", aplicarFiltros);

    modalProducto.addEventListener("click", evento => {

        if (evento.target === modalProducto) {
            cerrarModalProducto();
        }

    });

}


// =========================================
// INICIAR MÓDULO
// =========================================

async function iniciarModuloProductos() {

    if (!validarSesion()) return;

    configurarMenuMovil();

    cargarPerfilAdministrador();

    inicializarEventos();

    await Promise.all([
        cargarMarcas(),
        cargarCategorias()
    ]);

    await cargarProductos();

}

document.addEventListener("DOMContentLoaded", iniciarModuloProductos);