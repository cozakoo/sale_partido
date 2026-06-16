-- Seed de configuraciones de horario para que los locales sean "activos"
-- y aparezcan al aplicar filtros (ubicacion, deporte, fecha, horario).
-- Idempotente: se puede correr varias veces sin duplicar.

-- 1) Una configuracion_horario activa por cada cancha.
--    duracion_turno = 60 min en nanosegundos (Hibernate guarda Duration en ns).
INSERT INTO configuracion_horario (uuid, activo, duracion_turno, cancha_uuid)
SELECT md5('ch-' || c.uuid::text)::uuid, true, 3600000000000, c.uuid
FROM cancha c
WHERE NOT EXISTS (
    SELECT 1 FROM configuracion_horario ch WHERE ch.cancha_uuid = c.uuid
);

-- 2) Los 7 dias de la semana por cada configuracion_horario (08:00 a 23:00).
INSERT INTO configuracion_dia (uuid, dia_semana, hora_inicio, hora_fin, configuracion_horario_uuid)
SELECT md5('cd-' || ch.uuid::text || '-' || d.dia)::uuid, d.dia, TIME '08:00', TIME '23:00', ch.uuid
FROM configuracion_horario ch
CROSS JOIN (VALUES
    ('MONDAY'),('TUESDAY'),('WEDNESDAY'),('THURSDAY'),('FRIDAY'),('SATURDAY'),('SUNDAY')
) AS d(dia)
WHERE NOT EXISTS (
    SELECT 1 FROM configuracion_dia cd
    WHERE cd.configuracion_horario_uuid = ch.uuid AND cd.dia_semana = d.dia
);
