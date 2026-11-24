--liquibase formatted sql
--changeset corpaliv:auth-001
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(254) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    rol VARCHAR(40) NOT NULL,
    activo BOOLEAN NOT NULL
);
