import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ILocationMenuItemOverride } from '../location-menu-item-override.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../location-menu-item-override.test-samples';

import { LocationMenuItemOverrideService } from './location-menu-item-override.service';

const requireRestSample: ILocationMenuItemOverride = {
  ...sampleWithRequiredData,
};

describe('LocationMenuItemOverride Service', () => {
  let service: LocationMenuItemOverrideService;
  let httpMock: HttpTestingController;
  let expectedResult: ILocationMenuItemOverride | ILocationMenuItemOverride[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LocationMenuItemOverrideService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a LocationMenuItemOverride', () => {
      const locationMenuItemOverride = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(locationMenuItemOverride).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LocationMenuItemOverride', () => {
      const locationMenuItemOverride = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(locationMenuItemOverride).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LocationMenuItemOverride', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LocationMenuItemOverride', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LocationMenuItemOverride', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addLocationMenuItemOverrideToCollectionIfMissing', () => {
      it('should add a LocationMenuItemOverride to an empty array', () => {
        const locationMenuItemOverride: ILocationMenuItemOverride = sampleWithRequiredData;
        expectedResult = service.addLocationMenuItemOverrideToCollectionIfMissing([], locationMenuItemOverride);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(locationMenuItemOverride);
      });

      it('should not add a LocationMenuItemOverride to an array that contains it', () => {
        const locationMenuItemOverride: ILocationMenuItemOverride = sampleWithRequiredData;
        const locationMenuItemOverrideCollection: ILocationMenuItemOverride[] = [
          {
            ...locationMenuItemOverride,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLocationMenuItemOverrideToCollectionIfMissing(
          locationMenuItemOverrideCollection,
          locationMenuItemOverride,
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LocationMenuItemOverride to an array that doesn't contain it", () => {
        const locationMenuItemOverride: ILocationMenuItemOverride = sampleWithRequiredData;
        const locationMenuItemOverrideCollection: ILocationMenuItemOverride[] = [sampleWithPartialData];
        expectedResult = service.addLocationMenuItemOverrideToCollectionIfMissing(
          locationMenuItemOverrideCollection,
          locationMenuItemOverride,
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(locationMenuItemOverride);
      });

      it('should add only unique LocationMenuItemOverride to an array', () => {
        const locationMenuItemOverrideArray: ILocationMenuItemOverride[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const locationMenuItemOverrideCollection: ILocationMenuItemOverride[] = [sampleWithRequiredData];
        expectedResult = service.addLocationMenuItemOverrideToCollectionIfMissing(
          locationMenuItemOverrideCollection,
          ...locationMenuItemOverrideArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const locationMenuItemOverride: ILocationMenuItemOverride = sampleWithRequiredData;
        const locationMenuItemOverride2: ILocationMenuItemOverride = sampleWithPartialData;
        expectedResult = service.addLocationMenuItemOverrideToCollectionIfMissing([], locationMenuItemOverride, locationMenuItemOverride2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(locationMenuItemOverride);
        expect(expectedResult).toContain(locationMenuItemOverride2);
      });

      it('should accept null and undefined values', () => {
        const locationMenuItemOverride: ILocationMenuItemOverride = sampleWithRequiredData;
        expectedResult = service.addLocationMenuItemOverrideToCollectionIfMissing([], null, locationMenuItemOverride, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(locationMenuItemOverride);
      });

      it('should return initial array if no LocationMenuItemOverride is added', () => {
        const locationMenuItemOverrideCollection: ILocationMenuItemOverride[] = [sampleWithRequiredData];
        expectedResult = service.addLocationMenuItemOverrideToCollectionIfMissing(locationMenuItemOverrideCollection, undefined, null);
        expect(expectedResult).toEqual(locationMenuItemOverrideCollection);
      });
    });

    describe('compareLocationMenuItemOverride', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLocationMenuItemOverride(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 27299 };
        const entity2 = null;

        const compareResult1 = service.compareLocationMenuItemOverride(entity1, entity2);
        const compareResult2 = service.compareLocationMenuItemOverride(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 27299 };
        const entity2 = { id: 10167 };

        const compareResult1 = service.compareLocationMenuItemOverride(entity1, entity2);
        const compareResult2 = service.compareLocationMenuItemOverride(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 27299 };
        const entity2 = { id: 27299 };

        const compareResult1 = service.compareLocationMenuItemOverride(entity1, entity2);
        const compareResult2 = service.compareLocationMenuItemOverride(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
