CREATE DATABASE IF NOT EXISTS biblioteca_universitaria
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE biblioteca_universitaria;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    identificador VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    tipo_usuario VARCHAR(30) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE materiales (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    titulo VARCHAR(150) NOT NULL,
    tipo_material VARCHAR(30) NOT NULL,
    cantidad_total INT NOT NULL,
    cantidad_disponible INT NOT NULL
);

CREATE TABLE prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    material_id INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    fecha_devolucion DATE NULL,

    CONSTRAINT fk_prestamos_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_prestamos_material
        FOREIGN KEY (material_id)
        REFERENCES materiales(id)
);

CREATE TABLE multas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id INT NOT NULL,
    dias_atraso INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    fecha_generacion DATE NOT NULL,
    pagada BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_multas_prestamo
        FOREIGN KEY (prestamo_id)
        REFERENCES prestamos(id)
);