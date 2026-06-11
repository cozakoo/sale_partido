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
import { EventoDetalle } from '../../models/evento-detalle.model';
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

  ngOnInit(): void {
    this.service
      .getEventos()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((eventos) => {
        this.eventos.set(eventos);
        this.cargando.set(false);
      });
  }

  abrirEvento(evento: EventoDetalle): void {
    const ref = this.modalService.open(EventoModalComponent, {
      size: 'lg',
      centered: true,
      scrollable: true,
    });
    ref.componentInstance.eventoId = evento.id;
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