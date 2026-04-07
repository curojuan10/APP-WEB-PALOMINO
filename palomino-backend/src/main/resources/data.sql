-- Script para insertar datos iniciales en la BD
-- Se ejecuta automáticamente con Hibernate (ddl-auto=update)

-- Insertar roles si no existen
INSERT IGNORE INTO roles (nombre) VALUES ('ADMIN');
INSERT IGNORE INTO roles (nombre) VALUES ('CLIENTE');

