import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IWaitingList } from '../waiting-list.model';
import { WaitingListService } from '../service/waiting-list.service';

const waitingListResolve = (route: ActivatedRouteSnapshot): Observable<null | IWaitingList> => {
  const id = route.params.id;
  if (id) {
    return inject(WaitingListService)
      .find(id)
      .pipe(
        mergeMap((waitingList: HttpResponse<IWaitingList>) => {
          if (waitingList.body) {
            return of(waitingList.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default waitingListResolve;
