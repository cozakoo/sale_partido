import { Routes } from '@angular/router';

export const LOCALES_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/locales-list.page/locales-list.page').then(m => m.LocalesListPage)
  },
  // específicas ANTES de :uuid
  {
    path: ':uuid/configuraciones-horarios',
    loadComponent: () =>
      import('./pages/configuracion-disponibilidad.page/configuracion-disponibilidad.page').then(
        m => m.ConfiguracionDisponibilidadPage
      ),
  },
  {
    path: ':uuid/calendario',
    loadComponent: () =>
      import('./pages/calendario-disponibilidad.page/calendario-disponibilidad.page').then(
        m => m.CalendarioDisponibilidadPage
      ),
  },
  // :uuid al final
  {
    path: ':uuid',
    loadComponent: () =>
      import('./pages/local-detail.page/local-detail.page').then(m => m.LocalDetailPage)
  },
];