import { Component, inject, OnInit, signal } from '@angular/core';
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
      @if (localSearch()) {
        <div class="d-flex justify-content-between align-items-start mb-4 flex-wrap gap-2">
          <div>
            <h2 class="mb-1">{{ localSearch()?.nombre }}</h2>
            <p class="text-muted mb-0">
              📍 {{ localSearch()?.ubicacion }} &middot; 🕐 {{ localSearch()?.horario }}
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
            <p class="card-text">{{ localSearch()?.descripcion }}</p>
            <p class="small text-muted mb-0">📞 {{ localSearch()?.telefono }}</p>
          </div>
        </div>

        <div class="card shadow-sm mb-4">
          <div class="card-body">
            <h5 class="card-title">Deportes</h5>
            <div>
              @for (deporte of localSearch()?.deportes ?? []; track deporte) {
                <span class="badge bg-primary bg-opacity-10 text-primary me-1 fs-6">{{ deporte }}</span>
              }
            </div>
          </div>
        </div>

        <div class="card shadow-sm mb-4">
          <div class="card-body">
            <h5 class="card-title">Canchas</h5>
            @for (cancha of localSearch()?.canchas ?? []; track cancha.uuid) {
              <div class="mb-3 pb-3 border-bottom">
                <h6 class="mb-2">{{ cancha.nombre }}</h6>
                <p class="small text-muted mb-1">🏆 {{ cancha.deporte }}</p>
                <p class="small text-muted mb-0">👥 Capacidad: {{ cancha.capacidad }} personas</p>
              </div>
            }
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

  localSearch = signal<LocalSearchResult | null>(null);

  ngOnInit() {
    const uuid = this.route.snapshot.paramMap.get('uuid');

    if (uuid) {
      this.busquedaService.obtenerPorUuid(uuid).subscribe({
        next: (local) => {
          this.localSearch.set(local || null);
        }
      });
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
