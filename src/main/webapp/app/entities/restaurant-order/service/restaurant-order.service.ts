import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRestaurantOrder, NewRestaurantOrder } from '../restaurant-order.model';

export type PartialUpdateRestaurantOrder = Partial<IRestaurantOrder> & Pick<IRestaurantOrder, 'id'>;

type RestOf<T extends IRestaurantOrder | NewRestaurantOrder> = Omit<
  T,
  'scheduledFor' | 'estimatedReadyTime' | 'confirmedAt' | 'completedAt' | 'createdAt' | 'updatedAt'
> & {
  scheduledFor?: string | null;
  estimatedReadyTime?: string | null;
  confirmedAt?: string | null;
  completedAt?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type RestRestaurantOrder = RestOf<IRestaurantOrder>;

export type NewRestRestaurantOrder = RestOf<NewRestaurantOrder>;

export type PartialUpdateRestRestaurantOrder = RestOf<PartialUpdateRestaurantOrder>;

export type EntityResponseType = HttpResponse<IRestaurantOrder>;
export type EntityArrayResponseType = HttpResponse<IRestaurantOrder[]>;

@Injectable({ providedIn: 'root' })
export class RestaurantOrderService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/restaurant-orders');

  create(restaurantOrder: NewRestaurantOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(restaurantOrder);
    return this.http
      .post<RestRestaurantOrder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(restaurantOrder: IRestaurantOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(restaurantOrder);
    return this.http
      .put<RestRestaurantOrder>(`${this.resourceUrl}/${this.getRestaurantOrderIdentifier(restaurantOrder)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(restaurantOrder: PartialUpdateRestaurantOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(restaurantOrder);
    return this.http
      .patch<RestRestaurantOrder>(`${this.resourceUrl}/${this.getRestaurantOrderIdentifier(restaurantOrder)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRestaurantOrder>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRestaurantOrder[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRestaurantOrderIdentifier(restaurantOrder: Pick<IRestaurantOrder, 'id'>): number {
    return restaurantOrder.id;
  }

  compareRestaurantOrder(o1: Pick<IRestaurantOrder, 'id'> | null, o2: Pick<IRestaurantOrder, 'id'> | null): boolean {
    return o1 && o2 ? this.getRestaurantOrderIdentifier(o1) === this.getRestaurantOrderIdentifier(o2) : o1 === o2;
  }

  addRestaurantOrderToCollectionIfMissing<Type extends Pick<IRestaurantOrder, 'id'>>(
    restaurantOrderCollection: Type[],
    ...restaurantOrdersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const restaurantOrders: Type[] = restaurantOrdersToCheck.filter(isPresent);
    if (restaurantOrders.length > 0) {
      const restaurantOrderCollectionIdentifiers = restaurantOrderCollection.map(restaurantOrderItem =>
        this.getRestaurantOrderIdentifier(restaurantOrderItem),
      );
      const restaurantOrdersToAdd = restaurantOrders.filter(restaurantOrderItem => {
        const restaurantOrderIdentifier = this.getRestaurantOrderIdentifier(restaurantOrderItem);
        if (restaurantOrderCollectionIdentifiers.includes(restaurantOrderIdentifier)) {
          return false;
        }
        restaurantOrderCollectionIdentifiers.push(restaurantOrderIdentifier);
        return true;
      });
      return [...restaurantOrdersToAdd, ...restaurantOrderCollection];
    }
    return restaurantOrderCollection;
  }

  protected convertDateFromClient<T extends IRestaurantOrder | NewRestaurantOrder | PartialUpdateRestaurantOrder>(
    restaurantOrder: T,
  ): RestOf<T> {
    return {
      ...restaurantOrder,
      scheduledFor: restaurantOrder.scheduledFor?.toJSON() ?? null,
      estimatedReadyTime: restaurantOrder.estimatedReadyTime?.toJSON() ?? null,
      confirmedAt: restaurantOrder.confirmedAt?.toJSON() ?? null,
      completedAt: restaurantOrder.completedAt?.toJSON() ?? null,
      createdAt: restaurantOrder.createdAt?.toJSON() ?? null,
      updatedAt: restaurantOrder.updatedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restRestaurantOrder: RestRestaurantOrder): IRestaurantOrder {
    return {
      ...restRestaurantOrder,
      scheduledFor: restRestaurantOrder.scheduledFor ? dayjs(restRestaurantOrder.scheduledFor) : undefined,
      estimatedReadyTime: restRestaurantOrder.estimatedReadyTime ? dayjs(restRestaurantOrder.estimatedReadyTime) : undefined,
      confirmedAt: restRestaurantOrder.confirmedAt ? dayjs(restRestaurantOrder.confirmedAt) : undefined,
      completedAt: restRestaurantOrder.completedAt ? dayjs(restRestaurantOrder.completedAt) : undefined,
      createdAt: restRestaurantOrder.createdAt ? dayjs(restRestaurantOrder.createdAt) : undefined,
      updatedAt: restRestaurantOrder.updatedAt ? dayjs(restRestaurantOrder.updatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestRestaurantOrder>): HttpResponse<IRestaurantOrder> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRestaurantOrder[]>): HttpResponse<IRestaurantOrder[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
