CREATE TABLE IF NOT EXISTS tarifario_licencia (
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  clase_licencia VARCHAR(2) NOT NULL,
                                                  vigencia_anios INT NOT NULL,
                                                  costo DOUBLE NOT NULL
);

INSERT INTO tarifario_licencia (clase_licencia, vigencia_anios, costo) VALUES
                                                                           ('A', 5, 40.0), ('A', 4, 30.0), ('A', 3, 25.0), ('A', 1, 20.0),
                                                                           ('B', 5, 40.0), ('B', 4, 30.0), ('B', 3, 25.0), ('B', 1, 20.0),
                                                                           ('C', 5, 47.0), ('C', 4, 35.0), ('C', 3, 30.0), ('C', 1, 23.0),
                                                                           ('E', 5, 59.0), ('E', 4, 44.0), ('E', 3, 39.0), ('E', 1, 29.0),
                                                                           ('G', 5, 40.0), ('G', 4, 30.0), ('G', 3, 25.0), ('G', 1, 20.0);