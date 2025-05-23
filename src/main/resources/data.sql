INSERT INTO tarifario_licencia (clase_licencia, vigencia_anios, costo) VALUES
                                                                           ('A', 5, 40.0), ('A', 4, 30.0), ('A', 3, 25.0), ('A', 1, 20.0),
                                                                           ('B', 5, 40.0), ('B', 4, 30.0), ('B', 3, 25.0), ('B', 1, 20.0),
                                                                           ('C', 5, 47.0), ('C', 4, 35.0), ('C', 3, 30.0), ('C', 1, 23.0),
                                                                           ('E', 5, 59.0), ('E', 4, 44.0), ('E', 3, 39.0), ('E', 1, 29.0),
                                                                           ('G', 5, 40.0), ('G', 4, 30.0), ('G', 3, 25.0), ('G', 1, 20.0);
INSERT INTO usuarios (nombre, apellido, fecha_nacimiento, username, password, rol) VALUES
                                                                                      ('Administrador', 'Sistema', '1970-01-01', 'admin', 'admin123', 'SUPER_USER'),
                                                                                      ('Operador',    'Turno1',  '1985-06-15', 'operador', 'operador123', 'OPERADOR');