document.addEventListener("DOMContentLoaded", () => {
    const $ = id => document.getElementById(id);
    let pending = [], approved = [], view = "pending", rejectId = null;

    const esc = v => String(v ?? "").replace(/[&<>"']/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;"
    }[c]));

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

        toggle.addEventListener("click", () => {
            const isOpen = document.body.classList.toggle("sidebar-open");
            toggle.setAttribute("aria-expanded", String(isOpen));
            toggle.setAttribute("aria-label", isOpen ? "Cerrar menú" : "Abrir menú");
            toggle.innerHTML = isOpen
                ? '<i class="fa-solid fa-xmark"></i>'
                : '<i class="fa-solid fa-bars"></i>';
        });

        backdrop.addEventListener("click", closeMenu);

        sidebar.querySelectorAll("a").forEach(link => {
            link.addEventListener("click", closeMenu);
        });

        document.addEventListener("keydown", e => {
            if (e.key === "Escape") closeMenu();
        });

        window.addEventListener("resize", () => {
            if (window.innerWidth > 900) closeMenu();
        });
    }

    function profile() {
        const n = sessionStorage.getItem("nombre") || "Administrador";
        const a = sessionStorage.getItem("apellido") || "";
        const full = `${n} ${a}`.trim();
        const i = (n[0] || "A") + (a[0] || "");

        $("sidebarAdminName").textContent = full;
        $("topbarAdminName").textContent = full;
        $("sidebarAdminInitials").textContent = i.toUpperCase();
        $("topbarAdminInitials").textContent = i.toUpperCase();
        $("currentDate").textContent = "BAKE TRACK · ADMINISTRACIÓN";
    }

    const date = v => v ? new Date(v).toLocaleDateString("es-CO") : "—";

    function render() {
        const list = view === "pending" ? pending : approved;

        $("pendingCount").textContent = pending.length;
        $("approvedCount").textContent = approved.length;
        $("pendingTabCount").textContent = pending.length;
        $("userListTitle").textContent = view === "pending"
            ? "Solicitudes de acceso" : "Empleados aprobados";
        $("userListSubtitle").textContent = view === "pending"
            ? "Aprueba o rechaza los registros enviados por los empleados."
            : "Personas habilitadas para ingresar al sistema.";

        $("usersHead").innerHTML = view === "pending"
            ? '<tr><th>COLABORADOR</th><th>CORREO</th><th>FECHA DE REGISTRO</th><th>ESTADO</th><th>ACCIONES</th></tr>'
            : '<tr><th>COLABORADOR</th><th>CORREO</th><th>FECHA DE REGISTRO</th><th>ESTADO</th><th>ID USUARIO</th></tr>';

        if (!list.length) {
            $("usersBody").innerHTML = `<tr><td colspan="5" class="table-empty">${
                view === "pending"
                    ? "No hay solicitudes pendientes. ¡Todo está al día!"
                    : "Aún no hay empleados aprobados."
            }</td></tr>`;
            return;
        }

        $("usersBody").innerHTML = list.map(u => `
            <tr>
                <td>
                    <div class="user-identity">
                        <span class="user-avatar">${esc((u.nombre?.[0] || "") + (u.apellido?.[0] || ""))}</span>
                        <strong>${esc(u.nombre)} ${esc(u.apellido)}</strong>
                    </div>
                </td>
                <td>${esc(u.correo)}</td>
                <td>${esc(date(u.fechaRegistro))}</td>
                <td><span class="user-status ${view}">${view === "pending" ? "PENDIENTE" : "APROBADO"}</span></td>
                <td>${view === "pending" ? `
                    <div class="user-actions">
                        <button class="approve-user" data-approve="${esc(u.idUsuario)}">
                            <i class="fa-solid fa-check"></i> Aprobar
                        </button>
                        <button class="reject-user" data-reject="${esc(u.idUsuario)}">
                            <i class="fa-solid fa-xmark"></i> Rechazar
                        </button>
                    </div>` : `#${esc(u.idUsuario)}`}
                </td>
            </tr>
        `).join("");

        $("usersBody").querySelectorAll("[data-approve]").forEach(b => {
            b.addEventListener("click", () => approve(b.dataset.approve));
        });

        $("usersBody").querySelectorAll("[data-reject]").forEach(b => {
            b.addEventListener("click", () => openReject(b.dataset.reject));
        });
    }

    async function load() {
        $("usersBody").innerHTML = '<tr><td colspan="5" class="table-loading">Consultando usuarios...</td></tr>';

        try {
            const [p, a] = await Promise.all([
                apiRequest("/api/usuarios/pendientes"),
                apiRequest("/api/usuarios/empleados-aprobados")
            ]);

            pending = Array.isArray(p) ? p : [];
            approved = Array.isArray(a) ? a : [];
            render();
        } catch (e) {
            console.error(e);
            $("usersBody").innerHTML = `<tr><td colspan="5" class="table-error">${esc(e.message)}</td></tr>`;
            LogiTrackUI.mostrarMensaje({
                tipo: "error",
                titulo: "No fue posible cargar usuarios",
                mensaje: e.message
            });
        }
    }

    async function approve(id) {
        const ok = await LogiTrackUI.confirmar({
            titulo: "¿Aprobar solicitud?",
            mensaje: "El empleado podrá iniciar sesión en LogiTrack después de aprobar su registro.",
            textoConfirmar: "Sí, aprobar",
            tipo: "info"
        });

        if (!ok) return;

        try {
            await apiRequest(`/api/usuarios/${encodeURIComponent(id)}/aprobar`, { method: "PUT" });
            LogiTrackUI.mostrarMensaje({
                tipo: "success",
                titulo: "Solicitud aprobada",
                mensaje: "El empleado ya puede iniciar sesión."
            });
            await load();
        } catch (e) {
            LogiTrackUI.mostrarMensaje({
                tipo: "error",
                titulo: "No se pudo aprobar",
                mensaje: e.message
            });
        }
    }

    function openReject(id) {
        rejectId = id;
        $("rejectReason").value = "";
        $("rejectError").textContent = "";
        $("rejectModal").hidden = false;
        $("rejectReason").focus();
    }

    function closeReject() {
        $("rejectModal").hidden = true;
        rejectId = null;
    }

    async function reject() {
        const motivo = $("rejectReason").value.trim();

        if (!motivo) {
            $("rejectError").textContent = "Debes indicar el motivo del rechazo.";
            return;
        }

        if (!rejectId) return;

        const b = $("confirmReject");
        b.disabled = true;

        try {
            await apiRequest(
                `/api/usuarios/${encodeURIComponent(rejectId)}/rechazar?motivo=${encodeURIComponent(motivo)}`,
                { method: "PUT" }
            );

            closeReject();
            LogiTrackUI.mostrarMensaje({
                tipo: "success",
                titulo: "Solicitud rechazada",
                mensaje: "La decisión quedó registrada correctamente."
            });
            await load();
        } catch (e) {
            $("rejectError").textContent = e.message;
        } finally {
            b.disabled = false;
        }
    }

    document.querySelectorAll(".user-tab").forEach(b => {
        b.addEventListener("click", () => {
            view = b.dataset.view;
            document.querySelectorAll(".user-tab").forEach(x => {
                x.classList.toggle("active", x === b);
            });
            render();
        });
    });

    $("refreshUsers").addEventListener("click", load);
    $("closeReject").addEventListener("click", closeReject);
    $("cancelReject").addEventListener("click", closeReject);
    $("confirmReject").addEventListener("click", reject);
    $("rejectModal").addEventListener("click", e => {
        if (e.target === $("rejectModal")) closeReject();
    });
    $("logoutButton").addEventListener("click", () => {
        sessionStorage.clear();
        location.href = "index.html";
    });

    configureMobileMenu();
    profile();
    load();
});