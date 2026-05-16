import { Component, Input, Output, EventEmitter, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Turno } from '../../models/calendario';

@Component({
  selector: 'app-turno-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './turno-item.component.html',   // o template inline
  styleUrl: './turno-item.component.scss',
  encapsulation: ViewEncapsulation.None  // ← agregá esto

})
export class TurnoItemComponent {
  @Input() turno!: Turno;
  @Input() abierto = false;
  @Output() toggleDetalle = new EventEmitter<void>();
}