import { ConfiguracionHorario } from "./configuracion-horario"

export interface Cancha {
  uuid: number
  nombre: string
  configuracionesHorarios: ConfiguracionHorario[]
}