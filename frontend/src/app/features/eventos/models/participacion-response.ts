import { EstadoParticipacion } from './estado-participacion';

export interface ParticipacionResponse {
  uuid: string;
  estado: EstadoParticipacion;
  esInvitacion: boolean;
  fechaEstado?: string;
}
