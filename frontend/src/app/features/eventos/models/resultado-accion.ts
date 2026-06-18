import { EstadoParticipacion } from './estado-participacion';

export interface ResultadoAccion {
  exito: boolean;
  nuevoEstadoParticipacion?: EstadoParticipacion;
  nuevoEstadoInvitacion?: EstadoParticipacion;
  mensaje: string;
  mensajeTestid: string; // el data-testid del mensaje a mostrar
}
