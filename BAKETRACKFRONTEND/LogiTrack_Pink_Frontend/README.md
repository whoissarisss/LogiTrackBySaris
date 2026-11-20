# LogiTrack — Frontend Pink Edition

Frontend web de LogiTrack, conectado al backend Spring Boot existente.

## Identidad visual
- Paleta completamente rosada: rosa profundo, rosa empolvado, blush y blanco cálido.
- No utiliza tonos azules en la interfaz.
- Navegación y componentes coherentes entre módulos.

## Requisitos
- Backend Spring Boot ejecutándose en `http://localhost:8080`.
- MySQL y datos de prueba configurados en el backend.
- Navegador actualizado.
- Visual Studio Code con Live Server (o servidor estático equivalente).

## Cómo ejecutar
1. Descomprime esta carpeta.
2. Abre `FRONTENDLOGITRACK` en Visual Studio Code.
3. Inicia el backend Spring Boot en el puerto `8080`.
4. Abre `index.html` con Live Server usando el puerto **5500**.
   - Origen admitido por el CORS del backend: `http://127.0.0.1:5500` o `http://localhost:5500`.
5. Inicia sesión con el usuario administrador que tengas configurado en el backend.

La URL de la API está centralizada en `js/api.js` como `http://localhost:8080`.

## Módulos incluidos
- Login y registro de solicitudes de empleados.
- Dashboard administrativo.
- Bodegas.
- Productos.
- Categorías.
- Marcas.
- Inventario.
- Movimientos.
- Reportes (consultas construidas con endpoints existentes de movimientos e inventarios).
- Auditoría (consulta, filtros y detalle).
- Usuarios (solicitudes pendientes, aprobación y rechazo con motivo).
- Espacio de trabajo para empleados (historial de sus movimientos).

## Flujo de registro de empleados
1. Desde `index.html`, el empleado selecciona **Crear cuenta**.
2. Completa nombre, apellido, correo y contraseña (mínimo 8 caracteres).
3. El frontend envía `POST /api/usuarios/registro`.
4. El backend registra la solicitud como `PENDIENTE`.
5. El administrador entra a **Usuarios** y la aprueba o rechaza.
6. Si se aprueba, el empleado puede iniciar sesión y acceder a `empleado.html`.

## Endpoints de autenticación y usuarios usados
- `POST /api/auth/login`
- `POST /api/usuarios/registro`
- `GET /api/usuarios/pendientes`
- `GET /api/usuarios/empleados-aprobados`
- `PUT /api/usuarios/{idUsuario}/aprobar`
- `PUT /api/usuarios/{idUsuario}/rechazar?motivo=...`

## Endpoints de auditoría usados
- `GET /api/auditorias`
- `GET /api/auditorias/{idAuditoria}`

## Nota importante
Este paquete conserva los módulos y scripts recibidos en el ZIP original, añade los módulos que faltaban y aplica la identidad visual rosa. Las funcionalidades dependen de que el backend esté encendido, las credenciales sean válidas y el usuario tenga el rol/permisos correspondientes. No se ha cambiado el backend.
