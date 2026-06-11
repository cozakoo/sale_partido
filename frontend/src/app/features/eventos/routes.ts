import { Routes } from '@angular/router';
import { EventoDetallePage } from './pages/evento-detalle.page/evento-detalle.page';
import { EventosListaPage } from './pages/eventos-lista.page/eventos-lista.page';

export const EVENTOS_ROUTES: Routes = [
  {
    path: '',
    component: EventosListaPage,
  },   {
    path: 'new',
    loadComponent: () =>
      import('../eventos/pages/creacion-evento.page/creacion-evento.page').then((m) => m.CreacionEventoPage),
  },
  
  {
    path: ':id',
    component: EventoDetallePage,
  }


  
];