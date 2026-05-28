import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { TurnoItemComponent } from '../components/turno-item/turno-item.component';
import { CalendarioDisponibilidadService } from '../services/calendario-disponibilidad.service';
import { DisponibilidadCanchaBackendDTO, TurnoBackendDTO } from '../models/disponibilidad-cancha';
import { DiaCalendario, Turno, EstadoTurno, EstadoEvento } from '../models/calendario';

@Component({
  selector: 'app-calendario-disponibilidad',
  standalone: true,
  imports: [CommonModule, TurnoItemComponent],
  templateUrl: './calendario-disponibilidad.page.html',
  styleUrl: './calendario-disponibilidad.page.scss'
})
export class CalendarioDisponibilidadPage implements OnInit {
  private service = inject(CalendarioDisponibilidadService);
  private route = inject(ActivatedRoute);

  localUuid!: string;
  fechaInicio = this.getLunes(new Date());
  semana: DiaCalendario[] = [];
  offsetSemana = 0;

  loading = false;
  loadError = false;

  diasAbiertos: Record<number, boolean> = {};
  turnosAbiertos: Record<string, boolean> = {};

  ngOnInit() {
    this.localUuid = this.route.snapshot.paramMap.get('uuid')!;
    this.cargarSemana();
  }

  cargarSemana() {
    this.loading = true;
    this.loadError = false;

    this.service.getDisponibilidad(this.localUuid, this.fechaInicio, this.fechaFin).subscribe({
      next: data => {
        this.semana = this.mapearASemana(data);
        this.loading = false;
      },
      error: () => {
        this.loadError = true;
        this.loading = false;
      }
    });
  }

  private mapearASemana(canchas: DisponibilidadCanchaBackendDTO[]): DiaCalendario[] {
    const diasMap = new Map<string, DiaCalendario>();

    for (let i = 0; i < 7; i++) {
      const d = new Date(this.fechaInicio);
      d.setDate(d.getDate() + i);
      const key = d.toISOString().split('T')[0];
      diasMap.set(key, { fecha: d, turnos: [] });
    }

    canchas.forEach(cancha => {
      cancha.turnos.forEach(turnoDTO => {
        const dia = diasMap.get(turnoDTO.fecha);
        if (dia) {
          dia.turnos.push(this.mapearTurno(turnoDTO));
        }
      });
    });

    diasMap.forEach(dia => {
      dia.turnos.sort((a, b) => a.horaInicio.localeCompare(b.horaInicio));
    });

    return Array.from(diasMap.values());
  }

  private mapearTurno(dto: TurnoBackendDTO): Turno {
    return {
      id: `${dto.fecha}-${dto.espacioNombre}-${dto.horaInicio}`,
      horaInicio: dto.horaInicio.substring(0, 5),
      horaFin: dto.horaFin.substring(0, 5),
      espacioNombre: dto.espacioNombre,
      deporte: dto.deporte ?? '',
      estado: this.mapearEstado(dto),
      reserva: dto.reserva ? {
        organizadorNombre: dto.reserva.nombreOrganizador,
        cantidadConfirmados: dto.reserva.cantidadParticipantesConfirmados,
        capacidad: dto.reserva.capacidad,
        estadoEvento: dto.reserva.estadoEvento.toLowerCase() as EstadoEvento
      } : undefined
    };
  }

  private mapearEstado(dto: TurnoBackendDTO): EstadoTurno {
    if (dto.estado === 'LIBRE') return 'libre';
    if (dto.reserva?.estadoEvento === 'PENDIENTE') return 'incompleto';
    return 'ocupado';
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