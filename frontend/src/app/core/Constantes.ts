import { ConfiguracionDia } from "../features/canchas/models/configuracion-dia";

export class Constantes {

    static readonly API = 'http://localhost:8080/'
    static readonly ENDPOINT_LOCALES = this.API + 'locales'
    static readonly ENDPOINT_CANCHAS = this.API + 'canchas'
    static readonly SLOT_DURATIONS_MINUTES = [30, 45, 60, 90, 120];
    static readonly DEFAULT_DAYS = [
        { diaSemana: 'MONDAY', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'TUESDAY', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'WEDNESDAY', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'THURSDAY', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'FRIDAY', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'SATURDAY', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'SUNDAY', activo: false, horaInicio: '10:00:00', horaFin: '22:00:00' },
    ] as ConfiguracionDia[]
    
}