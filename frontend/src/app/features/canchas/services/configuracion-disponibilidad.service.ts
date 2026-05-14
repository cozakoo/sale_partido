import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Constantes } from '../../../core/Constantes';
import { CanchaDetail } from '../models/cancha-detail';
import { LocalDetail } from '../models/local-detail';
import { LocalSummary } from '../models/local-summary';
import { CanchaSummary } from '../models/cancha-summary';
import { SaveCanchaConfiguracionHorarioRequest } from '../models/cancha-configuracion-horario-request';
import { SaveCanchaConfiguracionHorarioResponse } from '../models/cancha-configuracion-horario-response';

@Injectable({ providedIn: 'root' })
export class LocalService {
  private http = inject(HttpClient)

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

}
