import { Routes } from '@angular/router';

export const EVENTOS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/eventos-lista.page/eventos-lista.page').then((m) => m.EventosListaPage),
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./pages/creacion-evento.page/creacion-evento.page').then((m) => m.CreacionEventoPage),
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./pages/evento-detalle.page/evento-detalle.page').then((m) => m.EventoDetallePage),
  }
];