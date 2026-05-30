import { Routes } from '@angular/router';
import { LOCALES_ROUTES } from './features/locales/routes';
import { EVENTOS_ROUTES } from './features/eventos/routes';

export const routes: Routes = [
  {
    path: 'locales',
    children: LOCALES_ROUTES
  },
  {
    path: 'eventos',
    children: EVENTOS_ROUTES
  },
  {
    path: '',
    redirectTo: 'locales',
    pathMatch: 'full'
  }
];