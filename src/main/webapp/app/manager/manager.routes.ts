import { Routes } from '@angular/router';
import { Authority } from 'app/config/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./manager-layout/manager-layout.component'),
    data: { authorities: [Authority.MANAGER, Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
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
