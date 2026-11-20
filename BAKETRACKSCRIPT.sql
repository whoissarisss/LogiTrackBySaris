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



-- MARCAS
INSERT INTO marca (id_marca, nombre) VALUES
(1, 'Hobart'),
(2, 'Javar'),
(3, 'KitchenAid'),
(4, 'Oster'),
(5, 'Unox'),
(6, 'Vevor'),
(7, 'Ferneto'),
(8, 'Gapan'),
(9, 'Hamilton Beach');

-- CATEGORÍAS
INSERT INTO categoria (id_categoria, nombre) VALUES
(1, 'Batidoras y mezcladoras industriales'),
(2, 'Hornos profesionales'),
(3, 'Laminadoras y divisoras de masa'),
(4, 'Equipos de preparación industrial');

-- PRODUCTOS
INSERT INTO producto
(id_producto, nombre, descripcion, estado, id_marca, id_categoria)
VALUES
(1, 'Batidora planetaria industrial',
 'Batidora para producción industrial de pastelería',
 1, 1, 1),

(2, 'Amasadora industrial',
 'Equipo industrial para amasado de mezclas',
 1, 1, 1),

(3, 'Batidora industrial de pedestal',
 'Batidora profesional para producción de panadería',
 1, 2, 1),

(4, 'Batidora de pedestal profesional',
 'Batidora profesional para pastelería',
 1, 3, 1),

(5, 'Batidora profesional',
 'Equipo profesional para preparación de mezclas',
 1, 4, 1),

(6, 'Horno de convección industrial',
 'Horno profesional para cocción uniforme',
 1, 5, 2),

(7, 'Horno de convección comercial',
 'Horno comercial para panadería y repostería',
 1, 6, 2),

(8, 'Horno profesional para panadería',
 'Horno para producción de productos horneados',
 1, 5, 2),

(9, 'Laminadora industrial de masa',
 'Equipo para laminado de masas',
 1, 7, 3),

(10, 'Divisora industrial de masa',
 'Equipo para división de masas',
 1, 8, 3),

(11, 'Sobadora industrial',
 'Equipo para procesamiento y refinado de masa',
 1, 7, 3),

(12, 'Procesador industrial de alimentos',
 'Equipo industrial para procesamiento de alimentos',
 1, 9, 4),

(13, 'Tamizadora industrial',
 'Equipo para tamizado de ingredientes',
 1, 2, 4),

(14, 'Mezcladora industrial de alta capacidad',
 'Equipo industrial para mezclado de grandes volúmenes',
 1, 1, 1),

(15, 'Procesador profesional de alimentos',
 'Equipo profesional para preparación de alimentos',
 1, 4, 4);

-- CIUDAD
 INSERT INTO ciudad (nombre, departamento, pais)
VALUES
('Bucaramanga', 'Santander', 'Colombia'),
('Bogotá', 'Cundinamarca', 'Colombia'),
('Medellín', 'Antioquia', 'Colombia'),
('Cali', 'Valle del Cauca', 'Colombia'),
('Cúcuta', 'Norte de Santander', 'Colombia');

-- BODEGA
INSERT INTO bodega (nombre, capacidad, id_ciudad, id_encargado)
VALUES
('Bodega Mezcladoras', 500, 1, 1),
('Bodega Cocción', 400, 2, 1),
('Bodega Procesamiento y Complementarios', 350, 3, 1);

-- INVENTARIO
INSERT INTO inventario (id_bodega, id_producto, stock_actual)
VALUES
(1, 1, 15),
(1, 2, 12),
(1, 3, 18),
(1, 4, 20),
(1, 5, 16),
(1, 14, 10),
(2, 6, 12),
(2, 7, 15),
(2, 8, 10),
(3, 9, 14),
(3, 10, 12),
(3, 11, 16),
(3, 12, 15),
(3, 13, 18),
(3, 15, 12),
(3, 1, 8),
(3, 2, 10),
(3, 6, 6);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 14 DAY), 'ENTRADA', 1, NULL, 1);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 1, 17);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 20 DAY), 'ENTRADA', 1, NULL, 1);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 2, 15);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 2 DAY), 'ENTRADA', 1, NULL, 1);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 3, 18);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 8 DAY), 'ENTRADA', 1, NULL, 1);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 4, 20);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 14 DAY), 'ENTRADA', 1, NULL, 1);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 5, 16);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 20 DAY), 'ENTRADA', 1, NULL, 1);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 14, 10);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 2 DAY), 'ENTRADA', 1, NULL, 2);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 6, 14);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 8 DAY), 'ENTRADA', 1, NULL, 2);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 7, 16);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 14 DAY), 'ENTRADA', 1, NULL, 2);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 8, 10);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 20 DAY), 'ENTRADA', 1, NULL, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 9, 14);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 2 DAY), 'ENTRADA', 1, NULL, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 10, 12);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 8 DAY), 'ENTRADA', 1, NULL, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 11, 16);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 14 DAY), 'ENTRADA', 1, NULL, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 12, 15);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 20 DAY), 'ENTRADA', 1, NULL, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 13, 18);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 2 DAY), 'ENTRADA', 1, NULL, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 15, 12);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 8 DAY), 'ENTRADA', 1, NULL, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 1, 8);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 14 DAY), 'ENTRADA', 1, NULL, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 2, 7);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 20 DAY), 'ENTRADA', 1, NULL, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 6, 4);

-- SALIDAS

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 1 DAY), 'SALIDA', 1, 1, NULL);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 1, 2);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 2 DAY), 'SALIDA', 1, 2, NULL);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 7, 1);

-- TRANSFERENCIAS

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 3 DAY), 'TRANSFERENCIA', 1, 1, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 2, 3);

INSERT INTO movimiento (fecha, tipo_movimiento, id_usuario, id_bodega_origen, id_bodega_destino)
VALUES (DATE_SUB(NOW(), INTERVAL 4 DAY), 'TRANSFERENCIA', 1, 2, 3);
SET @movimiento_id = LAST_INSERT_ID();
INSERT INTO detalle_movimiento (id_movimiento, id_producto, cantidad) VALUES (@movimiento_id, 6, 2);

-- AUDITORÍA

INSERT INTO auditoria
(fecha_hora, operacion, usuario_id, tabla_afectada, registro_id, valores_anteriores, valores_nuevos)
VALUES
(DATE_SUB(NOW(), INTERVAL 6 DAY), 'INSERT', 1, 'movimiento', 1, NULL, JSON_OBJECT('tipo_movimiento', 'ENTRADA', 'origen', NULL, 'destino', 1)),
(DATE_SUB(NOW(), INTERVAL 5 DAY), 'INSERT', 1, 'movimiento', 2, NULL, JSON_OBJECT('tipo_movimiento', 'ENTRADA', 'destino', 1)),
(DATE_SUB(NOW(), INTERVAL 4 DAY), 'INSERT', 1, 'movimiento', 19, NULL, JSON_OBJECT('tipo_movimiento', 'SALIDA', 'bodega_origen', 1)),
(DATE_SUB(NOW(), INTERVAL 3 DAY), 'INSERT', 1, 'movimiento', 21, NULL, JSON_OBJECT('tipo_movimiento', 'TRANSFERENCIA', 'origen', 1, 'destino', 3)),
(DATE_SUB(NOW(), INTERVAL 2 DAY), 'UPDATE', 1, 'inventario', 1, JSON_OBJECT('stock_actual', 17), JSON_OBJECT('stock_actual', 15));
