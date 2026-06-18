import { NivelHabilidadDTO } from './nivel-habilidad-dto';

export interface CanchaDeporteResponse {
  deporteUuid: string;
  deporteNombre: string;
  niveles: NivelHabilidadDTO[];
}
