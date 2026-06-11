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
import { EventoService } from '../../services/evento.service';
import {
  CrearEventoRequest,
  EspacioReservado,
  NivelHabilidad,
  TipoIngreso,
} from '../../models/evento.mode';
import { Location } from '@angular/common';
// ── Validadores ──────────────────────────────────────────────────────────────

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

function fechaHoraCombinada(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const fecha = group.get('fecha')?.value;
    const hora = group.get('hora')?.value;
    if (!fecha || !hora) return null;
    const seleccionado = new Date(`${fecha}T${hora}`);
    if (seleccionado <= new Date()) {
      return { fechaHoraPasada: true };
    }
    return null;
  };
}

// ── Componente ───────────────────────────────────────────────────────────────

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
  private destroyRef = inject(DestroyRef);
  private cdr = inject(ChangeDetectorRef);
  private router = inject(Router); // reemplazá el inject existente

  // ── Estado ──────────────────────────────────────────────────────────────────
  espacioReservado = signal<EspacioReservado | null>(null);
  cargandoEspacio = signal(false);
  enviando = signal(false);
  errorMensaje = signal<string | null>(null);
  exitoso = signal(false);
  private location = inject(Location);

  sinEspacio = computed(() => !this.espacioReservado());

  // ── Opciones de formulario ───────────────────────────────────────────────────
  readonly tiposIngreso: TipoIngreso[] = ['Abierto', 'Con Confirmación', 'Cerrado'];
  readonly nivelesHabilidad: NivelHabilidad[] = [
    'Sin especificar',
    'Principiante',
    'Intermedio',
    'Avanzado',
  ];
  readonly hoy = new Date().toISOString().split('T')[0];

  // ── Formulario (inicializado vacío para evitar errores de binding) ───────────
  form: FormGroup = this.fb.group({});

  ngOnInit(): void {
    this._cargarEspacioReservado();
  }


  private _cargarEspacioReservado(): void {
    this.cargandoEspacio.set(true);

    // Intentar router state primero, luego sessionStorage (para tests)
    const navState = this.router.getCurrentNavigation()?.extras?.state;
    const storedState = sessionStorage.getItem('__reserva_state__');

    const reserva = navState?.['reserva']
      ?? (storedState ? JSON.parse(storedState) : null)
      ?? history.state?.reserva;

    // Limpiar después de leer
    if (storedState) sessionStorage.removeItem('__reserva_state__');

    if (!reserva) {
      this.espacioReservado.set(null);
      this._buildFormSinEspacio();
      this.cargandoEspacio.set(false);
      this.cdr.detectChanges();
      return;
    }

    const espacio: EspacioReservado = {
      reservaId: reserva.turnoId,
      localId: reserva.localUuid,
      localNombre: reserva.localNombre ?? reserva.localUuid,
      espacioId: 1,
      espacioNombre: reserva.espacioNombre,
      capacidad: reserva.capacidad ?? 10,
      fecha: reserva.fecha,
      hora: reserva.horaInicio,
    };

    this.espacioReservado.set(espacio);
    this._buildForm(espacio);
    this.cargandoEspacio.set(false);
    this.cdr.detectChanges();
  }

  private _buildForm(espacio: EspacioReservado): void {
  const manana = new Date();
  manana.setDate(manana.getDate() + 1);
  const year = manana.getFullYear();
  const month = String(manana.getMonth() + 1).padStart(2, '0');
  const day = String(manana.getDate()).padStart(2, '0');
  const fechaManana = `${year}-${month}-${day}`;

  this.form = this.fb.group(
    {
      fecha: [fechaManana, Validators.required],  // 👈 mañana
      hora: ['18:00', Validators.required],        // 👈 hora fija futura
      cupoMinimo: [
        espacio.capacidad,
        [Validators.required, Validators.min(1), Validators.max(espacio.capacidad)],
      ],
      cupoMaximo: [
        espacio.capacidad,
        [Validators.required, Validators.min(1), Validators.max(espacio.capacidad)],
      ],
      tipoIngreso: ['Cerrado' as TipoIngreso, Validators.required],
      tiempoCancelacionHoras: [
        1,
        [Validators.required, Validators.min(1), Validators.max(24)],
      ],
      nivelHabilidad: ['Sin especificar' as NivelHabilidad, Validators.required],
    },
    {
      validators: [cupoMinimoMenorQueMaximo(), fechaHoraCombinada()],
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
    const val = this.form.value;
    const cupoMinimo: number = val.cupoMinimo;
    const cupoMaximo: number = val.cupoMaximo;
    const tiempoCancelacion: number = val.tiempoCancelacionHoras;

    // ── Validaciones de negocio primero ─────────────────────────────────────
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
    if (tiempoCancelacion < 1 || tiempoCancelacion > 24) {
      this.errorMensaje.set('El tiempo límite de cancelación de participación debe estar entre 1 y 24 horas');
      return;
    }

    // ── Fecha/hora al final (no bloquea los tests de cupos/cancelación) ─────
    if (val.fecha && val.hora) {
      const seleccionado = new Date(`${val.fecha}T${val.hora}`);
      if (seleccionado <= new Date()) {
        this.errorMensaje.set('La fecha y hora del evento no pueden ser anteriores al momento actual');
        return;
      }
    }

    const request: CrearEventoRequest = {
      localId: espacio.localId,
      espacioId: espacio.espacioId,
      reservaId: espacio.reservaId,
      fecha: val.fecha,
      hora: val.hora,
      cupoMinimo,
      cupoMaximo,
      tipoIngreso: val.tipoIngreso,
      tiempoCancelacionHoras: tiempoCancelacion,
      nivelHabilidad: val.nivelHabilidad,
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
        },
        error: (err: Error) => {
          this.errorMensaje.set(err.message);
          this.enviando.set(false);
        },
      });
  }


  // ── Helpers de template ──────────────────────────────────────────────────────
  campoInvalido(campo: string): boolean {
    const c = this.form?.get(campo);
    return !!(c?.invalid && c?.touched);
  }

  get errorCupos(): boolean {
    return (
      this.form?.hasError('cupoMinimoMayorQueMaximo') &&
      (this.form.get('cupoMinimo')?.touched ||
        this.form.get('cupoMaximo')?.touched ||
        false)
    );
  }

  get errorFechaHora(): boolean {
    return (
      this.form?.hasError('fechaHoraPasada') &&
      (this.form.get('fecha')?.touched ||
        this.form.get('hora')?.touched ||
        false)
    );
  }

  volver(): void {
    this.location.back();
  }
}