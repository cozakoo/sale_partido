import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'configuracion/horarios',
    loadComponent: () =>
      import('./features/schedule-config/components/schedule-config/schedule-config').then(
        m => m.ScheduleConfigComponent
      ),
  },
  { path: '', redirectTo: 'configuracion/horarios', pathMatch: 'full' },
];