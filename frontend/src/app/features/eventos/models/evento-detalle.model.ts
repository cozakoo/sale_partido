export type TipoIngreso = 'ABIERTO' | 'CON_CONFIRMACION' | 'CERRADO';
export type EstadoEvento = 'DISPONIBLE' | 'COMPLETO' | 'CANCELADO' | 'FINALIZADO';
export type EstadoParticipacion = 'PENDIENTE' | 'CONFIRMADO' | 'RECHAZADO' | 'CANCELADO';

export interface NivelRequerido {
  uuid: string;
  nombre: string;
  orden: number;
  deporte: string;
}

export interface Cancha {
  uuid: string;
  nombre: string;
}

export interface Local {
  uuid: string;
  nombre: string;
  direccion: string;
}

export interface Turno {
  uuid: string;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  cancha: Cancha;
  local: Local;
}

export interface Participante {
  uuid: string;
  nombre: string;
}

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

export interface UsuarioSesion {
  uuid: string;
  nombre: string;
  habilidades: Record<string, string>;
}

export interface ParticipacionResponse {
  uuid: string;
  estado: EstadoParticipacion;
  esInvitacion: boolean;
  fechaEstado?: string;
}

export interface ResultadoAccion {
  exito: boolean;
  nuevoEstadoParticipacion?: EstadoParticipacion;
  nuevoEstadoInvitacion?: EstadoParticipacion;
  mensaje: string;
  mensajeTestid: string; // el data-testid del mensaje a mostrar
}