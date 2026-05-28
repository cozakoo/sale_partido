import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { LocalSearchResult } from '../models/local-search-result';

@Injectable({ providedIn: 'root' })
export class BusquedaLocalesService {

  private readonly mockLocales: LocalSearchResult[] = [
    {
      uuid: 'a1b2c3d4-e29b-41d4-a716-446655440001',
      nombre: 'Club Deportivo Centro',
      ubicacion: 'Centro',
      deportes: ['Fútbol', 'Tenis', 'Pádel'],
      telefono: '+54 280 411-1001',
      descripcion: 'Complejo deportivo céntrico con canchas de césped sintético y polvo de ladrillo.',
      horario: '08:00 - 23:00',
    },
    {
      uuid: 'a1b2c3d4-e29b-41d4-a716-446655440002',
      nombre: 'Estadio del Sur',
      ubicacion: 'Sur',
      deportes: ['Fútbol', 'Básquet'],
      telefono: '+54 280 411-1002',
      descripcion: 'Canchas de fútbol 11 y básquet cubierto. Estacionamiento amplio.',
      horario: '09:00 - 22:00',
    },
    {
      uuid: 'a1b2c3d4-e29b-41d4-a716-446655440003',
      nombre: 'La Canchita',
      ubicacion: 'Norte',
      deportes: ['Fútbol', 'Pádel', 'Voley'],
      telefono: '+54 280 411-1003',
      descripcion: 'Espacio familiar con canchas de fútbol 5, pádel y voley playa.',
      horario: '10:00 - 22:00',
    },
    {
      uuid: 'a1b2c3d4-e29b-41d4-a716-446655440004',
      nombre: 'Madryn Tenis Club',
      ubicacion: 'Centro',
      deportes: ['Tenis', 'Pádel'],
      telefono: '+54 280 411-1004',
      descripcion: 'Club especializado en tenis y pádel con profesorado incluido.',
      horario: '07:00 - 22:00',
    },
    {
      uuid: 'a1b2c3d4-e29b-41d4-a716-446655440005',
      nombre: 'Polideportivo Municipal',
      ubicacion: 'Oeste',
      deportes: ['Básquet', 'Voley', 'Fútbol', 'Hockey'],
      telefono: '+54 280 411-1005',
      descripcion: 'Polideportivo municipal con instalaciones para múltiples disciplinas.',
      horario: '08:00 - 21:00',
    },
    {
      uuid: 'a1b2c3d4-e29b-41d4-a716-446655440006',
      nombre: 'Sports Center',
      ubicacion: 'Norte',
      deportes: ['Básquet', 'Voley', 'Tenis'],
      telefono: '+54 280 411-1006',
      descripcion: 'Centro deportivo techado con canchas de última generación.',
      horario: '08:00 - 00:00',
    },
    {
      uuid: 'a1b2c3d4-e29b-41d4-a716-446655440007',
      nombre: 'La Cancha de al Lado',
      ubicacion: 'Sur',
      deportes: ['Fútbol', 'Pádel'],
      telefono: '+54 280 411-1007',
      descripcion: 'Canchas de fútbol 5 y pádel con iluminación LED. Confitería.',
      horario: '10:00 - 23:00',
    },
    {
      uuid: 'a1b2c3d4-e29b-41d4-a716-446655440008',
      nombre: 'Green Park',
      ubicacion: 'Oeste',
      deportes: ['Fútbol', 'Hockey', 'Tenis'],
      telefono: '+54 280 411-1008',
      descripcion: 'Complejo con amplios espacios verdes, canchas de hockey y tenis.',
      horario: '08:00 - 20:00',
    },
  ];

  private readonly ubicaciones: string[] = ['Centro', 'Norte', 'Sur', 'Oeste'];

  private readonly deportes: string[] = ['Fútbol', 'Básquet', 'Tenis', 'Pádel', 'Voley', 'Hockey'];

  getUbicaciones(): string[] {
    return this.ubicaciones;
  }

  getDeportes(): string[] {
    return this.deportes;
  }

  buscar(filtros: { texto?: string; ubicacion?: string; deporte?: string }): Observable<LocalSearchResult[]> {
    let resultados = [...this.mockLocales];

    if (filtros.texto?.trim()) {
      const termino = filtros.texto.toLowerCase().trim();
      resultados = resultados.filter(
        l =>
          l.nombre.toLowerCase().includes(termino) ||
          l.descripcion.toLowerCase().includes(termino) ||
          l.ubicacion.toLowerCase().includes(termino),
      );
    }

    if (filtros.ubicacion) {
      resultados = resultados.filter(l => l.ubicacion === filtros.ubicacion);
    }

    if (filtros.deporte) {
      resultados = resultados.filter(l => l.deportes.includes(filtros.deporte!));
    }

    return of(resultados);
  }

  obtenerPorUuid(uuid: string): LocalSearchResult | undefined {
    return this.mockLocales.find(l => l.uuid === uuid);
  }
}
