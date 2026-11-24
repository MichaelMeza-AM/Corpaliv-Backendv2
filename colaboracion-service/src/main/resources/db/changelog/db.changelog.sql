--liquibase formatted sql
--changeset corpaliv:colaboraciones-001
CREATE TABLE solicitudes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(32) NOT NULL,
    nombre VARCHAR(160) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    estado VARCHAR(32) NOT NULL,
    fecha_registro BIGINT NOT NULL,
    correo VARCHAR(254) NOT NULL,
    telefono VARCHAR(32),
    organizacion VARCHAR(160),
    cargo VARCHAR(160),
    detalle_colaboracion VARCHAR(2000),
    notificaciones BOOLEAN,
    donante_tipo VARCHAR(20),
    monto_clp BIGINT,
    anonimo BOOLEAN
);
CREATE TABLE historial_solicitudes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    solicitud_id BIGINT NOT NULL,
    tipo VARCHAR(32) NOT NULL,
    descripcion VARCHAR(2000) NOT NULL,
    estado_anterior VARCHAR(32),
    estado_nuevo VARCHAR(32),
    fecha_registro BIGINT NOT NULL,
    CONSTRAINT fk_historial_solicitud FOREIGN KEY(solicitud_id) REFERENCES solicitudes(id) ON DELETE CASCADE
);
CREATE INDEX idx_solicitudes_fecha ON solicitudes(fecha_registro);
CREATE INDEX idx_historial_solicitud ON historial_solicitudes(solicitud_id);
