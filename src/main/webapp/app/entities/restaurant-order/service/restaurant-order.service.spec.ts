import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IRestaurantOrder } from '../restaurant-order.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../restaurant-order.test-samples';

import { RestRestaurantOrder, RestaurantOrderService } from './restaurant-order.service';

const requireRestSample: RestRestaurantOrder = {
  ...sampleWithRequiredData,
  scheduledFor: sampleWithRequiredData.scheduledFor?.toJSON(),
  estimatedReadyTime: sampleWithRequiredData.estimatedReadyTime?.toJSON(),
  confirmedAt: sampleWithRequiredData.confirmedAt?.toJSON(),
  completedAt: sampleWithRequiredData.completedAt?.toJSON(),
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
  updatedAt: sampleWithRequiredData.updatedAt?.toJSON(),
};

describe('RestaurantOrder Service', () => {
  let service: RestaurantOrderService;
  let httpMock: HttpTestingController;
  let expectedResult: IRestaurantOrder | IRestaurantOrder[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RestaurantOrderService);
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

    it('should create a RestaurantOrder', () => {
      const restaurantOrder = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(restaurantOrder).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a RestaurantOrder', () => {
      const restaurantOrder = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(restaurantOrder).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a RestaurantOrder', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RestaurantOrder', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a RestaurantOrder', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRestaurantOrderToCollectionIfMissing', () => {
      it('should add a RestaurantOrder to an empty array', () => {
        const restaurantOrder: IRestaurantOrder = sampleWithRequiredData;
        expectedResult = service.addRestaurantOrderToCollectionIfMissing([], restaurantOrder);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(restaurantOrder);
      });

      it('should not add a RestaurantOrder to an array that contains it', () => {
        const restaurantOrder: IRestaurantOrder = sampleWithRequiredData;
        const restaurantOrderCollection: IRestaurantOrder[] = [
          {
            ...restaurantOrder,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRestaurantOrderToCollectionIfMissing(restaurantOrderCollection, restaurantOrder);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RestaurantOrder to an array that doesn't contain it", () => {
        const restaurantOrder: IRestaurantOrder = sampleWithRequiredData;
        const restaurantOrderCollection: IRestaurantOrder[] = [sampleWithPartialData];
        expectedResult = service.addRestaurantOrderToCollectionIfMissing(restaurantOrderCollection, restaurantOrder);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(restaurantOrder);
      });

      it('should add only unique RestaurantOrder to an array', () => {
        const restaurantOrderArray: IRestaurantOrder[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const restaurantOrderCollection: IRestaurantOrder[] = [sampleWithRequiredData];
        expectedResult = service.addRestaurantOrderToCollectionIfMissing(restaurantOrderCollection, ...restaurantOrderArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const restaurantOrder: IRestaurantOrder = sampleWithRequiredData;
        const restaurantOrder2: IRestaurantOrder = sampleWithPartialData;
        expectedResult = service.addRestaurantOrderToCollectionIfMissing([], restaurantOrder, restaurantOrder2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(restaurantOrder);
        expect(expectedResult).toContain(restaurantOrder2);
      });

      it('should accept null and undefined values', () => {
        const restaurantOrder: IRestaurantOrder = sampleWithRequiredData;
        expectedResult = service.addRestaurantOrderToCollectionIfMissing([], null, restaurantOrder, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(restaurantOrder);
      });

      it('should return initial array if no RestaurantOrder is added', () => {
        const restaurantOrderCollection: IRestaurantOrder[] = [sampleWithRequiredData];
        expectedResult = service.addRestaurantOrderToCollectionIfMissing(restaurantOrderCollection, undefined, null);
        expect(expectedResult).toEqual(restaurantOrderCollection);
      });
    });

    describe('compareRestaurantOrder', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRestaurantOrder(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 2705 };
        const entity2 = null;

        const compareResult1 = service.compareRestaurantOrder(entity1, entity2);
        const compareResult2 = service.compareRestaurantOrder(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 2705 };
        const entity2 = { id: 25621 };

        const compareResult1 = service.compareRestaurantOrder(entity1, entity2);
        const compareResult2 = service.compareRestaurantOrder(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 2705 };
        const entity2 = { id: 2705 };

        const compareResult1 = service.compareRestaurantOrder(entity1, entity2);
        const compareResult2 = service.compareRestaurantOrder(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
