# language: es

Característica: Búsqueda de locales deportivos
  Como jugador
  Quiero buscar locales deportivos
  Para conocer opciones donde reservar espacios y organizar eventos

  Antecedentes:
    Dado que existen locales deportivos registrados y activos en el sistema
    Y cada local cuenta con información de ubicación, deportes disponibles y horarios de atención

  Escenario: Visualizar listado de locales disponibles
    Cuando el jugador accede a la sección "Buscar locales"
    Entonces el sistema muestra un listado de locales registrados y activos
    Y cada local visualiza los siguientes datos:
      | nombre                 |
      | ubicación              |
      | deportes disponibles   |
      | horarios de atención   |

  Escenario: Filtrar locales por ubicación
    Cuando el jugador selecciona una ubicación determinada
    Y aplica el filtro de búsqueda
    Entonces el sistema muestra únicamente los locales que pertenecen a la ubicación seleccionada

  Escenario: Filtrar locales por deporte
    Cuando el jugador selecciona un tipo de deporte
    Y aplica el filtro de búsqueda
    Entonces el sistema muestra únicamente los locales que disponen de ese deporte

  Escenario: Filtrar locales por fecha y horario
    Cuando el jugador selecciona una fecha y un horario disponible
    Y aplica el filtro de búsqueda
    Entonces el sistema muestra únicamente los locales con disponibilidad para ese rango

  Escenario: Aplicar múltiples filtros de búsqueda
    Cuando el jugador selecciona ubicación, deporte y horario
    Y aplica los filtros de búsqueda
    Entonces el sistema muestra únicamente los locales que cumplen todos los criterios seleccionados

  Escenario: No se encontraron resultados
    Cuando el jugador aplica filtros que no coinciden con ningún local
    Entonces el sistema muestra el mensaje "No se encontraron resultados para la búsqueda realizada"

  Escenario: Actualización dinámica del listado filtrado
    Cuando el jugador modifica alguno de los filtros de búsqueda
    Entonces el sistema actualiza el listado de locales según los nuevos criterios seleccionados