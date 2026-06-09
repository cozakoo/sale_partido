import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  computed,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import {
  EstadoInvitacion,
  EventoDetalle,
  ResultadoAccion,
  UsuarioSesion,
} from '../../models/evento-detalle.model';
import { EventoParticipacionService } from '../../services/evento-participacion.service';

@Component({
  selector: 'app-accion-participacion',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './accion-participacion.component.html',
  styleUrl: './accion-participacion.component.scss',
})
export class AccionParticipacionComponent {
  // ── Inputs ─────────────────────────────────────────────────────────────────
  evento = input.required<EventoDetalle>();
  usuario = input.required<UsuarioSesion>();
  invitacion = input.required<EstadoInvitacion>();
  yaParticipa = input<boolean>(false);
  cargando = input<boolean>(false);

  // ── Outputs ────────────────────────────────────────────────────────────────
  accionEjecutada = output<ResultadoAccion>();

  // ── DI ─────────────────────────────────────────────────────────────────────
  private service = inject(EventoParticipacionService);
  private destroyRef = inject(DestroyRef);

  // ── Estado local post-acción ───────────────────────────────────────────────
  estadoParticipacionTexto = signal<string | null>(null);
  estadoInvitacionTexto = signal<string | null>(null);
  mensajeActivo = signal<string | null>(null);

  // ── Computed ───────────────────────────────────────────────────────────────

  cupoLleno = computed(
    () => this.evento().participantesConfirmados.length >= this.evento().cupoMaximo
  );

  nivelIncompatible = computed(() => {
    const requerido = this.evento().nivelRequerido;
    if (requerido === 'CUALQUIERA') return false;
    return this.usuario().nivelHabilidad !== requerido;
  });

  invitacionPendiente = computed(
    () =>
      this.invitacion().tieneInvitacion &&
      this.invitacion().estadoRespuesta === 'PENDIENTE'
  );

  invitacionYaRespondida = computed(
    () =>
      this.invitacion().tieneInvitacion &&
      !!this.invitacion().estadoRespuesta &&
      this.invitacion().estadoRespuesta !== 'PENDIENTE'
  );

  mostrarBotonesInvitacion = computed(
    () => this.invitacionPendiente() && !this.estadoInvitacionTexto()
  );

  mostrarInvitacionYaRespondida = computed(
    () => this.invitacionYaRespondida() && !this.estadoInvitacionTexto()
  );

  mostrarBotonUnirse = computed(
    () =>
      this.evento().tipoIngreso === 'ABIERTO' &&
      !this.invitacionPendiente() &&
      !this.estadoParticipacionTexto()
  );

  mostrarBotonSolicitar = computed(
    () =>
      this.evento().tipoIngreso === 'CON_CONFIRMACION' &&
      !this.invitacionPendiente() &&
      !this.estadoParticipacionTexto()
  );

  mostrarMensajeCerrado = computed(
    () =>
      this.evento().tipoIngreso === 'CERRADO' &&
      !this.invitacion().tieneInvitacion
  );

  // ── Handlers ───────────────────────────────────────────────────────────────

  onUnirse(): void {
    if (this.nivelIncompatible()) {
      this.mensajeActivo.set('mensaje-nivel-incompatible');
      return;
    }
    if (this.yaParticipa()) {
      this.mensajeActivo.set('mensaje-ya-participa');
      return;
    }
    this.service
      .unirse(this.evento().id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((resultado) => {
        this.estadoParticipacionTexto.set('Confirmado');
        this.mensajeActivo.set('mensaje-confirmacion-participacion');
        this.accionEjecutada.emit(resultado);
      });
  }

  onSolicitar(): void {
    if (this.nivelIncompatible()) {
      this.mensajeActivo.set('mensaje-nivel-incompatible');
      return;
    }
    this.service
      .solicitarParticipacion(this.evento().id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((resultado) => {
        this.estadoParticipacionTexto.set('Pendiente');
        this.mensajeActivo.set('mensaje-confirmacion-solicitud');
        this.accionEjecutada.emit(resultado);
      });
  }

  onAceptarInvitacion(): void {
    this.service
      .aceptarInvitacion(this.evento().id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((resultado) => {
        this.estadoParticipacionTexto.set('Confirmado');
        this.estadoInvitacionTexto.set('Aceptada');
        this.mensajeActivo.set('mensaje-confirmacion-participacion');
        this.accionEjecutada.emit(resultado);
      });
  }

  onRechazarInvitacion(): void {
    this.service
      .rechazarInvitacion(this.evento().id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((resultado) => {
        this.estadoInvitacionTexto.set('Rechazada');
        this.mensajeActivo.set('mensaje-confirmacion-rechazo');
        this.accionEjecutada.emit(resultado);
      });
  }

  onAceptarInvitacionYaRespondida(): void {
    this.mensajeActivo.set('mensaje-invitacion-ya-procesada');
  }
}