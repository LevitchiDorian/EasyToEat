import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPromotion } from '../promotion.model';
import { PromotionService } from '../service/promotion.service';

const promotionResolve = (route: ActivatedRouteSnapshot): Observable<null | IPromotion> => {
  const id = route.params.id;
  if (id) {
    return inject(PromotionService)
      .find(id)
      .pipe(
        mergeMap((promotion: HttpResponse<IPromotion>) => {
          if (promotion.body) {
            return of(promotion.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default promotionResolve;
