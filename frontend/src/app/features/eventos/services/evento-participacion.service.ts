import { Injectable, signal, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, delay, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Constantes } from '../../../core/Constantes';
import {
  EventoDetalle,
  UsuarioSesion,
  EstadoParticipacion,
  ParticipacionResponse,
} from '../models/evento-detalle.model';

@Injectable({ providedIn: 'root' })
export class EventoParticipacionService {
  private http = inject(HttpClient);

  private _mockEventos: Record<string, EventoDetalle> = {
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

  private _mockUsers: UsuarioSesion[] = [
    { uuid: 'user-participante-01', nombre: 'Federico Cotrena (Mock)', habilidades: { 'Fútbol': 'INTERMEDIO' } },
    { uuid: 'user-participante-02', nombre: 'Ana García (Mock)', habilidades: { 'Fútbol': 'INTERMEDIO', 'Tenis': 'AVANZADO' } },
    { uuid: 'user-participante-03', nombre: 'Bruno Martínez (Mock)', habilidades: { 'Básquet': 'AVANZADO' } }
  ];

  private mockUsuario: UsuarioSesion = this._mockUsers[0];

  private _usuarioActual = signal<UsuarioSesion | null>(null);

  // Invitaciones y participaciones iniciales mockeadas en memoria
  private _initialParticipaciones: Record<string, Record<string, ParticipacionResponse>> = {
    'evento-uuid-2': {
      'user-participante-01': { uuid: 'part-uuid-2', estado: 'PENDIENTE', esInvitacion: true }
    },
    'evento-uuid-3': {
      'user-participante-01': { uuid: 'part-uuid-3', estado: 'PENDIENTE', esInvitacion: true }
    }
  };

  // Señal de estado en memoria para reflejar cambios en la sesión de navegación actual
  private _participacionesState = signal<Record<string, Record<string, ParticipacionResponse>>>(this._initialParticipaciones);

  // =========================================================================
  // MÉTODOS DE SERVICIO (API / MOCKS)
  // =========================================================================
  // NOTA TEMPORAL: El chequeo 'navigator.webdriver' permite simular peticiones HTTP
  // reales durante los tests automatizados (para que Playwright pueda interceptarlos),
  // mientras que devuelve mocks en memoria para el desarrollo local manual.
  // UNA VEZ CONECTADO AL BACKEND: Se debe eliminar el condicional 'if (navigator.webdriver)'
  // de todos los métodos y dejar únicamente la llamada HTTP real (HttpClient).
  // =========================================================================

  getEvento(uuid: string): Observable<EventoDetalle> {
    if (typeof navigator !== 'undefined' && navigator.webdriver) {
      return this.http.get<EventoDetalle>(`${Constantes.ENDPOINT_EVENTOS}/${uuid}`);
    }
    const evento = this._mockEventos[uuid];
    if (!evento) {
      return throwError(() => new Error(`Evento ${uuid} no encontrado`));
    }
    return of({ ...evento }).pipe(delay(300));
  }

  getEventos(): Observable<EventoDetalle[]> {
    if (typeof navigator !== 'undefined' && navigator.webdriver) {
      return this.http.get<EventoDetalle[]>(Constantes.ENDPOINT_EVENTOS);
    }
    const todos = Object.values(this._mockEventos).map(e => ({ ...e }));
    return of(todos).pipe(delay(300));
  }

  getUsuarios(): Observable<UsuarioSesion[]> {
    if (typeof navigator !== 'undefined' && navigator.webdriver) {
      return this.http.get<UsuarioSesion[]>(`${Constantes.API}usuarios`);
    }
    return of([...this._mockUsers]).pipe(delay(100));
  }

  setUsuarioActual(usuario: UsuarioSesion): void {
    this._usuarioActual.set(usuario);
  }

  getUsuarioActual(): Observable<UsuarioSesion> {
    const user = this._usuarioActual();
    if (user) {
      return of(user);
    }
    if (typeof navigator !== 'undefined' && navigator.webdriver) {
      return this.http.get<UsuarioSesion>(`${Constantes.API}usuarios/me`);
    }
    return of({ ...this.mockUsuario }).pipe(delay(100));
  }

  getParticipacionUsuario(eventoUuid: string, usuarioUuid: string): Observable<ParticipacionResponse | null> {
    if (typeof navigator !== 'undefined' && navigator.webdriver) {
      return this.http.get<ParticipacionResponse>(`${Constantes.ENDPOINT_EVENTOS}/${eventoUuid}/participaciones/usuario/${usuarioUuid}`).pipe(
        catchError((err: any) => {
          if (err instanceof HttpErrorResponse && err.status === 404) {
            return of(null);
          }
          return throwError(() => err);
        })
      );
    }
    const epMap = this._participacionesState()[eventoUuid];
    const part = epMap ? epMap[usuarioUuid] : null;
    return of(part ? { ...part } : null).pipe(delay(150));
  }

  unirse(eventoUuid: string, usuarioUuid: string): Observable<ParticipacionResponse> {
    if (typeof navigator !== 'undefined' && navigator.webdriver) {
      return this.http.post<ParticipacionResponse>(`${Constantes.ENDPOINT_EVENTOS}/${eventoUuid}/participaciones`, { usuarioUuid });
    }
    const newPart: ParticipacionResponse = {
      uuid: `part-uuid-new-${Date.now()}`,
      estado: 'CONFIRMADO',
      esInvitacion: false,
      fechaEstado: new Date().toISOString()
    };

    this.actualizarEstadoParticipacion(eventoUuid, usuarioUuid, newPart);

    // Agregar el usuario a la lista de participantes confirmados del mock
    const evento = this._mockEventos[eventoUuid];
    if (evento && !evento.participantes.some(p => p.uuid === usuarioUuid)) {
      const userObj = this._mockUsers.find(u => u.uuid === usuarioUuid) || this.mockUsuario;
      evento.participantes = [...evento.participantes, { uuid: usuarioUuid, nombre: userObj.nombre }];
      evento.participantesConfirmados = evento.participantes.length;
    }

    return of(newPart).pipe(delay(400));
  }

  solicitarParticipacion(eventoUuid: string, usuarioUuid: string): Observable<ParticipacionResponse> {
    if (typeof navigator !== 'undefined' && navigator.webdriver) {
      return this.http.post<ParticipacionResponse>(`${Constantes.ENDPOINT_EVENTOS}/${eventoUuid}/solicitudes`, { usuarioUuid });
    }
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
    if (typeof navigator !== 'undefined' && navigator.webdriver) {
      return this.http.patch<ParticipacionResponse>(`${Constantes.ENDPOINT_PARTICIPACIONES}/${participacionUuid}`, { estado });
    }
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
      const evento = this._mockEventos[foundEventoUuid];
      if (evento && !evento.participantes.some(p => p.uuid === foundUsuarioUuid)) {
        const userObj = this._mockUsers.find(u => u.uuid === foundUsuarioUuid) || this.mockUsuario;
        evento.participantes = [...evento.participantes, { uuid: foundUsuarioUuid, nombre: userObj.nombre }];
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