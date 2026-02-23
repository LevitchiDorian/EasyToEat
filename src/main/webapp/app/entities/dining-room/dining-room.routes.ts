import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import DiningRoomResolve from './route/dining-room-routing-resolve.service';

const diningRoomRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/dining-room.component').then(m => m.DiningRoomComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/dining-room-detail.component').then(m => m.DiningRoomDetailComponent),
    resolve: {
      diningRoom: DiningRoomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/dining-room-update.component').then(m => m.DiningRoomUpdateComponent),
    resolve: {
      diningRoom: DiningRoomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/dining-room-update.component').then(m => m.DiningRoomUpdateComponent),
    resolve: {
      diningRoom: DiningRoomResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default diningRoomRoute;
