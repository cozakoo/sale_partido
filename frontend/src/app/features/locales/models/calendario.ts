export type EstadoTurno = 'libre' | 'ocupado' | 'incompleto';
export type EstadoEvento = 'pendiente' | 'confirmado' | 'finalizado' | 'cancelado';

export interface DetalleReserva {
  organizadorNombre: string;
  cantidadConfirmados: number;
  capacidadTotal: number;
  estadoEvento: EstadoEvento;
}

export interface Turno {
  id: string;
  horaInicio: string;
  horaFin: string;
  espacioNombre: string;
  deporte: string;
  estado: EstadoTurno;
  reserva?: DetalleReserva;
}

export interface DiaCalendario {
  fecha: Date;
  turnos: Turno[];
}