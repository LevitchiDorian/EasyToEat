import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILocationHours } from '../location-hours.model';
import { LocationHoursService } from '../service/location-hours.service';

const locationHoursResolve = (route: ActivatedRouteSnapshot): Observable<null | ILocationHours> => {
  const id = route.params.id;
  if (id) {
    return inject(LocationHoursService)
      .find(id)
      .pipe(
        mergeMap((locationHours: HttpResponse<ILocationHours>) => {
          if (locationHours.body) {
            return of(locationHours.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default locationHoursResolve;
