import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDiningRoom } from '../dining-room.model';
import { DiningRoomService } from '../service/dining-room.service';

const diningRoomResolve = (route: ActivatedRouteSnapshot): Observable<null | IDiningRoom> => {
  const id = route.params.id;
  if (id) {
    return inject(DiningRoomService)
      .find(id)
      .pipe(
        mergeMap((diningRoom: HttpResponse<IDiningRoom>) => {
          if (diningRoom.body) {
            return of(diningRoom.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default diningRoomResolve;
