import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILocationMenuItemOverride, NewLocationMenuItemOverride } from '../location-menu-item-override.model';

export type PartialUpdateLocationMenuItemOverride = Partial<ILocationMenuItemOverride> & Pick<ILocationMenuItemOverride, 'id'>;

export type EntityResponseType = HttpResponse<ILocationMenuItemOverride>;
export type EntityArrayResponseType = HttpResponse<ILocationMenuItemOverride[]>;

@Injectable({ providedIn: 'root' })
export class LocationMenuItemOverrideService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/location-menu-item-overrides');

  create(locationMenuItemOverride: NewLocationMenuItemOverride): Observable<EntityResponseType> {
    return this.http.post<ILocationMenuItemOverride>(this.resourceUrl, locationMenuItemOverride, { observe: 'response' });
  }

  update(locationMenuItemOverride: ILocationMenuItemOverride): Observable<EntityResponseType> {
    return this.http.put<ILocationMenuItemOverride>(
      `${this.resourceUrl}/${this.getLocationMenuItemOverrideIdentifier(locationMenuItemOverride)}`,
      locationMenuItemOverride,
      { observe: 'response' },
    );
  }

  partialUpdate(locationMenuItemOverride: PartialUpdateLocationMenuItemOverride): Observable<EntityResponseType> {
    return this.http.patch<ILocationMenuItemOverride>(
      `${this.resourceUrl}/${this.getLocationMenuItemOverrideIdentifier(locationMenuItemOverride)}`,
      locationMenuItemOverride,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILocationMenuItemOverride>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILocationMenuItemOverride[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getLocationMenuItemOverrideIdentifier(locationMenuItemOverride: Pick<ILocationMenuItemOverride, 'id'>): number {
    return locationMenuItemOverride.id;
  }

  compareLocationMenuItemOverride(
    o1: Pick<ILocationMenuItemOverride, 'id'> | null,
    o2: Pick<ILocationMenuItemOverride, 'id'> | null,
  ): boolean {
    return o1 && o2 ? this.getLocationMenuItemOverrideIdentifier(o1) === this.getLocationMenuItemOverrideIdentifier(o2) : o1 === o2;
  }

  addLocationMenuItemOverrideToCollectionIfMissing<Type extends Pick<ILocationMenuItemOverride, 'id'>>(
    locationMenuItemOverrideCollection: Type[],
    ...locationMenuItemOverridesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const locationMenuItemOverrides: Type[] = locationMenuItemOverridesToCheck.filter(isPresent);
    if (locationMenuItemOverrides.length > 0) {
      const locationMenuItemOverrideCollectionIdentifiers = locationMenuItemOverrideCollection.map(locationMenuItemOverrideItem =>
        this.getLocationMenuItemOverrideIdentifier(locationMenuItemOverrideItem),
      );
      const locationMenuItemOverridesToAdd = locationMenuItemOverrides.filter(locationMenuItemOverrideItem => {
        const locationMenuItemOverrideIdentifier = this.getLocationMenuItemOverrideIdentifier(locationMenuItemOverrideItem);
        if (locationMenuItemOverrideCollectionIdentifiers.includes(locationMenuItemOverrideIdentifier)) {
          return false;
        }
        locationMenuItemOverrideCollectionIdentifiers.push(locationMenuItemOverrideIdentifier);
        return true;
      });
      return [...locationMenuItemOverridesToAdd, ...locationMenuItemOverrideCollection];
    }
    return locationMenuItemOverrideCollection;
  }
}
