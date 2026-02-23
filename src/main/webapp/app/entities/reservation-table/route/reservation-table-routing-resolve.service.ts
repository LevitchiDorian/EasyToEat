import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IReservationTable } from '../reservation-table.model';
import { ReservationTableService } from '../service/reservation-table.service';

const reservationTableResolve = (route: ActivatedRouteSnapshot): Observable<null | IReservationTable> => {
  const id = route.params.id;
  if (id) {
    return inject(ReservationTableService)
      .find(id)
      .pipe(
        mergeMap((reservationTable: HttpResponse<IReservationTable>) => {
          if (reservationTable.body) {
            return of(reservationTable.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default reservationTableResolve;
