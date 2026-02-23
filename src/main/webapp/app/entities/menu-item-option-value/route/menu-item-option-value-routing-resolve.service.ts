import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMenuItemOptionValue } from '../menu-item-option-value.model';
import { MenuItemOptionValueService } from '../service/menu-item-option-value.service';

const menuItemOptionValueResolve = (route: ActivatedRouteSnapshot): Observable<null | IMenuItemOptionValue> => {
  const id = route.params.id;
  if (id) {
    return inject(MenuItemOptionValueService)
      .find(id)
      .pipe(
        mergeMap((menuItemOptionValue: HttpResponse<IMenuItemOptionValue>) => {
          if (menuItemOptionValue.body) {
            return of(menuItemOptionValue.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default menuItemOptionValueResolve;
