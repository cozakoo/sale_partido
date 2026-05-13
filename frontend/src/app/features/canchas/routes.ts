import { Routes } from '@angular/router';

export const CANCHAS_ROUTES: Routes = [
    {
    path: 'configuracion/horarios',
    loadComponent: () =>
      import('./pages/configuracion-disponibilidad.page').then(
        m => m.ConfiguracionDisponibilidadPage
      ),
  },
];
