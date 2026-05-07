import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { ScheduleConfig, Space } from '../components/schedule-config/schedule-config.model';
 
@Injectable({ providedIn: 'root' })
export class ScheduleConfigService {
  private apiUrl = '/api/schedule-config';
 
  constructor(private http: HttpClient) {}
 
  getSpaces(): Observable<Space[]> {
    return of([
      { id: 1, name: 'Cancha 1' },
      { id: 2, name: 'Cancha 2' },
      { id: 3, name: 'Cancha 3' },
    ]);
  }
 
  getConfig(spaceId?: number | null): Observable<ScheduleConfig> {
    const params = spaceId ? `?spaceId=${spaceId}` : '';
    return this.http.get<ScheduleConfig>(`${this.apiUrl}${params}`);
  }
 
  saveConfig(config: ScheduleConfig): Observable<ScheduleConfig> {
    return this.http.post<ScheduleConfig>(this.apiUrl, config);
  }
}
 