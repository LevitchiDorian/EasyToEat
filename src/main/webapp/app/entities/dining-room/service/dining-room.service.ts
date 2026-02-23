import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDiningRoom, NewDiningRoom } from '../dining-room.model';

export type PartialUpdateDiningRoom = Partial<IDiningRoom> & Pick<IDiningRoom, 'id'>;

export type EntityResponseType = HttpResponse<IDiningRoom>;
export type EntityArrayResponseType = HttpResponse<IDiningRoom[]>;

@Injectable({ providedIn: 'root' })
export class DiningRoomService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/dining-rooms');

  create(diningRoom: NewDiningRoom): Observable<EntityResponseType> {
    return this.http.post<IDiningRoom>(this.resourceUrl, diningRoom, { observe: 'response' });
  }

  update(diningRoom: IDiningRoom): Observable<EntityResponseType> {
    return this.http.put<IDiningRoom>(`${this.resourceUrl}/${this.getDiningRoomIdentifier(diningRoom)}`, diningRoom, {
      observe: 'response',
    });
  }

  partialUpdate(diningRoom: PartialUpdateDiningRoom): Observable<EntityResponseType> {
    return this.http.patch<IDiningRoom>(`${this.resourceUrl}/${this.getDiningRoomIdentifier(diningRoom)}`, diningRoom, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDiningRoom>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDiningRoom[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDiningRoomIdentifier(diningRoom: Pick<IDiningRoom, 'id'>): number {
    return diningRoom.id;
  }

  compareDiningRoom(o1: Pick<IDiningRoom, 'id'> | null, o2: Pick<IDiningRoom, 'id'> | null): boolean {
    return o1 && o2 ? this.getDiningRoomIdentifier(o1) === this.getDiningRoomIdentifier(o2) : o1 === o2;
  }

  addDiningRoomToCollectionIfMissing<Type extends Pick<IDiningRoom, 'id'>>(
    diningRoomCollection: Type[],
    ...diningRoomsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const diningRooms: Type[] = diningRoomsToCheck.filter(isPresent);
    if (diningRooms.length > 0) {
      const diningRoomCollectionIdentifiers = diningRoomCollection.map(diningRoomItem => this.getDiningRoomIdentifier(diningRoomItem));
      const diningRoomsToAdd = diningRooms.filter(diningRoomItem => {
        const diningRoomIdentifier = this.getDiningRoomIdentifier(diningRoomItem);
        if (diningRoomCollectionIdentifiers.includes(diningRoomIdentifier)) {
          return false;
        }
        diningRoomCollectionIdentifiers.push(diningRoomIdentifier);
        return true;
      });
      return [...diningRoomsToAdd, ...diningRoomCollection];
    }
    return diningRoomCollection;
  }
}
