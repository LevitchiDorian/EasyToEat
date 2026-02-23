import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMenuItemAllergen } from '../menu-item-allergen.model';
import { MenuItemAllergenService } from '../service/menu-item-allergen.service';

const menuItemAllergenResolve = (route: ActivatedRouteSnapshot): Observable<null | IMenuItemAllergen> => {
  const id = route.params.id;
  if (id) {
    return inject(MenuItemAllergenService)
      .find(id)
      .pipe(
        mergeMap((menuItemAllergen: HttpResponse<IMenuItemAllergen>) => {
          if (menuItemAllergen.body) {
            return of(menuItemAllergen.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default menuItemAllergenResolve;
