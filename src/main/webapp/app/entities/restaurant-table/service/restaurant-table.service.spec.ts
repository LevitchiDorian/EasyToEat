import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IRestaurantTable } from '../restaurant-table.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../restaurant-table.test-samples';

import { RestaurantTableService } from './restaurant-table.service';

const requireRestSample: IRestaurantTable = {
  ...sampleWithRequiredData,
};

describe('RestaurantTable Service', () => {
  let service: RestaurantTableService;
  let httpMock: HttpTestingController;
  let expectedResult: IRestaurantTable | IRestaurantTable[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RestaurantTableService);
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

    it('should create a RestaurantTable', () => {
      const restaurantTable = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(restaurantTable).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RestaurantTable', () => {
      const restaurantTable = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(restaurantTable).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RestaurantTable', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RestaurantTable', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RestaurantTable', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRestaurantTableToCollectionIfMissing', () => {
      it('should add a RestaurantTable to an empty array', () => {
        const restaurantTable: IRestaurantTable = sampleWithRequiredData;
        expectedResult = service.addRestaurantTableToCollectionIfMissing([], restaurantTable);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(restaurantTable);
      });

      it('should not add a RestaurantTable to an array that contains it', () => {
        const restaurantTable: IRestaurantTable = sampleWithRequiredData;
        const restaurantTableCollection: IRestaurantTable[] = [
          {
            ...restaurantTable,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRestaurantTableToCollectionIfMissing(restaurantTableCollection, restaurantTable);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RestaurantTable to an array that doesn't contain it", () => {
        const restaurantTable: IRestaurantTable = sampleWithRequiredData;
        const restaurantTableCollection: IRestaurantTable[] = [sampleWithPartialData];
        expectedResult = service.addRestaurantTableToCollectionIfMissing(restaurantTableCollection, restaurantTable);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(restaurantTable);
      });

      it('should add only unique RestaurantTable to an array', () => {
        const restaurantTableArray: IRestaurantTable[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const restaurantTableCollection: IRestaurantTable[] = [sampleWithRequiredData];
        expectedResult = service.addRestaurantTableToCollectionIfMissing(restaurantTableCollection, ...restaurantTableArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const restaurantTable: IRestaurantTable = sampleWithRequiredData;
        const restaurantTable2: IRestaurantTable = sampleWithPartialData;
        expectedResult = service.addRestaurantTableToCollectionIfMissing([], restaurantTable, restaurantTable2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(restaurantTable);
        expect(expectedResult).toContain(restaurantTable2);
      });

      it('should accept null and undefined values', () => {
        const restaurantTable: IRestaurantTable = sampleWithRequiredData;
        expectedResult = service.addRestaurantTableToCollectionIfMissing([], null, restaurantTable, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(restaurantTable);
      });

      it('should return initial array if no RestaurantTable is added', () => {
        const restaurantTableCollection: IRestaurantTable[] = [sampleWithRequiredData];
        expectedResult = service.addRestaurantTableToCollectionIfMissing(restaurantTableCollection, undefined, null);
        expect(expectedResult).toEqual(restaurantTableCollection);
      });
    });

    describe('compareRestaurantTable', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRestaurantTable(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 23892 };
        const entity2 = null;

        const compareResult1 = service.compareRestaurantTable(entity1, entity2);
        const compareResult2 = service.compareRestaurantTable(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 23892 };
        const entity2 = { id: 31409 };

        const compareResult1 = service.compareRestaurantTable(entity1, entity2);
        const compareResult2 = service.compareRestaurantTable(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 23892 };
        const entity2 = { id: 23892 };

        const compareResult1 = service.compareRestaurantTable(entity1, entity2);
        const compareResult2 = service.compareRestaurantTable(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
