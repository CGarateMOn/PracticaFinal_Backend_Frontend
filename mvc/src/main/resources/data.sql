
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