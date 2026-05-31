export interface LocalSearchResult {
  uuid: string;
  nombre: string;
  ubicacion: string;
  deportes: string[];
  telefono: string;
  descripcion: string;
  horario: HorarioAtencion[];
  deportesDisponibles: string[];
  canchas: Cancha[];
}

export interface HorarioAtencion {
  dia: string;
  horarioApertura: string;
  horarioCierre: string;
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
