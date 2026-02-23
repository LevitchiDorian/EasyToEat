import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import LocationMenuItemOverrideResolve from './route/location-menu-item-override-routing-resolve.service';

const locationMenuItemOverrideRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/location-menu-item-override.component').then(m => m.LocationMenuItemOverrideComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () =>
      import('./detail/location-menu-item-override-detail.component').then(m => m.LocationMenuItemOverrideDetailComponent),
    resolve: {
      locationMenuItemOverride: LocationMenuItemOverrideResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () =>
      import('./update/location-menu-item-override-update.component').then(m => m.LocationMenuItemOverrideUpdateComponent),
    resolve: {
      locationMenuItemOverride: LocationMenuItemOverrideResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () =>
      import('./update/location-menu-item-override-update.component').then(m => m.LocationMenuItemOverrideUpdateComponent),
    resolve: {
      locationMenuItemOverride: LocationMenuItemOverrideResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default locationMenuItemOverrideRoute;
