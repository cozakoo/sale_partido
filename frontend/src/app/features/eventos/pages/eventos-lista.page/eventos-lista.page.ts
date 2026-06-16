import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  OnInit,
  inject,
  signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EventoParticipacionService } from '../../services/evento-participacion.service';
import { EventoModalComponent } from '../../components/evento-modal/evento-modal.component';
import { EventoDetalle, UsuarioSesion } from '../../models/evento-detalle.model';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-eventos-lista-page',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './eventos-lista.page.html',
  styleUrl: './eventos-lista.page.scss',
  imports: [CommonModule, RouterModule],
})
export class EventosListaPage implements OnInit {
  private service = inject(EventoParticipacionService);
  private modalService = inject(NgbModal);
  private destroyRef = inject(DestroyRef);

  eventos = signal<EventoDetalle[]>([]);
  cargando = signal(true);
  usuarios = signal<UsuarioSesion[]>([]);
  usuarioSeleccionado = signal<UsuarioSesion | null>(null);

  ngOnInit(): void {
    this.service
      .getUsuarios()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (usuarios) => {
          this.usuarios.set(usuarios);
          if (usuarios.length > 0) {
            const primerUsuario = usuarios[0];
            this.usuarioSeleccionado.set(primerUsuario);
            this.service.setUsuarioActual(primerUsuario);
          }
          this.cargarEventos();
        },
        error: (err) => {
          console.error('Error al cargar la lista de usuarios:', err);
          this.cargarEventos(); // Intentamos cargar los eventos aunque fallen los usuarios
        }
      });
  }

  private cargarEventos(): void {
    this.cargando.set(true);
    this.service
      .getEventos()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (eventos) => {
          this.eventos.set(eventos);
          this.cargando.set(false);
        },
        error: (err) => {
          console.error('Error al cargar los eventos desde el backend:', err);
          this.cargando.set(false);
        }
      });
  }

  onUsuarioCambiado(event: Event): void {
    const selectEl = event.target as HTMLSelectElement;
    const selectedUuid = selectEl.value;
    const usuario = this.usuarios().find(u => u.uuid === selectedUuid);
    if (usuario) {
      this.usuarioSeleccionado.set(usuario);
      this.service.setUsuarioActual(usuario);
      this.cargarEventos();
    }
  }

  getHabilidadStr(usuario: UsuarioSesion): string {
    if (!usuario.habilidades || Object.keys(usuario.habilidades).length === 0) {
      return 'Sin nivel';
    }
    return Object.entries(usuario.habilidades)
      .map(([deporte, nivel]) => `${deporte}: ${nivel}`)
      .join(', ');
  }

  getHabilidadesArray(usuario: UsuarioSesion | null): { deporte: string, nivel: string }[] {
    if (!usuario || !usuario.habilidades) return [];
    return Object.entries(usuario.habilidades).map(([deporte, nivel]) => ({
      deporte,
      nivel,
    }));
  }

  abrirEvento(evento: EventoDetalle): void {
    const ref = this.modalService.open(EventoModalComponent, {
      size: 'lg',
      centered: true,
      scrollable: true,
    });
    ref.componentInstance.eventoUuid = evento.uuid;
  }

  tipoIngresoLabel(tipo: string): string {
    const map: Record<string, string> = {
      ABIERTO: 'Abierto',
      CON_CONFIRMACION: 'Con confirmación',
      CERRADO: 'Cerrado',
    };
    return map[tipo] ?? tipo;
  }

  tipoIngresoBadge(tipo: string): string {
    const map: Record<string, string> = {
      ABIERTO: 'success',
      CON_CONFIRMACION: 'warning',
      CERRADO: 'secondary',
    };
    return map[tipo] ?? 'secondary';
  }
}