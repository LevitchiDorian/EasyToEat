import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ILocationHours } from '../location-hours.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../location-hours.test-samples';

import { LocationHoursService } from './location-hours.service';

const requireRestSample: ILocationHours = {
  ...sampleWithRequiredData,
};

describe('LocationHours Service', () => {
  let service: LocationHoursService;
  let httpMock: HttpTestingController;
  let expectedResult: ILocationHours | ILocationHours[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LocationHoursService);
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

    it('should create a LocationHours', () => {
      const locationHours = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(locationHours).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LocationHours', () => {
      const locationHours = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(locationHours).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LocationHours', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LocationHours', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LocationHours', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addLocationHoursToCollectionIfMissing', () => {
      it('should add a LocationHours to an empty array', () => {
        const locationHours: ILocationHours = sampleWithRequiredData;
        expectedResult = service.addLocationHoursToCollectionIfMissing([], locationHours);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(locationHours);
      });

      it('should not add a LocationHours to an array that contains it', () => {
        const locationHours: ILocationHours = sampleWithRequiredData;
        const locationHoursCollection: ILocationHours[] = [
          {
            ...locationHours,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLocationHoursToCollectionIfMissing(locationHoursCollection, locationHours);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LocationHours to an array that doesn't contain it", () => {
        const locationHours: ILocationHours = sampleWithRequiredData;
        const locationHoursCollection: ILocationHours[] = [sampleWithPartialData];
        expectedResult = service.addLocationHoursToCollectionIfMissing(locationHoursCollection, locationHours);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(locationHours);
      });

      it('should add only unique LocationHours to an array', () => {
        const locationHoursArray: ILocationHours[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const locationHoursCollection: ILocationHours[] = [sampleWithRequiredData];
        expectedResult = service.addLocationHoursToCollectionIfMissing(locationHoursCollection, ...locationHoursArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const locationHours: ILocationHours = sampleWithRequiredData;
        const locationHours2: ILocationHours = sampleWithPartialData;
        expectedResult = service.addLocationHoursToCollectionIfMissing([], locationHours, locationHours2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(locationHours);
        expect(expectedResult).toContain(locationHours2);
      });

      it('should accept null and undefined values', () => {
        const locationHours: ILocationHours = sampleWithRequiredData;
        expectedResult = service.addLocationHoursToCollectionIfMissing([], null, locationHours, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(locationHours);
      });

      it('should return initial array if no LocationHours is added', () => {
        const locationHoursCollection: ILocationHours[] = [sampleWithRequiredData];
        expectedResult = service.addLocationHoursToCollectionIfMissing(locationHoursCollection, undefined, null);
        expect(expectedResult).toEqual(locationHoursCollection);
      });
    });

    describe('compareLocationHours', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLocationHours(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 18708 };
        const entity2 = null;

        const compareResult1 = service.compareLocationHours(entity1, entity2);
        const compareResult2 = service.compareLocationHours(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 18708 };
        const entity2 = { id: 29297 };

        const compareResult1 = service.compareLocationHours(entity1, entity2);
        const compareResult2 = service.compareLocationHours(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 18708 };
        const entity2 = { id: 18708 };

        const compareResult1 = service.compareLocationHours(entity1, entity2);
        const compareResult2 = service.compareLocationHours(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
