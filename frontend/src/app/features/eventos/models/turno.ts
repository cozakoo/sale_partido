import { Cancha } from './cancha';
import { Local } from './local';

export interface Turno {
  uuid: string;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  cancha: Cancha;
  local: Local;
}
