--liquibase formatted sql
--changeset corpaliv:productos-001
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(160) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    precio_clp INTEGER NOT NULL,
    activo BOOLEAN NOT NULL
);
--changeset corpaliv:productos-002
INSERT INTO productos(id,nombre,descripcion,precio_clp,activo) VALUES
(1,'Difusor colgante','Producto solidario CORPALIV',48000,TRUE),
(2,'Difusor de aroma','Producto solidario CORPALIV',62000,TRUE),
(3,'Bolsa Alba Lab','Producto solidario CORPALIV',42000,TRUE),
(4,'Guatero de semillas pequeño','Producto solidario CORPALIV',75000,TRUE);
