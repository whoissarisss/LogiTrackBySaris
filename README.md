# BAKE TRACK
### Sistema web de gestión y control de inventario para panaderías

<p align="center">
  <strong>Organiza. Controla. Administra.</strong>
  <br>
  Una solución web para gestionar productos, bodegas, existencias y movimientos de inventario de manera centralizada.
</p>

<p align="center">
  <img src="assets/bake-track-logo.png" alt="Logo Bake Track" width="190">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-Spring%20Boot-805841?style=for-the-badge" alt="Java Spring Boot">
  <img src="https://img.shields.io/badge/Base%20de%20datos-MySQL-397B70?style=for-the-badge" alt="MySQL">
  <img src="https://img.shields.io/badge/Frontend-HTML%20%7C%20CSS%20%7C%20JS-D99B85?style=for-the-badge" alt="Frontend">
  <img src="https://img.shields.io/badge/API-REST-805841?style=for-the-badge" alt="API REST">
  <img src="https://img.shields.io/badge/Documentación-Swagger-6B9B83?style=for-the-badge" alt="Swagger">
</p>

---

## CONTENIDO

- [Descripción del proyecto](#descripción-del-proyecto)
- [Objetivos](#objetivos)
- [Funcionalidades](#funcionalidades-del-sistema)
- [Tecnologías](#tecnologías-utilizadas)
- [Arquitectura](#arquitectura-del-sistema)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Base de datos](#base-de-datos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Documentación de la API](#documentación-de-la-api)
- [Seguridad](#seguridad-y-control-de-acceso)
- [Autora](#autora)

---

## DESCRIPCIÓN DEL PROYECTO

**Bake Track** es una aplicación web diseñada para facilitar la gestión y el control de inventarios en una panadería.

El sistema permite administrar productos, marcas, categorías, bodegas y existencias, así como registrar entradas, salidas y transferencias de inventario.

También incorpora herramientas de consulta y generación de reportes, seguimiento de actividades mediante auditoría y administración de usuarios con diferentes niveles de acceso.

Su desarrollo busca centralizar la información y ofrecer una herramienta que permita llevar un control organizado de las operaciones relacionadas con el inventario.

## OBJETIVOS

### Objetivo general

Desarrollar un sistema web para gestionar y controlar el inventario de una panadería, permitiendo administrar productos, bodegas, existencias y movimientos, con mecanismos de autenticación, autorización y trazabilidad de las operaciones.

### Objetivos específicos

- Administrar la información de productos, marcas y categorías.
- Gestionar las bodegas y su capacidad.
- Controlar las existencias de productos.
- Registrar entradas, salidas y transferencias de inventario.
- Consultar información mediante reportes.
- Controlar el acceso de usuarios según sus roles.
- Mantener un registro de las actividades realizadas en el sistema.

---

## FUNCIONALIDADES DEL SISTEMA

El sistema está organizado en módulos que permiten administrar las diferentes operaciones de inventario.

| Módulo | Descripción |
|---|---|
| Usuarios | Registro, consulta y administración de usuarios. |
| Autenticación | Inicio de sesión y validación mediante JWT. |
| Productos | Administración de productos. |
| Marcas | Gestión de marcas asociadas a los productos. |
| Categorías | Organización de productos por categorías. |
| Ciudades | Administración de información de ciudades. |
| Bodegas | Gestión de bodegas y capacidad. |
| Inventario | Consulta y control de existencias. |
| Movimientos | Registro de entradas, salidas y transferencias. |
| Reportes | Consultas de inventario y movimientos. |
| Auditoría | Consulta y seguimiento de actividades registradas. |

### Control de inventario

Permite consultar las existencias generales, identificar productos con stock bajo y revisar la distribución del inventario por bodega.

### Movimientos

Permite registrar y consultar las operaciones realizadas sobre el inventario:

- Entradas de productos.
- Salidas de productos.
- Transferencias entre bodegas.
- Movimientos por usuario.
- Movimientos por tipo.
- Movimientos por bodega.
- Movimientos por rango de fechas.

### Reportes

El sistema permite consultar información para facilitar el seguimiento del inventario:

- Inventario general.
- Inventario por bodega.
- Inventario por producto.
- Productos con stock bajo.
- Resumen de existencias por bodega.
- Movimientos por usuario, tipo y bodega.
- Movimientos entre fechas.
- Productos con mayor movimiento.

---

## TECNOLOGÍAS UTILIZADAS

### Backend

| Tecnología | Uso |
|---|---|
| Java | Lenguaje de programación. |
| Spring Boot | Desarrollo de la aplicación backend. |
| Spring Web | Construcción de servicios REST. |
| Spring Data JPA | Persistencia y acceso a datos. |
| Spring Security | Seguridad y control de acceso. |
| JWT | Autenticación basada en tokens. |
| Maven | Gestión de dependencias y compilación. |

### Frontend

| Tecnología | Uso |
|---|---|
| HTML5 | Estructura de las interfaces. |
| CSS3 | Diseño y estilos visuales. |
| JavaScript | Interactividad y consumo de servicios REST. |

### Base de datos y herramientas

| Herramienta | Uso |
|---|---|
| MySQL | Gestión de la base de datos. |
| Swagger / OpenAPI | Documentación y pruebas de la API. |
| Postman | Pruebas de servicios REST. |
| Visual Studio Code | Entorno de desarrollo. |
| Git | Control de versiones. |
| GitHub | Repositorio del proyecto. |

---

## ARQUITECTURA DEL SISTEMA

Bake Track utiliza una arquitectura por capas en el backend, separando las responsabilidades de presentación, lógica de negocio y acceso a datos.

```mermaid
flowchart TD
    A[Usuario] --> B[Frontend]
    B --> C[API REST - Spring Boot]
    C --> D[Controllers]
    D --> E[Services]
    E --> F[Repositories]
    F --> G[(MySQL)]

    C --> H[Spring Security + JWT]
    C --> I[Swagger / OpenAPI]
```

### Organización por capas

| Capa | Responsabilidad |
|---|---|
| Controller | Recibe las solicitudes HTTP y devuelve las respuestas. |
| Service | Contiene la lógica de negocio. |
| Repository | Gestiona el acceso a la base de datos. |
| Model | Representa las entidades del sistema. |
| DTO | Define los objetos utilizados para transferir información. |
| Auth | Contiene los componentes relacionados con autenticación y seguridad. |

---

## ESTRUCTURA DEL PROYECTO

La organización general del backend sigue una estructura por paquetes:

```text
BAKE-TRACK/
│
├── src/
│
