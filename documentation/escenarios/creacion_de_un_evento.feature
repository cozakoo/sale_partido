# language: es
Característica: Creación de un evento deportivo (E3-H01 / E3-H02)
  
  CONTEXTO DE LA HISTORIA DE USUARIO:
  Como organizador
  Quiero crear un evento deportivo definiendo todos sus detalles
  Para publicarlo en la plataforma y atraer a los participantes adecuados.

  ESPECIFICACIONES Y REGLAS DE NEGOCIO:
  - Campos obligatorios: Fecha y horario (tomados de la reserva), cupo mínimo y máximo de jugadores, y tipo de ingreso.
  - Tipo de ingreso: Puede ser 'Abierto', 'Con Confirmación' o 'Cerrado'. Predeterminado: 'Cerrado'.
  - Tiempo límite de cancelación: Opcional. Valor predeterminado: 1 hora antes del inicio del evento.
  - Nivel de habilidad: Opcional.
  - Estado inicial: Al crearse, el estado del evento debe ser 'En espera de participantes'.
  - Reserva de espacio: Requiere reservar un turno en un espacio/cancha en un local previamente seleccionado en la búsqueda.
  - Validación de cupos: El cupo mínimo debe ser mayor a cero. Por defecto, será la capacidad máxima del espacio.

  Antecedentes:
    Dado que el "Organizador" ha iniciado sesión en la plataforma
    Y ha buscado y seleccionado previamente el local "La Canchita"
    Y ha seleccionado la "Cancha 1 - Fútbol" del local

  # FLUJO PRINCIPAL
  Escenario: Creación exitosa de un evento con los valores predeterminados
    # Regla de éxito: Al definir los campos mínimos obligatorios y obviar los opcionales, el sistema aplica los valores por defecto.
    Cuando el organizador reserva un turno para la fecha "2026-11-20" a las "18:00" horas
    Y establece el cupo máximo usando la capacidad de la cancha
    Y no especifica el cupo mínimo, tipo de ingreso, ni tiempo límite de cancelación
    Y confirma la creación del evento
    Entonces el sistema registra el evento exitosamente
    Y se genera la reserva del turno en la cancha seleccionada
    Y el cupo mínimo se establece automáticamente en la capacidad de la cancha
    Y el tipo de ingreso se establece automáticamente en "Cerrado"
    Y el tiempo límite de cancelación se establece en "1 hora antes del inicio"
    Y el estado del evento es "En espera de participantes"

  # FLUJO ALTERNATIVO
  Escenario: Creación exitosa de un evento personalizando todos los campos opcionales
    # Regla de éxito: El organizador puede personalizar los cupos, tipo de ingreso, tiempos límite y requisitos extras (como habilidad).
    Cuando el organizador reserva un turno para la fecha "2026-11-20" a las "20:00" horas
    Y establece el cupo máximo usando la capacidad de la cancha
    Y establece un cupo mínimo de jugadores menor a la capacidad máxima de la cancha
    Y selecciona el tipo de ingreso "Abierto"
    Y define un tiempo límite de cancelación de "24 horas antes del inicio"
    Y define el nivel de habilidad requerido como "Intermedio"
    Y confirma la creación del evento
    Entonces el sistema registra el evento exitosamente
    Y el tipo de ingreso es "Abierto"
    Y el nivel de habilidad requerido es "Intermedio"
    Y el tiempo límite de cancelación es "24 horas antes del inicio"
    Y el estado del evento es "En espera de participantes"

  # CASO DE BORDE: ERROR
  Escenario: Intento de creación de evento con un cupo mínimo inválido (cero o menor)
    # Regla de error: El sistema valida estrictamente que el cupo mínimo sea siempre mayor a cero.
    Cuando el organizador reserva un turno para la fecha "2026-11-20" a las "19:00" horas
    Y establece el cupo máximo usando la capacidad de la cancha
    Y establece el cupo mínimo en 0 jugadores
    Y confirma la creación del evento
    Entonces el sistema rechaza la creación
    Y muestra el mensaje de error "El cupo mínimo de jugadores debe ser mayor a cero"
    Y no se registra ningún evento en el sistema

  # CASO DE BORDE: ERROR
  Escenario: Intento de creación de evento sin haber seleccionado previamente un espacio
    # Regla de error: No se puede crear un evento si no hay una reserva asociada a un espacio previamente buscado y seleccionado.
    Dado que el organizador NO ha seleccionado ninguna cancha previamente
    Cuando intenta crear un evento para la fecha "2026-11-20" a las "18:00" horas
    Y confirma la creación del evento
    Entonces el sistema rechaza la creación
    Y muestra el mensaje de error "Debe seleccionar y reservar un espacio para el evento"
