import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILocationMenuItemOverride } from '../location-menu-item-override.model';
import { LocationMenuItemOverrideService } from '../service/location-menu-item-override.service';

const locationMenuItemOverrideResolve = (route: ActivatedRouteSnapshot): Observable<null | ILocationMenuItemOverride> => {
  const id = route.params.id;
  if (id) {
    return inject(LocationMenuItemOverrideService)
      .find(id)
      .pipe(
        mergeMap((locationMenuItemOverride: HttpResponse<ILocationMenuItemOverride>) => {
          if (locationMenuItemOverride.body) {
            return of(locationMenuItemOverride.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default locationMenuItemOverrideResolve;
