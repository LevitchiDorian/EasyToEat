import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RestaurantTableResolve from './route/restaurant-table-routing-resolve.service';

const restaurantTableRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/restaurant-table.component').then(m => m.RestaurantTableComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/restaurant-table-detail.component').then(m => m.RestaurantTableDetailComponent),
    resolve: {
      restaurantTable: RestaurantTableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/restaurant-table-update.component').then(m => m.RestaurantTableUpdateComponent),
    resolve: {
      restaurantTable: RestaurantTableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/restaurant-table-update.component').then(m => m.RestaurantTableUpdateComponent),
    resolve: {
      restaurantTable: RestaurantTableResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default restaurantTableRoute;
