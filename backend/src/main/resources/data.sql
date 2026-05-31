-- Script de seed para inicializar datos de prueba
-- Spring Boot ejecuta automáticamente este archivo en el arranque

-- Insertar deportes (si no existen)
INSERT INTO deporte (uuid, nombre) VALUES
('a0000000-0000-0000-0000-000000000001', 'Fútbol')
ON CONFLICT DO NOTHING;

INSERT INTO deporte (uuid, nombre) VALUES
('a0000000-0000-0000-0000-000000000002', 'Tenis')
ON CONFLICT DO NOTHING;

INSERT INTO deporte (uuid, nombre) VALUES
('a0000000-0000-0000-0000-000000000003', 'Pádel')
ON CONFLICT DO NOTHING;

INSERT INTO deporte (uuid, nombre) VALUES
('a0000000-0000-0000-0000-000000000004', 'Básquet')
ON CONFLICT DO NOTHING;

-- Insertar locales (si no existen)
INSERT INTO local (uuid, nombre, direccion, telefono, descripcion, horario) VALUES
('a1b2c3d4-e29b-41d4-a716-446655440001', 'Club Deportivo Centro', 'Centro', '+54 280 411-1001', 'Complejo deportivo céntrico con canchas de césped sintético y polvo de ladrillo.', '08:00-23:00')
ON CONFLICT DO NOTHING;

INSERT INTO local (uuid, nombre, direccion, telefono, descripcion, horario) VALUES
('a1b2c3d4-e29b-41d4-a716-446655440002', 'Estadio del Sur', 'Sur', '+54 280 411-1002', 'Canchas de fútbol 11 y básquet cubierto. Estacionamiento amplio.', '09:00-22:00')
ON CONFLICT DO NOTHING;

INSERT INTO local (uuid, nombre, direccion, telefono, descripcion, horario) VALUES
('a1b2c3d4-e29b-41d4-a716-446655440003', 'La Canchita', 'Norte', '+54 280 411-1003', 'Espacio familiar con canchas de fútbol 5, pádel y voley playa.', '10:00-22:00')
ON CONFLICT DO NOTHING;

INSERT INTO local (uuid, nombre, direccion, telefono, descripcion, horario) VALUES
('a1b2c3d4-e29b-41d4-a716-446655440004', 'Madryn Tenis Club', 'Centro', '+54 280 411-1004', 'Club especializado en tenis y pádel con profesorado incluido.', '07:00-22:00')
ON CONFLICT DO NOTHING;

-- Insertar canchas (si no existen)
INSERT INTO cancha (uuid, nombre, deporte_uuid, capacidad, local_uuid) VALUES
('c1000001-0000-0000-0000-000000000001', 'Cancha 1 - Fútbol', 'a0000000-0000-0000-0000-000000000001', 11, 'a1b2c3d4-e29b-41d4-a716-446655440001')
ON CONFLICT DO NOTHING;

INSERT INTO cancha (uuid, nombre, deporte_uuid, capacidad, local_uuid) VALUES
('c1000002-0000-0000-0000-000000000001', 'Cancha 2 - Tenis', 'a0000000-0000-0000-0000-000000000002', 2, 'a1b2c3d4-e29b-41d4-a716-446655440001')
ON CONFLICT DO NOTHING;

INSERT INTO cancha (uuid, nombre, deporte_uuid, capacidad, local_uuid) VALUES
('c2000001-0000-0000-0000-000000000002', 'Cancha 1 - Fútbol', 'a0000000-0000-0000-0000-000000000001', 11, 'a1b2c3d4-e29b-41d4-a716-446655440002')
ON CONFLICT DO NOTHING;

INSERT INTO cancha (uuid, nombre, deporte_uuid, capacidad, local_uuid) VALUES
('c2000002-0000-0000-0000-000000000002', 'Cancha 2 - Básquet', 'a0000000-0000-0000-0000-000000000004', 10, 'a1b2c3d4-e29b-41d4-a716-446655440002')
ON CONFLICT DO NOTHING;

INSERT INTO cancha (uuid, nombre, deporte_uuid, capacidad, local_uuid) VALUES
('c3000001-0000-0000-0000-000000000003', 'Cancha 1 - Fútbol', 'a0000000-0000-0000-0000-000000000001', 5, 'a1b2c3d4-e29b-41d4-a716-446655440003')
ON CONFLICT DO NOTHING;

INSERT INTO cancha (uuid, nombre, deporte_uuid, capacidad, local_uuid) VALUES
('c3000002-0000-0000-0000-000000000003', 'Cancha 2 - Pádel', 'a0000000-0000-0000-0000-000000000003', 4, 'a1b2c3d4-e29b-41d4-a716-446655440003')
ON CONFLICT DO NOTHING;

INSERT INTO cancha (uuid, nombre, deporte_uuid, capacidad, local_uuid) VALUES
('c4000001-0000-0000-0000-000000000004', 'Cancha 1 - Tenis', 'a0000000-0000-0000-0000-000000000002', 2, 'a1b2c3d4-e29b-41d4-a716-446655440004')
ON CONFLICT DO NOTHING;

INSERT INTO cancha (uuid, nombre, deporte_uuid, capacidad, local_uuid) VALUES
('c4000002-0000-0000-0000-000000000004', 'Cancha 2 - Pádel', 'a0000000-0000-0000-0000-000000000003', 4, 'a1b2c3d4-e29b-41d4-a716-446655440004')
ON CONFLICT DO NOTHING;
