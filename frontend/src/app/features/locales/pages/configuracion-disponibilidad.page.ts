import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TurnoItemComponent } from '../components/turno-item/turno-item.component';
import { BarraFiltrosComponent } from '../components/barra-filtros/barra-filtros.component';
import { LocalService } from '../services/local.service';
import { DisponibilidadCanchaBackendDTO, TurnoBackendDTO } from '../models/disponibilidad-cancha';
import { DiaCalendario, Turno, EstadoTurno, EstadoEvento, FilterSelection } from '../models/calendario';
import { ActivatedRoute, Router } from '@angular/router';
import { UsuarioSesion } from '../../eventos/models/evento-detalle.model';
import { EventoParticipacionService } from '../../eventos/services/evento-participacion.service';

@Component({
  selector: 'app-calendario-disponibilidad',
  standalone: true,
  imports: [CommonModule, TurnoItemComponent, BarraFiltrosComponent],
  templateUrl: './calendario-disponibilidad.page.html',
  styleUrl: './calendario-disponibilidad.page.scss',
})
export class CalendarioDisponibilidadPage implements OnInit {
  private service = inject(LocalService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private serviceUsr = inject(EventoParticipacionService);

  usuarios = signal<UsuarioSesion[]>([]);
  usuarioSeleccionado = signal<UsuarioSesion | null>(null);

  localUuid!: string;
  // ── Nombre del local cargado desde el backend ─────────────────────────────
  localNombre = signal<string>('');

  fechaInicio = signal(this.getLunes(new Date()));
  offsetSemana = signal(0);

  loading = signal(false);
  loadError = signal(false);

  diasAbiertos = signal<Record<number, boolean>>({});
  turnosAbiertos = signal<Record<string, boolean>>({});

  semana = signal<DiaCalendario[]>([]);
  vista = signal<'dia' | 'semana'>('semana');
  diaSeleccionado = signal(0);

  seleccionFiltros = signal<FilterSelection>({
    estados: [],
    espacios: [],
    deportes: [],
  });

  private _mockUsers: UsuarioSesion[] = [
    {
      uuid: 'user-participante-01',
      nombre: 'Federico Cotrena (Mock)',
      habilidades: { Fútbol: 'INTERMEDIO' },
    },
    {
      uuid: 'user-participante-02',
      nombre: 'Ana García (Mock)',
      habilidades: { Fútbol: 'INTERMEDIO', Tenis: 'AVANZADO' },
    },
    {
      uuid: 'user-participante-03',
      nombre: 'Bruno Martínez (Mock)',
      habilidades: { Básquet: 'AVANZADO' },
    },
  ];

  private _usuarioActual = signal<UsuarioSesion | null>(null);

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
    const tieneFiltros =
      sel.estados.length > 0 || sel.espacios.length > 0 || sel.deportes.length > 0;

    if (!tieneFiltros) return data;

    return data.map((dia) => ({
      ...dia,
      turnos: dia.turnos.filter((turno) => {
        const pasaEstado = sel.estados.length === 0 || sel.estados.includes(turno.estado);
        const pasaEspacio = sel.espacios.length === 0 || sel.espacios.includes(turno.espacioNombre);
        const pasaDeporte = sel.deportes.length === 0 || sel.deportes.includes(turno.deporte);
        return pasaEstado && pasaEspacio && pasaDeporte;
      }),
    }));
  });

  ngOnInit() {
    this.localUuid = this.route.snapshot.paramMap.get('uuid')!;
    this.usuarios.set(this._mockUsers);
    this.usuarioSeleccionado.set(this._mockUsers[0]);

    // ── Cargar nombre del local ───────────────────────────────────────────────
    this.service.obtenerPorUuid(this.localUuid).subscribe({
      next: (local) => {
        if (local) this.localNombre.set(local.nombre);
      },
      error: () => {
        // no crítico — la página funciona igual sin el nombre
      },
    });

    this.cargarSemana();
  }

  cargarSemana() {
    this.loading.set(true);
    this.loadError.set(false);

    this.service
      .getDisponibilidad(this.localUuid, this.fechaInicio(), this.fechaFin)
      .subscribe({
        next: (data) => {
          this.semana.set(this.mapearASemana(data));
          this.loading.set(false);
        },
        error: () => {
          this.loadError.set(true);
          this.loading.set(false);
        },
      });
  }

  private mapearASemana(canchas: DisponibilidadCanchaBackendDTO[]): DiaCalendario[] {
    const diasMap = new Map<string, DiaCalendario>();

    for (let i = 0; i < 7; i++) {
      const d = new Date(this.fechaInicio());
      d.setDate(d.getDate() + i);
      const key = this.formatearFechaLocal(d);
      diasMap.set(key, { fecha: d, turnos: [] });
    }

    canchas.forEach((cancha) => {
      cancha.turnos.forEach((turnoDTO) => {
        const dia = diasMap.get(turnoDTO.fecha);
        if (dia) {
          dia.turnos.push(this.mapearTurno(turnoDTO, cancha));
        }
      });
    });

    diasMap.forEach((dia) => {
      dia.turnos.sort((a, b) => a.horaInicio.localeCompare(b.horaInicio));
    });

    return Array.from(diasMap.values());
  }

  // ── Ahora recibe también la cancha para tener uuid y nombre ───────────────
  private mapearTurno(dto: TurnoBackendDTO, cancha: DisponibilidadCanchaBackendDTO): Turno {
    return {
      id: `${dto.fecha}-${dto.espacioNombre}-${dto.horaInicio}`,
      turnoUuid: dto.uuid,
      fecha: dto.fecha,
      horaInicio: dto.horaInicio.substring(0, 5),
      horaFin: dto.horaFin.substring(0, 5),
      espacioNombre: dto.espacioNombre,
      canchaNombre: cancha.canchaNombre,
      canchaUuid: cancha.canchaUuid,
      deporte: dto.deporte ?? '',
      estado: this.mapearEstado(dto),
      capacidad: dto.turno?.capacidad ?? 0,
      turno: dto.turno
        ? {
            organizadorNombre: dto.turno.nombreOrganizador,
            cantidadConfirmados: dto.turno.cantidadParticipantesConfirmados,
            capacidad: dto.turno.capacidad,
            estadoEvento: dto.turno.estadoEvento.toLowerCase() as EstadoEvento,
          }
        : undefined,
    };
  }

  private mapearEstado(dto: TurnoBackendDTO): EstadoTurno {
    if (dto.estado === 'LIBRE') return 'libre';

    const ahora = new Date();
    const fechaFinTurno = this.crearFechaHora(dto.fecha, dto.horaFin);

    if (fechaFinTurno < ahora) return 'finalizado';

    switch (dto.turno?.estadoEvento) {
      case 'PENDIENTE':
        return 'incompleto';
      case 'CONFIRMADO':
      case 'FINALIZADO':
      default:
        return 'ocupado';
    }
  }

  // ── Navegación al formulario de creación ─────────────────────────────────
  onReservar(turno: Turno): void {
    this.router.navigate(['/eventos/new'], {
      state: {
        reserva: {
          turnoUuid: turno.turnoUuid,
          localUuid: this.localUuid,
          localNombre: this.localNombre(),
          canchaUuid: turno.canchaUuid,
          canchaNombre: turno.canchaNombre,
          capacidad: turno.capacidad,
          fecha: turno.fecha,
          horaInicio: turno.horaInicio,
          horaFin: turno.horaFin,
        },
      },
    });
  }

  // ── Resto de métodos sin cambios ──────────────────────────────────────────

  cambiarVista(vista: 'dia' | 'semana') {
    this.vista.set(vista);
  }

  navegar(delta: number) {
    if (this.vista() === 'semana') {
      const d = new Date(this.fechaInicio());
      d.setDate(d.getDate() + delta * 7);
      this.fechaInicio.set(d);
      this.cargarSemana();
      return;
    }

    const nuevoIndex = this.diaSeleccionado() + delta;

    if (nuevoIndex >= 0 && nuevoIndex <= 6) {
      this.diaSeleccionado.set(nuevoIndex);
      return;
    }

    const d = new Date(this.fechaInicio());
    d.setDate(d.getDate() + delta * 7);
    this.fechaInicio.set(d);
    this.diaSeleccionado.set(nuevoIndex < 0 ? 6 : 0);
    this.cargarSemana();
  }

  irHoy() {
    this.offsetSemana.set(0);
    this.fechaInicio.set(this.getLunes(new Date()));

    if (this.vista() === 'dia') {
      const hoy = this.formatearFechaLocal(new Date());
      const semana = this.semanaFiltrada();
      const index = semana.findIndex(
        (dia) => this.formatearFechaLocal(dia.fecha) === hoy
      );
      this.diaSeleccionado.set(index >= 0 ? index : 0);
    }

    this.cargarSemana();
  }

  toggleDia(i: number) {
    this.diasAbiertos.update((v) => ({ ...v, [i]: !v[i] }));
  }

  isDiaAbierto(i: number): boolean {
    return this.diasAbiertos()[i] !== false;
  }

  toggleTurno(id: string) {
    this.turnosAbiertos.update((v) => ({ ...v, [id]: !v[id] }));
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

  onUsuarioCambiado(event: Event): void {
    const selectEl = event.target as HTMLSelectElement;
    const selectedUuid = selectEl.value;
    const usuario = this.usuarios().find((u) => u.uuid === selectedUuid);
    if (usuario) {
      this.usuarioSeleccionado.set(usuario);
      this.serviceUsr.setUsuarioActual(usuario);
      this.cargarSemana();
    }
  }

  setUsuarioActual(usuario: UsuarioSesion): void {
    this._usuarioActual.set(usuario);
  }

  get fechaFin(): Date {
    const f = this.fechaInicio();
    const d = new Date(f.getFullYear(), f.getMonth(), f.getDate());
    d.setDate(d.getDate() + 6);
    return d;
  }

  private getLunes(fecha: Date): Date {
    const d = new Date(fecha.getFullYear(), fecha.getMonth(), fecha.getDate());
    const diaSemana = d.getDay();
    const diff = diaSemana === 0 ? -6 : 1 - diaSemana;
    d.setDate(d.getDate() + diff);
    return d;
  }

  private crearFechaHora(fecha: string, hora: string): Date {
    const [year, month, day] = fecha.split('-').map(Number);
    const [hours, minutes] = hora.split(':').map(Number);
    return new Date(year, month - 1, day, hours, minutes);
  }

  private formatearFechaLocal(d: Date): string {
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}