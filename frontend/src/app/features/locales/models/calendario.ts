export type EstadoTurno = 'libre' | 'ocupado' | 'incompleto' | 'finalizado';
export type EstadoEvento = 'pendiente' | 'confirmado' | 'finalizado' | 'cancelado';

export interface DetalleTurno {
  organizadorNombre: string;
  cantidadConfirmados: number;
  capacidad: number;
  estadoEvento: EstadoEvento;
}

export interface Turno {
  id: string;            // compuesto: fecha-espacio-hora (para tracking)
  turnoUuid: string;     // uuid real del Turno en la BD
  fecha: string;         // 'YYYY-MM-DD'
  horaInicio: string;    // 'HH:mm'
  horaFin: string;       // 'HH:mm'
  espacioNombre: string;
  canchaNombre: string;  // nombre de la cancha (viene de DisponibilidadCanchaBackendDTO)
  canchaUuid: string;    // uuid de la cancha
  deporte: string;
  estado: EstadoTurno;
  capacidad: number;     // capacidad máxima de la cancha
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