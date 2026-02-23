import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MenuItemResolve from './route/menu-item-routing-resolve.service';

const menuItemRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/menu-item.component').then(m => m.MenuItemComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/menu-item-detail.component').then(m => m.MenuItemDetailComponent),
    resolve: {
      menuItem: MenuItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/menu-item-update.component').then(m => m.MenuItemUpdateComponent),
    resolve: {
      menuItem: MenuItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/menu-item-update.component').then(m => m.MenuItemUpdateComponent),
    resolve: {
      menuItem: MenuItemResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default menuItemRoute;
