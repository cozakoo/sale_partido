import { TipoEvento } from './tipo-evento';
import { EstadoEvento } from './estado-evento';
import { NivelRequeridoResponse } from './nivel-requerido-response';
import { TurnoEventoResponse } from './turno-evento-response';

export interface EventoResponse {
  uuid: string;
  nombre: string;
  tipo: TipoEvento;
  estado: EstadoEvento;
  cupoMinimo: number;
  cupoMaximo: number;
  participantesConfirmados: number;
  limiteCancelacionParticipacion?: number;
  nivelRequerido: NivelRequeridoResponse | null;
  turno: TurnoEventoResponse;
  organizador?: { uuid: string; nombre: string };
}
