import { TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { ActivatedRoute, ActivatedRouteSnapshot, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { ILocationMenuItemOverride } from '../location-menu-item-override.model';
import { LocationMenuItemOverrideService } from '../service/location-menu-item-override.service';

import locationMenuItemOverrideResolve from './location-menu-item-override-routing-resolve.service';

describe('LocationMenuItemOverride routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: LocationMenuItemOverrideService;
  let resultLocationMenuItemOverride: ILocationMenuItemOverride | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(LocationMenuItemOverrideService);
    resultLocationMenuItemOverride = undefined;
  });

  describe('resolve', () => {
    it('should return ILocationMenuItemOverride returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        locationMenuItemOverrideResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultLocationMenuItemOverride = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultLocationMenuItemOverride).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        locationMenuItemOverrideResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultLocationMenuItemOverride = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toHaveBeenCalled();
      expect(resultLocationMenuItemOverride).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<ILocationMenuItemOverride>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        locationMenuItemOverrideResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultLocationMenuItemOverride = result;
          },
        });
      });

      // THEN
      expect(service.find).toHaveBeenCalledWith(123);
      expect(resultLocationMenuItemOverride).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
