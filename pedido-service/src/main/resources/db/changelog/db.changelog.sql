--liquibase formatted sql
--changeset corpaliv:pedidos-001
CREATE TABLE pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    producto_nombre VARCHAR(160) NOT NULL,
    precio_unitario_clp INTEGER NOT NULL,
    nombre_retira VARCHAR(160) NOT NULL,
    cantidad INTEGER NOT NULL,
    fecha_retiro BIGINT NOT NULL,
    envoltura_regalo BOOLEAN NOT NULL,
    acepta_condiciones BOOLEAN NOT NULL,
    total_clp BIGINT NOT NULL,
    fecha_registro BIGINT NOT NULL,
    estado VARCHAR(32) NOT NULL,
    correo VARCHAR(254) NOT NULL,
    notas VARCHAR(1000)
);
CREATE TABLE historial_pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    tipo VARCHAR(32) NOT NULL,
    descripcion VARCHAR(2000) NOT NULL,
    estado_anterior VARCHAR(32),
    estado_nuevo VARCHAR(32),
    fecha_registro BIGINT NOT NULL,
    CONSTRAINT fk_historial_pedido FOREIGN KEY(pedido_id) REFERENCES pedidos(id)
);
CREATE INDEX idx_pedidos_fecha ON pedidos(fecha_registro);
CREATE INDEX idx_historial_pedido ON historial_pedidos(pedido_id);
