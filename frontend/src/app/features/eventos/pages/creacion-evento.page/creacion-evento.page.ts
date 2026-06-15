import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  OnInit,
  inject,
  signal,
  computed,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { EventoService } from '../../services/evento.service';
import { EventoParticipacionService } from '../../services/evento-participacion.service';

import { UsuarioSesion } from '../../models/evento-detalle.model';
import { CrearEventoRequest, EspacioReservado, NivelHabilidadDTO, TipoEvento, TipoIngresoLabel } from '../../models/evento.model';

// ── Mapas de conversión label UI → enum API ───────────────────────────────────
// Los tests E2E usan los labels en español; la conversión ocurre en confirmar()
const TIPO_INGRESO_MAP: Record<TipoIngresoLabel, TipoEvento> = {
  'Abierto': 'ABIERTO',
  'Con Confirmación': 'CON_CONFIRMACION',
  'Cerrado': 'CERRADO',
};

// ── Validadores ───────────────────────────────────────────────────────────────

function cupoMinimoMenorQueMaximo(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const minimo = group.get('cupoMinimo')?.value;
    const maximo = group.get('cupoMaximo')?.value;
    if (minimo !== null && maximo !== null && minimo > maximo) {
      return { cupoMinimoMayorQueMaximo: true };
    }
    return null;
  };
}

// ── Componente ────────────────────────────────────────────────────────────────

@Component({
  selector: 'app-creacion-evento-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './creacion-evento.page.html',
  styleUrls: ['./creacion-evento.page.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreacionEventoPage implements OnInit {
  private fb = inject(FormBuilder);
  private eventoService = inject(EventoService);
  private serviceUsr = inject(EventoParticipacionService);
  private destroyRef = inject(DestroyRef);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router);
  private location = inject(Location);

  // ── Estado ───────────────────────────────────────────────────────────────────
  espacioReservado = signal<EspacioReservado | null>(null);
  cargandoEspacio = signal(false);
  enviando = signal(false);
  errorMensaje = signal<string | null>(null);
  exitoso = signal(false);
  usuarioActual = signal<UsuarioSesion | null>(null);
  usuarios = signal<UsuarioSesion[]>([]);

  sinEspacio = computed(() => !this.espacioReservado());

  // ── Opciones del formulario ───────────────────────────────────────────────────
  readonly tiposIngreso: TipoIngresoLabel[] = ['Abierto', 'Con Confirmación', 'Cerrado'];
  deporteNombre = signal<string>('');
  niveles = signal<NivelHabilidadDTO[]>([]);
  nivelHabilidadSeleccionado = signal<NivelHabilidadDTO | null>(null);

  nivelHabilidadLabel = computed(() => {
    return this.nivelHabilidadSeleccionado()?.nombre ?? 'Sin especificar';
  });

  form: FormGroup = this.fb.group({});

  ngOnInit(): void {
    this.serviceUsr.getUsuarios().subscribe((usuarios) => {
      this.usuarios.set(usuarios);
    });
    this.serviceUsr.getUsuarioActual().subscribe((usuario) => {
      this.usuarioActual.set(usuario);
    });
    this._cargarEspacioReservado();
  }

  onUsuarioCambiado(event: Event): void {
    const selectEl = event.target as HTMLSelectElement;
    const selectedUuid = selectEl.value;
    const usuario = this.usuarios().find(u => u.uuid === selectedUuid);
    if (usuario) {
      this.usuarioActual.set(usuario);
      this.serviceUsr.setUsuarioActual(usuario);
    }
  }

  onNivelCambiado(event: Event): void {
    const selectEl = event.target as HTMLSelectElement;
    const selectedUuid = selectEl.value;
    const nivel = this.niveles().find(n => n.uuid === selectedUuid);
    this.nivelHabilidadSeleccionado.set(nivel || null);
  }

  private _cargarEspacioReservado(): void {
    this.cargandoEspacio.set(true);
    // Prioridad: router state → sessionStorage (compatibilidad tests E2E)
    const navState = this.router.getCurrentNavigation()?.extras?.state;
    const storedState = sessionStorage.getItem('__reserva_state__');

    const reserva =
      navState?.['reserva'] ??
      (storedState ? JSON.parse(storedState) : null) ??
      history.state?.reserva;
    console.log('Cargando espacio reservado...', reserva);

    if (storedState) sessionStorage.removeItem('__reserva_state__');

    if (!reserva) {
      this.espacioReservado.set(null);
      this._buildFormSinEspacio();
      this.cargandoEspacio.set(false);
      this.cdr.detectChanges();
      return;
    }

    // Fallback para tests E2E que aún mandan turnoId en lugar de turnoUuid
    const espacio: EspacioReservado = {
      turnoUuid: reserva.turnoUuid ?? reserva.turnoId ?? '',
      localUuid: reserva.localUuid ?? '',
      localNombre: reserva.localNombre ?? reserva.localUuid ?? '',
      canchaUuid: reserva.canchaUuid ?? '',
      canchaNombre: reserva.canchaNombre ?? reserva.espacioNombre ?? '',
      capacidad: reserva.capacidad ?? 10,
      fecha: reserva.fecha ?? '',
      horaInicio: reserva.horaInicio ?? '',
      horaFin: reserva.horaFin ?? '',
    };

    this.espacioReservado.set(espacio);
    this._buildForm(espacio);

    if (espacio.canchaUuid) {
      this.eventoService.getCanchaDeporte(espacio.canchaUuid)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (res) => {
            this.deporteNombre.set(res.deporteNombre);
            this.niveles.set(res.niveles);
            this.cdr.detectChanges();
          },
          error: (err) => {
            console.error('Error fetching cancha deporte and levels:', err);
          }
        });
    }

    this.cargandoEspacio.set(false);
    this.cdr.detectChanges();
  }

  private _buildForm(espacio: EspacioReservado): void {
    this.form = this.fb.group(
      {
        // fecha y hora son readonly — se muestran pero no se editan
        fecha: [{ value: espacio.fecha, disabled: true }],
        hora: [{ value: espacio.horaInicio, disabled: true }],
        nombre: [
          `Evento en ${espacio.canchaNombre || espacio.localNombre}`,
          [Validators.required, Validators.pattern(/.*\S.*/)],
        ],
        cupoMinimo: [
          1,                    // ← valor inicial 1 en vez de espacio.capacidad
          [Validators.required, Validators.min(1), Validators.max(espacio.capacidad)],
        ],
        cupoMaximo: [
          espacio.capacidad,
          [Validators.required, Validators.min(1), Validators.max(espacio.capacidad)],
        ],
        tipoIngreso: ['Cerrado' as TipoIngresoLabel, Validators.required],
        // El form trabaja en horas (1–24) para que los tests E2E no cambien
        // La conversión a minutos (×60) ocurre en confirmar()
        tiempoCancelacionHoras: [
          1,
          [Validators.required, Validators.min(1), Validators.max(24)],
        ],
        nivelHabilidad: [''],
      },
      {
        validators: [cupoMinimoMenorQueMaximo()],
      }
    );

    this.form.updateValueAndValidity();
    this.cdr.detectChanges();
  }

  private _buildFormSinEspacio(): void {
    this.form = this.fb.group({});
  }

  confirmar(): void {
    if (this.sinEspacio()) {
      this.errorMensaje.set('Debe seleccionar y reservar un espacio para el evento');
      return;
    }

    this.form.markAllAsTouched();

    const espacio = this.espacioReservado()!;
    const val = this.form.getRawValue(); // getRawValue incluye campos disabled
    const cupoMinimo: number = val.cupoMinimo;
    const cupoMaximo: number = val.cupoMaximo;
    const tiempoCancelacionHoras: number = val.tiempoCancelacionHoras;
    const nombre: string = val.nombre ? val.nombre.trim() : '';

    // ── Validaciones de negocio ───────────────────────────────────────────────
    if (!nombre) {
      this.errorMensaje.set('El nombre del evento es requerido y no puede estar vacío');
      return;
    }
    if (cupoMinimo <= 0) {
      this.errorMensaje.set('El cupo mínimo de jugadores debe ser mayor a cero');
      return;
    }
    if (cupoMaximo > espacio.capacidad) {
      this.errorMensaje.set('El cupo máximo no puede superar la capacidad máxima de la cancha');
      return;
    }
    if (cupoMinimo > cupoMaximo) {
      this.errorMensaje.set('El cupo mínimo no puede ser mayor al cupo máximo');
      return;
    }
    if (tiempoCancelacionHoras < 1 || tiempoCancelacionHoras > 24) {
      this.errorMensaje.set(
        'El tiempo límite de cancelación de participación debe estar entre 1 y 24 horas'
      );
      return;
    }

    if (!this.form.valid) return;

    // ── Conversiones label → enum y horas → minutos ───────────────────────────
    const tipoEvento: TipoEvento = TIPO_INGRESO_MAP[val.tipoIngreso as TipoIngresoLabel] ?? 'CERRADO';
    const nivelUuid = val.nivelHabilidad || null;
    const limiteCancelacionMinutos = tiempoCancelacionHoras * 60; // 60–1440 minutos

    const formatTime = (time: string): string => {
      if (!time) return '00:00:00';
      const parts = time.split(':');
      if (parts.length === 2) return `${time}:00`;
      return time;
    };

    const request: CrearEventoRequest = {
      turno: {
        fecha: espacio.fecha,
        horaInicio: formatTime(espacio.horaInicio),
        horaFin: formatTime(espacio.horaFin),
        canchaUuid: espacio.canchaUuid,
      },
      organizadorUuid: this.usuarioActual()?.uuid ?? '',
      nombre: nombre,
      tipo: tipoEvento,
      cupoMinimo,
      cupoMaximo,
      limiteCancelacionParticipacion: limiteCancelacionMinutos,
      nivelRequeridoUuid: nivelUuid,
    };

    this.enviando.set(true);
    this.errorMensaje.set(null);

    this.eventoService
      .crearEvento(request)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.enviando.set(false);
          this.exitoso.set(true);
          this.cdr.detectChanges();
        },
        error: (err: Error) => {
          this.errorMensaje.set(err.message);
          this.enviando.set(false);
          this.cdr.detectChanges();
        },
      });
  }

  // ── Helpers de template ───────────────────────────────────────────────────────
  campoInvalido(campo: string): boolean {
    const c = this.form?.get(campo);
    return !!(c?.invalid && c?.touched);
  }

  get errorCupos(): boolean {
    return (
      this.form?.hasError('cupoMinimoMayorQueMaximo') &&
      (this.form.get('cupoMinimo')?.touched || this.form.get('cupoMaximo')?.touched || false)
    );
  }

  volver(): void {
    this.location.back();
  }
}