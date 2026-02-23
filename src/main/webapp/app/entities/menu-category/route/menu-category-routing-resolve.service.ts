import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMenuCategory } from '../menu-category.model';
import { MenuCategoryService } from '../service/menu-category.service';

const menuCategoryResolve = (route: ActivatedRouteSnapshot): Observable<null | IMenuCategory> => {
  const id = route.params.id;
  if (id) {
    return inject(MenuCategoryService)
      .find(id)
      .pipe(
        mergeMap((menuCategory: HttpResponse<IMenuCategory>) => {
          if (menuCategory.body) {
            return of(menuCategory.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default menuCategoryResolve;
