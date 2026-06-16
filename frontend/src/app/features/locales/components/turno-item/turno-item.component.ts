import { Component, input, output, ChangeDetectionStrategy } from '@angular/core';
import { Turno } from '../../models/calendario';

@Component({
  selector: 'app-turno-item',
  standalone: true,
  templateUrl: './turno-item.component.html',
  styleUrl: './turno-item.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TurnoItemComponent {
  turno = input.required<Turno>();
  abierto = input(false);
  toggleDetalle = output<void>();
  reservar = output<void>();

}