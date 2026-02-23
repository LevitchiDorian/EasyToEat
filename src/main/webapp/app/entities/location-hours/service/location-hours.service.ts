import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILocationHours, NewLocationHours } from '../location-hours.model';

export type PartialUpdateLocationHours = Partial<ILocationHours> & Pick<ILocationHours, 'id'>;

export type EntityResponseType = HttpResponse<ILocationHours>;
export type EntityArrayResponseType = HttpResponse<ILocationHours[]>;

@Injectable({ providedIn: 'root' })
export class LocationHoursService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/location-hours');

  create(locationHours: NewLocationHours): Observable<EntityResponseType> {
    return this.http.post<ILocationHours>(this.resourceUrl, locationHours, { observe: 'response' });
  }

  update(locationHours: ILocationHours): Observable<EntityResponseType> {
    return this.http.put<ILocationHours>(`${this.resourceUrl}/${this.getLocationHoursIdentifier(locationHours)}`, locationHours, {
      observe: 'response',
    });
  }

  partialUpdate(locationHours: PartialUpdateLocationHours): Observable<EntityResponseType> {
    return this.http.patch<ILocationHours>(`${this.resourceUrl}/${this.getLocationHoursIdentifier(locationHours)}`, locationHours, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILocationHours>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILocationHours[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getLocationHoursIdentifier(locationHours: Pick<ILocationHours, 'id'>): number {
    return locationHours.id;
  }

  compareLocationHours(o1: Pick<ILocationHours, 'id'> | null, o2: Pick<ILocationHours, 'id'> | null): boolean {
    return o1 && o2 ? this.getLocationHoursIdentifier(o1) === this.getLocationHoursIdentifier(o2) : o1 === o2;
  }

  addLocationHoursToCollectionIfMissing<Type extends Pick<ILocationHours, 'id'>>(
    locationHoursCollection: Type[],
    ...locationHoursToCheck: (Type | null | undefined)[]
  ): Type[] {
    const locationHours: Type[] = locationHoursToCheck.filter(isPresent);
    if (locationHours.length > 0) {
      const locationHoursCollectionIdentifiers = locationHoursCollection.map(locationHoursItem =>
        this.getLocationHoursIdentifier(locationHoursItem),
      );
      const locationHoursToAdd = locationHours.filter(locationHoursItem => {
        const locationHoursIdentifier = this.getLocationHoursIdentifier(locationHoursItem);
        if (locationHoursCollectionIdentifiers.includes(locationHoursIdentifier)) {
          return false;
        }
        locationHoursCollectionIdentifiers.push(locationHoursIdentifier);
        return true;
      });
      return [...locationHoursToAdd, ...locationHoursCollection];
    }
    return locationHoursCollection;
  }
}
