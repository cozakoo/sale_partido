import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { DiaCalendario } from '../models/calendario.model';

@Injectable({ providedIn: 'root' })
export class CalendarioDisponibilidadService {

  private SEMANAS: Record<number, DiaCalendario[]> = {
    0: [
      {
        fecha: new Date(), turnos: [
          { id:'t1', horaInicio:'09:00', horaFin:'10:00', espacioNombre:'Cancha 1', deporte:'Fútbol', estado:'libre' },
          { id:'t2', horaInicio:'11:00', horaFin:'12:00', espacioNombre:'Cancha 2', deporte:'Tenis', estado:'ocupado',
            reserva:{ organizadorNombre:'Juan Pérez', cantidadConfirmados:3, capacidadTotal:4, estadoEvento:'confirmado' } },
          { id:'t3', horaInicio:'14:00', horaFin:'15:00', espacioNombre:'Cancha 3', deporte:'Pádel', estado:'incompleto',
            reserva:{ organizadorNombre:'María García', cantidadConfirmados:1, capacidadTotal:2, estadoEvento:'pendiente' } },
        ]
      },
      {
        fecha: this.addDays(new Date(), 1), turnos: [
          { id:'t4', horaInicio:'08:00', horaFin:'09:00', espacioNombre:'Cancha 1', deporte:'Fútbol', estado:'ocupado',
            reserva:{ organizadorNombre:'Carlos López', cantidadConfirmados:8, capacidadTotal:10, estadoEvento:'confirmado' } },
          { id:'t5', horaInicio:'10:00', horaFin:'11:00', espacioNombre:'Cancha 2', deporte:'Básquet', estado:'libre' },
        ]
      },
      { fecha: this.addDays(new Date(), 2), turnos: [] },
      {
        fecha: this.addDays(new Date(), 3), turnos: [
          { id:'t6', horaInicio:'09:00', horaFin:'10:30', espacioNombre:'Cancha 1', deporte:'Fútbol', estado:'ocupado',
            reserva:{ organizadorNombre:'Ana Martínez', cantidadConfirmados:10, capacidadTotal:10, estadoEvento:'confirmado' } },
          { id:'t7', horaInicio:'16:00', horaFin:'17:00', espacioNombre:'Cancha 4', deporte:'Tenis', estado:'libre' },
        ]
      },
      {
        fecha: this.addDays(new Date(), 4), turnos: [
          { id:'t8', horaInicio:'07:00', horaFin:'08:00', espacioNombre:'Cancha 3', deporte:'Fútbol', estado:'libre' },
        ]
      },
      {
        fecha: this.addDays(new Date(), 5), turnos: [
          { id:'t9', horaInicio:'10:00', horaFin:'12:00', espacioNombre:'Cancha 1', deporte:'Fútbol', estado:'ocupado',
            reserva:{ organizadorNombre:'Diego Torres', cantidadConfirmados:6, capacidadTotal:6, estadoEvento:'confirmado' } },
        ]
      },
      { fecha: this.addDays(new Date(), 6), turnos: [] },
    ],
    1: [
      {
        fecha: this.addDays(new Date(), 7), turnos: [
          { id:'t10', horaInicio:'10:00', horaFin:'11:00', espacioNombre:'Cancha 5', deporte:'Voley', estado:'libre' },
        ]
      },
      {
        fecha: this.addDays(new Date(), 8), turnos: [
          { id:'t11', horaInicio:'09:00', horaFin:'10:00', espacioNombre:'Cancha 2', deporte:'Tenis', estado:'ocupado',
            reserva:{ organizadorNombre:'Laura Fernández', cantidadConfirmados:2, capacidadTotal:2, estadoEvento:'confirmado' } },
          { id:'t12', horaInicio:'15:00', horaFin:'16:00', espacioNombre:'Cancha 3', deporte:'Pádel', estado:'incompleto',
            reserva:{ organizadorNombre:'Sergio Ruiz', cantidadConfirmados:1, capacidadTotal:4, estadoEvento:'pendiente' } },
        ]
      },
      { fecha: this.addDays(new Date(), 9), turnos: [] },
      { fecha: this.addDays(new Date(), 10), turnos: [] },
      {
        fecha: this.addDays(new Date(), 11), turnos: [
          { id:'t13', horaInicio:'08:00', horaFin:'09:00', espacioNombre:'Cancha 1', deporte:'Básquet', estado:'libre' },
          { id:'t14', horaInicio:'11:00', horaFin:'13:00', espacioNombre:'Cancha 4', deporte:'Fútbol', estado:'ocupado',
            reserva:{ organizadorNombre:'Valentina Cruz', cantidadConfirmados:9, capacidadTotal:10, estadoEvento:'confirmado' } },
        ]
      },
      { fecha: this.addDays(new Date(), 12), turnos: [] },
      { fecha: this.addDays(new Date(), 13), turnos: [] },
    ],
    [-1]: [
      {
        fecha: this.addDays(new Date(), -7), turnos: [
          { id:'t15', horaInicio:'09:00', horaFin:'10:00', espacioNombre:'Cancha 2', deporte:'Tenis', estado:'ocupado',
            reserva:{ organizadorNombre:'Pedro Gómez', cantidadConfirmados:2, capacidadTotal:2, estadoEvento:'finalizado' } },
        ]
      },
      { fecha: this.addDays(new Date(), -6), turnos: [] },
      {
        fecha: this.addDays(new Date(), -5), turnos: [
          { id:'t16', horaInicio:'16:00', horaFin:'17:00', espacioNombre:'Cancha 1', deporte:'Fútbol', estado:'ocupado',
            reserva:{ organizadorNombre:'Lucía Herrera', cantidadConfirmados:8, capacidadTotal:10, estadoEvento:'finalizado' } },
          { id:'t17', horaInicio:'18:00', horaFin:'19:00', espacioNombre:'Cancha 3', deporte:'Pádel', estado:'libre' },
        ]
      },
      { fecha: this.addDays(new Date(), -4), turnos: [] },
      { fecha: this.addDays(new Date(), -3), turnos: [] },
      { fecha: this.addDays(new Date(), -2), turnos: [] },
      { fecha: this.addDays(new Date(), -1), turnos: [] },
    ],
  };

  private addDays(fecha: Date, dias: number): Date {
    const d = new Date(fecha);
    d.setDate(d.getDate() + dias);
    return d;
  }

  getSemana(fechaInicio: Date, offset: number = 0): Observable<DiaCalendario[]> {
    return of(this.SEMANAS[offset] ?? this.SEMANAS[0]);
  }
}