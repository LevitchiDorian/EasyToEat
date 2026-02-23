import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOrderItemOptionSelection } from '../order-item-option-selection.model';
import { OrderItemOptionSelectionService } from '../service/order-item-option-selection.service';

const orderItemOptionSelectionResolve = (route: ActivatedRouteSnapshot): Observable<null | IOrderItemOptionSelection> => {
  const id = route.params.id;
  if (id) {
    return inject(OrderItemOptionSelectionService)
      .find(id)
      .pipe(
        mergeMap((orderItemOptionSelection: HttpResponse<IOrderItemOptionSelection>) => {
          if (orderItemOptionSelection.body) {
            return of(orderItemOptionSelection.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default orderItemOptionSelectionResolve;
