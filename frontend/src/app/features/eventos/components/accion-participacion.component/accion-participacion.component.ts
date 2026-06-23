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

import { EventoDetalle } from '../../models/evento-detalle';
import { ParticipacionResponse } from '../../models/participacion-response';
import { ResultadoAccion } from '../../models/resultado-accion';
import { UsuarioSesion } from '../../models/usuario-sesion';
import { EventoService } from '../../services/evento.service';

function obtenerOrdenNivel(nivelNombre: string, deporte: string): number {
  const normalizar = (str: string) =>
    str ? str.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim() : '';

  const nombreNorm = normalizar(nivelNombre);
  const deporteNorm = normalizar(deporte);

  if (deporteNorm === 'paddle' || deporteNorm === 'padel') {
    if (nombreNorm.includes('8va')) return 1;
    if (nombreNorm.includes('7ma')) return 2;
    if (nombreNorm.includes('6ta')) return 3;
    if (nombreNorm.includes('5ta')) return 4;
    if (nombreNorm.includes('4ta')) return 5;
    if (nombreNorm.includes('3ra')) return 6;
    if (nombreNorm.includes('2da')) return 7;
    if (nombreNorm.includes('1ra')) return 8;
  }

  if (nombreNorm.includes('principiante')) return 1;
  if (nombreNorm.includes('intermedio')) return 2;
  if (nombreNorm.includes('avanzado')) return 3;

  return 0;
}

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
  private service = inject(EventoService);
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

    if (!nivelUsuario) return true;

    const ordenUsuario = obtenerOrdenNivel(nivelUsuario, this.evento().deporte);
    return ordenUsuario < requerido.orden;
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
    () => false
  );

  mostrarBotonUnirse = computed(
    () =>
      this.evento().estado === 'DISPONIBLE' &&
      this.evento().tipo === 'ABIERTO' &&
      !this.yaParticipa() &&
      !this.invitacionPendiente() &&
      !this.participacion()?.esInvitacion &&
      !this.estadoParticipacionTexto() &&
      this.participacion()?.estado !== 'CONFIRMADO' &&
      this.participacion()?.estado !== 'PENDIENTE'
  );

  mostrarBotonSolicitar = computed(
    () =>
      this.evento().estado === 'DISPONIBLE' &&
      this.evento().tipo === 'CON_CONFIRMACION' &&
      !this.yaParticipa() &&
      !this.participacion()?.esInvitacion &&
      !this.estadoParticipacionTexto() &&
      this.participacion()?.estado !== 'CONFIRMADO' &&
      this.participacion()?.estado !== 'PENDIENTE'
  );

  mostrarMensajeCerrado = computed(
    () =>
      this.evento().tipo === 'CERRADO' &&
      (!this.participacion() || !this.participacion()?.esInvitacion)
  );

  mensajeAMostrar = computed(() => {
    if (this.mensajeActivo()) {
      return this.mensajeActivo();
    }
    if (this.invitacionYaRespondida()) {
      return 'mensaje-invitacion-ya-procesada';
    }
    if (this.yaParticipa() || this.participacion()?.estado === 'CONFIRMADO') {
      return 'mensaje-ya-participa';
    }
    if (this.participacion()?.estado === 'PENDIENTE' && !this.participacion()?.esInvitacion) {
      return 'mensaje-confirmacion-solicitud';
    }
    return null;
  });

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