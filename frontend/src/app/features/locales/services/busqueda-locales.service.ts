import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { LocalSearchResult } from '../models/local-search-result';
import { Constantes } from '../../../core/Constantes';

@Injectable({ providedIn: 'root' })
export class BusquedaLocalesService {
  private http = inject(HttpClient);

  private readonly mockLocales: LocalSearchResult[] = [];

  private readonly ubicaciones: string[] = ['Centro', 'Norte', 'Sur', 'Oeste'];

  private readonly deportes: string[] = ['Fútbol', 'Básquet', 'Tenis', 'Pádel', 'Voley', 'Hockey'];

  getUbicaciones(): string[] {
    return this.ubicaciones;
  }

  getDeportes(): string[] {
    return this.deportes;
  }

  buscar(filtros: { texto?: string; ubicacion?: string; deporte?: string }): Observable<LocalSearchResult[]> {
    let params = new HttpParams();

    if (filtros.texto?.trim()) {
      params = params.set('texto', filtros.texto.trim());
    }

    if (filtros.ubicacion) {
      params = params.set('ubicacion', filtros.ubicacion);
    }

    if (filtros.deporte) {
      params = params.set('tipoDeporte', filtros.deporte);
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
}
