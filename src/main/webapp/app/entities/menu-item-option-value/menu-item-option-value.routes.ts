import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MenuItemOptionValueResolve from './route/menu-item-option-value-routing-resolve.service';

const menuItemOptionValueRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/menu-item-option-value.component').then(m => m.MenuItemOptionValueComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/menu-item-option-value-detail.component').then(m => m.MenuItemOptionValueDetailComponent),
    resolve: {
      menuItemOptionValue: MenuItemOptionValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/menu-item-option-value-update.component').then(m => m.MenuItemOptionValueUpdateComponent),
    resolve: {
      menuItemOptionValue: MenuItemOptionValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/menu-item-option-value-update.component').then(m => m.MenuItemOptionValueUpdateComponent),
    resolve: {
      menuItemOptionValue: MenuItemOptionValueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default menuItemOptionValueRoute;
