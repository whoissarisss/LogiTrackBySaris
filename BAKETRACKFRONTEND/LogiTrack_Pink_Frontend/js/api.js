
const API_URL = "http://localhost:8080";

async function apiRequest(endpoint, options = {}) {
    const token = sessionStorage.getItem("token");

    const headers = {
        ...options.headers
    };

    // No forzar JSON cuando se envían archivos o FormData.
    if (!(options.body instanceof FormData) && !headers["Content-Type"]) {
        headers["Content-Type"] = "application/json";
    }

    if (token) {
        headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        headers
    });

    if (response.status === 401 && token) {
        sessionStorage.removeItem("token");
        sessionStorage.removeItem("rol");
        sessionStorage.removeItem("idUsuario");
        sessionStorage.removeItem("nombre");
        sessionStorage.removeItem("apellido");
        sessionStorage.removeItem("correo");

        window.location.href = "index.html";

        throw new Error("Tu sesión ha expirado. Inicia sesión nuevamente.");
    }

    const contenido = await response.text();

    let data = null;

    if (contenido) {
        try {
            data = JSON.parse(contenido);
        } catch {
            data = contenido;
        }
    }

    if (!response.ok) {
        const mensaje =
            typeof data === "string"
                ? data
                : data?.mensaje || data?.message || data?.error;

        throw new Error(
            mensaje || `Error en la solicitud: ${response.status}`
        );
    }

    return data;
}
