import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { BusquedaLocalesService } from '../services/busqueda-locales.service';
import { LocalSearchResult } from '../models/local-search-result';

@Component({
  selector: 'app-local-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-4">
      @if (localSearch) {
        <div class="d-flex justify-content-between align-items-start mb-4 flex-wrap gap-2">
          <div>
            <h2 class="mb-1">{{ localSearch.nombre }}</h2>
            <p class="text-muted mb-0">
              📍 {{ localSearch.ubicacion }} &middot; 🕐 {{ localSearch.horario }}
            </p>
          </div>
          <div class="d-flex gap-2">
            <button class="btn btn-outline-primary" (click)="configurarHorarios()">
              Configurar horarios
            </button>
            <button class="btn btn-primary" (click)="verDisponibilidad()">
              Ver disponibilidad
            </button>
          </div>
        </div>

        <div class="card shadow-sm mb-4">
          <div class="card-body">
            <h5 class="card-title">Información General</h5>
            <p class="card-text">{{ localSearch.descripcion }}</p>
            <p class="small text-muted mb-0">📞 {{ localSearch.telefono }}</p>
          </div>
        </div>

        <div class="card shadow-sm mb-4">
          <div class="card-body">
            <h5 class="card-title">Deportes</h5>
            <div>
              @for (deporte of localSearch.deportes; track deporte) {
                <span class="badge bg-primary bg-opacity-10 text-primary me-1 fs-6">{{ deporte }}</span>
              }
            </div>
          </div>
        </div>
      } @else {
        <div class="text-center py-5 text-muted">
          <p class="fs-5">Cargando local...</p>
        </div>
      }

      <button class="btn btn-outline-secondary" (click)="volver()">
        &laquo; Volver al listado
      </button>
    </div>
  `
})
export class LocalDetailPage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private busquedaService = inject(BusquedaLocalesService);

  localSearch: LocalSearchResult | null = null;

  ngOnInit() {
    const uuid = this.route.snapshot.paramMap.get('uuid');

    if (uuid) {
      const mock = this.busquedaService.obtenerPorUuid(uuid);
      if (mock) {
        this.localSearch = mock;
      }
    }
  }

  configurarHorarios() {
    this.router.navigate(['configuraciones-horarios'], { relativeTo: this.route });
  }

  verDisponibilidad() {
    this.router.navigate(['calendario'], { relativeTo: this.route });
  }

  volver() {
    this.router.navigate(['..'], { relativeTo: this.route });
  }
}
