import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { EventoParticipacionService } from '../../services/evento-participacion.service';
import { AccionParticipacionComponent } from '../accion-participacion.component/accion-participacion.component';
import { ParticipantesListaComponent } from '../participantes-lista-component/participantes-lista-component';
import {
  EstadoInvitacion,
  EstadoParticipacion,
  EventoDetalle,
  ResultadoAccion,
  UsuarioSesion,
} from '../../models/evento-detalle.model';

@Component({
  selector: 'app-evento-modal',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [AccionParticipacionComponent, ParticipantesListaComponent],
  templateUrl: './evento-modal.component.html',
  styleUrl: './evento-modal.component.scss',
})
export class EventoModalComponent implements OnInit {
  activeModal = inject(NgbActiveModal);
  private service = inject(EventoParticipacionService);
  private destroyRef = inject(DestroyRef);

  // Seteado desde el page antes de abrir
  eventoId!: number;

  evento = signal<EventoDetalle | null>(null);
  usuario = signal<UsuarioSesion | null>(null);
  invitacion = signal<EstadoInvitacion | null>(null);
  estadoParticipacion = signal<EstadoParticipacion | null>(null);
  yaParticipa = signal(false);
  cargando = signal(true);

  ngOnInit(): void {
    this.service
      .getEvento(this.eventoId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((evento) => {
        this.evento.set(evento);
        this.cargarContexto();
      });
  }

  private cargarContexto(): void {
    this.service
      .getUsuarioActual()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((usuario) => {
        this.usuario.set(usuario);
        const estado = this.service.getEstadoParticipacion(this.eventoId);
        this.estadoParticipacion.set(estado);
        this.yaParticipa.set(estado !== null);
      });

    this.service
      .getEstadoInvitacion(this.eventoId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((invitacion) => {
        this.invitacion.set(invitacion);
        this.cargando.set(false);
      });
  }

  onAccionEjecutada(resultado: ResultadoAccion): void {
    if (resultado.nuevoEstadoParticipacion) {
      this.estadoParticipacion.set(resultado.nuevoEstadoParticipacion);
      this.yaParticipa.set(true);
    }
  }

  tipoIngresoLabel(): string {
    const map: Record<string, string> = {
      ABIERTO: 'Abierto',
      CON_CONFIRMACION: 'Con confirmación',
      CERRADO: 'Cerrado',
    };
    return map[this.evento()?.tipoIngreso ?? ''] ?? '';
  }

  tipoIngresoBadge(): string {
    const map: Record<string, string> = {
      ABIERTO: 'success',
      CON_CONFIRMACION: 'warning',
      CERRADO: 'secondary',
    };
    return map[this.evento()?.tipoIngreso ?? ''] ?? 'secondary';
  }
}