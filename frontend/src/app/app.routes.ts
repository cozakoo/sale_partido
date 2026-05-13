import { Routes } from '@angular/router';
import { CANCHAS_ROUTES } from './features/canchas/routes';
import { EVENTOS_ROUTES } from './features/eventos/routes';

export const routes: Routes = [
  {
    path: 'canchas',
    children: CANCHAS_ROUTES
  },
  {
    path: 'eventos',
    children: EVENTOS_ROUTES
  }
];