import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RestaurantOrderResolve from './route/restaurant-order-routing-resolve.service';

const restaurantOrderRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/restaurant-order.component').then(m => m.RestaurantOrderComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/restaurant-order-detail.component').then(m => m.RestaurantOrderDetailComponent),
    resolve: {
      restaurantOrder: RestaurantOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/restaurant-order-update.component').then(m => m.RestaurantOrderUpdateComponent),
    resolve: {
      restaurantOrder: RestaurantOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/restaurant-order-update.component').then(m => m.RestaurantOrderUpdateComponent),
    resolve: {
      restaurantOrder: RestaurantOrderResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default restaurantOrderRoute;
