import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MenuItemOptionResolve from './route/menu-item-option-routing-resolve.service';

const menuItemOptionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/menu-item-option.component').then(m => m.MenuItemOptionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/menu-item-option-detail.component').then(m => m.MenuItemOptionDetailComponent),
    resolve: {
      menuItemOption: MenuItemOptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/menu-item-option-update.component').then(m => m.MenuItemOptionUpdateComponent),
    resolve: {
      menuItemOption: MenuItemOptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/menu-item-option-update.component').then(m => m.MenuItemOptionUpdateComponent),
    resolve: {
      menuItemOption: MenuItemOptionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default menuItemOptionRoute;
