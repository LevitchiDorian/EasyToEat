import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import OrderItemOptionSelectionResolve from './route/order-item-option-selection-routing-resolve.service';

const orderItemOptionSelectionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/order-item-option-selection.component').then(m => m.OrderItemOptionSelectionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () =>
      import('./detail/order-item-option-selection-detail.component').then(m => m.OrderItemOptionSelectionDetailComponent),
    resolve: {
      orderItemOptionSelection: OrderItemOptionSelectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./update/order-item-option-selection-update.component').then(m => m.OrderItemOptionSelectionUpdateComponent),
    resolve: {
      orderItemOptionSelection: OrderItemOptionSelectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () =>
      import('./update/order-item-option-selection-update.component').then(m => m.OrderItemOptionSelectionUpdateComponent),
    resolve: {
      orderItemOptionSelection: OrderItemOptionSelectionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default orderItemOptionSelectionRoute;
