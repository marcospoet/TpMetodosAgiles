-- 1. BORRAR TODAS LAS TARIFAS ACTUALES (opcional si querés empezar limpio)
DELETE FROM tarifario_licencia;
INSERT INTO tarifario_licencia (clase_licencia, vigencia_anios, costo) VALUES
-- A
('A', 5, 40.0), ('A', 4, 30.0), ('A', 3, 25.0), ('A', 1, 20.0),
-- B
('B', 5, 40.0), ('B', 4, 30.0), ('B', 3, 25.0), ('B', 1, 20.0),
-- C
('C', 5, 47.0), ('C', 4, 35.0), ('C', 3, 30.0), ('C', 1, 23.0),
-- D (asumimos valores similares a C por ser profesional)
('D', 5, 50.0), ('D', 4, 38.0), ('D', 3, 32.0), ('D', 1, 25.0),
-- E
('E', 5, 59.0), ('E', 4, 44.0), ('E', 3, 39.0), ('E', 1, 29.0),
-- F (mismo costo que clase A/B)
('F', 5, 40.0), ('F', 4, 30.0), ('F', 3, 25.0), ('F', 1, 20.0),
-- G
('G', 5, 40.0), ('G', 4, 30.0), ('G', 3, 25.0), ('G', 1, 20.0);
