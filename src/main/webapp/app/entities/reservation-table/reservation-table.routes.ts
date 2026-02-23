import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ReservationTableResolve from './route/reservation-table-routing-resolve.service';

const reservationTableRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/reservation-table.component').then(m => m.ReservationTableComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/reservation-table-detail.component').then(m => m.ReservationTableDetailComponent),
    resolve: {
      reservationTable: ReservationTableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/reservation-table-update.component').then(m => m.ReservationTableUpdateComponent),
    resolve: {
      reservationTable: ReservationTableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/reservation-table-update.component').then(m => m.ReservationTableUpdateComponent),
    resolve: {
      reservationTable: ReservationTableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default reservationTableRoute;
