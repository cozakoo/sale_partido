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
  EventoDetalle,
  ParticipacionResponse,
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
  participacion = input<ParticipacionResponse | null>(null);
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
    () => this.evento().participantes.length >= this.evento().cupoMaximo
  );

  nivelIncompatible = computed(() => {
    // Si es una invitación, se ignora el nivel requerido porque el organizador lo avala
    if (this.participacion()?.esInvitacion) return false;

    const requerido = this.evento().nivelRequerido;
    if (!requerido) return false;

    const normalizar = (str: string) =>
      str ? str.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim() : '';

    const deporteNormalizado = normalizar(this.evento().deporte);
    const habilidades = this.usuario().habilidades || {};
    const keyEncontrada = Object.keys(habilidades).find(
      key => normalizar(key) === deporteNormalizado
    );
    const nivelUsuario = keyEncontrada ? habilidades[keyEncontrada] : undefined;

    return nivelUsuario !== requerido.nombre;
  });

  invitacionPendiente = computed(
    () =>
      this.participacion()?.esInvitacion === true &&
      this.participacion()?.estado === 'PENDIENTE'
  );

  invitacionYaRespondida = computed(
    () =>
      this.participacion()?.esInvitacion === true &&
      !!this.participacion()?.estado &&
      this.participacion()?.estado !== 'PENDIENTE'
  );

  mostrarBotonesInvitacion = computed(
    () => this.invitacionPendiente() && !this.estadoInvitacionTexto()
  );

  mostrarInvitacionYaRespondida = computed(
    () => this.invitacionYaRespondida() && !this.estadoInvitacionTexto()
  );

  mostrarBotonUnirse = computed(
    () =>
      this.evento().tipo === 'ABIERTO' &&
      !this.participacion()?.esInvitacion &&
      !this.estadoParticipacionTexto()
  );

  mostrarBotonSolicitar = computed(
    () =>
      this.evento().tipo === 'CON_CONFIRMACION' &&
      !this.participacion()?.esInvitacion &&
      !this.estadoParticipacionTexto()
  );

  mostrarMensajeCerrado = computed(
    () =>
      this.evento().tipo === 'CERRADO' &&
      (!this.participacion() || !this.participacion()?.esInvitacion)
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
      .unirse(this.evento().uuid, this.usuario().uuid)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((resultado) => {
        this.estadoParticipacionTexto.set('Confirmado');
        this.mensajeActivo.set('mensaje-confirmacion-participacion');
        this.accionEjecutada.emit({
          exito: true,
          nuevoEstadoParticipacion: 'CONFIRMADO',
          mensaje: 'Tu participación fue confirmada',
          mensajeTestid: 'mensaje-confirmacion-participacion',
        });
      });
  }

  onSolicitar(): void {
    if (this.nivelIncompatible()) {
      this.mensajeActivo.set('mensaje-nivel-incompatible');
      return;
    }
    this.service
      .solicitarParticipacion(this.evento().uuid, this.usuario().uuid)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((resultado) => {
        this.estadoParticipacionTexto.set('Pendiente');
        this.mensajeActivo.set('mensaje-confirmacion-solicitud');
        this.accionEjecutada.emit({
          exito: true,
          nuevoEstadoParticipacion: 'PENDIENTE',
          mensaje: 'Tu solicitud fue enviada',
          mensajeTestid: 'mensaje-confirmacion-solicitud',
        });
      });
  }

  onAceptarInvitacion(): void {
    const partUuid = this.participacion()?.uuid;
    if (!partUuid) return;
    this.service
      .responderInvitacion(partUuid, 'CONFIRMADO')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((resultado) => {
        this.estadoParticipacionTexto.set('Confirmado');
        this.estadoInvitacionTexto.set('Aceptada');
        this.mensajeActivo.set('mensaje-confirmacion-participacion');
        this.accionEjecutada.emit({
          exito: true,
          nuevoEstadoParticipacion: 'CONFIRMADO',
          nuevoEstadoInvitacion: 'CONFIRMADO',
          mensaje: 'Tu participación fue confirmada',
          mensajeTestid: 'mensaje-confirmacion-participacion',
        });
      });
  }

  onRechazarInvitacion(): void {
    const partUuid = this.participacion()?.uuid;
    if (!partUuid) return;
    this.service
      .responderInvitacion(partUuid, 'RECHAZADO')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((resultado) => {
        this.estadoInvitacionTexto.set('Rechazada');
        this.mensajeActivo.set('mensaje-confirmacion-rechazo');
        this.accionEjecutada.emit({
          exito: true,
          nuevoEstadoInvitacion: 'RECHAZADO',
          mensaje: 'Rechazaste la invitación',
          mensajeTestid: 'mensaje-confirmacion-rechazo',
        });
      });
  }

  onAceptarInvitacionYaRespondida(): void {
    this.mensajeActivo.set('mensaje-invitacion-ya-procesada');
  }
}