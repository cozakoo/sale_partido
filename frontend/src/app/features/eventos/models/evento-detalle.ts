import { TipoIngreso } from './tipo-ingreso';
import { EstadoEvento } from './estado-evento';
import { NivelRequerido } from './nivel-requerido';
import { Turno } from './turno';
import { Participante } from './participante';

export interface EventoDetalle {
  uuid: string;
  nombre: string;
  deporte: string;
  descripcion?: string;
  tipo: TipoIngreso;
  estado: EstadoEvento;
  cupoMinimo: number;
  cupoMaximo: number;
  participantesConfirmados: number;
  participantes: Participante[];
  nivelRequerido: NivelRequerido | null;
  turno: Turno;
}
