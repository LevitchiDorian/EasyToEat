import { Routes } from '@angular/router';

import { Authority } from 'app/config/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./chef-layout/chef-layout.component'),
    canActivate: [UserRouteAccessService],
    data: { authorities: [Authority.CHEF, Authority.ADMIN, Authority.MANAGER] },
    children: [
      {
        path: '',
        loadComponent: () => import('./chef-dashboard/chef-dashboard.component'),
        title: 'Bucătărie — EasyToEat',
      },
      {
        path: 'istoric',
        loadComponent: () => import('./chef-history/chef-history.component'),
        title: 'Istoric comenzi — EasyToEat',
      },
      {
        path: 'raport',
        loadComponent: () => import('./chef-reports/chef-reports.component'),
        title: 'Raport — EasyToEat',
      },
    ],
  },
];

export default routes;
