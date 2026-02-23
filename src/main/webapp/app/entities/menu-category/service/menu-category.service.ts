import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMenuCategory, NewMenuCategory } from '../menu-category.model';

export type PartialUpdateMenuCategory = Partial<IMenuCategory> & Pick<IMenuCategory, 'id'>;

export type EntityResponseType = HttpResponse<IMenuCategory>;
export type EntityArrayResponseType = HttpResponse<IMenuCategory[]>;

@Injectable({ providedIn: 'root' })
export class MenuCategoryService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/menu-categories');

  create(menuCategory: NewMenuCategory): Observable<EntityResponseType> {
    return this.http.post<IMenuCategory>(this.resourceUrl, menuCategory, { observe: 'response' });
  }

  update(menuCategory: IMenuCategory): Observable<EntityResponseType> {
    return this.http.put<IMenuCategory>(`${this.resourceUrl}/${this.getMenuCategoryIdentifier(menuCategory)}`, menuCategory, {
      observe: 'response',
    });
  }

  partialUpdate(menuCategory: PartialUpdateMenuCategory): Observable<EntityResponseType> {
    return this.http.patch<IMenuCategory>(`${this.resourceUrl}/${this.getMenuCategoryIdentifier(menuCategory)}`, menuCategory, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMenuCategory>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMenuCategory[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMenuCategoryIdentifier(menuCategory: Pick<IMenuCategory, 'id'>): number {
    return menuCategory.id;
  }

  compareMenuCategory(o1: Pick<IMenuCategory, 'id'> | null, o2: Pick<IMenuCategory, 'id'> | null): boolean {
    return o1 && o2 ? this.getMenuCategoryIdentifier(o1) === this.getMenuCategoryIdentifier(o2) : o1 === o2;
  }

  addMenuCategoryToCollectionIfMissing<Type extends Pick<IMenuCategory, 'id'>>(
    menuCategoryCollection: Type[],
    ...menuCategoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const menuCategories: Type[] = menuCategoriesToCheck.filter(isPresent);
    if (menuCategories.length > 0) {
      const menuCategoryCollectionIdentifiers = menuCategoryCollection.map(menuCategoryItem =>
        this.getMenuCategoryIdentifier(menuCategoryItem),
      );
      const menuCategoriesToAdd = menuCategories.filter(menuCategoryItem => {
        const menuCategoryIdentifier = this.getMenuCategoryIdentifier(menuCategoryItem);
        if (menuCategoryCollectionIdentifiers.includes(menuCategoryIdentifier)) {
          return false;
        }
        menuCategoryCollectionIdentifiers.push(menuCategoryIdentifier);
        return true;
      });
      return [...menuCategoriesToAdd, ...menuCategoryCollection];
    }
    return menuCategoryCollection;
  }
}
