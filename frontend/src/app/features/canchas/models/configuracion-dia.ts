import { DiaSemana } from "./dia-semana";

export interface ConfiguracionDia {
  diaSemana: DiaSemana;
  activo: boolean;
  horaInicio: string;
  horaFin: string;
}