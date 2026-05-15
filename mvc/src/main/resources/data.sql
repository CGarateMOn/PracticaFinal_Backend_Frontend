
-- Se usa WHERE NOT EXISTS para evitar duplicados en cada arranque de la aplicación.


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

INSERT INTO usuarios (nombre, apellidos, email, password, telefono, rol, activo, fecha_registro)
SELECT 'Lady Amalia IV de Inglaterra', 'Amalia', 'amalia@premium.com', 'nbW1k43JDpIQ8yBgGHtTkg==:puWmqRLR1ftsaQLQo/V2Kg==', '+34 633333333', 'USER', TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'amalia@premium.com');

INSERT INTO usuarios (nombre, apellidos, email, password, telefono, rol, activo, fecha_registro)
SELECT 'Asistenta Limpiadora Martina', 'Asistenta Martina Limpiadora', 'martina@premium.com', 'nbW1k43JDpIQ8yBgGHtTkg==:puWmqRLR1ftsaQLQo/V2Kg==', '+34 644444444', 'USER', TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'martina@premium.com');

INSERT INTO usuarios (nombre, apellidos, email, password, telefono, rol, activo, fecha_registro)
SELECT 'Raffaella Peruana Acevichada', 'Raffaella', 'raffaella@premium.com', 'nbW1k43JDpIQ8yBgGHtTkg==:puWmqRLR1ftsaQLQo/V2Kg==', '+34 655555555', 'USER', TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'raffaella@premium.com');

INSERT INTO usuarios (nombre, apellidos, email, password, telefono, rol, activo, fecha_registro)
SELECT 'Irene Paseadora de Perros Dueña de Tres Chihuahuas', 'Irene', 'irene@premium.com', 'nbW1k43JDpIQ8yBgGHtTkg==:puWmqRLR1ftsaQLQo/V2Kg==', '+34 666666666', 'USER', TRUE, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE email = 'irene@premium.com');

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

-- Reservas para Admin
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'admin@premium.com'), 1, '2026-05-28', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'admin@premium.com') AND fecha_reserva = '2026-05-28' AND hora_inicio = '10:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'admin@premium.com'), 2, '2026-05-05', '12:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'admin@premium.com') AND fecha_reserva = '2026-05-05' AND hora_inicio = '12:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'admin@premium.com'), 3, '2026-05-15', '18:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'admin@premium.com') AND fecha_reserva = '2026-05-15' AND hora_inicio = '18:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'admin@premium.com'), 1, '2026-04-20', '10:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'admin@premium.com') AND fecha_reserva = '2026-04-20' AND hora_inicio = '10:00:00');

-- Reservas para Carlos
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'carlos@premium.com'), 1, '2026-04-29', '09:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'carlos@premium.com') AND fecha_reserva = '2026-04-29' AND hora_inicio = '09:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'carlos@premium.com'), 3, '2026-05-10', '17:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'carlos@premium.com') AND fecha_reserva = '2026-05-10' AND hora_inicio = '17:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'carlos@premium.com'), 2, '2026-05-20', '11:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'carlos@premium.com') AND fecha_reserva = '2026-05-20' AND hora_inicio = '11:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'carlos@premium.com'), 1, '2026-04-15', '16:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'carlos@premium.com') AND fecha_reserva = '2026-04-15' AND hora_inicio = '16:00:00');

-- Reservas para María
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'maria@premium.com'), 2, '2026-04-30', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'maria@premium.com') AND fecha_reserva = '2026-04-30' AND hora_inicio = '10:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'maria@premium.com'), 1, '2026-05-08', '19:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'maria@premium.com') AND fecha_reserva = '2026-05-08' AND hora_inicio = '19:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'maria@premium.com'), 3, '2026-05-25', '12:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'maria@premium.com') AND fecha_reserva = '2026-05-25' AND hora_inicio = '12:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'maria@premium.com'), 2, '2026-06-01', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'maria@premium.com') AND fecha_reserva = '2026-06-01' AND hora_inicio = '10:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'maria@premium.com'), 1, '2026-04-10', '15:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'maria@premium.com') AND fecha_reserva = '2026-04-10' AND hora_inicio = '15:00:00');

-- Reservas para Amalia
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'amalia@premium.com'), 3, '2026-05-22', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'amalia@premium.com') AND fecha_reserva = '2026-05-22' AND hora_inicio = '10:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'amalia@premium.com'), 1, '2026-06-03', '16:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'amalia@premium.com') AND fecha_reserva = '2026-06-03' AND hora_inicio = '16:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'amalia@premium.com'), 2, '2026-06-10', '12:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'amalia@premium.com') AND fecha_reserva = '2026-06-10' AND hora_inicio = '12:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'amalia@premium.com'), 1, '2026-04-18', '11:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'amalia@premium.com') AND fecha_reserva = '2026-04-18' AND hora_inicio = '11:00:00');

-- Reservas para Martina
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'martina@premium.com'), 2, '2026-05-27', '09:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'martina@premium.com') AND fecha_reserva = '2026-05-27' AND hora_inicio = '09:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'martina@premium.com'), 3, '2026-06-05', '14:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'martina@premium.com') AND fecha_reserva = '2026-06-05' AND hora_inicio = '14:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'martina@premium.com'), 1, '2026-04-22', '17:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'martina@premium.com') AND fecha_reserva = '2026-04-22' AND hora_inicio = '17:00:00');

-- Reservas para Raffaella
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'raffaella@premium.com'), 1, '2026-05-30', '11:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'raffaella@premium.com') AND fecha_reserva = '2026-05-30' AND hora_inicio = '11:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'raffaella@premium.com'), 3, '2026-06-08', '18:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'raffaella@premium.com') AND fecha_reserva = '2026-06-08' AND hora_inicio = '18:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'raffaella@premium.com'), 2, '2026-04-25', '10:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'raffaella@premium.com') AND fecha_reserva = '2026-04-25' AND hora_inicio = '10:00:00');

-- Reservas para Irene
INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'irene@premium.com'), 2, '2026-06-02', '10:00:00', 60, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'irene@premium.com') AND fecha_reserva = '2026-06-02' AND hora_inicio = '10:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'irene@premium.com'), 1, '2026-06-12', '17:00:00', 90, 'ACTIVA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'irene@premium.com') AND fecha_reserva = '2026-06-12' AND hora_inicio = '17:00:00');

INSERT INTO reservas (id_usuario, id_pista, fecha_reserva, hora_inicio, duracion_minutos, estado, fecha_creacion)
SELECT (SELECT id_usuario FROM usuarios WHERE email = 'irene@premium.com'), 3, '2026-04-28', '12:00:00', 60, 'CANCELADA', CURRENT_TIMESTAMP
    WHERE NOT EXISTS (SELECT 1 FROM reservas WHERE id_usuario = (SELECT id_usuario FROM usuarios WHERE email = 'irene@premium.com') AND fecha_reserva = '2026-04-28' AND hora_inicio = '12:00:00');