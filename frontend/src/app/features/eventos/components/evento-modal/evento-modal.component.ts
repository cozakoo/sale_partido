import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
  computed,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { EventoParticipacionService } from '../../services/evento-participacion.service';
import { AccionParticipacionComponent } from '../accion-participacion.component/accion-participacion.component';
import { ParticipantesListaComponent } from '../participantes-lista-component/participantes-lista-component';
import {
  EventoDetalle,
  ParticipacionResponse,
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
  eventoUuid!: string;

  evento = signal<EventoDetalle | null>(null);
  usuario = signal<UsuarioSesion | null>(null);
  participacion = signal<ParticipacionResponse | null>(null);
  yaParticipa = computed(() => this.participacion() !== null);
  cargando = signal(true);

  ngOnInit(): void {
    this.service
      .getEvento(this.eventoUuid)
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

        this.service
          .getParticipacionUsuario(this.eventoUuid, usuario.uuid)
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe((participacion) => {
            this.participacion.set(participacion);
            this.cargando.set(false);
          });
      });
  }

  onAccionEjecutada(resultado: ResultadoAccion): void {
    const current = this.participacion();
    if (resultado.nuevoEstadoParticipacion) {
      this.participacion.set({
        uuid: current?.uuid || `part-uuid-new-${Date.now()}`,
        estado: resultado.nuevoEstadoParticipacion,
        esInvitacion: current?.esInvitacion || false,
      });
      // Re-fetch the event to update the participants list
      this.service.getEvento(this.eventoUuid).subscribe((evento) => {
        this.evento.set(evento);
      });
    } else if (resultado.nuevoEstadoInvitacion) {
      this.participacion.set({
        uuid: current?.uuid || `part-uuid-new-${Date.now()}`,
        estado: resultado.nuevoEstadoInvitacion,
        esInvitacion: current?.esInvitacion || false,
      });
      this.service.getEvento(this.eventoUuid).subscribe((evento) => {
        this.evento.set(evento);
      });
    }
  }

  tipoIngresoLabel(): string {
    const map: Record<string, string> = {
      ABIERTO: 'Abierto',
      CON_CONFIRMACION: 'Con confirmación',
      CERRADO: 'Cerrado',
    };
    return map[this.evento()?.tipo ?? ''] ?? '';
  }

  tipoIngresoBadge(): string {
    const map: Record<string, string> = {
      ABIERTO: 'success',
      CON_CONFIRMACION: 'warning',
      CERRADO: 'secondary',
    };
    return map[this.evento()?.tipo ?? ''] ?? 'secondary';
  }
}