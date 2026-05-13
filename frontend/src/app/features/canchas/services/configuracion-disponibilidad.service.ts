import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Constantes } from '../../../core/Constantes';
import { ConfiguracionHorario } from '../models/configuracion-horario';
import { Cancha } from '../models/cancha';
 
@Injectable({ providedIn: 'root' })
export class ScheduleConfigService {
  private apiUrl = Constantes.ENDPOINT_CANCHAS;
 
  constructor(private http: HttpClient) {}
 
  getSpaces(): Observable<Cancha[]> {
    return of([
      { uuid: 1, nombre: 'Cancha 1' },
      { uuid: 2, nombre: 'Cancha 2' },
      { uuid: 3, nombre: 'Cancha 3' },
    ] as Cancha[]);
  }
 
  getConfig(spaceId?: number | null): Observable<ConfiguracionHorario> {
    const params = spaceId ? `?spaceId=${spaceId}` : '';
    return this.http.get<ConfiguracionHorario>(`${this.apiUrl}${params}`);
  }
 
  saveConfig(config: ConfiguracionHorario): Observable<ConfiguracionHorario> {
    return this.http.post<ConfiguracionHorario>(this.apiUrl, config);
  }

/*
    getSemana(fechaInicio: Date): Observable<DiaCalendario[]> {
    const params = new HttpParams()
      .set('desde', fechaInicio.toISOString().split('T')[0]);
    return this.http.get<DiaCalendario[]>(`${this.base}/semana`, { params }); }*/
 
}
 