import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMenuItemOption } from '../menu-item-option.model';
import { MenuItemOptionService } from '../service/menu-item-option.service';

const menuItemOptionResolve = (route: ActivatedRouteSnapshot): Observable<null | IMenuItemOption> => {
  const id = route.params.id;
  if (id) {
    return inject(MenuItemOptionService)
      .find(id)
      .pipe(
        mergeMap((menuItemOption: HttpResponse<IMenuItemOption>) => {
          if (menuItemOption.body) {
            return of(menuItemOption.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default menuItemOptionResolve;
