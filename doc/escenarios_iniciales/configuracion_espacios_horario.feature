# language: es

@configuracion_espacios_horario
Característica: Configuración de disponibilidad y duración de turnos
  Como propietario de un establecimiento deportivo,
  quiero configurar los horarios de disponibilidad y la duración de los turnos de mis espacios,
  para que los organizadores puedan reservarlos correctamente dentro del horario habilitado.

  Antecedentes:
    Dado que existe una cancha con número 1 en el sistema
    Y el propietario ha iniciado sesión en el panel de administración

  Escenario: Asignación de horario global de disponibilidad
    Cuando el propietario selecciona la opción de asignar horario global de "Lunes" a "Lunes"
    Y define la franja horaria de "10:00" a "00:00"
    Entonces la cancha debe figurar como "Disponible" en ese rango horario siempre que no existan turnos previos

  Escenario: Configuración de la duración estándar del turno
    Cuando el propietario establece la duración estándar del turno en "60" minutos
    Entonces en la información detallada de la cancha se debe visualizar que la duración es de "60 minutos"

  Escenario: Reserva exitosa de un turno dentro del horario habilitado
    Dado que la cancha tiene una duración de turno de "60" minutos
    Y tiene un horario habilitado de "Lunes a Lunes" de "10:00" a "00:00"
    Cuando un usuario selecciona y confirma el botón de tomar turno para las "11:00"
    Entonces el turno debe ser asignado exitosamente al usuario
    Y el turno de las "11:00" ya no debe estar disponible para otros usuarios

  Escenario: Restricción de reserva en días marcados como cerrados
    Cuando el propietario selecciona el día "Lunes" y marca la opción "Cerrado"
    Entonces el sistema debe impedir que el usuario seleccione el día "Lunes" para tomar un turno

  Escenario: Prioridad de horario individual sobre horario global (Excepción)
    Dado que la cancha tiene un horario global de "10:00" a "00:00"
    Cuando el propietario modifica el horario específico del día "Martes" de "10:00" a "16:00"
    Y un usuario intenta solicitar un turno el día "Martes" a las "17:00"
    Entonces el sistema debe informar al usuario que la cancha se encuentra cerrada en ese horario