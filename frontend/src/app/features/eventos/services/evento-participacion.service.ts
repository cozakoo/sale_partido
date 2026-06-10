import { Injectable, signal } from '@angular/core';
import { Observable, of, delay, throwError } from 'rxjs';
import {
  EventoDetalle,
  UsuarioSesion,
  EstadoParticipacion,
  ParticipacionResponse,
} from '../models/evento-detalle.model';

@Injectable({ providedIn: 'root' })
export class EventoParticipacionService {

  private mockEventos: Record<string, EventoDetalle> = {
    'evento-uuid-1': {
      uuid: 'evento-uuid-1',
      nombre: 'Picado del domingo',
      deporte: 'Fútbol',
      descripcion: 'Un picado tranqui para terminar el finde.',
      tipo: 'ABIERTO',
      estado: 'DISPONIBLE',
      cupoMinimo: 10,
      cupoMaximo: 20,
      participantesConfirmados: 2,
      participantes: [
        { uuid: 'p10', nombre: 'Mauricio Cardenas' },
        { uuid: 'p11', nombre: 'Gabriel Sorrentino' },
      ],
      nivelRequerido: {
        uuid: 'nivel-1',
        nombre: 'INTERMEDIO',
        orden: 2,
        deporte: 'Fútbol'
      },
      turno: {
        uuid: 't1',
        fecha: '2026-07-15',
        horaInicio: '18:00',
        horaFin: '19:00',
        cancha: {
          uuid: 'c1',
          nombre: 'Cancha A'
        },
        local: {
          uuid: 'l1',
          nombre: 'Club Atlético Norte',
          direccion: 'Av. Siempreviva 742'
        }
      }
    },
    'evento-uuid-2': {
      uuid: 'evento-uuid-2',
      nombre: 'Liga nocturna',
      deporte: 'Básquet',
      descripcion: 'Torneo regular nocturno.',
      tipo: 'CON_CONFIRMACION',
      estado: 'DISPONIBLE',
      cupoMinimo: 8,
      cupoMaximo: 10,
      participantesConfirmados: 10,
      participantes: Array.from({ length: 10 }, (_, i) => ({ uuid: `p20-${i}`, nombre: `Jugador ${i + 1}` })),
      nivelRequerido: {
        uuid: 'nivel-2',
        nombre: 'AVANZADO',
        orden: 3,
        deporte: 'Básquet'
      },
      turno: {
        uuid: 't2',
        fecha: '2026-07-15',
        horaInicio: '21:00',
        horaFin: '22:00',
        cancha: {
          uuid: 'c2',
          nombre: 'Cancha central de Básquet'
        },
        local: {
          uuid: 'l1',
          nombre: 'Club Atlético Norte',
          direccion: 'Av. Siempreviva 742'
        }
      }
    },
    'evento-uuid-3': {
      uuid: 'evento-uuid-3',
      nombre: 'Dobles privado',
      deporte: 'Tenis',
      descripcion: 'Solo para amigos invitados.',
      tipo: 'CERRADO',
      estado: 'DISPONIBLE',
      cupoMinimo: 4,
      cupoMaximo: 4,
      participantesConfirmados: 1,
      participantes: [{ uuid: 'p30', nombre: 'Sofía Méndez' }],
      nivelRequerido: {
        uuid: 'nivel-3',
        nombre: 'INTERMEDIO',
        orden: 2,
        deporte: 'Tenis'
      },
      turno: {
        uuid: 't3',
        fecha: '2026-07-20',
        horaInicio: '09:00',
        horaFin: '10:30',
        cancha: {
          uuid: 'c3',
          nombre: 'Cancha de polvo de ladrillo 1'
        },
        local: {
          uuid: 'l2',
          nombre: 'Club de Tenis Patagonia',
          direccion: 'Roca 890, Puerto Madryn'
        }
      }
    },
  };

  private mockUsuario: UsuarioSesion = {
    uuid: 'user-participante-01',
    nombre: 'Federico Cotrena',
    nivelHabilidad: 'INTERMEDIO',
  };

  // Invitaciones y participaciones iniciales mockeadas en memoria
  private initialParticipaciones: Record<string, Record<string, ParticipacionResponse>> = {
    'evento-uuid-2': {
      'user-participante-01': { uuid: 'part-uuid-2', estado: 'PENDIENTE', esInvitacion: true }
    },
    'evento-uuid-3': {
      'user-participante-01': { uuid: 'part-uuid-3', estado: 'PENDIENTE', esInvitacion: true }
    }
  };

  // Señal de estado en memoria para reflejar cambios en la sesión de navegación actual
  private _participacionesState = signal<Record<string, Record<string, ParticipacionResponse>>>(this.initialParticipaciones);

  getEvento(uuid: string): Observable<EventoDetalle> {
    const evento = this.mockEventos[uuid];
    if (!evento) {
      return throwError(() => new Error(`Evento ${uuid} no encontrado`));
    }
    return of({ ...evento }).pipe(delay(300));
  }

  getEventos(): Observable<EventoDetalle[]> {
    const todos = Object.values(this.mockEventos).map(e => ({ ...e }));
    return of(todos).pipe(delay(300));
  }

  getUsuarioActual(): Observable<UsuarioSesion> {
    return of({ ...this.mockUsuario }).pipe(delay(100));
  }

  getParticipacionUsuario(eventoUuid: string, usuarioUuid: string): Observable<ParticipacionResponse | null> {
    const epMap = this._participacionesState()[eventoUuid];
    const part = epMap ? epMap[usuarioUuid] : null;
    return of(part ? { ...part } : null).pipe(delay(150));
  }

  unirse(eventoUuid: string, usuarioUuid: string): Observable<ParticipacionResponse> {
    const newPart: ParticipacionResponse = {
      uuid: `part-uuid-new-${Date.now()}`,
      estado: 'CONFIRMADO',
      esInvitacion: false,
      fechaEstado: new Date().toISOString()
    };

    this.actualizarEstadoParticipacion(eventoUuid, usuarioUuid, newPart);

    // Agregar el usuario a la lista de participantes confirmados del mock
    const evento = this.mockEventos[eventoUuid];
    if (evento && !evento.participantes.some(p => p.uuid === usuarioUuid)) {
      evento.participantes = [...evento.participantes, { uuid: usuarioUuid, nombre: this.mockUsuario.nombre }];
      evento.participantesConfirmados = evento.participantes.length;
    }

    return of(newPart).pipe(delay(400));
  }

  solicitarParticipacion(eventoUuid: string, usuarioUuid: string): Observable<ParticipacionResponse> {
    const newPart: ParticipacionResponse = {
      uuid: `part-uuid-new-${Date.now()}`,
      estado: 'PENDIENTE',
      esInvitacion: false,
      fechaEstado: new Date().toISOString()
    };

    this.actualizarEstadoParticipacion(eventoUuid, usuarioUuid, newPart);

    return of(newPart).pipe(delay(400));
  }

  responderInvitacion(participacionUuid: string, estado: 'CONFIRMADO' | 'RECHAZADO'): Observable<ParticipacionResponse> {
    // Buscar la participación correspondiente
    let foundEventoUuid = '';
    let foundUsuarioUuid = '';
    let foundPart: ParticipacionResponse | null = null;

    const state = this._participacionesState();
    for (const [eventoUuid, userMap] of Object.entries(state)) {
      for (const [usuarioUuid, part] of Object.entries(userMap)) {
        if (part.uuid === participacionUuid) {
          foundEventoUuid = eventoUuid;
          foundUsuarioUuid = usuarioUuid;
          foundPart = part;
          break;
        }
      }
      if (foundPart) break;
    }

    if (!foundPart) {
      return throwError(() => new Error(`Participación ${participacionUuid} no encontrada`));
    }

    const updatedPart: ParticipacionResponse = {
      ...foundPart,
      estado: estado as EstadoParticipacion,
      fechaEstado: new Date().toISOString()
    };

    this.actualizarEstadoParticipacion(foundEventoUuid, foundUsuarioUuid, updatedPart);

    // Si aceptó, agregar a la lista de confirmados del evento
    if (estado === 'CONFIRMADO') {
      const evento = this.mockEventos[foundEventoUuid];
      if (evento && !evento.participantes.some(p => p.uuid === foundUsuarioUuid)) {
        evento.participantes = [...evento.participantes, { uuid: foundUsuarioUuid, nombre: this.mockUsuario.nombre }];
        evento.participantesConfirmados = evento.participantes.length;
      }
    }

    return of(updatedPart).pipe(delay(400));
  }

  private actualizarEstadoParticipacion(eventoUuid: string, usuarioUuid: string, part: ParticipacionResponse): void {
    this._participacionesState.update(state => {
      const eventMap = state[eventoUuid] ? { ...state[eventoUuid] } : {};
      eventMap[usuarioUuid] = part;
      return {
        ...state,
        [eventoUuid]: eventMap
      };
    });
  }
}