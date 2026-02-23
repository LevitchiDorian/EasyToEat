import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MenuItemAllergenResolve from './route/menu-item-allergen-routing-resolve.service';

const menuItemAllergenRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/menu-item-allergen.component').then(m => m.MenuItemAllergenComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/menu-item-allergen-detail.component').then(m => m.MenuItemAllergenDetailComponent),
    resolve: {
      menuItemAllergen: MenuItemAllergenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/menu-item-allergen-update.component').then(m => m.MenuItemAllergenUpdateComponent),
    resolve: {
      menuItemAllergen: MenuItemAllergenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/menu-item-allergen-update.component').then(m => m.MenuItemAllergenUpdateComponent),
    resolve: {
      menuItemAllergen: MenuItemAllergenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default menuItemAllergenRoute;
