-- Script de seed para tests con H2 (sintaxis compatible)
-- Spring Boot carga automáticamente este archivo en tests

-- Insertar deportes
MERGE INTO deporte (uuid, nombre) VALUES
('a0000000-0000-0000-0000-000000000001', 'Fútbol'),
('a0000000-0000-0000-0000-000000000002', 'Tenis'),
('a0000000-0000-0000-0000-000000000003', 'Pádel'),
('a0000000-0000-0000-0000-000000000004', 'Básquet');

-- Insertar localidad
MERGE INTO localidad (uuid, nombre) VALUES
('b0000000-0000-0000-0000-000000000001', 'Puerto Madryn');

-- Insertar ubicaciones
MERGE INTO ubicacion (uuid, localidad_uuid, direccion) VALUES
('b1b2c3d4-e29b-41d4-a716-446655440001', 'b0000000-0000-0000-0000-000000000001', 'Centro'),
('b1b2c3d4-e29b-41d4-a716-446655440002', 'b0000000-0000-0000-0000-000000000001', 'Sur'),
('b1b2c3d4-e29b-41d4-a716-446655440003', 'b0000000-0000-0000-0000-000000000001', 'Norte'),
('b1b2c3d4-e29b-41d4-a716-446655440004', 'b0000000-0000-0000-0000-000000000001', 'Centro');

-- Insertar locales
MERGE INTO local (uuid, nombre, ubicacion_uuid, telefono, descripcion) VALUES
('a1b2c3d4-e29b-41d4-a716-446655440001', 'Club Deportivo Centro', 'b1b2c3d4-e29b-41d4-a716-446655440001', '+54 280 411-1001', 'Complejo deportivo céntrico con canchas de césped sintético y polvo de ladrillo.'),
('a1b2c3d4-e29b-41d4-a716-446655440002', 'Estadio del Sur', 'b1b2c3d4-e29b-41d4-a716-446655440002', '+54 280 411-1002', 'Canchas de fútbol 11 y básquet cubierto. Estacionamiento amplio.'),
('a1b2c3d4-e29b-41d4-a716-446655440003', 'La Canchita', 'b1b2c3d4-e29b-41d4-a716-446655440003', '+54 280 411-1003', 'Espacio familiar con canchas de fútbol 5, pádel y voley playa.'),
('a1b2c3d4-e29b-41d4-a716-446655440004', 'Madryn Tenis Club', 'b1b2c3d4-e29b-41d4-a716-446655440004', '+54 280 411-1004', 'Club especializado en tenis y pádel con profesorado incluido.');

-- Insertar canchas
MERGE INTO cancha (uuid, nombre, deporte_uuid, capacidad, local_uuid) VALUES
('c1000001-0000-0000-0000-000000000001', 'Cancha 1 - Fútbol', 'a0000000-0000-0000-0000-000000000001', 11, 'a1b2c3d4-e29b-41d4-a716-446655440001'),
('c1000002-0000-0000-0000-000000000001', 'Cancha 2 - Tenis', 'a0000000-0000-0000-0000-000000000002', 2, 'a1b2c3d4-e29b-41d4-a716-446655440001'),
('c2000001-0000-0000-0000-000000000002', 'Cancha 1 - Fútbol', 'a0000000-0000-0000-0000-000000000001', 11, 'a1b2c3d4-e29b-41d4-a716-446655440002'),
('c2000002-0000-0000-0000-000000000002', 'Cancha 2 - Básquet', 'a0000000-0000-0000-0000-000000000004', 10, 'a1b2c3d4-e29b-41d4-a716-446655440002'),
('c3000001-0000-0000-0000-000000000003', 'Cancha 1 - Fútbol', 'a0000000-0000-0000-0000-000000000001', 5, 'a1b2c3d4-e29b-41d4-a716-446655440003'),
('c3000002-0000-0000-0000-000000000003', 'Cancha 2 - Pádel', 'a0000000-0000-0000-0000-000000000003', 4, 'a1b2c3d4-e29b-41d4-a716-446655440003'),
('c4000001-0000-0000-0000-000000000004', 'Cancha 1 - Tenis', 'a0000000-0000-0000-0000-000000000002', 2, 'a1b2c3d4-e29b-41d4-a716-446655440004'),
('c4000002-0000-0000-0000-000000000004', 'Cancha 2 - Pádel', 'a0000000-0000-0000-0000-000000000003', 4, 'a1b2c3d4-e29b-41d4-a716-446655440004');
