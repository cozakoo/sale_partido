import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { CrearEventoRequest, EventoResponse, CanchaDeporteResponse } from '../models/evento.model';
import { Constantes } from '../../../core/Constantes';

@Injectable({ providedIn: 'root' })
export class EventoService {
  private http = inject(HttpClient);
  readonly ultimoEventoCreado = signal<EventoResponse | null>(null);

  crearEvento(request: CrearEventoRequest): Observable<EventoResponse> {
    return this.http.post<EventoResponse>(Constantes.ENDPOINT_EVENTOS, request).pipe(
      tap((evento) => this.ultimoEventoCreado.set(evento))
    );
  }

  getCanchaDeporte(canchaUuid: string): Observable<CanchaDeporteResponse> {
    return this.http.get<CanchaDeporteResponse>(`${Constantes.ENDPOINT_CANCHAS}/${canchaUuid}/deporte`);
  }
}