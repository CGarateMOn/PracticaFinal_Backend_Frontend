-- Usuarios
INSERT INTO usuarios (nombre, apellidos, email, password, telefono, rol, activo, fecha_registro)
SELECT 'Admin', 'Premium', 'admin@premium.com', 'nbW1k43JDpIQ8yBgGHtTkg==:puWmqRLR1ftsaQLQo/V2Kg==', '+34 600000000', 'ADMIN', TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'admin@premium.com');

INSERT INTO usuarios (nombre, apellidos, email, password, telefono, rol, activo, fecha_registro)
SELECT 'Carlos', 'Martín', 'carlos@premium.com', 'nbW1k43JDpIQ8yBgGHtTkg==:puWmqRLR1ftsaQLQo/V2Kg==', '+34 611111111', 'USER', TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'carlos@premium.com');

INSERT INTO usuarios (nombre, apellidos, email, password, telefono, rol, activo, fecha_registro)
SELECT 'María', 'García', 'maria@premium.com', 'nbW1k43JDpIQ8yBgGHtTkg==:puWmqRLR1ftsaQLQo/V2Kg==', '+34 622222222', 'USER', TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'maria@premium.com');

-- Pistas
INSERT INTO pistas (nombre, ubicacion, precio_hora, activa, fecha_alta)
SELECT 'Pista 1', 'Zona Norte', 15.00, TRUE, CURRENT_DATE
    WHERE NOT EXISTS (SELECT 1 FROM pistas WHERE nombre = 'Pista 1');

INSERT INTO pistas (nombre, ubicacion, precio_hora, activa, fecha_alta)
SELECT 'Pista 2', 'Zona Norte', 15.00, TRUE, CURRENT_DATE
    WHERE NOT EXISTS (SELECT 1 FROM pistas WHERE nombre = 'Pista 2');

INSERT INTO pistas (nombre, ubicacion, precio_hora, activa, fecha_alta)
SELECT 'Pista 3', 'Zona Sur', 20.00, TRUE, CURRENT_DATE
    WHERE NOT EXISTS (SELECT 1 FROM pistas WHERE nombre = 'Pista 3');

INSERT INTO pistas (nombre, ubicacion, precio_hora, activa, fecha_alta)
SELECT 'Pista 4', 'Zona Sur', 20.00, FALSE, CURRENT_DATE
    WHERE NOT EXISTS (SELECT 1 FROM pistas WHERE nombre = 'Pista 4');

-- Reservas para Admin (ahora es el id=1)
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (1, 1, '2026-04-28', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (1, 2, '2026-05-05', '12:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (1, 3, '2026-05-15', '18:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (1, 1, '2026-04-20', '10:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP);

-- Reservas para Carlos (ahora es el id=2)
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (2, 1, '2026-04-29', '09:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (2, 3, '2026-05-10', '17:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (2, 2, '2026-05-20', '11:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (2, 1, '2026-04-15', '16:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP);

-- Reservas para Maria (ahora es el id=3)
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (3, 2, '2026-04-30', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (3, 1, '2026-05-08', '19:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (3, 3, '2026-05-25', '12:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (3, 2, '2026-06-01', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (3, 1, '2026-04-10', '15:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP);