-- Estas son queries para no tener que abrir Dbeaver
-- Solo se usan para debug

-- Listar tablas
/*
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
  AND table_type = 'BASE TABLE';
*/

-- Nivel de deporte
--/*
SELECT * FROM nivel_deporte
where uuid = '3eb9da46-311a-4f98-b586-8fef6a0f3858'
--*/