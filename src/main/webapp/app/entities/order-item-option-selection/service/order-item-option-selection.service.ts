import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrderItemOptionSelection, NewOrderItemOptionSelection } from '../order-item-option-selection.model';

export type PartialUpdateOrderItemOptionSelection = Partial<IOrderItemOptionSelection> & Pick<IOrderItemOptionSelection, 'id'>;

export type EntityResponseType = HttpResponse<IOrderItemOptionSelection>;
export type EntityArrayResponseType = HttpResponse<IOrderItemOptionSelection[]>;

@Injectable({ providedIn: 'root' })
export class OrderItemOptionSelectionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/order-item-option-selections');

  create(orderItemOptionSelection: NewOrderItemOptionSelection): Observable<EntityResponseType> {
    return this.http.post<IOrderItemOptionSelection>(this.resourceUrl, orderItemOptionSelection, { observe: 'response' });
  }

  update(orderItemOptionSelection: IOrderItemOptionSelection): Observable<EntityResponseType> {
    return this.http.put<IOrderItemOptionSelection>(
      `${this.resourceUrl}/${this.getOrderItemOptionSelectionIdentifier(orderItemOptionSelection)}`,
      orderItemOptionSelection,
      { observe: 'response' },
    );
  }

  partialUpdate(orderItemOptionSelection: PartialUpdateOrderItemOptionSelection): Observable<EntityResponseType> {
    return this.http.patch<IOrderItemOptionSelection>(
      `${this.resourceUrl}/${this.getOrderItemOptionSelectionIdentifier(orderItemOptionSelection)}`,
      orderItemOptionSelection,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IOrderItemOptionSelection>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IOrderItemOptionSelection[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOrderItemOptionSelectionIdentifier(orderItemOptionSelection: Pick<IOrderItemOptionSelection, 'id'>): number {
    return orderItemOptionSelection.id;
  }

  compareOrderItemOptionSelection(
    o1: Pick<IOrderItemOptionSelection, 'id'> | null,
    o2: Pick<IOrderItemOptionSelection, 'id'> | null,
  ): boolean {
    return o1 && o2 ? this.getOrderItemOptionSelectionIdentifier(o1) === this.getOrderItemOptionSelectionIdentifier(o2) : o1 === o2;
  }

  addOrderItemOptionSelectionToCollectionIfMissing<Type extends Pick<IOrderItemOptionSelection, 'id'>>(
    orderItemOptionSelectionCollection: Type[],
    ...orderItemOptionSelectionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const orderItemOptionSelections: Type[] = orderItemOptionSelectionsToCheck.filter(isPresent);
    if (orderItemOptionSelections.length > 0) {
      const orderItemOptionSelectionCollectionIdentifiers = orderItemOptionSelectionCollection.map(orderItemOptionSelectionItem =>
        this.getOrderItemOptionSelectionIdentifier(orderItemOptionSelectionItem),
      );
      const orderItemOptionSelectionsToAdd = orderItemOptionSelections.filter(orderItemOptionSelectionItem => {
        const orderItemOptionSelectionIdentifier = this.getOrderItemOptionSelectionIdentifier(orderItemOptionSelectionItem);
        if (orderItemOptionSelectionCollectionIdentifiers.includes(orderItemOptionSelectionIdentifier)) {
          return false;
        }
        orderItemOptionSelectionCollectionIdentifiers.push(orderItemOptionSelectionIdentifier);
        return true;
      });
      return [...orderItemOptionSelectionsToAdd, ...orderItemOptionSelectionCollection];
    }
    return orderItemOptionSelectionCollection;
  }
}
