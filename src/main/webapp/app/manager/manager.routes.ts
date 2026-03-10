import { Routes } from '@angular/router';
import { ManagerRouteAccessService } from 'app/core/auth/manager-route-access.service';

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./manager-layout/manager-layout.component'),
    canActivate: [ManagerRouteAccessService],
    children: [
      {
        path: 'floor-plan',
        loadComponent: () => import('./manager-floor-plan/manager-floor-plan.component'),
        title: 'Harta sălii — Manager',
      },
      {
        path: 'rezervari',
        loadComponent: () => import('./manager-reservations/manager-reservations.component'),
        title: 'Rezervări — Manager',
      },
      {
        path: 'personal',
        loadComponent: () => import('./manager-staff/manager-staff.component'),
        title: 'Personal — Manager',
      },
      {
        path: 'meniu',
        loadComponent: () => import('./manager-menu/manager-menu.component'),
        title: 'Meniu — Manager',
      },
      {
        path: 'rapoarte',
        loadComponent: () => import('./manager-reports/manager-reports.component'),
        title: 'Rapoarte — Manager',
      },
      {
        path: '',
        redirectTo: 'floor-plan',
        pathMatch: 'full',
      },
    ],
  },
];

export default routes;
