import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRestaurantTable, NewRestaurantTable } from '../restaurant-table.model';

export type PartialUpdateRestaurantTable = Partial<IRestaurantTable> & Pick<IRestaurantTable, 'id'>;

export type EntityResponseType = HttpResponse<IRestaurantTable>;
export type EntityArrayResponseType = HttpResponse<IRestaurantTable[]>;

@Injectable({ providedIn: 'root' })
export class RestaurantTableService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/restaurant-tables');

  create(restaurantTable: NewRestaurantTable): Observable<EntityResponseType> {
    return this.http.post<IRestaurantTable>(this.resourceUrl, restaurantTable, { observe: 'response' });
  }

  update(restaurantTable: IRestaurantTable): Observable<EntityResponseType> {
    return this.http.put<IRestaurantTable>(`${this.resourceUrl}/${this.getRestaurantTableIdentifier(restaurantTable)}`, restaurantTable, {
      observe: 'response',
    });
  }

  partialUpdate(restaurantTable: PartialUpdateRestaurantTable): Observable<EntityResponseType> {
    return this.http.patch<IRestaurantTable>(`${this.resourceUrl}/${this.getRestaurantTableIdentifier(restaurantTable)}`, restaurantTable, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IRestaurantTable>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IRestaurantTable[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRestaurantTableIdentifier(restaurantTable: Pick<IRestaurantTable, 'id'>): number {
    return restaurantTable.id;
  }

  compareRestaurantTable(o1: Pick<IRestaurantTable, 'id'> | null, o2: Pick<IRestaurantTable, 'id'> | null): boolean {
    return o1 && o2 ? this.getRestaurantTableIdentifier(o1) === this.getRestaurantTableIdentifier(o2) : o1 === o2;
  }

  addRestaurantTableToCollectionIfMissing<Type extends Pick<IRestaurantTable, 'id'>>(
    restaurantTableCollection: Type[],
    ...restaurantTablesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const restaurantTables: Type[] = restaurantTablesToCheck.filter(isPresent);
    if (restaurantTables.length > 0) {
      const restaurantTableCollectionIdentifiers = restaurantTableCollection.map(restaurantTableItem =>
        this.getRestaurantTableIdentifier(restaurantTableItem),
      );
      const restaurantTablesToAdd = restaurantTables.filter(restaurantTableItem => {
        const restaurantTableIdentifier = this.getRestaurantTableIdentifier(restaurantTableItem);
        if (restaurantTableCollectionIdentifiers.includes(restaurantTableIdentifier)) {
          return false;
        }
        restaurantTableCollectionIdentifiers.push(restaurantTableIdentifier);
        return true;
      });
      return [...restaurantTablesToAdd, ...restaurantTableCollection];
    }
    return restaurantTableCollection;
  }
}
