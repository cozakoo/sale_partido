import { ConfiguracionHorario } from "./configuracion-horario"

export interface CanchaDetail {
  uuid: number
  nombre: string
  configuracionesHorarios: ConfiguracionHorario[]
}