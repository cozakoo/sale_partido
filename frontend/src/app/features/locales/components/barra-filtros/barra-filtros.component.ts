import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EstadoTurno, FilterSelection } from '../../models/calendario';

@Component({
  selector: 'app-barra-filtros',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './barra-filtros.component.html',
  styleUrl: './barra-filtros.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BarraFiltrosComponent {
  @Input({ required: true }) opciones!: {
    estados: EstadoTurno[];
    espacios: string[];
    deportes: string[];
  };

  @Input({ required: true }) seleccion!: FilterSelection;

  @Output() seleccionChange = new EventEmitter<FilterSelection>();
  @Output() limpiar = new EventEmitter<void>();

  toggleEstado(valor: EstadoTurno) {
    const nuevos = this.seleccion.estados.includes(valor)
      ? this.seleccion.estados.filter(e => e !== valor)
      : [...this.seleccion.estados, valor];

    this.emitir({ ...this.seleccion, estados: nuevos });
  }

  toggleEspacio(valor: string) {
    const nuevos = this.seleccion.espacios.includes(valor)
      ? this.seleccion.espacios.filter(e => e !== valor)
      : [...this.seleccion.espacios, valor];

    this.emitir({ ...this.seleccion, espacios: nuevos });
  }

  toggleDeporte(valor: string) {
    const nuevos = this.seleccion.deportes.includes(valor)
      ? this.seleccion.deportes.filter(d => d !== valor)
      : [...this.seleccion.deportes, valor];

    this.emitir({ ...this.seleccion, deportes: nuevos });
  }

  private emitir(seleccion: FilterSelection) {
    this.seleccionChange.emit(seleccion);
  }
}
