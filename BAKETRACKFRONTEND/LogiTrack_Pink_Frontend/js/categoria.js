
// BAKE TRACK - MÓDULO DE CATEGORÍAS

document.addEventListener("DOMContentLoaded", () => {

    const categoryTableBody = document.getElementById("categoryTableBody");
    const categoryCount = document.getElementById("categoryCount");
    const categoryResults = document.getElementById("categoryResults");
    const categorySearch = document.getElementById("categorySearch");
    const categoryModal = document.getElementById("categoryModal");
    const categoryForm = document.getElementById("categoryForm");
    const categoryId = document.getElementById("categoryId");
    const categoryName = document.getElementById("categoryName");
    const categoryModalTitle = document.getElementById("categoryModalTitle");
    const categoryFormMessage = document.getElementById("categoryFormMessage");
    const saveCategory = document.getElementById("saveCategory");

    let categorias = [];

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
    // SEGURIDAD HTML
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
    // MENSAJES DEL FORMULARIO
    // ==========================================

    function mostrarMensaje(mensaje, tipo = "error") {
        categoryFormMessage.textContent = mensaje;
        categoryFormMessage.className = `form-message ${tipo}`;
    }

    function limpiarMensaje() {
        categoryFormMessage.textContent = "";
        categoryFormMessage.className = "form-message";
    }

    // ==========================================
    // CARGAR CATEGORÍAS
    // ==========================================

    async function cargarCategorias() {

        categoryTableBody.innerHTML = `
            <tr>
                <td colspan="3" class="table-loading">
                    <i class="fa-solid fa-spinner fa-spin"></i>
                    Cargando categorías...
                </td>
            </tr>`;

        try {
            const respuesta = await apiRequest("/api/categorias");

            categorias = Array.isArray(respuesta) ? respuesta : [];

            categoryCount.textContent =
                categorias.length.toLocaleString("es-CO");

            filtrarCategorias();

        } catch (error) {
            console.error("Error al cargar categorías:", error);

            categoryTableBody.innerHTML = `
                <tr>
                    <td colspan="3" class="table-empty">
                        No fue posible cargar las categorías.
                        Verifica la conexión e inténtalo nuevamente.
                    </td>
                </tr>`;

            categoryCount.textContent = "--";
            categoryResults.textContent =
                "No se pudieron cargar los resultados";
        }
    }

    // ==========================================
    // MOSTRAR CATEGORÍAS
    // ==========================================

    function mostrarCategorias(lista) {

        if (!lista.length) {
            categoryTableBody.innerHTML = `
                <tr>
                    <td colspan="3" class="table-empty">
                        No se encontraron categorías registradas.
                    </td>
                </tr>`;
        } else {

            categoryTableBody.innerHTML = lista.map(categoria => `
                <tr>
                    <td class="category-id">
                        #${escaparHTML(categoria.idCategoria)}
                    </td>

                    <td class="category-name">
                        ${escaparHTML(categoria.nombre)}
                    </td>

                    <td>
                        <div class="category-actions">
                            <button
                                class="category-action"
                                type="button"
                                data-action="edit"
                                data-id="${escaparHTML(categoria.idCategoria)}"
                                title="Editar categoría"
                                aria-label="Editar categoría">
                                <i class="fa-solid fa-pen"></i>
                            </button>

                            <button
                                class="category-action delete"
                                type="button"
                                data-action="delete"
                                data-id="${escaparHTML(categoria.idCategoria)}"
                                title="Eliminar categoría"
                                aria-label="Eliminar categoría">
                                <i class="fa-solid fa-trash"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join("");
        }

        categoryResults.textContent =
            `Mostrando ${lista.length} de ${categorias.length} categorías`;
    }

    // ==========================================
    // FILTRAR CATEGORÍAS
    // ==========================================

    function filtrarCategorias() {

        const termino = categorySearch.value
            .trim()
            .toLocaleLowerCase("es");

        const resultado = categorias.filter(categoria =>
            String(categoria.nombre || "")
                .toLocaleLowerCase("es")
                .includes(termino)
        );

        mostrarCategorias(resultado);
    }

    // ==========================================
    // ABRIR MODAL
    // ==========================================

    function abrirModal(categoria = null) {

        categoryForm.reset();
        limpiarMensaje();

        categoryId.value = categoria ? categoria.idCategoria : "";
        categoryName.value = categoria ? categoria.nombre : "";

        categoryModalTitle.textContent = categoria
            ? "Editar categoría"
            : "Registrar nueva categoría";

        saveCategory.textContent = categoria
            ? "Actualizar categoría"
            : "Guardar categoría";

        categoryModal.style.display = "flex";
        categoryName.focus();
    }

    // ==========================================
    // CERRAR MODAL
    // ==========================================

    function cerrarModal() {

        categoryModal.style.display = "none";
        categoryForm.reset();
        categoryId.value = "";
        limpiarMensaje();
    }

    // ==========================================
    // GUARDAR O ACTUALIZAR CATEGORÍA
    // ==========================================

    async function guardarCategoria(event) {

        event.preventDefault();

        const id = categoryId.value;

        const datos = {
            nombre: categoryName.value.trim()
        };

        if (!datos.nombre) {
            mostrarMensaje("El nombre de la categoría es obligatorio.");
            categoryName.focus();
            return;
        }

        saveCategory.disabled = true;
        saveCategory.textContent = id
            ? "Actualizando..."
            : "Guardando...";

        try {

            if (id) {
                await apiRequest(`/api/categorias/${id}`, {
                    method: "PUT",
                    body: JSON.stringify(datos)
                });

            } else {
                await apiRequest("/api/categorias", {
                    method: "POST",
                    body: JSON.stringify(datos)
                });
            }

            cerrarModal();
            await cargarCategorias();

            if (
                typeof LogiTrackUI !== "undefined" &&
                LogiTrackUI.mostrarMensaje
            ) {
                LogiTrackUI.mostrarMensaje({
                    tipo: "success",
                    mensaje: id
                        ? "Categoría actualizada correctamente."
                        : "Categoría registrada correctamente."
                });
            } else {
                alert(
                    id
                        ? "Categoría actualizada correctamente."
                        : "Categoría registrada correctamente."
                );
            }

        } catch (error) {

            console.error("Error al guardar categoría:", error);

            mostrarMensaje(
                error.message || "No fue posible guardar la categoría."
            );

        } finally {

            saveCategory.disabled = false;

            saveCategory.textContent = id
                ? "Actualizar categoría"
                : "Guardar categoría";
        }
    }

    // ==========================================
    // ELIMINAR CATEGORÍA
    // ==========================================

    async function eliminarCategoria(id) {

        const categoria = categorias.find(
            item => String(item.idCategoria) === String(id)
        );

        if (!categoria) return;

        const confirmado = confirm(
            `¿Deseas eliminar la categoría "${categoria.nombre}"?`
        );

        if (!confirmado) return;

        try {

            await apiRequest(`/api/categorias/${id}`, {
                method: "DELETE"
            });

            await cargarCategorias();

            if (
                typeof LogiTrackUI !== "undefined" &&
                LogiTrackUI.mostrarMensaje
            ) {
                LogiTrackUI.mostrarMensaje({
                    tipo: "success",
                    mensaje: "Categoría eliminada correctamente."
                });
            } else {
                alert("Categoría eliminada correctamente.");
            }

        } catch (error) {

            console.error("Error al eliminar categoría:", error);

            alert(
                error.message ||
                "No fue posible eliminar la categoría. Puede estar asociada a productos."
            );
        }
    }

    // ==========================================
    // EVENTOS
    // ==========================================

    document
        .getElementById("openCategoryModal")
        .addEventListener("click", () => abrirModal());

    document
        .getElementById("closeCategoryModal")
        .addEventListener("click", cerrarModal);

    document
        .getElementById("cancelCategory")
        .addEventListener("click", cerrarModal);

    document
        .getElementById("refreshCategories")
        .addEventListener("click", cargarCategorias);

    categorySearch.addEventListener("input", filtrarCategorias);

    categoryForm.addEventListener("submit", guardarCategoria);

    categoryTableBody.addEventListener("click", event => {

        const boton = event.target.closest("button[data-action]");

        if (!boton) return;

        const id = boton.dataset.id;

        if (boton.dataset.action === "edit") {

            const categoria = categorias.find(
                item => String(item.idCategoria) === String(id)
            );

            if (categoria) abrirModal(categoria);

        } else if (boton.dataset.action === "delete") {

            eliminarCategoria(id);
        }
    });

    categoryModal.addEventListener("click", event => {

        if (event.target === categoryModal) {
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
    cargarCategorias();

});
