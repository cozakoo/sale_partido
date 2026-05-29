import { ActivatedRoute } from '@angular/router';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TurnoItemComponent } from '../components/turno-item/turno-item.component';
import { BarraFiltrosComponent } from '../components/barra-filtros/barra-filtros.component';
import { CalendarioDisponibilidadService } from '../services/calendario-disponibilidad.service';
import { DisponibilidadCanchaBackendDTO, TurnoBackendDTO } from '../models/disponibilidad-cancha';
import { DiaCalendario, Turno, EstadoTurno, EstadoEvento, FilterSelection } from '../models/calendario';

@Component({
  selector: 'app-calendario-disponibilidad',
  standalone: true,
  imports: [CommonModule, TurnoItemComponent, BarraFiltrosComponent],
  templateUrl: './calendario-disponibilidad.page.html',
  styleUrl: './calendario-disponibilidad.page.scss'
})
export class CalendarioDisponibilidadPage implements OnInit {
  private service = inject(CalendarioDisponibilidadService);
  private route = inject(ActivatedRoute);

  localUuid!: string;
  fechaInicio = signal(this.getLunes(new Date()));
  offsetSemana = signal(0);

  loading = signal(false);
  loadError = signal(false);

  diasAbiertos = signal<Record<number, boolean>>({});
  turnosAbiertos = signal<Record<string, boolean>>({});

  semana = signal<DiaCalendario[]>([]);
  vista = signal<'dia' | 'semana'>('semana');

  diaSeleccionado = signal(0);

  cambiarVista(vista: 'dia' | 'semana') {
    this.vista.set(vista);
  }
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

  ngOnInit() {
    this.localUuid = this.route.snapshot.paramMap.get('uuid')!;
    this.cargarSemana();
  }

  cargarSemana() {
    this.loading.set(true);
    this.loadError.set(false);

    this.service.getDisponibilidad(this.localUuid, this.fechaInicio(), this.fechaFin).subscribe({
      next: data => {
        this.semana.set(this.mapearASemana(data));
        this.loading.set(false);
      },
      error: () => {
        this.loadError.set(true);
        this.loading.set(false);
      }
    });
  }

  private mapearASemana(canchas: DisponibilidadCanchaBackendDTO[]): DiaCalendario[] {
    const diasMap = new Map<string, DiaCalendario>();

    for (let i = 0; i < 7; i++) {
      const d = new Date(this.fechaInicio());
      d.setDate(d.getDate() + i);
      const key = this.formatearFechaLocal(d); // Usa timezone local, no UTC
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
    if (dto.estado === 'LIBRE') {
      return 'libre';
    }

    const ahora = new Date();
    const fechaFinTurno = this.crearFechaHora(dto.fecha, dto.horaFin);

    if (fechaFinTurno < ahora) {
      return 'finalizado';
    }

    switch (dto.reserva?.estadoEvento) {
      case 'PENDIENTE':
        return 'incompleto';
      case 'CONFIRMADO':
      case 'FINALIZADO':
      default:
        return 'ocupado';
    }
  }

  private crearFechaHora(fecha: string, hora: string): Date {
    const [year, month, day] = fecha.split('-').map(Number);
    const [hours, minutes] = hora.split(':').map(Number);

    return new Date(year, month - 1, day, hours, minutes);
  }

  private formatearFechaLocal(d: Date): string {
    // Formatear en timezone local (no convertir a UTC como toISOString())
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  navegar(delta: number) {
    if (this.vista() === 'semana') {
      const d = new Date(this.fechaInicio());
      d.setDate(d.getDate() + delta * 7);
      this.fechaInicio.set(d);
      this.cargarSemana();
      return;
    }

    // Vista día
    const nuevoIndex = this.diaSeleccionado() + delta;

    if (nuevoIndex >= 0 && nuevoIndex <= 6) {
      // Sigue dentro de la semana cargada, solo mover índice
      this.diaSeleccionado.set(nuevoIndex);
      return;
    }

    // Cruzó el límite de la semana, cargar semana anterior/siguiente
    const d = new Date(this.fechaInicio());
    d.setDate(d.getDate() + delta * 7);
    this.fechaInicio.set(d);

    // Posicionar en el extremo correcto
    this.diaSeleccionado.set(nuevoIndex < 0 ? 6 : 0);
    this.cargarSemana();
  }

  irHoy() {
    this.offsetSemana.set(0);
    this.fechaInicio.set(this.getLunes(new Date()));
    this.cargarSemana();
  }

  toggleDia(i: number) {
    this.diasAbiertos.update(v => ({
      ...v,
      [i]: !v[i]
    }));
  }

  isDiaAbierto(i: number): boolean {
    return this.diasAbiertos()[i] !== false;
  }

  toggleTurno(id: string) {
    this.turnosAbiertos.update(v => ({
      ...v,
      [id]: !v[id]
    }));
  }

  isTurnoAbierto(id: string): boolean {
    return !!this.turnosAbiertos()[id];
  }

  onFiltrosCambiar(seleccion: FilterSelection) {
    this.seleccionFiltros.set(seleccion);
  }

  limpiarFiltros() {
    this.seleccionFiltros.set({ estados: [], espacios: [], deportes: [] });
  }

  get fechaFin(): Date {
    const f = this.fechaInicio();
    const year = f.getFullYear();
    const month = f.getMonth();
    const day = f.getDate();
    const d = new Date(year, month, day);
    d.setDate(d.getDate() + 6);
    return d;
  }

  private getLunes(fecha: Date): Date {
    const year = fecha.getFullYear();
    const month = fecha.getMonth();
    const day = fecha.getDate();
    console.log('getLunes input → year:', year, 'month:', month, 'day:', day);
    const d = new Date(year, month, day);
    const diaSemana = d.getDay();
    console.log('diaSemana:', diaSemana);
    const diff = diaSemana === 0 ? -6 : 1 - diaSemana;
    d.setDate(d.getDate() + diff);
    console.log('lunes calculado:', d);
    return d;
  }
}
