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

  textoBusqueda = '';
  ubicacionSeleccionada = '';
  deporteSeleccionado = '';

  ubicaciones = this.busquedaService.getUbicaciones();
  deportes = this.busquedaService.getDeportes();

  constructor() {
    this.buscar();
  }

  buscar() {
    this.buscando.set(true);

    this.busquedaService
      .buscar({
        texto: this.textoBusqueda,
        ubicacion: this.ubicacionSeleccionada || undefined,
        deporte: this.deporteSeleccionado || undefined,
      })
      .subscribe({
        next: (data) => this.resultados.set(data),
        complete: () => this.buscando.set(false),
      });
  }

  limpiarFiltros() {
    this.textoBusqueda = '';
    this.ubicacionSeleccionada = '';
    this.deporteSeleccionado = '';
    this.buscar();
  }

  verDetalle(uuid: string) {
    this.router.navigate([uuid], { relativeTo: this.route });
  }
}
