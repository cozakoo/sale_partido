export interface LocalSearchResult {
  uuid: string;
  nombre: string;
  ubicacion: string;
  deportes: string[];
  telefono: string;
  descripcion: string;
  horario: string;
  deportesDisponibles: string[];
  canchas: Cancha[];
}

export interface Cancha {
  uuid: string;
  nombre: string;
  deporte: string;
  capacidad: number;
  configuracionesHorarios: ConfiguracionHorario[];
}

export interface ConfiguracionHorario {
  horaInicio: string;
  horaFin: string;
  duracionTurno: number;
}
