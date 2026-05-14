import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { LocalService } from '../services/configuracion-disponibilidad.service';
import { LocalSummary } from '../models/local-summary';

@Component({
  selector: 'app-locales-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-4">
      <h1 class="mb-4">Mis Locales</h1>
      <div class="row">
        @for (local of locales; track local.uuid) {
          <div class="col-md-4 mb-4">
            <div class="card h-100 shadow-sm" style="cursor: pointer;" (click)="verDetalle(local.uuid)">
              <div class="card-body">
                <h5 class="card-title">{{ local.nombre }}</h5>
                <h6 class="card-subtitle mb-3 text-muted">Canchas disponibles:</h6>
                <ul class="list-group list-group-flush">
                  @for (cancha of local.canchas || []; track cancha.nombre || cancha.uuid) {
                    <li class="list-group-item">{{ cancha.nombre }}</li>
                  }
                  @if (!local.canchas || local.canchas.length === 0) {
                    <li class="list-group-item text-muted">No hay canchas registradas</li>
                  }
                </ul>
              </div>
              <div class="card-footer bg-transparent text-primary text-end border-top-0">
                Ver detalle &raquo;
              </div>
            </div>
          </div>
        }
      </div>
    </div>
  `
})
export class ListadoLocalesPage implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private localService = inject(LocalService);

  locales: LocalSummary[] | any[] = [];

  ngOnInit() {
    this.localService.getLocalesSummary().subscribe(data => {
      this.locales = data;
    });
  }

  verDetalle(uuid: string) {
    this.router.navigate([uuid], { relativeTo: this.route });
  }
}
