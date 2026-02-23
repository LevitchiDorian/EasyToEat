import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IWaitingList, NewWaitingList } from '../waiting-list.model';

export type PartialUpdateWaitingList = Partial<IWaitingList> & Pick<IWaitingList, 'id'>;

type RestOf<T extends IWaitingList | NewWaitingList> = Omit<T, 'requestedDate' | 'expiresAt' | 'createdAt'> & {
  requestedDate?: string | null;
  expiresAt?: string | null;
  createdAt?: string | null;
};

export type RestWaitingList = RestOf<IWaitingList>;

export type NewRestWaitingList = RestOf<NewWaitingList>;

export type PartialUpdateRestWaitingList = RestOf<PartialUpdateWaitingList>;

export type EntityResponseType = HttpResponse<IWaitingList>;
export type EntityArrayResponseType = HttpResponse<IWaitingList[]>;

@Injectable({ providedIn: 'root' })
export class WaitingListService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/waiting-lists');

  create(waitingList: NewWaitingList): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(waitingList);
    return this.http
      .post<RestWaitingList>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(waitingList: IWaitingList): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(waitingList);
    return this.http
      .put<RestWaitingList>(`${this.resourceUrl}/${this.getWaitingListIdentifier(waitingList)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(waitingList: PartialUpdateWaitingList): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(waitingList);
    return this.http
      .patch<RestWaitingList>(`${this.resourceUrl}/${this.getWaitingListIdentifier(waitingList)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestWaitingList>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestWaitingList[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getWaitingListIdentifier(waitingList: Pick<IWaitingList, 'id'>): number {
    return waitingList.id;
  }

  compareWaitingList(o1: Pick<IWaitingList, 'id'> | null, o2: Pick<IWaitingList, 'id'> | null): boolean {
    return o1 && o2 ? this.getWaitingListIdentifier(o1) === this.getWaitingListIdentifier(o2) : o1 === o2;
  }

  addWaitingListToCollectionIfMissing<Type extends Pick<IWaitingList, 'id'>>(
    waitingListCollection: Type[],
    ...waitingListsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const waitingLists: Type[] = waitingListsToCheck.filter(isPresent);
    if (waitingLists.length > 0) {
      const waitingListCollectionIdentifiers = waitingListCollection.map(waitingListItem => this.getWaitingListIdentifier(waitingListItem));
      const waitingListsToAdd = waitingLists.filter(waitingListItem => {
        const waitingListIdentifier = this.getWaitingListIdentifier(waitingListItem);
        if (waitingListCollectionIdentifiers.includes(waitingListIdentifier)) {
          return false;
        }
        waitingListCollectionIdentifiers.push(waitingListIdentifier);
        return true;
      });
      return [...waitingListsToAdd, ...waitingListCollection];
    }
    return waitingListCollection;
  }

  protected convertDateFromClient<T extends IWaitingList | NewWaitingList | PartialUpdateWaitingList>(waitingList: T): RestOf<T> {
    return {
      ...waitingList,
      requestedDate: waitingList.requestedDate?.format(DATE_FORMAT) ?? null,
      expiresAt: waitingList.expiresAt?.toJSON() ?? null,
      createdAt: waitingList.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restWaitingList: RestWaitingList): IWaitingList {
    return {
      ...restWaitingList,
      requestedDate: restWaitingList.requestedDate ? dayjs(restWaitingList.requestedDate) : undefined,
      expiresAt: restWaitingList.expiresAt ? dayjs(restWaitingList.expiresAt) : undefined,
      createdAt: restWaitingList.createdAt ? dayjs(restWaitingList.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestWaitingList>): HttpResponse<IWaitingList> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestWaitingList[]>): HttpResponse<IWaitingList[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
