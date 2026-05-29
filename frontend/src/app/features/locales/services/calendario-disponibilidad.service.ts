import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { DiaCalendario } from '../models/calendario';
import { Constantes } from '../../../core/Constantes';
import { DisponibilidadCanchaBackendDTO } from '../models/disponibilidad-cancha';
import { HttpClient, HttpParams } from '@angular/common/http';
@Injectable({ providedIn: 'root' })
export class CalendarioDisponibilidadService {
  private http = inject(HttpClient);
  private addDays(fecha: Date, dias: number): Date {
    const d = new Date(fecha);
    d.setDate(d.getDate() + dias);
    return d;
  }

 
    getDisponibilidad(localUuid: string, fechaInicio: Date, fechaFin: Date): Observable<DisponibilidadCanchaBackendDTO[]> {
    const params = new HttpParams()
      .set('fechaInicio', fechaInicio.toISOString().split('T')[0])
      .set('fechaFin', fechaFin.toISOString().split('T')[0]);

    return this.http.get<DisponibilidadCanchaBackendDTO[]>(
      `${Constantes.ENDPOINT_LOCALES}/${localUuid}/disponibilidad`,
      { params }
    );
  }

}