# language: es

@configuracion_espacios_horario
Característica: Configuración de disponibilidad y duración de turnos
  Como propietario de un establecimiento deportivo,
  quiero configurar los horarios de disponibilidad y la duración de los turnos de mis espacios,
  para que los organizadores puedan reservarlos correctamente dentro del horario habilitado.

  Antecedentes:
    Dado que existe una cancha con número 1 en el sistema
    Y el propietario ha iniciado sesión

  Escenario: Asignación de horario global de disponibilidad
    Cuando el propietario selecciona la opción de asignar horario global de "Lunes" a "Lunes"
    Y define la franja horaria de "10:00" a "00:00"
    Entonces la cancha debe figurar como "Disponible" en ese rango horario siempre que no existan turnos previos

  Escenario: Aplicar configuración de horario a todas las canchas
    Dado que el propietario está configurando el horario de una cancha
    Cuando presiona el botón para aplicar ese horario al resto de las canchas
    Entonces el sistema debe pedir confirmación de la acción
    Y al confirmar, el horario debe aplicarse a todas las canchas

  Escenario: Configuración de la duración del turno mediante lista predefinida
    Cuando el propietario va a establecer la duración de los turnos
    Entonces solo debe poder seleccionar opciones de una lista predefinida de "30", "60", "90" o "120" minutos
    Y el campo no debe permitir entrada libre de texto, para que sea simple de validar

  Escenario: Validación del formato de hora en 24 horas
    Cuando el propietario ingresa o visualiza la hora de apertura o cierre
    Entonces el formato de la hora debe ser en 24 hs (no am/pm)

  Escenario: Validación de hora de apertura menor a hora de cierre
    Cuando el propietario define la franja horaria de un día
    Entonces la hora de apertura debe ser menor a la hora de cierre

  Escenario: Incrementos de tiempo en saltos de 30 minutos
    Cuando el propietario ajusta los valores de horario o duración de turno
    Entonces el tiempo debe incrementar en saltos de 30 minutos, para mantener la coherencia de turnos y horarios

  Escenario: Advertencia cuando el horario no es coherente con la duración de turno
    Cuando el propietario define un horario y una duración de turno
    Y una cantidad entera de turnos no ocupa todo el horario definido, de manera que sobran minutos
    Entonces el sistema debe advertir que el horario no es del todo coherente con la duración de turno
