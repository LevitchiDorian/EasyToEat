import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import PromotionResolve from './route/promotion-routing-resolve.service';

const promotionRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/promotion.component').then(m => m.PromotionComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/promotion-detail.component').then(m => m.PromotionDetailComponent),
    resolve: {
      promotion: PromotionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/promotion-update.component').then(m => m.PromotionUpdateComponent),
    resolve: {
      promotion: PromotionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/promotion-update.component').then(m => m.PromotionUpdateComponent),
    resolve: {
      promotion: PromotionResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default promotionRoute;
