import { CanchaSummary } from "./cancha-summary"

export interface LocalDetail {
    uuid: string
    nombre: string
    canchas: CanchaSummary[]
}