import { Injectable, signal } from '@angular/core';
import { Observable, of, throwError, delay } from 'rxjs';
import { CrearEventoRequest, EspacioReservado, EventoCreado } from '../models/evento.mode';

// ─────────────────────────────────────────────────────────────────────────────
// MOCK DATA — reemplazar por llamadas HTTP cuando el backend esté listo
// ─────────────────────────────────────────────────────────────────────────────
const MOCK_ESPACIO_RESERVADO: EspacioReservado = {
  reservaId: 101,
  localId: 1,
  localNombre: 'Complejo Deportivo Patagonia',
  espacioId: 3,
  espacioNombre: 'Cancha de Fútbol 5 — Sintético',
  capacidad: 10,
  fecha: '2026-11-20',
  hora: '18:00',
};

let mockEventoIdCounter = 1000;

@Injectable({ providedIn: 'root' })
export class EventoService {

  /** Señal interna para el último evento creado (útil para navegación post-creación) */
  readonly ultimoEventoCreado = signal<EventoCreado | null>(null);

  /**
   * Devuelve el espacio/reserva preseleccionado para la creación del evento.
   * En producción: GET /reservas/:id con el id pasado por ruta/estado.
   */
  obtenerEspacioReservado(reservaId: number): Observable<EspacioReservado> {
    // Mock: ignora el id y devuelve el espacio de prueba
    return of({ ...MOCK_ESPACIO_RESERVADO }).pipe(delay(300));
  }

  /**
   * Crea el evento con los datos del formulario.
   * En producción: POST /eventos
   */
  crearEvento(request: CrearEventoRequest): Observable<EventoCreado> {
    // Validaciones de negocio (el backend también las hará)
    if (request.cupoMinimo <= 0) {
      return throwError(() => new Error('El cupo mínimo de jugadores debe ser mayor a cero'));
    }
    if (request.cupoMinimo > request.cupoMaximo) {
      return throwError(() => new Error('El cupo mínimo no puede ser mayor al cupo máximo'));
    }
    if (request.cupoMaximo > MOCK_ESPACIO_RESERVADO.capacidad) {
      return throwError(() => new Error('El cupo máximo no puede superar la capacidad máxima de la cancha'));
    }
    if (request.tiempoCancelacionHoras < 1 || request.tiempoCancelacionHoras > 24) {
      return throwError(() => new Error('El tiempo límite de cancelación de participación debe estar entre 1 y 24 horas'));
    }

    const eventoCreado: EventoCreado = {
      id: ++mockEventoIdCounter,
      ...request,
      estado: 'En espera de participantes',
    };

    this.ultimoEventoCreado.set(eventoCreado);
    return of(eventoCreado).pipe(delay(400));
  }
}