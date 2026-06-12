// ── Enums de la API ───────────────────────────────────────────────────────────
export type TipoEvento = 'ABIERTO' | 'CON_CONFIRMACION' | 'CERRADO';
export type EstadoEvento = 'DISPONIBLE' | 'COMPLETO' | 'CANCELADO' | 'FINALIZADO';

// ── Labels del formulario (UI solamente, no van al backend) ───────────────────
export type TipoIngresoLabel = 'Abierto' | 'Con Confirmación' | 'Cerrado';
export type NivelHabilidadLabel = 'Principiante' | 'Intermedio' | 'Avanzado' | 'Sin especificar';

// ── Request hacia POST /eventos ───────────────────────────────────────────────
export interface CrearEventoRequest {
  turnoUuid: string;
  organizadorUuid: string;
  nombre: string;
  tipo?: TipoEvento;                       // opcional — default CERRADO en backend
  cupoMinimo: number;
  cupoMaximo: number;
  limiteCancelacionParticipacion?: number; // minutos, rango 60–1440
  nivelRequeridoUuid?: string | null;      // opcional
}

// ── Respuesta del 201 ─────────────────────────────────────────────────────────
export interface NivelRequeridoResponse {
  uuid: string;
  nombre: string;
  orden: number;
  deporte: string;
}

export interface TurnoEventoResponse {
  uuid: string;
  fecha: string;        // 'YYYY-MM-DD'
  horaInicio: string;   // 'HH:mm:ss'
  horaFin: string;      // 'HH:mm:ss'
  cancha: { uuid: string; nombre: string };
  local: { uuid: string; nombre: string; direccion: string };
}

export interface EventoResponse {
  uuid: string;
  nombre: string;
  tipo: TipoEvento;
  estado: EstadoEvento;
  cupoMinimo: number;
  cupoMaximo: number;
  participantesConfirmados: number;
  limiteCancelacionParticipacion: number;
  nivelRequerido: NivelRequeridoResponse | null;
  turno: TurnoEventoResponse;
  organizador: { uuid: string; nombre: string };
}

// ── Datos de la reserva que vienen del calendario ─────────────────────────────
export interface EspacioReservado {
  turnoUuid: string;
  localUuid: string;
  localNombre: string;
  canchaUuid: string;
  canchaNombre: string;
  capacidad: number;
  fecha: string;        // 'YYYY-MM-DD'
  horaInicio: string;   // 'HH:mm'
  horaFin: string;      // 'HH:mm'
}