import { Injectable, signal, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Constantes } from '../../../core/Constantes';
import {
  EventoDetalle,
  UsuarioSesion,
  ParticipacionResponse,
} from '../models/evento-detalle.model';

@Injectable({ providedIn: 'root' })
export class EventoParticipacionService {
  private http = inject(HttpClient);

  private _usuarioActual = signal<UsuarioSesion | null>(null);

  // =========================================================================
  // MÉTODOS DE SERVICIO (API)
  // =========================================================================

  getEvento(uuid: string): Observable<EventoDetalle> {
    return this.http.get<EventoDetalle>(`${Constantes.ENDPOINT_EVENTOS}/${uuid}`);
  }

  getEventos(): Observable<EventoDetalle[]> {
    return this.http.get<EventoDetalle[]>(Constantes.ENDPOINT_EVENTOS);
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