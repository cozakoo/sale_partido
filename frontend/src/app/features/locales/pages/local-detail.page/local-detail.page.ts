import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { LocalService } from '../../services/local.service';
import { LocalSearchResult } from '../../models/local-search-result';

@Component({
  selector: 'app-local-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-4">
      @if (localSearch()) {
        <div class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3 border-bottom pb-3">
          <div>
            <h2 class="mb-1 fw-bold text-dark">{{ localSearch()?.nombre }}</h2>
            <p class="text-muted mb-0">Detalles e información del complejo deportivo</p>
          </div>
    <div class="d-flex gap-2">
  <button class="btn btn-outline-primary" (click)="configurarHorarios()">
    Configurar horarios
  </button>
  <button class="btn btn-outline-success" (click)="verEventos()">
    Ver eventos
  </button>
  <button class="btn btn-primary" (click)="verDisponibilidad()">
    Ver disponibilidad
  </button>
</div>
        </div>

        <div class="card shadow-sm mb-4">
          <div class="card-body">
            <h5 class="card-title mb-3 fw-semibold text-primary">Información General</h5>
            @if (localSearch()?.descripcion) {
              <p class="card-text mb-4 text-secondary">{{ localSearch()?.descripcion }}</p>
            }
            
            <hr class="my-3 opacity-25">

            <div class="row g-3">
              <div class="col-md-6">
                <div class="d-flex align-items-start mb-3">
                  <span class="fs-5 me-2">📍</span>
                  <div>
                    <span class="text-muted d-block small fw-bold text-uppercase">Dirección</span>
                    <span class="text-dark">{{ localSearch()?.ubicacion || 'No especificada' }}</span>
                  </div>
                </div>

                <div class="d-flex align-items-start">
                  <span class="fs-5 me-2">📞</span>
                  <div>
                    <span class="text-muted d-block small fw-bold text-uppercase">Teléfono</span>
                    <span class="text-dark">{{ localSearch()?.telefono || 'No disponible' }}</span>
                  </div>
                </div>
              </div>

              <div class="col-md-6">
                <div class="d-flex align-items-start">
                  <span class="fs-5 me-2">🕐</span>
                  <div class="w-100">
                    <span class="text-muted d-block small fw-bold text-uppercase mb-2">Horarios de Atención</span>
                    @if (localSearch()?.horario && localSearch()!.horario.length > 0) {
                      <div class="d-flex flex-column gap-1">
                        @for (h of localSearch()?.horario ?? []; track h.dia) {
                          <div class="d-flex justify-content-between border-bottom pb-1 small">
                            <span class="fw-semibold text-secondary">{{ formatDia(h.dia) }}</span>
                            <span class="text-dark">{{ formatHorario(h.horarioApertura) }} - {{ formatHorario(h.horarioCierre) }} hs</span>
                          </div>
                        }
                      </div>
                    } @else {
                      <span class="text-muted small">Sin horarios configurados.</span>
                    }
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="card shadow-sm mb-4">
          <div class="card-body">
            <h5 class="card-title fw-semibold text-primary">Deportes</h5>
            <div>
              @for (deporte of localSearch()?.deportes ?? []; track deporte) {
                <span class="badge bg-primary bg-opacity-10 text-primary me-1 fs-6">{{ deporte }}</span>
              }
            </div>
          </div>
        </div>

        <div class="card shadow-sm mb-4">
          <div class="card-body">
            <h5 class="card-title fw-semibold text-primary">Canchas</h5>
            @for (cancha of localSearch()?.canchas ?? []; track cancha.uuid) {
              <div class="mb-3 pb-3 border-bottom">
                <h6 class="mb-2 fw-semibold text-dark">{{ cancha.nombre }}</h6>
                <p class="small text-muted mb-1">🏆 {{ cancha.deporte }}</p>
                <p class="small text-muted mb-0">👥 Capacidad: {{ cancha.capacidad }} personas</p>
              </div>
            }
          </div>
        </div>
      } @else {
        <div class="text-center py-5 text-muted">
          <div class="spinner-border text-primary mb-3" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
          <p class="fs-5">Cargando local...</p>
        </div>
      }

      <button class="btn btn-outline-secondary mb-4" (click)="volver()">
        &laquo; Volver al listado
      </button>
    </div>
  `
})
export class LocalDetailPage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private localService = inject(LocalService);

  localSearch = signal<LocalSearchResult | null>(null);

  ngOnInit() {
    const uuid = this.route.snapshot.paramMap.get('uuid');

    if (uuid) {
      this.localService.obtenerPorUuid(uuid).subscribe({
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
    if (!dia) return '';
    const dias: Record<string, string> = {
      'MONDAY': 'Lunes',
      'TUESDAY': 'Martes',
      'WEDNESDAY': 'Miércoles',
      'THURSDAY': 'Jueves',
      'FRIDAY': 'Viernes',
      'SATURDAY': 'Sábado',
      'SUNDAY': 'Domingo'
    };
    return dias[dia.toUpperCase()] || dia;
  }

  formatHorario(hora: string | any): string {
    if (!hora) return '';
    if (Array.isArray(hora)) {
      return `${String(hora[0]).padStart(2, '0')}:${String(hora[1] || 0).padStart(2, '0')}`;
    }
    if (typeof hora === 'string') {
      return hora.substring(0, 5);
    }
    return String(hora);
  }

  verEventos() {
    this.router.navigate(['/eventos']);
  }
}
