import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRestaurantOrder } from '../restaurant-order.model';
import { RestaurantOrderService } from '../service/restaurant-order.service';

const restaurantOrderResolve = (route: ActivatedRouteSnapshot): Observable<null | IRestaurantOrder> => {
  const id = route.params.id;
  if (id) {
    return inject(RestaurantOrderService)
      .find(id)
      .pipe(
        mergeMap((restaurantOrder: HttpResponse<IRestaurantOrder>) => {
          if (restaurantOrder.body) {
            return of(restaurantOrder.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default restaurantOrderResolve;
