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
import { ActivatedRoute } from '@angular/router';

import { EventoParticipacionService } from '../../services/evento-participacion.service';
import { EventoInfoComponent } from '../../components/evento-info.component/evento-info.component';
import { AccionParticipacionComponent } from '../../components/accion-participacion.component/accion-participacion.component';
import { ParticipantesListaComponent } from '../../components/participantes-lista-component/participantes-lista-component';
import {
  EventoDetalle,
  ParticipacionResponse,
  ResultadoAccion,
  UsuarioSesion,
} from '../../models/evento-detalle.model';

@Component({
  selector: 'app-evento-detalle-page',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    EventoInfoComponent,
    ParticipantesListaComponent,
    AccionParticipacionComponent,
  ],
  templateUrl: './evento-detalle.page.html',
  styleUrl: './evento-detalle.page.scss',
})
export class EventoDetallePage implements OnInit {
  private route = inject(ActivatedRoute);
  private service = inject(EventoParticipacionService);
  private destroyRef = inject(DestroyRef);

  evento = signal<EventoDetalle | null>(null);
  usuario = signal<UsuarioSesion | null>(null);
  participacion = signal<ParticipacionResponse | null>(null);
  yaParticipa = computed(() => this.participacion() !== null);

  cargandoPagina = signal(true);
  cargandoAccion = signal(false);
  error = signal<string | null>(null);

  private eventoUuid!: string;

  ngOnInit(): void {
    this.eventoUuid = this.route.snapshot.paramMap.get('id') || '';
    this.cargarDatos();
  }

  private cargarDatos(): void {
    this.cargandoPagina.set(true);

    this.service
      .getEvento(this.eventoUuid)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (evento) => {
          this.evento.set(evento);
          this.cargarContextoUsuario();
        },
        error: () => {
          this.error.set('No se pudo cargar el evento. Intentá de nuevo más tarde.');
          this.cargandoPagina.set(false);
        },
      });
  }

  private cargarContextoUsuario(): void {
    this.service
      .getUsuarioActual()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((usuario) => {
        this.usuario.set(usuario);

        this.service
          .getParticipacionUsuario(this.eventoUuid, usuario.uuid)
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe({
            next: (participacion) => {
              this.participacion.set(participacion);
              this.cargandoPagina.set(false);
            },
            error: () => {
              this.participacion.set(null);
              this.cargandoPagina.set(false);
            },
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
      this.service.getEvento(this.eventoUuid).subscribe(evento => {
        this.evento.set(evento);
      });
    } else if (resultado.nuevoEstadoInvitacion) {
      this.participacion.set({
        uuid: current?.uuid || `part-uuid-new-${Date.now()}`,
        estado: resultado.nuevoEstadoInvitacion,
        esInvitacion: current?.esInvitacion || false,
      });
      this.service.getEvento(this.eventoUuid).subscribe(evento => {
        this.evento.set(evento);
      });
    }
  }
}