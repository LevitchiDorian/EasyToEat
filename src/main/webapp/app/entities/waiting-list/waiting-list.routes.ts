import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import WaitingListResolve from './route/waiting-list-routing-resolve.service';

const waitingListRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/waiting-list.component').then(m => m.WaitingListComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/waiting-list-detail.component').then(m => m.WaitingListDetailComponent),
    resolve: {
      waitingList: WaitingListResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/waiting-list-update.component').then(m => m.WaitingListUpdateComponent),
    resolve: {
      waitingList: WaitingListResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/waiting-list-update.component').then(m => m.WaitingListUpdateComponent),
    resolve: {
      waitingList: WaitingListResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default waitingListRoute;
