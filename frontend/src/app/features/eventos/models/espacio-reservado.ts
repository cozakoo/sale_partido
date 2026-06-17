export interface EspacioReservado {
  turnoUuid: string;
  localUuid: string;
  localNombre: string;
  canchaUuid: string;
  canchaNombre: string;
  capacidad: number;
  fecha: string;        // 'YYYY-MM-DD'
  horaInicio: string;   // 'HH:mm'
  horaFin: string;      // 'HH:mm'
}
