import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { TurnoItemComponent } from '../components/turno-item/turno-item.component';
import { BarraFiltrosComponent } from '../components/barra-filtros/barra-filtros.component';
import { CalendarioDisponibilidadService } from '../services/calendario-disponibilidad.service';
import { DiaCalendario, EstadoTurno, FilterSelection } from '../models/calendario';

@Component({
  selector: 'app-calendario-disponibilidad',
  standalone: true,
  imports: [CommonModule, TurnoItemComponent, BarraFiltrosComponent],
  templateUrl: './calendario-disponibilidad.page.html',
  styleUrl: './calendario-disponibilidad.page.scss'
})
export class CalendarioDisponibilidadPage implements OnInit {
  private service = inject(CalendarioDisponibilidadService);

  fechaInicio = this.getLunes(new Date());
  semana = signal<DiaCalendario[]>([]);
  offsetSemana = 0;
  diasAbiertos: Record<number, boolean> = {};
  turnosAbiertos: Record<string, boolean> = {};

  seleccionFiltros = signal<FilterSelection>({
    estados: [],
    espacios: [],
    deportes: [],
  });

  opcionesFiltros = computed(() => {
    const data = this.semana();
    const estados = new Set<EstadoTurno>();
    const espacios = new Set<string>();
    const deportes = new Set<string>();

    for (const dia of data) {
      for (const turno of dia.turnos) {
        estados.add(turno.estado);
        espacios.add(turno.espacioNombre);
        deportes.add(turno.deporte);
      }
    }

    return {
      estados: Array.from(estados),
      espacios: Array.from(espacios).sort(),
      deportes: Array.from(deportes).sort(),
    };
  });

  semanaFiltrada = computed(() => {
    const data = this.semana();
    const sel = this.seleccionFiltros();
    const tieneFiltros = sel.estados.length > 0 || sel.espacios.length > 0 || sel.deportes.length > 0;

    if (!tieneFiltros) return data;

    return data.map(dia => ({
      ...dia,
      turnos: dia.turnos.filter(turno => {
        const pasaEstado = sel.estados.length === 0 || sel.estados.includes(turno.estado);
        const pasaEspacio = sel.espacios.length === 0 || sel.espacios.includes(turno.espacioNombre);
        const pasaDeporte = sel.deportes.length === 0 || sel.deportes.includes(turno.deporte);
        return pasaEstado && pasaEspacio && pasaDeporte;
      }),
    }));
  });

  ngOnInit() { this.cargarSemana(); }

  cargarSemana() {
    this.service.getSemana(this.fechaInicio, this.offsetSemana).subscribe(data => {
      this.semana.set(data);
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

  onFiltrosCambiar(seleccion: FilterSelection) {
    this.seleccionFiltros.set(seleccion);
  }

  limpiarFiltros() {
    this.seleccionFiltros.set({ estados: [], espacios: [], deportes: [] });
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
