import { Injectable, signal } from '@angular/core';
import { Observable, of } from 'rxjs';
import { CrearEventoRequest, EventoResponse } from '../models/evento.model';
@Injectable({ providedIn: 'root' })
export class EventoService {
  readonly ultimoEventoCreado = signal<EventoResponse | null>(null);

  crearEvento(request: CrearEventoRequest): Observable<EventoResponse> {
    const mock: EventoResponse = {
      uuid: crypto.randomUUID(),
      nombre: request.nombre,
      tipo: request.tipo ?? 'CERRADO',
      estado: 'DISPONIBLE',
      cupoMinimo: request.cupoMinimo,
      cupoMaximo: request.cupoMaximo,
      participantesConfirmados: 0,
      limiteCancelacionParticipacion: request.limiteCancelacionParticipacion ?? 60,
      nivelRequerido: request.nivelRequeridoUuid
        ? {
            uuid: request.nivelRequeridoUuid,
            nombre: 'Intermedio',
            orden: 2,
            deporte: 'Fútbol',
          }
        : null,
      turno: {
        uuid: request.turnoUuid,
        fecha: '2026-06-15',
        horaInicio: '18:00:00',
        horaFin: '19:00:00',
        cancha: { uuid: 'cancha-mock', nombre: 'Cancha A' },
        local: {
          uuid: 'local-mock',
          nombre: 'Complejo Mock',
          direccion: 'Av. Roca 123, Puerto Madryn',
        },
      },
      organizador: { uuid: request.organizadorUuid, nombre: 'Usuario Mock' },
    };

    this.ultimoEventoCreado.set(mock);
    return of(mock);
  }
}