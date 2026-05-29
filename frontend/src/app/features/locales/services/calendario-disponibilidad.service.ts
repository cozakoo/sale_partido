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