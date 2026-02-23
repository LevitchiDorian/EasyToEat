import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMenuItemOptionValue, NewMenuItemOptionValue } from '../menu-item-option-value.model';

export type PartialUpdateMenuItemOptionValue = Partial<IMenuItemOptionValue> & Pick<IMenuItemOptionValue, 'id'>;

export type EntityResponseType = HttpResponse<IMenuItemOptionValue>;
export type EntityArrayResponseType = HttpResponse<IMenuItemOptionValue[]>;

@Injectable({ providedIn: 'root' })
export class MenuItemOptionValueService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/menu-item-option-values');

  create(menuItemOptionValue: NewMenuItemOptionValue): Observable<EntityResponseType> {
    return this.http.post<IMenuItemOptionValue>(this.resourceUrl, menuItemOptionValue, { observe: 'response' });
  }

  update(menuItemOptionValue: IMenuItemOptionValue): Observable<EntityResponseType> {
    return this.http.put<IMenuItemOptionValue>(
      `${this.resourceUrl}/${this.getMenuItemOptionValueIdentifier(menuItemOptionValue)}`,
      menuItemOptionValue,
      { observe: 'response' },
    );
  }

  partialUpdate(menuItemOptionValue: PartialUpdateMenuItemOptionValue): Observable<EntityResponseType> {
    return this.http.patch<IMenuItemOptionValue>(
      `${this.resourceUrl}/${this.getMenuItemOptionValueIdentifier(menuItemOptionValue)}`,
      menuItemOptionValue,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMenuItemOptionValue>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMenuItemOptionValue[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMenuItemOptionValueIdentifier(menuItemOptionValue: Pick<IMenuItemOptionValue, 'id'>): number {
    return menuItemOptionValue.id;
  }

  compareMenuItemOptionValue(o1: Pick<IMenuItemOptionValue, 'id'> | null, o2: Pick<IMenuItemOptionValue, 'id'> | null): boolean {
    return o1 && o2 ? this.getMenuItemOptionValueIdentifier(o1) === this.getMenuItemOptionValueIdentifier(o2) : o1 === o2;
  }

  addMenuItemOptionValueToCollectionIfMissing<Type extends Pick<IMenuItemOptionValue, 'id'>>(
    menuItemOptionValueCollection: Type[],
    ...menuItemOptionValuesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const menuItemOptionValues: Type[] = menuItemOptionValuesToCheck.filter(isPresent);
    if (menuItemOptionValues.length > 0) {
      const menuItemOptionValueCollectionIdentifiers = menuItemOptionValueCollection.map(menuItemOptionValueItem =>
        this.getMenuItemOptionValueIdentifier(menuItemOptionValueItem),
      );
      const menuItemOptionValuesToAdd = menuItemOptionValues.filter(menuItemOptionValueItem => {
        const menuItemOptionValueIdentifier = this.getMenuItemOptionValueIdentifier(menuItemOptionValueItem);
        if (menuItemOptionValueCollectionIdentifiers.includes(menuItemOptionValueIdentifier)) {
          return false;
        }
        menuItemOptionValueCollectionIdentifiers.push(menuItemOptionValueIdentifier);
        return true;
      });
      return [...menuItemOptionValuesToAdd, ...menuItemOptionValueCollection];
    }
    return menuItemOptionValueCollection;
  }
}
