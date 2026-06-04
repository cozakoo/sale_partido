# language: es

Definiciones
- "Posee cupos disponibles":
    la cantidad actual de participantes del evento es estrictamente menor al cupo máximo del evento.
- "Cumple con el nivel requerido":
    el nivel del participante es igual o mayor al nivel de habilidad definido para el evento.
- "Participante registrado":
    participante que ya posee una participación Confirmada o Pendiente en el evento.
- "Invitación respondida":
    invitación que ya fue aceptada o rechazada previamente.
 
Característica: Unión de participantes a eventos deportivos
Como participante
Quiero unirme a un evento deportivo
Para asegurar mi lugar y participar del encuentro

Antecedentes:
Dado que existe un evento deportivo disponible para inscripción
Y el participante se encuentra autenticado en el sistema
   
Escenario: Visualizar el detalle completo de un evento
Cuando el participante selecciona un evento deportivo
Entonces se visualizan los siguientes datos:

| deporte                        |
| fecha                          |
| hora                           |
| local                          |
| dirección                      |
| nivel requerido                |
| cupo mínimo                    |
| cupo máximo                    |
| participantes confirmados      |
| tipo de ingreso                |

Escenario: Unirse directamente a un evento abierto
Dado que el evento es de tipo "Abierto"
Y el evento posee cupos disponibles
Y el participante cumple con el nivel requerido
Cuando el participante presiona el botón "Unirse"
Entonces su estado de participación queda como "Confirmado"
Y el sistema muestra una confirmación de participación

Escenario: Solicitar participación en un evento con confirmación
Dado que el evento es de tipo "Con Confirmación"
Y el evento posee cupos disponibles
Y el participante cumple con el nivel requerido
Cuando el participante presiona el botón "Solicitar participación"
Entonces su estado de participación queda como "Pendiente"
Y el sistema muestra una confirmación de envío de solicitud

Escenario: Aceptar invitación a un evento cerrado
Dado que el participante recibió una invitación para un evento de tipo "Cerrado"
Cuando el participante presiona el botón "Aceptar invitación"
Entonces queda registrado como participante del evento
Y el sistema muestra una confirmación de participación

Escenario: Rechazar invitación a un evento cerrado
Dado que el participante recibió una invitación para un evento de tipo "Cerrado"
Cuando el participante presiona el botón "Rechazar invitación"
Entonces la invitación queda rechazada
Y el sistema muestra una confirmación de rechazo

Escenario: Aceptar invitación a un evento con confirmación
Dado que el participante recibió una invitación para un evento de tipo "Con Confirmación"
Cuando el participante presiona el botón "Aceptar invitación"
Entonces queda registrada su respuesta a la invitación
Y el sistema muestra una confirmación de participación

Escenario: Rechazar invitación a un evento con confirmación
Dado que el participante recibió una invitación para un evento de tipo "Con Confirmación"
Cuando el participante presiona el botón "Rechazar invitación"
Entonces la invitación queda rechazada
Y el sistema muestra una confirmación de rechazo

Escenario: Intentar unirse a un evento sin cupos disponibles
Dado que el evento alcanzó su cupo máximo
Cuando el participante visualiza el detalle del evento
Entonces la opción "Unirse" se encuentra deshabilitada
Y el sistema informa que no hay cupos disponibles

Escenario: Intentar unirse a un evento con nivel de habilidad incompatible
Dado que el evento posee un nivel de habilidad requerido
Y el nivel del participante es diferente al requerido
Cuando el participante intenta unirse al evento
Entonces el sistema impide la participación
Y informa que no cumple con el nivel requerido

Escenario: Intentar solicitar participación a un evento con confirmación y nivel incompatible
Dado que el evento es de tipo "Con Confirmación"
Y el evento posee un nivel de habilidad requerido
Y el nivel del participante es diferente al requerido
Cuando el participante presiona el botón "Solicitar participación"
Entonces el sistema impide el envío de la solicitud
Y informa que no cumple con el nivel requerido

Escenario: Intentar unirse a un evento en el que ya participa
Dado que el participante ya se encuentra registrado en el evento
Cuando intenta unirse nuevamente
Entonces el sistema impide la operación
Y informa que ya participa del evento

Escenario: Intentar responder una invitación ya respondida
Dado que el participante ya respondió la invitación del evento
Cuando intenta responderla nuevamente
Entonces el sistema impide la operación
Y informa que la invitación ya fue procesada