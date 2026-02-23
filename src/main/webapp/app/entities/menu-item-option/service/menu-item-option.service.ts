import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMenuItemOption, NewMenuItemOption } from '../menu-item-option.model';

export type PartialUpdateMenuItemOption = Partial<IMenuItemOption> & Pick<IMenuItemOption, 'id'>;

export type EntityResponseType = HttpResponse<IMenuItemOption>;
export type EntityArrayResponseType = HttpResponse<IMenuItemOption[]>;

@Injectable({ providedIn: 'root' })
export class MenuItemOptionService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/menu-item-options');

  create(menuItemOption: NewMenuItemOption): Observable<EntityResponseType> {
    return this.http.post<IMenuItemOption>(this.resourceUrl, menuItemOption, { observe: 'response' });
  }

  update(menuItemOption: IMenuItemOption): Observable<EntityResponseType> {
    return this.http.put<IMenuItemOption>(`${this.resourceUrl}/${this.getMenuItemOptionIdentifier(menuItemOption)}`, menuItemOption, {
      observe: 'response',
    });
  }

  partialUpdate(menuItemOption: PartialUpdateMenuItemOption): Observable<EntityResponseType> {
    return this.http.patch<IMenuItemOption>(`${this.resourceUrl}/${this.getMenuItemOptionIdentifier(menuItemOption)}`, menuItemOption, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMenuItemOption>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMenuItemOption[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMenuItemOptionIdentifier(menuItemOption: Pick<IMenuItemOption, 'id'>): number {
    return menuItemOption.id;
  }

  compareMenuItemOption(o1: Pick<IMenuItemOption, 'id'> | null, o2: Pick<IMenuItemOption, 'id'> | null): boolean {
    return o1 && o2 ? this.getMenuItemOptionIdentifier(o1) === this.getMenuItemOptionIdentifier(o2) : o1 === o2;
  }

  addMenuItemOptionToCollectionIfMissing<Type extends Pick<IMenuItemOption, 'id'>>(
    menuItemOptionCollection: Type[],
    ...menuItemOptionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const menuItemOptions: Type[] = menuItemOptionsToCheck.filter(isPresent);
    if (menuItemOptions.length > 0) {
      const menuItemOptionCollectionIdentifiers = menuItemOptionCollection.map(menuItemOptionItem =>
        this.getMenuItemOptionIdentifier(menuItemOptionItem),
      );
      const menuItemOptionsToAdd = menuItemOptions.filter(menuItemOptionItem => {
        const menuItemOptionIdentifier = this.getMenuItemOptionIdentifier(menuItemOptionItem);
        if (menuItemOptionCollectionIdentifiers.includes(menuItemOptionIdentifier)) {
          return false;
        }
        menuItemOptionCollectionIdentifiers.push(menuItemOptionIdentifier);
        return true;
      });
      return [...menuItemOptionsToAdd, ...menuItemOptionCollection];
    }
    return menuItemOptionCollection;
  }
}
