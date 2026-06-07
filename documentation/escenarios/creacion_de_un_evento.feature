# language: es
Característica: Creación de un evento deportivo E3-H01
  
  CONTEXTO DE LA HISTORIA DE USUARIO:
  Como organizador
  Quiero crear un evento deportivo definiendo todos sus detalles
  Para publicarlo en la plataforma y atraer a los participantes adecuados.

  ESPECIFICACIONES Y REGLAS DE NEGOCIO:
  - Interfaz de creación: La interfaz muestra los valores precargados por defecto (ej. cupo mínimo igual a capacidad, tipo de ingreso 'Cerrado', tiempo de cancelación 1 hora) y permite al usuario modificarlos si selecciona un valor personalizado.
  - Campos obligatorios: Fecha y horario (tomados de la reserva), cupo mínimo y máximo de jugadores, y tipo de ingreso.
  - Tipo de ingreso: Puede ser 'Abierto', 'Con Confirmación' o 'Cerrado'. Predeterminado: 'Cerrado'.
  - Tiempo límite de cancelación de participación: Opcional. Valor predeterminado: 1 hora antes del inicio del evento. Debe ser un valor válido entre 1 hora y 24 horas.
  - Nivel de habilidad: Opcional. Valores permitidos: 'Principiante', 'Intermedio', 'Avanzado', 'Sin especificar'.
  - Deportes permitidos: 'Fútbol', 'Pádel', 'Tenis', 'Básquet'.
  - Estado inicial: Al crearse, el estado del evento debe ser 'En espera de participantes'.
  - Reserva de espacio: Requiere reservar un turno en un espacio/cancha en un local previamente seleccionado.
  - Validación de cupos: El cupo mínimo debe ser mayor a cero. El cupo máximo debe ser mayor o igual al cupo mínimo, y menor o igual a la capacidad máxima de la cancha.

  Antecedentes:
    Dado que el "Organizador" ha iniciado sesión en la plataforma
    Y ha buscado y seleccionado previamente el local "La Canchita"
    Y ha seleccionado la "Cancha 1 - Fútbol" del local con capacidad máxima de 10 jugadores

  # FLUJOS PRINCIPALES Y ALTERNATIVOS
  Esquema del escenario: Creación exitosa de un evento deportivo
    Cuando el organizador reserva un turno para la fecha "<fecha>" a las "<hora>" horas
    Y establece el cupo máximo en "<cupoMaximo>"
    Y establece el cupo mínimo en "<cupoMinimo>"
    Y selecciona el tipo de ingreso "<tipoIngreso>"
    Y define un tiempo límite de cancelación de participación "<tiempoCancelacion>"
    Y define el nivel de habilidad requerido como "<nivelHabilidad>"
    Y confirma la creación del evento
    Entonces el sistema registra el evento exitosamente
    Y el tipo de ingreso es "<tipoIngresoEsperado>"
    Y el tiempo límite de cancelación de participación es "<tiempoCancelacionEsperado>"
    Y el estado del evento es "En espera de participantes"

    Ejemplos:
      | fecha      | hora  | cupoMaximo | cupoMinimo | tipoIngreso | tiempoCancelacion       | nivelHabilidad  | tipoIngresoEsperado | tiempoCancelacionEsperado |
      | 2026-11-20 | 18:00 | 10         | 10         | Cerrado     | 1 hora antes del inicio | Sin especificar | Cerrado             | 1 hora antes del inicio   |
      | 2026-11-20 | 20:00 | 10         | 8          | Abierto     | 24 horas antes          | Intermedio      | Abierto             | 24 horas antes            |

  # CASOS DE BORDE: ERRORES DE VALIDACIÓN
  Esquema del escenario: Intento fallido de creación de un evento deportivo por errores de validación
    Cuando el organizador reserva un turno para la fecha "<fecha>" a las "<hora>" horas
    Y establece el cupo máximo en "<cupoMaximo>"
    Y establece el cupo mínimo en "<cupoMinimo>"
    Y define un tiempo límite de cancelación de participación "<tiempoCancelacion>"
    Y confirma la creación del evento
    Entonces el sistema rechaza la creación
    Y muestra el mensaje de error "<mensajeError>"
    Y no se registra ningún evento en el sistema

    Ejemplos:
      | fecha      | hora  | cupoMaximo | cupoMinimo | tiempoCancelacion       | mensajeError                                                               |
      | 2026-11-20 | 19:00 | 10         | 0          | 1 hora antes del inicio | El cupo mínimo de jugadores debe ser mayor a cero                          |
      | 2026-11-20 | 19:00 | 10         | 12         | 1 hora antes del inicio | El cupo mínimo no puede ser mayor al cupo máximo                           |
      | 2026-11-20 | 19:00 | 15         | 10         | 1 hora antes del inicio | El cupo máximo no puede superar la capacidad máxima de la cancha           |
      | 2026-11-20 | 19:00 | 10         | 10         | 48 horas antes          | El tiempo límite de cancelación de participación debe estar entre 1 y 24 horas |

  # CASO DE BORDE: ERROR
  Escenario: Intento de creación de evento sin haber seleccionado previamente un espacio
    # Regla de error: No se puede crear un evento si no hay una reserva asociada a un espacio previamente buscado y seleccionado.
    Dado que el organizador NO ha seleccionado ninguna cancha previamente
    Cuando intenta crear un evento para la fecha "2026-11-20" a las "18:00" horas
    Y confirma la creación del evento
    Entonces el sistema rechaza la creación
    Y muestra el mensaje de error "Debe seleccionar y reservar un espacio para el evento"
