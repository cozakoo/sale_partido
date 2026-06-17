import { TipoEvento } from './tipo-evento';
import { CrearTurnoRequest } from './crear-turno-request';

export interface CrearEventoRequest {
  turno: CrearTurnoRequest;
  organizadorUuid: string;
  nombre: string;
  tipo?: TipoEvento;                       // opcional — default CERRADO en backend
  cupoMinimo: number;
  cupoMaximo: number;
  limiteCancelacionParticipacion?: number; // minutos, rango 60–1440
  nivelRequeridoUuid?: string | null;      // opcional
}
