import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import MenuCategoryResolve from './route/menu-category-routing-resolve.service';

const menuCategoryRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/menu-category.component').then(m => m.MenuCategoryComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/menu-category-detail.component').then(m => m.MenuCategoryDetailComponent),
    resolve: {
      menuCategory: MenuCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/menu-category-update.component').then(m => m.MenuCategoryUpdateComponent),
    resolve: {
      menuCategory: MenuCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/menu-category-update.component').then(m => m.MenuCategoryUpdateComponent),
    resolve: {
      menuCategory: MenuCategoryResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default menuCategoryRoute;
