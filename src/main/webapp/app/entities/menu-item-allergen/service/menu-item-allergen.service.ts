import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMenuItemAllergen, NewMenuItemAllergen } from '../menu-item-allergen.model';

export type PartialUpdateMenuItemAllergen = Partial<IMenuItemAllergen> & Pick<IMenuItemAllergen, 'id'>;

export type EntityResponseType = HttpResponse<IMenuItemAllergen>;
export type EntityArrayResponseType = HttpResponse<IMenuItemAllergen[]>;

@Injectable({ providedIn: 'root' })
export class MenuItemAllergenService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/menu-item-allergens');

  create(menuItemAllergen: NewMenuItemAllergen): Observable<EntityResponseType> {
    return this.http.post<IMenuItemAllergen>(this.resourceUrl, menuItemAllergen, { observe: 'response' });
  }

  update(menuItemAllergen: IMenuItemAllergen): Observable<EntityResponseType> {
    return this.http.put<IMenuItemAllergen>(
      `${this.resourceUrl}/${this.getMenuItemAllergenIdentifier(menuItemAllergen)}`,
      menuItemAllergen,
      { observe: 'response' },
    );
  }

  partialUpdate(menuItemAllergen: PartialUpdateMenuItemAllergen): Observable<EntityResponseType> {
    return this.http.patch<IMenuItemAllergen>(
      `${this.resourceUrl}/${this.getMenuItemAllergenIdentifier(menuItemAllergen)}`,
      menuItemAllergen,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMenuItemAllergen>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMenuItemAllergen[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMenuItemAllergenIdentifier(menuItemAllergen: Pick<IMenuItemAllergen, 'id'>): number {
    return menuItemAllergen.id;
  }

  compareMenuItemAllergen(o1: Pick<IMenuItemAllergen, 'id'> | null, o2: Pick<IMenuItemAllergen, 'id'> | null): boolean {
    return o1 && o2 ? this.getMenuItemAllergenIdentifier(o1) === this.getMenuItemAllergenIdentifier(o2) : o1 === o2;
  }

  addMenuItemAllergenToCollectionIfMissing<Type extends Pick<IMenuItemAllergen, 'id'>>(
    menuItemAllergenCollection: Type[],
    ...menuItemAllergensToCheck: (Type | null | undefined)[]
  ): Type[] {
    const menuItemAllergens: Type[] = menuItemAllergensToCheck.filter(isPresent);
    if (menuItemAllergens.length > 0) {
      const menuItemAllergenCollectionIdentifiers = menuItemAllergenCollection.map(menuItemAllergenItem =>
        this.getMenuItemAllergenIdentifier(menuItemAllergenItem),
      );
      const menuItemAllergensToAdd = menuItemAllergens.filter(menuItemAllergenItem => {
        const menuItemAllergenIdentifier = this.getMenuItemAllergenIdentifier(menuItemAllergenItem);
        if (menuItemAllergenCollectionIdentifiers.includes(menuItemAllergenIdentifier)) {
          return false;
        }
        menuItemAllergenCollectionIdentifiers.push(menuItemAllergenIdentifier);
        return true;
      });
      return [...menuItemAllergensToAdd, ...menuItemAllergenCollection];
    }
    return menuItemAllergenCollection;
  }
}
