import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRestaurantTable } from '../restaurant-table.model';
import { RestaurantTableService } from '../service/restaurant-table.service';

const restaurantTableResolve = (route: ActivatedRouteSnapshot): Observable<null | IRestaurantTable> => {
  const id = route.params.id;
  if (id) {
    return inject(RestaurantTableService)
      .find(id)
      .pipe(
        mergeMap((restaurantTable: HttpResponse<IRestaurantTable>) => {
          if (restaurantTable.body) {
            return of(restaurantTable.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default restaurantTableResolve;
