import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IReservationTable, NewReservationTable } from '../reservation-table.model';

export type PartialUpdateReservationTable = Partial<IReservationTable> & Pick<IReservationTable, 'id'>;

type RestOf<T extends IReservationTable | NewReservationTable> = Omit<T, 'assignedAt'> & {
  assignedAt?: string | null;
};

export type RestReservationTable = RestOf<IReservationTable>;

export type NewRestReservationTable = RestOf<NewReservationTable>;

export type PartialUpdateRestReservationTable = RestOf<PartialUpdateReservationTable>;

export type EntityResponseType = HttpResponse<IReservationTable>;
export type EntityArrayResponseType = HttpResponse<IReservationTable[]>;

@Injectable({ providedIn: 'root' })
export class ReservationTableService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/reservation-tables');

  create(reservationTable: NewReservationTable): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservationTable);
    return this.http
      .post<RestReservationTable>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(reservationTable: IReservationTable): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservationTable);
    return this.http
      .put<RestReservationTable>(`${this.resourceUrl}/${this.getReservationTableIdentifier(reservationTable)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(reservationTable: PartialUpdateReservationTable): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(reservationTable);
    return this.http
      .patch<RestReservationTable>(`${this.resourceUrl}/${this.getReservationTableIdentifier(reservationTable)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestReservationTable>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestReservationTable[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getReservationTableIdentifier(reservationTable: Pick<IReservationTable, 'id'>): number {
    return reservationTable.id;
  }

  compareReservationTable(o1: Pick<IReservationTable, 'id'> | null, o2: Pick<IReservationTable, 'id'> | null): boolean {
    return o1 && o2 ? this.getReservationTableIdentifier(o1) === this.getReservationTableIdentifier(o2) : o1 === o2;
  }

  addReservationTableToCollectionIfMissing<Type extends Pick<IReservationTable, 'id'>>(
    reservationTableCollection: Type[],
    ...reservationTablesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const reservationTables: Type[] = reservationTablesToCheck.filter(isPresent);
    if (reservationTables.length > 0) {
      const reservationTableCollectionIdentifiers = reservationTableCollection.map(reservationTableItem =>
        this.getReservationTableIdentifier(reservationTableItem),
      );
      const reservationTablesToAdd = reservationTables.filter(reservationTableItem => {
        const reservationTableIdentifier = this.getReservationTableIdentifier(reservationTableItem);
        if (reservationTableCollectionIdentifiers.includes(reservationTableIdentifier)) {
          return false;
        }
        reservationTableCollectionIdentifiers.push(reservationTableIdentifier);
        return true;
      });
      return [...reservationTablesToAdd, ...reservationTableCollection];
    }
    return reservationTableCollection;
  }

  protected convertDateFromClient<T extends IReservationTable | NewReservationTable | PartialUpdateReservationTable>(
    reservationTable: T,
  ): RestOf<T> {
    return {
      ...reservationTable,
      assignedAt: reservationTable.assignedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restReservationTable: RestReservationTable): IReservationTable {
    return {
      ...restReservationTable,
      assignedAt: restReservationTable.assignedAt ? dayjs(restReservationTable.assignedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestReservationTable>): HttpResponse<IReservationTable> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestReservationTable[]>): HttpResponse<IReservationTable[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
