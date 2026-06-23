import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Constantes } from '../../../core/constantes';

// Model imports
import { CrearEventoRequest } from '../models/crear-evento-request';
import { EventoResponse } from '../models/evento-response';
import { CanchaDeporteResponse } from '../models/cancha-deporte-response';
import { EventoDetalle } from '../models/evento-detalle';
import { UsuarioSesion } from '../models/usuario-sesion';
import { ParticipacionResponse } from '../models/participacion-response';

@Injectable({ providedIn: 'root' })
export class EventoService {
  private http = inject(HttpClient);

  readonly ultimoEventoCreado = signal<EventoResponse | null>(null);
  private _usuarioActual = signal<UsuarioSesion | null>(null);

  // =========================================================================
  // MÉTODOS DE CREACIÓN Y CONFIGURACIÓN (EventoService original)
  // =========================================================================

  crearEvento(request: CrearEventoRequest): Observable<EventoResponse> {
    return this.http.post<EventoResponse>(Constantes.ENDPOINT_EVENTOS, request).pipe(
      tap((evento) => this.ultimoEventoCreado.set(evento))
    );
  }

  getCanchaDeporte(canchaUuid: string): Observable<CanchaDeporteResponse> {
    return this.http.get<CanchaDeporteResponse>(`${Constantes.ENDPOINT_CANCHAS}/${canchaUuid}/deporte`);
  }

  // =========================================================================
  // MÉTODOS DE PARTICIPACIÓN (EventoParticipacionService original)
  // =========================================================================

  getEvento(uuid: string): Observable<EventoDetalle> {
    return this.http.get<EventoDetalle>(`${Constantes.ENDPOINT_EVENTOS}/${uuid}`);
  }

  getEventos(finalizados?: boolean): Observable<EventoDetalle[]> {
    let url = Constantes.ENDPOINT_EVENTOS;
    if (finalizados !== undefined) {
      url += `?finalizados=${finalizados}`;
    }
    return this.http.get<EventoDetalle[]>(url);
  }

  getUsuarios(): Observable<UsuarioSesion[]> {
    return this.http.get<UsuarioSesion[]>(`${Constantes.API}usuarios`);
  }

  setUsuarioActual(usuario: UsuarioSesion): void {
    this._usuarioActual.set(usuario);
  }

  getUsuarioActual(): Observable<UsuarioSesion> {
    const user = this._usuarioActual();
    if (user) {
      return of(user);
    }
    return this.getUsuarios().pipe(
      map((usuarios) => {
        if (usuarios && usuarios.length > 0) {
          const fallbackUser = usuarios[0];
          this._usuarioActual.set(fallbackUser);
          return fallbackUser;
        }
        throw new Error('No users available to simulate session');
      })
    );
  }

  getParticipacionUsuario(eventoUuid: string, usuarioUuid: string): Observable<ParticipacionResponse | null> {
    return this.http.get<ParticipacionResponse>(`${Constantes.ENDPOINT_EVENTOS}/${eventoUuid}/participaciones/usuario/${usuarioUuid}`).pipe(
      catchError((err: any) => {
        if (err instanceof HttpErrorResponse && err.status === 404) {
          return of(null);
        }
        return throwError(() => err);
      })
    );
  }

  unirse(eventoUuid: string, usuarioUuid: string): Observable<ParticipacionResponse> {
    return this.http.post<ParticipacionResponse>(`${Constantes.ENDPOINT_EVENTOS}/${eventoUuid}/participaciones`, { usuarioUuid });
  }

  solicitarParticipacion(eventoUuid: string, usuarioUuid: string): Observable<ParticipacionResponse> {
    return this.http.post<ParticipacionResponse>(`${Constantes.ENDPOINT_EVENTOS}/${eventoUuid}/solicitudes`, { usuarioUuid });
  }

  responderInvitacion(participacionUuid: string, estado: 'CONFIRMADO' | 'RECHAZADO'): Observable<ParticipacionResponse> {
    return this.http.patch<ParticipacionResponse>(`${Constantes.ENDPOINT_PARTICIPACIONES}/${participacionUuid}`, { estado });
  }
}