import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IMenuItemOptionValue } from '../menu-item-option-value.model';
import {
  sampleWithFullData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithRequiredData,
} from '../menu-item-option-value.test-samples';

import { MenuItemOptionValueService } from './menu-item-option-value.service';

const requireRestSample: IMenuItemOptionValue = {
  ...sampleWithRequiredData,
};

describe('MenuItemOptionValue Service', () => {
  let service: MenuItemOptionValueService;
  let httpMock: HttpTestingController;
  let expectedResult: IMenuItemOptionValue | IMenuItemOptionValue[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MenuItemOptionValueService);
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

    it('should create a MenuItemOptionValue', () => {
      const menuItemOptionValue = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(menuItemOptionValue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MenuItemOptionValue', () => {
      const menuItemOptionValue = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(menuItemOptionValue).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MenuItemOptionValue', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MenuItemOptionValue', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MenuItemOptionValue', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMenuItemOptionValueToCollectionIfMissing', () => {
      it('should add a MenuItemOptionValue to an empty array', () => {
        const menuItemOptionValue: IMenuItemOptionValue = sampleWithRequiredData;
        expectedResult = service.addMenuItemOptionValueToCollectionIfMissing([], menuItemOptionValue);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menuItemOptionValue);
      });

      it('should not add a MenuItemOptionValue to an array that contains it', () => {
        const menuItemOptionValue: IMenuItemOptionValue = sampleWithRequiredData;
        const menuItemOptionValueCollection: IMenuItemOptionValue[] = [
          {
            ...menuItemOptionValue,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMenuItemOptionValueToCollectionIfMissing(menuItemOptionValueCollection, menuItemOptionValue);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MenuItemOptionValue to an array that doesn't contain it", () => {
        const menuItemOptionValue: IMenuItemOptionValue = sampleWithRequiredData;
        const menuItemOptionValueCollection: IMenuItemOptionValue[] = [sampleWithPartialData];
        expectedResult = service.addMenuItemOptionValueToCollectionIfMissing(menuItemOptionValueCollection, menuItemOptionValue);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menuItemOptionValue);
      });

      it('should add only unique MenuItemOptionValue to an array', () => {
        const menuItemOptionValueArray: IMenuItemOptionValue[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const menuItemOptionValueCollection: IMenuItemOptionValue[] = [sampleWithRequiredData];
        expectedResult = service.addMenuItemOptionValueToCollectionIfMissing(menuItemOptionValueCollection, ...menuItemOptionValueArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const menuItemOptionValue: IMenuItemOptionValue = sampleWithRequiredData;
        const menuItemOptionValue2: IMenuItemOptionValue = sampleWithPartialData;
        expectedResult = service.addMenuItemOptionValueToCollectionIfMissing([], menuItemOptionValue, menuItemOptionValue2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menuItemOptionValue);
        expect(expectedResult).toContain(menuItemOptionValue2);
      });

      it('should accept null and undefined values', () => {
        const menuItemOptionValue: IMenuItemOptionValue = sampleWithRequiredData;
        expectedResult = service.addMenuItemOptionValueToCollectionIfMissing([], null, menuItemOptionValue, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menuItemOptionValue);
      });

      it('should return initial array if no MenuItemOptionValue is added', () => {
        const menuItemOptionValueCollection: IMenuItemOptionValue[] = [sampleWithRequiredData];
        expectedResult = service.addMenuItemOptionValueToCollectionIfMissing(menuItemOptionValueCollection, undefined, null);
        expect(expectedResult).toEqual(menuItemOptionValueCollection);
      });
    });

    describe('compareMenuItemOptionValue', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMenuItemOptionValue(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31564 };
        const entity2 = null;

        const compareResult1 = service.compareMenuItemOptionValue(entity1, entity2);
        const compareResult2 = service.compareMenuItemOptionValue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31564 };
        const entity2 = { id: 30184 };

        const compareResult1 = service.compareMenuItemOptionValue(entity1, entity2);
        const compareResult2 = service.compareMenuItemOptionValue(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31564 };
        const entity2 = { id: 31564 };

        const compareResult1 = service.compareMenuItemOptionValue(entity1, entity2);
        const compareResult2 = service.compareMenuItemOptionValue(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
