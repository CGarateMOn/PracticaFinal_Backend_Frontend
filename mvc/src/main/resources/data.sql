
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

-- Reservas para Admin (id=5)
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (5, 1, '2026-04-28', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (5, 2, '2026-05-05', '12:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (5, 3, '2026-05-15', '18:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (5, 1, '2026-04-20', '10:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP);

-- Reservas para Carlos (id=6)
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (6, 1, '2026-04-29', '09:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (6, 3, '2026-05-10', '17:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (6, 2, '2026-05-20', '11:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (6, 1, '2026-04-15', '16:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP);

-- Reservas para Maria (id=7)
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (7, 2, '2026-04-30', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (7, 1, '2026-05-08', '19:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (7, 3, '2026-05-25', '12:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (7, 2, '2026-06-01', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP);
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
VALUES (7, 1, '2026-04-10', '15:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP);