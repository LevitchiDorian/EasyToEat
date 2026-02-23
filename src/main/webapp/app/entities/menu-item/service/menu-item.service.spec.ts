import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IMenuItem } from '../menu-item.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../menu-item.test-samples';

import { MenuItemService } from './menu-item.service';

const requireRestSample: IMenuItem = {
  ...sampleWithRequiredData,
};

describe('MenuItem Service', () => {
  let service: MenuItemService;
  let httpMock: HttpTestingController;
  let expectedResult: IMenuItem | IMenuItem[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MenuItemService);
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

    it('should create a MenuItem', () => {
      const menuItem = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(menuItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MenuItem', () => {
      const menuItem = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(menuItem).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MenuItem', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MenuItem', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MenuItem', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMenuItemToCollectionIfMissing', () => {
      it('should add a MenuItem to an empty array', () => {
        const menuItem: IMenuItem = sampleWithRequiredData;
        expectedResult = service.addMenuItemToCollectionIfMissing([], menuItem);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menuItem);
      });

      it('should not add a MenuItem to an array that contains it', () => {
        const menuItem: IMenuItem = sampleWithRequiredData;
        const menuItemCollection: IMenuItem[] = [
          {
            ...menuItem,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMenuItemToCollectionIfMissing(menuItemCollection, menuItem);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MenuItem to an array that doesn't contain it", () => {
        const menuItem: IMenuItem = sampleWithRequiredData;
        const menuItemCollection: IMenuItem[] = [sampleWithPartialData];
        expectedResult = service.addMenuItemToCollectionIfMissing(menuItemCollection, menuItem);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menuItem);
      });

      it('should add only unique MenuItem to an array', () => {
        const menuItemArray: IMenuItem[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const menuItemCollection: IMenuItem[] = [sampleWithRequiredData];
        expectedResult = service.addMenuItemToCollectionIfMissing(menuItemCollection, ...menuItemArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const menuItem: IMenuItem = sampleWithRequiredData;
        const menuItem2: IMenuItem = sampleWithPartialData;
        expectedResult = service.addMenuItemToCollectionIfMissing([], menuItem, menuItem2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menuItem);
        expect(expectedResult).toContain(menuItem2);
      });

      it('should accept null and undefined values', () => {
        const menuItem: IMenuItem = sampleWithRequiredData;
        expectedResult = service.addMenuItemToCollectionIfMissing([], null, menuItem, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menuItem);
      });

      it('should return initial array if no MenuItem is added', () => {
        const menuItemCollection: IMenuItem[] = [sampleWithRequiredData];
        expectedResult = service.addMenuItemToCollectionIfMissing(menuItemCollection, undefined, null);
        expect(expectedResult).toEqual(menuItemCollection);
      });
    });

    describe('compareMenuItem', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMenuItem(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 11248 };
        const entity2 = null;

        const compareResult1 = service.compareMenuItem(entity1, entity2);
        const compareResult2 = service.compareMenuItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 11248 };
        const entity2 = { id: 12993 };

        const compareResult1 = service.compareMenuItem(entity1, entity2);
        const compareResult2 = service.compareMenuItem(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 11248 };
        const entity2 = { id: 11248 };

        const compareResult1 = service.compareMenuItem(entity1, entity2);
        const compareResult2 = service.compareMenuItem(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
