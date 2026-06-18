import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { EventoDetalle } from '../../models/evento-detalle';

@Component({
  selector: 'app-evento-info',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './evento-info.component.html',
  styleUrl: './evento-info.component.scss',
})
export class EventoInfoComponent {
  evento = input.required<EventoDetalle>();
}