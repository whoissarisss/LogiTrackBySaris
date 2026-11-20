document.addEventListener("DOMContentLoaded", () => {
    const $ = id => document.getElementById(id);
    let all = [];

    const esc = value => String(value ?? "—").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    }[character]));

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

    // PERFIL
    function profile() {
        const name = sessionStorage.getItem("nombre") || "Administrador";
        const surname = sessionStorage.getItem("apellido") || "";
        const fullName = `${name} ${surname}`.trim();
        const initials = (name[0] || "A") + (surname[0] || "");

        $("sidebarAdminName").textContent = fullName;
        $("topbarAdminName").textContent = fullName;
        $("sidebarAdminInitials").textContent = initials.toUpperCase();
        $("topbarAdminInitials").textContent = initials.toUpperCase();

        $("currentDate").textContent = "BAKE TRACK · TRAZABILIDAD";
    }

    const userName = record =>
        [record.usuario?.nombre, record.usuario?.apellido]
            .filter(Boolean)
            .join(" ") || "—";

    // FILTROS
    function filtered() {
        const operation = $("filterOperation").value;
        const table = $("filterTable").value.trim().toLowerCase();
        const userId = $("filterUser").value;

        return all.filter(record =>
            (!operation || record.operacion === operation) &&
            (!table || String(record.tablaAfectada || "")
                .toLowerCase().includes(table)) &&
            (!userId || String(record.usuario?.idUsuario) === userId)
        );
    }

    // TABLA Y RESUMEN
    function render() {
        const rows = filtered();

        $("auditCount").textContent =
            rows.length.toLocaleString("es-CO");

        $("auditLatest").textContent = rows.length
            ? new Date(
                rows.map(record => record.fechaHora).sort().at(-1)
            ).toLocaleString("es-CO")
            : "—";

        $("auditUsers").textContent = new Set(
            rows.map(record => record.usuario?.idUsuario).filter(Boolean)
        ).size;

        $("auditBody").innerHTML = rows.length
            ? [...rows]
                .sort((a, b) =>
                    new Date(b.fechaHora) - new Date(a.fechaHora)
                )
                .map(record => `
                    <tr>
                        <td>#${esc(record.idAuditoria)}</td>

                        <td>
                            ${esc(new Date(record.fechaHora)
                                .toLocaleString("es-CO"))}
                        </td>

                        <td>${esc(userName(record))}</td>

                        <td>
                            <span class="audit-operation ${String(record.operacion).toLowerCase()}">
                                ${esc(record.operacion)}
                            </span>
                        </td>

                        <td>${esc(record.tablaAfectada)}</td>

                        <td>${esc(record.registroId)}</td>

                        <td>
                            <button
                                class="audit-detail-button"
                                data-id="${esc(record.idAuditoria)}">

                                <i class="fa-regular fa-eye"></i>
                                Ver
                            </button>
                        </td>
                    </tr>
                `).join("")
            : `
                <tr>
                    <td colspan="7" class="table-empty">
                        No se encontraron registros de auditoría.
                    </td>
                </tr>
            `;

        $("auditBody").querySelectorAll("[data-id]").forEach(button => {
            button.addEventListener("click", () => {
                showDetail(button.dataset.id);
            });
        });
    }

    // CARGAR BITÁCORA
    async function load() {
        $("auditBody").innerHTML = `
            <tr>
                <td colspan="7" class="table-loading">
                    Consultando bitácora...
                </td>
            </tr>
        `;

        try {
            const data = await apiRequest("/api/auditorias");

            all = Array.isArray(data) ? data : [];

            render();

        } catch (error) {
            console.error(error);

            $("auditBody").innerHTML = `
                <tr>
                    <td colspan="7" class="table-error">
                        ${esc(error.message)}
                    </td>
                </tr>
            `;

            if (window.LogiTrackUI?.mostrarMensaje) {
                window.LogiTrackUI.mostrarMensaje({
                    tipo: "error",
                    titulo: "No fue posible consultar la auditoría",
                    mensaje: error.message
                });
            }
        }
    }

    // DETALLE DE AUDITORÍA
    async function showDetail(id) {
        try {
            const record = await apiRequest(
                `/api/auditorias/${encodeURIComponent(id)}`
            );

            $("auditDetailContent").innerHTML = `
                <div class="audit-detail-grid">

                    <div>
                        <small>ID DE AUDITORÍA</small>
                        <strong>#${esc(record.idAuditoria)}</strong>
                    </div>

                    <div>
                        <small>FECHA Y HORA</small>
                        <strong>
                            ${esc(new Date(record.fechaHora)
                                .toLocaleString("es-CO"))}
                        </strong>
                    </div>

                    <div>
                        <small>USUARIO</small>
                        <strong>${esc(userName(record))}</strong>
                    </div>

                    <div>
                        <small>OPERACIÓN</small>
                        <strong>${esc(record.operacion)}</strong>
                    </div>

                    <div>
                        <small>TABLA</small>
                        <strong>${esc(record.tablaAfectada)}</strong>
                    </div>

                    <div>
                        <small>ID DEL REGISTRO</small>
                        <strong>${esc(record.registroId)}</strong>
                    </div>

                </div>

                <div class="audit-json-columns">

                    <section>
                        <h3>Valores anteriores</h3>
                        <pre>${esc(record.valoresAnteriores ||
                            "Sin valores anteriores")}</pre>
                    </section>

                    <section>
                        <h3>Valores nuevos</h3>
                        <pre>${esc(record.valoresNuevos ||
                            "Sin valores nuevos")}</pre>
                    </section>

                </div>
            `;

            $("auditModal").hidden = false;

        } catch (error) {
            if (window.LogiTrackUI?.mostrarMensaje) {
                window.LogiTrackUI.mostrarMensaje({
                    tipo: "error",
                    mensaje: error.message
                });
            }
        }
    }

    // EVENTOS DE FILTROS
    $("applyAuditFilters").addEventListener("click", render);

    $("clearAuditFilters").addEventListener("click", () => {
        $("filterOperation").value = "";
        $("filterTable").value = "";
        $("filterUser").value = "";

        render();
    });

    $("refreshAudit").addEventListener("click", load);

    // CERRAR MODAL
    $("closeAuditModal").addEventListener("click", () => {
        $("auditModal").hidden = true;
    });

    $("auditModal").addEventListener("click", event => {
        if (event.target === $("auditModal")) {
            $("auditModal").hidden = true;
        }
    });

    document.addEventListener("keydown", event => {
        if (event.key === "Escape") {
            $("auditModal").hidden = true;
        }
    });

    // CERRAR SESIÓN
    $("logoutButton").addEventListener("click", () => {
        sessionStorage.clear();
        location.href = "index.html";
    });

    // INICIALIZAR
    configureMobileMenu();
    profile();
    load();
});