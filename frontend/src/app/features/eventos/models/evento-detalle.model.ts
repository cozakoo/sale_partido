export type TipoIngreso = 'ABIERTO' | 'CON_CONFIRMACION' | 'CERRADO';
export type EstadoParticipacion = 'CONFIRMADO' | 'PENDIENTE' | 'RECHAZADO';
export type EstadoInvitacionRespuesta = 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA';
export type NivelHabilidad = 'PRINCIPIANTE' | 'INTERMEDIO' | 'AVANZADO' | 'CUALQUIERA';

export interface Participante {
  id: number;
  nombre: string;
}

export interface EventoDetalle {
  id: number;
  deporte: string;
  titulo: string;
  fecha: string;
  hora: string;
  local: string;
  direccion: string;
  nivelRequerido: NivelHabilidad;
  cupoMinimo: number;
  cupoMaximo: number;
  participantesConfirmados: Participante[];
  tipoIngreso: TipoIngreso;
  descripcion?: string;
}

export interface UsuarioSesion {
  id: number;
  nombre: string;
  nivelHabilidad: NivelHabilidad;
}

export interface EstadoInvitacion {
  tieneInvitacion: boolean;
  estadoRespuesta?: EstadoInvitacionRespuesta; // PENDIENTE | ACEPTADA | RECHAZADA
}

export interface ResultadoAccion {
  exito: boolean;
  nuevoEstadoParticipacion?: EstadoParticipacion;
  nuevoEstadoInvitacion?: EstadoInvitacionRespuesta;
  mensaje: string;
  mensajeTestid: string; // el data-testid del mensaje a mostrar
}