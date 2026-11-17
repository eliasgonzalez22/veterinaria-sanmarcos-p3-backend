-- ==========================================================
-- Modelo de datos del microservicio de usuarios (referencia)
-- Hibernate crea estas tablas automaticamente (ddl-auto=update).
-- Este script sirve para documentar el modelo y crearlo a mano si se necesita.
-- Motor: MySQL 8
-- ==========================================================
CREATE DATABASE IF NOT EXISTS sanmarcos_usuarios CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sanmarcos_usuarios;

CREATE TABLE IF NOT EXISTS roles (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(30)  NOT NULL,
    descripcion VARCHAR(150) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_nombre (nombre)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    nombre              VARCHAR(80)  NOT NULL,
    email               VARCHAR(120) NOT NULL,
    clave_hash          VARCHAR(100) NOT NULL,   -- hash BCrypt, nunca la clave en texto plano
    rol_id              BIGINT       NOT NULL,
    activo              BIT(1)       NOT NULL DEFAULT b'1',
    fecha_creacion      DATETIME(6)  NOT NULL,
    fecha_actualizacion DATETIME(6)  NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_email (email),
    CONSTRAINT fk_usuarios_rol FOREIGN KEY (rol_id) REFERENCES roles (id)
);

INSERT IGNORE INTO roles (nombre, descripcion) VALUES
    ('ADMINISTRADOR', 'Gestiona el sistema completo, usuarios y roles'),
    ('RECEPCIONISTA', 'Personal de la clinica: ve, confirma y reagenda citas'),
    ('DUENO',         'Dueno de mascota: solicita citas y ve solo lo suyo');

-- Consultas utiles para mostrar en la presentacion:
-- SELECT u.id, u.nombre, u.email, r.nombre AS rol, u.activo, LEFT(u.clave_hash, 20) AS hash
--   FROM usuarios u JOIN roles r ON r.id = u.rol_id;
