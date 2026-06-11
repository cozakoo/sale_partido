export type TipoIngreso = 'Abierto' | 'Con Confirmación' | 'Cerrado';
export type NivelHabilidad = 'Principiante' | 'Intermedio' | 'Avanzado' | 'Sin especificar';
export type EstadoEvento = 'En espera de participantes' | 'Confirmado' | 'Cancelado' | 'Finalizado';

export interface CrearEventoRequest {
  localId: number;
  espacioId: number;
  reservaId: number;
  fecha: string;         // 'YYYY-MM-DD'
  hora: string;          // 'HH:mm'
  cupoMinimo: number;
  cupoMaximo: number;
  tipoIngreso: TipoIngreso;
  tiempoCancelacionHoras: number;
  nivelHabilidad: NivelHabilidad;
}

export interface EventoCreado {
  id: number;
  localId: number;
  espacioId: number;
  reservaId: number;
  fecha: string;
  hora: string;
  cupoMinimo: number;
  cupoMaximo: number;
  tipoIngreso: TipoIngreso;
  tiempoCancelacionHoras: number;
  nivelHabilidad: NivelHabilidad;
  estado: EstadoEvento;
}

export interface EspacioReservado {
  reservaId: number;
  localId: number;
  localNombre: string;
  espacioId: number;
  espacioNombre: string;
  capacidad: number;
  fecha: string;
  hora: string;
}