import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { Participante } from '../../models/participante';

@Component({
  selector: 'app-participantes-lista',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './participantes-lista-component.html',
  styleUrl: './participantes-lista-component.scss',

})
export class ParticipantesListaComponent {
  participantes = input.required<Participante[]>();
}