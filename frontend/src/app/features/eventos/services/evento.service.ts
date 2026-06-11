import { Injectable, signal } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { CrearEventoRequest, EspacioReservado, EventoCreado } from '../models/evento.mode';

let mockEventoIdCounter = 1000;

@Injectable({ providedIn: 'root' })
export class EventoService {
  readonly ultimoEventoCreado = signal<EventoCreado | null>(null);

  obtenerEspacioReservado(state: any): Observable<EspacioReservado> {
    const reserva = state?.reserva;
    if (!reserva) {
      return throwError(() => new Error('Sin espacio seleccionado'));
    }
    return of({
      reservaId: reserva.turnoId,
      localId: reserva.localUuid,
      localNombre: reserva.localUuid,
      espacioId: 1,
      espacioNombre: reserva.espacioNombre,
      capacidad: 10, // hasta que el backend lo mande
      fecha: reserva.fecha,
      hora: reserva.horaInicio,
    });
  }

  crearEvento(request: CrearEventoRequest): Observable<EventoCreado> {
    const eventoCreado: EventoCreado = {
      id: ++mockEventoIdCounter,
      ...request,
      estado: 'En espera de participantes',
    };
    this.ultimoEventoCreado.set(eventoCreado);
    return of(eventoCreado);
  }
}