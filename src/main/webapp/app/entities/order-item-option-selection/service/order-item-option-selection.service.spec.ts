import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IOrderItemOptionSelection } from '../order-item-option-selection.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../order-item-option-selection.test-samples';

import { OrderItemOptionSelectionService } from './order-item-option-selection.service';

const requireRestSample: IOrderItemOptionSelection = {
  ...sampleWithRequiredData,
};

describe('OrderItemOptionSelection Service', () => {
  let service: OrderItemOptionSelectionService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrderItemOptionSelection | IOrderItemOptionSelection[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(OrderItemOptionSelectionService);
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

    it('should create a OrderItemOptionSelection', () => {
      const orderItemOptionSelection = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(orderItemOptionSelection).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrderItemOptionSelection', () => {
      const orderItemOptionSelection = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(orderItemOptionSelection).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrderItemOptionSelection', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrderItemOptionSelection', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OrderItemOptionSelection', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOrderItemOptionSelectionToCollectionIfMissing', () => {
      it('should add a OrderItemOptionSelection to an empty array', () => {
        const orderItemOptionSelection: IOrderItemOptionSelection = sampleWithRequiredData;
        expectedResult = service.addOrderItemOptionSelectionToCollectionIfMissing([], orderItemOptionSelection);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderItemOptionSelection);
      });

      it('should not add a OrderItemOptionSelection to an array that contains it', () => {
        const orderItemOptionSelection: IOrderItemOptionSelection = sampleWithRequiredData;
        const orderItemOptionSelectionCollection: IOrderItemOptionSelection[] = [
          {
            ...orderItemOptionSelection,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrderItemOptionSelectionToCollectionIfMissing(
          orderItemOptionSelectionCollection,
          orderItemOptionSelection,
        );
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrderItemOptionSelection to an array that doesn't contain it", () => {
        const orderItemOptionSelection: IOrderItemOptionSelection = sampleWithRequiredData;
        const orderItemOptionSelectionCollection: IOrderItemOptionSelection[] = [sampleWithPartialData];
        expectedResult = service.addOrderItemOptionSelectionToCollectionIfMissing(
          orderItemOptionSelectionCollection,
          orderItemOptionSelection,
        );
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderItemOptionSelection);
      });

      it('should add only unique OrderItemOptionSelection to an array', () => {
        const orderItemOptionSelectionArray: IOrderItemOptionSelection[] = [
          sampleWithRequiredData,
          sampleWithPartialData,
          sampleWithFullData,
        ];
        const orderItemOptionSelectionCollection: IOrderItemOptionSelection[] = [sampleWithRequiredData];
        expectedResult = service.addOrderItemOptionSelectionToCollectionIfMissing(
          orderItemOptionSelectionCollection,
          ...orderItemOptionSelectionArray,
        );
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const orderItemOptionSelection: IOrderItemOptionSelection = sampleWithRequiredData;
        const orderItemOptionSelection2: IOrderItemOptionSelection = sampleWithPartialData;
        expectedResult = service.addOrderItemOptionSelectionToCollectionIfMissing([], orderItemOptionSelection, orderItemOptionSelection2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderItemOptionSelection);
        expect(expectedResult).toContain(orderItemOptionSelection2);
      });

      it('should accept null and undefined values', () => {
        const orderItemOptionSelection: IOrderItemOptionSelection = sampleWithRequiredData;
        expectedResult = service.addOrderItemOptionSelectionToCollectionIfMissing([], null, orderItemOptionSelection, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderItemOptionSelection);
      });

      it('should return initial array if no OrderItemOptionSelection is added', () => {
        const orderItemOptionSelectionCollection: IOrderItemOptionSelection[] = [sampleWithRequiredData];
        expectedResult = service.addOrderItemOptionSelectionToCollectionIfMissing(orderItemOptionSelectionCollection, undefined, null);
        expect(expectedResult).toEqual(orderItemOptionSelectionCollection);
      });
    });

    describe('compareOrderItemOptionSelection', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrderItemOptionSelection(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 17155 };
        const entity2 = null;

        const compareResult1 = service.compareOrderItemOptionSelection(entity1, entity2);
        const compareResult2 = service.compareOrderItemOptionSelection(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 17155 };
        const entity2 = { id: 2555 };

        const compareResult1 = service.compareOrderItemOptionSelection(entity1, entity2);
        const compareResult2 = service.compareOrderItemOptionSelection(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 17155 };
        const entity2 = { id: 17155 };

        const compareResult1 = service.compareOrderItemOptionSelection(entity1, entity2);
        const compareResult2 = service.compareOrderItemOptionSelection(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
