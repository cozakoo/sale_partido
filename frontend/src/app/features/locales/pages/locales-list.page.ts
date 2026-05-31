import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { BusquedaLocalesService } from '../services/busqueda-locales.service';
import { LocalSearchResult } from '../models/local-search-result';

@Component({
  selector: 'app-locales-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './locales-list.page.html',
  styleUrl: './locales-list.page.scss',
})
export class LocalesListPage {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private busquedaService = inject(BusquedaLocalesService);

  resultados = signal<LocalSearchResult[]>([]);
  buscando = signal(false);

  ubicacionSeleccionada = '';
  deporteSeleccionado = '';
  fechaSeleccionada = '';
  horarioDesdeSeleccionado = '';
  horarioHastaSeleccionado = '';

  ubicaciones = this.busquedaService.getUbicaciones();
  deportes = this.busquedaService.getDeportes();

  constructor() {
    this.buscar();
  }

  get isHorarioInvalido(): boolean {
    if (this.horarioDesdeSeleccionado && this.horarioHastaSeleccionado) {
      return this.horarioDesdeSeleccionado >= this.horarioHastaSeleccionado;
    }
    return false;
  }

  buscar() {
    if (this.isHorarioInvalido) {
      return;
    }

    this.buscando.set(true);

    this.busquedaService
      .buscar({
        ubicacion: this.ubicacionSeleccionada || undefined,
        deporte: this.deporteSeleccionado || undefined,
        fecha: this.fechaSeleccionada || undefined,
        horarioDesde: this.horarioDesdeSeleccionado || undefined,
        horarioHasta: this.horarioHastaSeleccionado || undefined,
      })
      .subscribe({
        next: (data) => this.resultados.set(data),
        complete: () => this.buscando.set(false),
      });
  }

  limpiarFiltros() {
    this.ubicacionSeleccionada = '';
    this.deporteSeleccionado = '';
    this.fechaSeleccionada = '';
    this.horarioDesdeSeleccionado = '';
    this.horarioHastaSeleccionado = '';
    this.buscar();
  }

  verDetalle(uuid: string) {
    this.router.navigate([uuid], { relativeTo: this.route });
  }

  formatDia(dia: string): string {
    const dias: Record<string, string> = {
      'MONDAY': 'Lunes',
      'TUESDAY': 'Martes',
      'WEDNESDAY': 'Miércoles',
      'THURSDAY': 'Jueves',
      'FRIDAY': 'Viernes',
      'SATURDAY': 'Sábado',
      'SUNDAY': 'Domingo'
    };
    return dias[dia] || dia;
  }

  formatHorario(hora: string): string {
    if (!hora) return '';
    return hora.substring(0, 5);
  }
  }