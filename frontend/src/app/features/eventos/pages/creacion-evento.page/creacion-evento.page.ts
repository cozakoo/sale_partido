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
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  private cdr = inject(ChangeDetectorRef);

  // ── Estado ──────────────────────────────────────────────────────────────────
  espacioReservado = signal<EspacioReservado | null>(null);
  cargandoEspacio = signal(false);
  enviando = signal(false);
  errorMensaje = signal<string | null>(null);
  exitoso = signal(false);

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
    this.eventoService
      .obtenerEspacioReservado(101)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (espacio) => {
          this.espacioReservado.set(espacio);
          this._buildForm(espacio);
          this.cargandoEspacio.set(false);
          this.cdr.detectChanges();
        },
        error: () => {
          this.espacioReservado.set(null);
          this._buildFormSinEspacio();
          this.cargandoEspacio.set(false);
          this.cdr.detectChanges();
        },
      });
  }

  private _buildForm(espacio: EspacioReservado): void {
    const ahora = new Date();
    const fechaHoy = ahora.toISOString().split('T')[0];
    const horaActual = ahora.toTimeString().slice(0, 5);

    this.form = this.fb.group(
      {
        fecha: [fechaHoy, Validators.required],
        hora: [horaActual, Validators.required],
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

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const espacio = this.espacioReservado()!;
    const val = this.form.value;

    const request: CrearEventoRequest = {
      localId: espacio.localId,
      espacioId: espacio.espacioId,
      reservaId: espacio.reservaId,
      fecha: val.fecha,
      hora: val.hora,
      cupoMinimo: val.cupoMinimo,
      cupoMaximo: val.cupoMaximo,
      tipoIngreso: val.tipoIngreso,
      tiempoCancelacionHoras: val.tiempoCancelacionHoras,
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
}