CREATE DATABASE IF NOT EXISTS logitrack_sararami2;

USE logitrack_sararami2;

-- 1. CIUDAD
CREATE TABLE ciudad (
    id_ciudad INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    departamento VARCHAR(100) NOT NULL,
    pais VARCHAR(80) NOT NULL,
    CONSTRAINT uq_ciudad_nombre_departamento_pais
        UNIQUE (nombre, departamento, pais)
) ENGINE=InnoDB;

-- 2. USUARIO

CREATE TABLE usuario (
    id_usuario INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    correo VARCHAR(120) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol ENUM('ADMIN', 'EMPLEADO') NOT NULL DEFAULT 'EMPLEADO',
    estado_solicitud ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO')
        NOT NULL DEFAULT 'PENDIENTE',
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_revision DATETIME NULL,
    motivo_rechazo VARCHAR(500) NULL,
    CONSTRAINT uq_usuario_correo UNIQUE (correo),
    CONSTRAINT chk_usuario_revision_estado CHECK (
        (estado_solicitud = 'PENDIENTE'
            AND fecha_revision IS NULL
            AND motivo_rechazo IS NULL)
        OR
        (estado_solicitud = 'APROBADO'
            AND fecha_revision IS NOT NULL
            AND motivo_rechazo IS NULL)
        OR
        (estado_solicitud = 'RECHAZADO'
            AND fecha_revision IS NOT NULL
            AND motivo_rechazo IS NOT NULL)
    )
) ENGINE=InnoDB;

-- 3. MARCA
CREATE TABLE marca (
    id_marca INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT uq_marca_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

-- 4. CATEGORIA
CREATE TABLE categoria (
    id_categoria INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT uq_categoria_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

-- 5. BODEGA

CREATE TABLE bodega (
    id_bodega INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    capacidad INT NOT NULL,
    id_ciudad INT UNSIGNED NOT NULL,
    id_encargado INT UNSIGNED NOT NULL,
    CONSTRAINT chk_bodega_capacidad CHECK (capacidad > 0),
    CONSTRAINT fk_bodega_ciudad
        FOREIGN KEY (id_ciudad) REFERENCES ciudad(id_ciudad)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_bodega_encargado
        FOREIGN KEY (id_encargado) REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_bodega_ciudad (id_ciudad),
    INDEX idx_bodega_encargado (id_encargado)
) ENGINE=InnoDB;

-- 6. PRODUCTO
CREATE TABLE producto (
    id_producto INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(300) NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    id_marca INT UNSIGNED NOT NULL,
    id_categoria INT UNSIGNED NOT NULL,
    CONSTRAINT fk_producto_marca
        FOREIGN KEY (id_marca) REFERENCES marca(id_marca)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_producto_marca (id_marca),
    INDEX idx_producto_categoria (id_categoria),
    INDEX idx_producto_estado (estado)
) ENGINE=InnoDB;

-- 7. INVENTARIO

CREATE TABLE inventario (
    id_inventario INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_bodega INT UNSIGNED NOT NULL,
    id_producto INT UNSIGNED NOT NULL,
    stock_actual DECIMAL(12,3) NOT NULL DEFAULT 0,
    CONSTRAINT uq_inventario_bodega_producto UNIQUE (id_bodega, id_producto),
    CONSTRAINT chk_inventario_stock CHECK (stock_actual >= 0),
    CONSTRAINT fk_inventario_bodega
        FOREIGN KEY (id_bodega) REFERENCES bodega(id_bodega)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_inventario_producto
        FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_inventario_producto (id_producto)
) ENGINE=InnoDB;

-- 8. MOVIMIENTO
-- ENTRADA: solo bodega destino.
-- SALIDA: solo bodega origen.
-- TRANSFERENCIA: origen y destino diferentes.

CREATE TABLE movimiento (
    id_movimiento INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_movimiento ENUM(
        'ENTRADA',
        'SALIDA',
        'TRANSFERENCIA'
    ) NOT NULL,
    id_usuario INT UNSIGNED NOT NULL,
    id_bodega_origen INT UNSIGNED NULL,
    id_bodega_destino INT UNSIGNED NULL,

    CONSTRAINT chk_movimiento_bodegas CHECK (
        (tipo_movimiento = 'ENTRADA'
            AND id_bodega_origen IS NULL
            AND id_bodega_destino IS NOT NULL)
        OR
        (tipo_movimiento = 'SALIDA'
            AND id_bodega_origen IS NOT NULL
            AND id_bodega_destino IS NULL)
        OR
        (tipo_movimiento = 'TRANSFERENCIA'
            AND id_bodega_origen IS NOT NULL
            AND id_bodega_destino IS NOT NULL
            AND id_bodega_origen <> id_bodega_destino)
    ),

    CONSTRAINT fk_movimiento_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT fk_movimiento_bodega_origen
        FOREIGN KEY (id_bodega_origen)
        REFERENCES bodega(id_bodega)
        ON UPDATE RESTRICT ON DELETE RESTRICT,

    CONSTRAINT fk_movimiento_bodega_destino
        FOREIGN KEY (id_bodega_destino)
        REFERENCES bodega(id_bodega)
        ON UPDATE RESTRICT ON DELETE RESTRICT,

    INDEX idx_movimiento_fecha (fecha),
    INDEX idx_movimiento_tipo_fecha (tipo_movimiento, fecha),
    INDEX idx_movimiento_usuario (id_usuario)
) ENGINE=InnoDB;


-- 9. DETALLE_MOVIMIENTO

CREATE TABLE detalle_movimiento (
    id_detalle INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_movimiento INT UNSIGNED NOT NULL,
    id_producto INT UNSIGNED NOT NULL,
    cantidad DECIMAL(12,3) NOT NULL,
    CONSTRAINT uq_detalle_movimiento_producto UNIQUE (id_movimiento, id_producto),
    CONSTRAINT chk_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT fk_detalle_movimiento
        FOREIGN KEY (id_movimiento) REFERENCES movimiento(id_movimiento)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    INDEX idx_detalle_producto (id_producto)
) ENGINE=InnoDB;



-- 10. AUDITORIA

CREATE TABLE auditoria (
    id_auditoria INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    operacion ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    usuario_id INT UNSIGNED NOT NULL,
    tabla_afectada VARCHAR(100) NOT NULL,
    registro_id INT UNSIGNED NOT NULL,
    valores_anteriores JSON NULL,
    valores_nuevos JSON NULL,

    CONSTRAINT fk_auditoria_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_auditoria_usuario_fecha (usuario_id, fecha_hora),
    INDEX idx_auditoria_operacion_fecha (operacion, fecha_hora),
    INDEX idx_auditoria_tabla_registro (tabla_afectada, registro_id)
) ENGINE=InnoDB;
