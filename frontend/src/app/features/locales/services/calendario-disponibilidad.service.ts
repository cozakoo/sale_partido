import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Constantes } from '../../../core/Constantes';
import { DisponibilidadCanchaBackendDTO } from '../models/disponibilidad-cancha';
import { HttpClient, HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class CalendarioDisponibilidadService {
  private http = inject(HttpClient);

  private formatFecha(fecha: Date): string {
    const year = fecha.getFullYear();
    const month = String(fecha.getMonth() + 1).padStart(2, '0');
    const day = String(fecha.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  getDisponibilidad(localUuid: string, fechaInicio: Date, fechaFin: Date): Observable<DisponibilidadCanchaBackendDTO[]> {
    const params = new HttpParams()
      .set('fechaInicio', this.formatFecha(fechaInicio))
      .set('fechaFin', this.formatFecha(fechaFin));

    return this.http.get<DisponibilidadCanchaBackendDTO[]>(
      `${Constantes.ENDPOINT_LOCALES}/${localUuid}/disponibilidad`,
      { params }
    );
  }
}