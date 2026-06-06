export type EstadoTurno = 'libre' | 'ocupado' | 'incompleto' | 'finalizado';
export type EstadoEvento = 'pendiente' | 'confirmado' | 'finalizado' | 'cancelado';

export interface DetalleTurno {
  organizadorNombre: string;
  cantidadConfirmados: number;
  capacidad: number;
  estadoEvento: EstadoEvento;

}

export interface Turno {
  id: string;
  horaInicio: string;
  horaFin: string;
  espacioNombre: string;
  deporte: string;
  estado: EstadoTurno;
  turno?: DetalleTurno;
}

export interface DiaCalendario {
  fecha: Date;
  turnos: Turno[];
}

export interface FilterSelection {
  estados: EstadoTurno[];
  espacios: string[];
  deportes: string[];
}