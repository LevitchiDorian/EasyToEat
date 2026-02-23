import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMenuItem } from '../menu-item.model';
import { MenuItemService } from '../service/menu-item.service';

const menuItemResolve = (route: ActivatedRouteSnapshot): Observable<null | IMenuItem> => {
  const id = route.params.id;
  if (id) {
    return inject(MenuItemService)
      .find(id)
      .pipe(
        mergeMap((menuItem: HttpResponse<IMenuItem>) => {
          if (menuItem.body) {
            return of(menuItem.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default menuItemResolve;
