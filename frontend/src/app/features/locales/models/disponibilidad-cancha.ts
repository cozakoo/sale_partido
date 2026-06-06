export interface TurnoDetalleBackendDTO {
  uuid: string;
  nombreOrganizador: string;
  deporte: string;
  cantidadParticipantesConfirmados: number;
  estadoEvento: string;
   capacidad: number;

}

export interface TurnoBackendDTO {
  fecha: string;
  horaInicio: string;
  horaFin: string;
  espacioNombre: string;
  deporte: string | null;
  estado: 'LIBRE' | 'OCUPADO';
  turno: TurnoDetalleBackendDTO | null;
}

export interface DisponibilidadCanchaBackendDTO {
  canchaUuid: string;
  canchaNombre: string;
  turnos: TurnoBackendDTO[];
}