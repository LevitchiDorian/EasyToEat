import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMenuItem, NewMenuItem } from '../menu-item.model';

export type PartialUpdateMenuItem = Partial<IMenuItem> & Pick<IMenuItem, 'id'>;

export type EntityResponseType = HttpResponse<IMenuItem>;
export type EntityArrayResponseType = HttpResponse<IMenuItem[]>;

@Injectable({ providedIn: 'root' })
export class MenuItemService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/menu-items');

  create(menuItem: NewMenuItem): Observable<EntityResponseType> {
    return this.http.post<IMenuItem>(this.resourceUrl, menuItem, { observe: 'response' });
  }

  update(menuItem: IMenuItem): Observable<EntityResponseType> {
    return this.http.put<IMenuItem>(`${this.resourceUrl}/${this.getMenuItemIdentifier(menuItem)}`, menuItem, { observe: 'response' });
  }

  partialUpdate(menuItem: PartialUpdateMenuItem): Observable<EntityResponseType> {
    return this.http.patch<IMenuItem>(`${this.resourceUrl}/${this.getMenuItemIdentifier(menuItem)}`, menuItem, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMenuItem>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMenuItem[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMenuItemIdentifier(menuItem: Pick<IMenuItem, 'id'>): number {
    return menuItem.id;
  }

  compareMenuItem(o1: Pick<IMenuItem, 'id'> | null, o2: Pick<IMenuItem, 'id'> | null): boolean {
    return o1 && o2 ? this.getMenuItemIdentifier(o1) === this.getMenuItemIdentifier(o2) : o1 === o2;
  }

  addMenuItemToCollectionIfMissing<Type extends Pick<IMenuItem, 'id'>>(
    menuItemCollection: Type[],
    ...menuItemsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const menuItems: Type[] = menuItemsToCheck.filter(isPresent);
    if (menuItems.length > 0) {
      const menuItemCollectionIdentifiers = menuItemCollection.map(menuItemItem => this.getMenuItemIdentifier(menuItemItem));
      const menuItemsToAdd = menuItems.filter(menuItemItem => {
        const menuItemIdentifier = this.getMenuItemIdentifier(menuItemItem);
        if (menuItemCollectionIdentifiers.includes(menuItemIdentifier)) {
          return false;
        }
        menuItemCollectionIdentifiers.push(menuItemIdentifier);
        return true;
      });
      return [...menuItemsToAdd, ...menuItemCollection];
    }
    return menuItemCollection;
  }
}
