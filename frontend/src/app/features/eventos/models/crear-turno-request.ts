export interface CrearTurnoRequest {
  fecha: string;        // 'YYYY-MM-DD'
  horaInicio: string;   // 'HH:mm:ss'
  horaFin: string;      // 'HH:mm:ss'
  canchaUuid: string;
}
