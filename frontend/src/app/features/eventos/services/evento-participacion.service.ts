import { Injectable, signal } from '@angular/core';
import { Observable, of, delay } from 'rxjs';
import {
  EventoDetalle,
  UsuarioSesion,
  EstadoInvitacion,
  EstadoParticipacion,
  EstadoInvitacionRespuesta,
  ResultadoAccion,
} from '../models/evento-detalle.model';

@Injectable({ providedIn: 'root' })
export class EventoParticipacionService {

  private mockEventos: Record<number, EventoDetalle> = {
    1: {
      id: 1,
      deporte: 'Fútbol',
      titulo: 'Picado del domingo',
      fecha: '2026-07-15',
      hora: '18:00',
      local: 'Club Atlético Norte',
      direccion: 'Av. Siempreviva 742',
      nivelRequerido: 'INTERMEDIO',
      cupoMinimo: 10,
      cupoMaximo: 20,
      participantesConfirmados: [
        { id: 10, nombre: 'Mauricio Cardenas' },
        { id: 11, nombre: 'Gabriel Sorrentino' },
      ],
      tipoIngreso: 'ABIERTO',
    },
    2: {
      id: 2,
      deporte: 'Básquet',
      titulo: 'Liga nocturna',
      fecha: '2026-07-15',
      hora: '21:00',
      local: 'Club Atlético Norte',
      direccion: 'Av. Siempreviva 742',
      nivelRequerido: 'AVANZADO',
      cupoMinimo: 8,
      cupoMaximo: 10,
      participantesConfirmados: Array.from({ length: 10 }, (_, i) => ({ id: 20 + i, nombre: `Jugador ${i + 1}` })),
      tipoIngreso: 'CON_CONFIRMACION',
    },
    3: {
      id: 3,
      deporte: 'Tenis',
      titulo: 'Dobles privado',
      fecha: '2026-07-20',
      hora: '09:00',
      local: 'Club de Tenis Patagonia',
      direccion: 'Roca 890, Puerto Madryn',
      nivelRequerido: 'INTERMEDIO',
      cupoMinimo: 4,
      cupoMaximo: 4,
      participantesConfirmados: [{ id: 30, nombre: 'Sofía Méndez' }],
      tipoIngreso: 'CERRADO',
    },
  };

  private mockUsuario: UsuarioSesion = {
    id: 99,
    nombre: 'Federico Cotrena',
    nivelHabilidad: 'INTERMEDIO',
  };

  private mockInvitaciones: Record<number, EstadoInvitacion> = {
    2: { tieneInvitacion: true, estadoRespuesta: 'PENDIENTE' },
    3: { tieneInvitacion: true, estadoRespuesta: 'PENDIENTE' },
  };

  // Estado en memoria (simula lo que vendría del backend en llamadas sucesivas)
  private _estadoParticipacion = signal<Record<number, EstadoParticipacion>>({});
  private _estadoInvitacion = signal<Record<number, EstadoInvitacionRespuesta>>({});
  private _participacionActual = signal<Record<number, boolean>>({});

  getEvento(id: number): Observable<EventoDetalle> {
    const evento = this.mockEventos[id];
    if (!evento) throw new Error(`Evento ${id} no encontrado`);
    return of({ ...evento }).pipe(delay(300));
  }

  getUsuarioActual(): Observable<UsuarioSesion> {
    return of({ ...this.mockUsuario }).pipe(delay(100));
  }

  getEstadoInvitacion(eventoId: number): Observable<EstadoInvitacion> {
    // Merge mock base con cambios en memoria
    const base = this.mockInvitaciones[eventoId] ?? { tieneInvitacion: false };
    const estadoMemoria = this._estadoInvitacion()[eventoId];
    return of({
      ...base,
      ...(estadoMemoria ? { estadoRespuesta: estadoMemoria } : {}),
    }).pipe(delay(150));
  }

  getEstadoParticipacion(eventoId: number): EstadoParticipacion | null {
    return this._estadoParticipacion()[eventoId] ?? null;
  }

  yaParticipa(eventoId: number): boolean {
    return this._participacionActual()[eventoId] ?? false;
  }

  unirse(eventoId: number): Observable<ResultadoAccion> {
    this._estadoParticipacion.update(e => ({ ...e, [eventoId]: 'CONFIRMADO' }));
    this._participacionActual.update(e => ({ ...e, [eventoId]: true }));
    return of({
      exito: true,
      nuevoEstadoParticipacion: 'CONFIRMADO' as EstadoParticipacion,
      mensaje: 'Tu participación fue confirmada',
      mensajeTestid: 'mensaje-confirmacion-participacion',
    }).pipe(delay(400));
  }

  solicitarParticipacion(eventoId: number): Observable<ResultadoAccion> {
    this._estadoParticipacion.update(e => ({ ...e, [eventoId]: 'PENDIENTE' }));
    this._participacionActual.update(e => ({ ...e, [eventoId]: true }));
    return of({
      exito: true,
      nuevoEstadoParticipacion: 'PENDIENTE' as EstadoParticipacion,
      mensaje: 'Tu solicitud fue enviada',
      mensajeTestid: 'mensaje-confirmacion-solicitud',
    }).pipe(delay(400));
  }

  aceptarInvitacion(eventoId: number): Observable<ResultadoAccion> {
    this._estadoParticipacion.update(e => ({ ...e, [eventoId]: 'CONFIRMADO' }));
    this._estadoInvitacion.update(e => ({ ...e, [eventoId]: 'ACEPTADA' }));
    return of({
      exito: true,
      nuevoEstadoParticipacion: 'CONFIRMADO' as EstadoParticipacion,
      nuevoEstadoInvitacion: 'ACEPTADA' as EstadoInvitacionRespuesta,
      mensaje: 'Tu participación fue confirmada',
      mensajeTestid: 'mensaje-confirmacion-participacion',
    }).pipe(delay(400));
  }

  rechazarInvitacion(eventoId: number): Observable<ResultadoAccion> {
    this._estadoInvitacion.update(e => ({ ...e, [eventoId]: 'RECHAZADA' }));
    return of({
      exito: true,
      nuevoEstadoInvitacion: 'RECHAZADA' as EstadoInvitacionRespuesta,
      mensaje: 'Rechazaste la invitación',
      mensajeTestid: 'mensaje-confirmacion-rechazo',
    }).pipe(delay(400));
  }

  getEventos(): Observable<EventoDetalle[]> {
  const todos = Object.values(this.mockEventos).map(e => ({ ...e }));
  return of(todos).pipe(delay(300));
}

}