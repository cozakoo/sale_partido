export interface TurnoEventoResponse {
  uuid: string;
  fecha: string;        // 'YYYY-MM-DD'
  horaInicio: string;   // 'HH:mm:ss'
  horaFin: string;      // 'HH:mm:ss'
  cancha: { uuid: string; nombre: string };
  local: { uuid: string; nombre: string; direccion: string };
}
