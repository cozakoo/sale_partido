import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { LocalService } from '../services/configuracion-disponibilidad.service';
import { LocalDetail } from '../models/local-detail';

@Component({
  selector: 'app-local-detail',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container mt-4">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>{{ local?.nombre || 'Cargando local...' }}</h2>
        <button class="btn btn-primary" (click)="configurarHorarios()">
          Configurar horarios
        </button>
      </div>

      <div class="card shadow-sm mb-4">
        <div class="card-body">
          <h5 class="card-title">Información General</h5>
          <p class="card-text">
            Desde este panel puedes administrar la información de tu local, gestionar las reservas y la disponibilidad.
          </p>
        </div>
      </div>
      
      <button class="btn btn-outline-secondary" (click)="volver()">
        &laquo; Volver al listado
      </button>
    </div>
  `
})
export class DetalleLocalPage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private localService = inject(LocalService);

  localUuid: string | null = null;
  local: LocalDetail | any = null;

  ngOnInit() {
    this.localUuid = this.route.snapshot.paramMap.get('uuid');
    
    if (this.localUuid) {
      this.localService.getLocalDetail(this.localUuid).subscribe(data => {
        this.local = data;
      });
    }
  }

  configurarHorarios() {
    this.router.navigate(['configuraciones-horarios'], { relativeTo: this.route });
  }

  volver() {
    this.router.navigate(['..'], { relativeTo: this.route });
  }
}
