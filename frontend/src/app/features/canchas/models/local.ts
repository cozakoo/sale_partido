import { Cancha } from "./cancha"

export interface Local {
    uuid: string
    nombre: string
    canchas: Cancha[]
}