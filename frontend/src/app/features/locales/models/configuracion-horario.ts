import { AlcanceConfiguracionHorario } from "./alcance-configuracion-horario";
import { ConfiguracionDia } from "./configuracion-dia";

export interface ConfiguracionHorario {
  alcance: AlcanceConfiguracionHorario;
  configuracionesDias: ConfiguracionDia[];
  /** En minutos */
  duracionTurno: number;
}
 