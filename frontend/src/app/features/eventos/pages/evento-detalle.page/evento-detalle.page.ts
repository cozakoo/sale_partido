
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
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
  EstadoInvitacion,
  EstadoParticipacion,
  EventoDetalle,
  ResultadoAccion,
  UsuarioSesion,} from '../../models/evento-detalle.model';

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
  invitacion = signal<EstadoInvitacion | null>(null);
  estadoParticipacion = signal<EstadoParticipacion | null>(null);
  yaParticipa = signal<boolean>(false);

  cargandoPagina = signal(true);
  cargandoAccion = signal(false);
  error = signal<string | null>(null);

  private eventoId!: number;

  ngOnInit(): void {
    this.eventoId = Number(this.route.snapshot.paramMap.get('id'));
    this.cargarDatos();
  }

  private cargarDatos(): void {
    this.cargandoPagina.set(true);

    this.service
      .getEvento(this.eventoId)
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
        const estado = this.service.getEstadoParticipacion(this.eventoId);
        this.estadoParticipacion.set(estado);
        this.yaParticipa.set(estado !== null);
      });

    this.service
      .getEstadoInvitacion(this.eventoId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((invitacion) => {
        this.invitacion.set(invitacion);
        this.cargandoPagina.set(false);
      });
  }

  onAccionEjecutada(resultado: ResultadoAccion): void {
    if (resultado.nuevoEstadoParticipacion) {
      this.estadoParticipacion.set(resultado.nuevoEstadoParticipacion);
      this.yaParticipa.set(true);
    }
  }
}