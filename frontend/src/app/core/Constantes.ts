import { ConfiguracionDia } from "../features/canchas/models/configuracion-dia";

export class Constantes {

    static readonly ENDPOINT_CANCHAS = '/locales'
    static readonly SLOT_DURATIONS_MINUTES = [30, 45, 60, 90, 120];
    static readonly DEFAULT_DAYS = [
        { diaSemana: 'Monday', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'Tuesday', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'Wednesday', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'Thursday', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'Friday', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'Saturday', activo: true, horaInicio: '10:00:00', horaFin: '22:00:00' },
        { diaSemana: 'Sunday', activo: false, horaInicio: '10:00:00', horaFin: '22:00:00' },
    ] as ConfiguracionDia[]
    
}