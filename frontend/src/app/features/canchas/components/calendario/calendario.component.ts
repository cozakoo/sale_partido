import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { CalendarioService } from '../../services/calendario.service';
import { DiaCalendario } from '../../models/calendario.model';
import { TurnoItemComponent } from '../turno-item/turno-item.component';

@Component({
  selector: 'app-calendario',
  standalone: true,
  imports: [CommonModule, TurnoItemComponent],
  templateUrl: './calendario.component.html',
  styleUrl: './calendario.component.scss'
})
export class CalendarioComponent implements OnInit {
  private service = inject(CalendarioService);

  fechaInicio = this.getLunes(new Date());
  semana: DiaCalendario[] = [];
  offsetSemana = 0;
  diasAbiertos: Record<number, boolean> = {};
  turnosAbiertos: Record<string, boolean> = {};

  ngOnInit() { this.cargarSemana(); }

  cargarSemana() {
    this.service.getSemana(this.fechaInicio, this.offsetSemana).subscribe(data => {
      this.semana = data;
    });
  }

  navegar(delta: number) {
    this.offsetSemana += delta;
    const d = new Date(this.fechaInicio);
    d.setDate(d.getDate() + delta * 7);
    this.fechaInicio = d;
    this.cargarSemana();
  }

  irHoy() {
    this.offsetSemana = 0;
    this.fechaInicio = this.getLunes(new Date());
    this.cargarSemana();
  }

  toggleDia(i: number) {
    this.diasAbiertos[i] = !this.diasAbiertos[i];
  }

  isDiaAbierto(i: number): boolean {
    return this.diasAbiertos[i] !== false;
  }

  toggleTurno(id: string) {
    this.turnosAbiertos[id] = !this.turnosAbiertos[id];
  }

  isTurnoAbierto(id: string): boolean {
    return !!this.turnosAbiertos[id];
  }

  get fechaFin(): Date {
    const d = new Date(this.fechaInicio);
    d.setDate(d.getDate() + 6);
    return d;
  }

  private getLunes(fecha: Date): Date {
    const d = new Date(fecha);
    const dia = d.getDay();
    const diff = dia === 0 ? -6 : 1 - dia;
    d.setDate(d.getDate() + diff);
    return d;
  }
}