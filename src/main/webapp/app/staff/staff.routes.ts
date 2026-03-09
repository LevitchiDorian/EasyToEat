import { Routes } from '@angular/router';
import { Authority } from 'app/config/authority.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

const routes: Routes = [
  {
    path: '',
    data: { authorities: [Authority.STAFF, Authority.MANAGER, Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    children: [
      {
        path: '',
        loadComponent: () => import('./staff-dashboard/staff-dashboard.component'),
        title: 'Panou Staff — EasyToEat',
      },
    ],
  },
];

export default routes;
