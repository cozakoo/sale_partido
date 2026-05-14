# language: es
Característica: Visualización de disponibilidad y gestión de turnos
  Como propietario de un local deportivo
  Quiero ver un calendario con la disponibilidad y ocupación de mis espacios
  Para estar al tanto de mis reservas y planificar la gestión de mi local

  Antecedentes:
    Dado que la "Cancha 1" tiene sus horarios configurados
    Y existen turnos tanto disponibles como ocupados en el sistema

  Escenario: Ver disponibilidad semanal
    Cuando el propietario presiona el botón "Ver disponibilidad" para una semana determinada
    Entonces el sistema muestra los horarios ocupados y disponibles de la semana seleccionada

  Escenario: Ver disponibilidad diaria
    Cuando el propietario presiona el botón "Ver disponibilidad" para un día determinado
    Entonces el sistema muestra los horarios ocupados y disponibles del día seleccionado

  Escenario: Ver detalle de un turno del día
    Cuando el propietario presiona el botón "Ver turnos hoy"
    Y selecciona "Ver detalle" en un turno específico
    Entonces se visualizan los siguientes datos:
      | campo                     |
      | nombre del organizador    |
      | deporte                   |
      | participantes confirmados |
      | estado del evento         |

  Escenario: Consultar historial de turnos finalizados
    Dado que la "Cancha 1" cuenta con al menos 10 turnos concretados
    Cuando el propietario presiona el botón "Ver historial de turnos"
    Y selecciona un partido finalizado en una fecha pasada
    Entonces se visualizan los siguientes datos del historial:
      | campo                   |
      | nombre del organizador  |
      | deporte                 |
      | cantidad de asistentes  |