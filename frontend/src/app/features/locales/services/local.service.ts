import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Constantes } from '../../../core/constantes';
import { CanchaDetail } from '../models/cancha-detail';
import { LocalDetail } from '../models/local-detail';
import { LocalSummary } from '../models/local-summary';
import { CanchaSummary } from '../models/cancha-summary';
import { LocalSearchResult } from '../models/local-search-result';
import { SaveCanchaConfiguracionHorarioRequest } from '../models/cancha-configuracion-horario-request';
import { SaveCanchaConfiguracionHorarioResponse } from '../models/cancha-configuracion-horario-response';
import { DisponibilidadCanchaBackendDTO } from '../models/disponibilidad-cancha';

@Injectable({ providedIn: 'root' })
export class LocalService {
  private http = inject(HttpClient);

  private readonly deportesList: string[] = ['Fútbol', 'Básquet', 'Tenis', 'Pádel', 'Voley', 'Hockey'];

  getDeportes(): string[] {
    return this.deportesList;
  }

  buscar(filtros: { ubicacion?: string; deporte?: string; fecha?: string; horarioDesde?: string; horarioHasta?: string }): Observable<LocalSearchResult[]> {
    let params = new HttpParams();

    if (filtros.ubicacion) {
      params = params.set('ubicacion', filtros.ubicacion);
    }
    if (filtros.deporte) {
      params = params.set('tipoDeporte', filtros.deporte);
    }
    if (filtros.fecha) {
      params = params.set('fecha', filtros.fecha);
    }
    if (filtros.horarioDesde) {
      params = params.set('horarioDesde', filtros.horarioDesde);
    }
    if (filtros.horarioHasta) {
      params = params.set('horarioHasta', filtros.horarioHasta);
    }

    return this.http.get<LocalSearchResult[]>(
      `${Constantes.ENDPOINT_LOCALES}`,
      { params }
    );
  }

  obtenerPorUuid(uuid: string): Observable<LocalSearchResult | undefined> {
    return this.http.get<LocalSearchResult>(
      `${Constantes.ENDPOINT_LOCALES}/${uuid}`
    ).pipe(
      map(resultado => resultado || undefined)
    );
  }

  getLocalesSummary(): Observable<LocalSummary[]> {
    return this.http.get<LocalSummary[]>(`${Constantes.ENDPOINT_LOCALES}`);
  }

  getCanchasSummary(): Observable<CanchaSummary[]> {
    return this.http.get<CanchaSummary[]>(`${Constantes.ENDPOINT_CANCHAS}`);
  }

  getLocalDetail(uuid: string): Observable<LocalDetail> {
    return this.http.get<LocalDetail>(`${Constantes.ENDPOINT_LOCALES}/${uuid}`);
  }

  getCanchaDetail(uuid: string): Observable<CanchaDetail> {
    return this.http.get<CanchaDetail>(`${Constantes.ENDPOINT_CANCHAS}/${uuid}`);
  }

  getCanchasSummaryFromLocal(uuid: string): Observable<CanchaSummary[]> {
    return this.http.get<CanchaSummary[]>(`${Constantes.ENDPOINT_LOCALES}/${uuid}/canchas?view=resumen`);
  }

  getCanchasDetailFromLocal(uuid: string): Observable<CanchaDetail[]> {
    return this.http.get<CanchaDetail[]>(`${Constantes.ENDPOINT_LOCALES}/${uuid}/canchas?view=detalle`);
  }

  saveLocalConfiguracionesHorarios(uuid: string, request: SaveCanchaConfiguracionHorarioRequest): Observable<SaveCanchaConfiguracionHorarioResponse> {
    return this.http.post<SaveCanchaConfiguracionHorarioResponse>(`${Constantes.ENDPOINT_LOCALES}/${uuid}/configuraciones-horarios`, request);
  }

  private formatearFechaLocal(d: Date): string {
    // Formatear en timezone local (no convertir a UTC como toISOString())
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  getDisponibilidad(localUuid: string, fechaInicio: Date, fechaFin: Date): Observable<DisponibilidadCanchaBackendDTO[]> {
    const params = new HttpParams()
      .set('fechaInicio', this.formatearFechaLocal(fechaInicio))
      .set('fechaFin', this.formatearFechaLocal(fechaFin));

    return this.http.get<DisponibilidadCanchaBackendDTO[]>(
      `${Constantes.ENDPOINT_LOCALES}/${localUuid}/disponibilidad`,
      { params }
    );
  }
}
