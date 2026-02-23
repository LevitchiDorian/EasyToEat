import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IMenuItemAllergen } from '../menu-item-allergen.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../menu-item-allergen.test-samples';

import { MenuItemAllergenService } from './menu-item-allergen.service';

const requireRestSample: IMenuItemAllergen = {
  ...sampleWithRequiredData,
};

describe('MenuItemAllergen Service', () => {
  let service: MenuItemAllergenService;
  let httpMock: HttpTestingController;
  let expectedResult: IMenuItemAllergen | IMenuItemAllergen[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MenuItemAllergenService);
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

    it('should create a MenuItemAllergen', () => {
      const menuItemAllergen = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(menuItemAllergen).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MenuItemAllergen', () => {
      const menuItemAllergen = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(menuItemAllergen).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MenuItemAllergen', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MenuItemAllergen', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MenuItemAllergen', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMenuItemAllergenToCollectionIfMissing', () => {
      it('should add a MenuItemAllergen to an empty array', () => {
        const menuItemAllergen: IMenuItemAllergen = sampleWithRequiredData;
        expectedResult = service.addMenuItemAllergenToCollectionIfMissing([], menuItemAllergen);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menuItemAllergen);
      });

      it('should not add a MenuItemAllergen to an array that contains it', () => {
        const menuItemAllergen: IMenuItemAllergen = sampleWithRequiredData;
        const menuItemAllergenCollection: IMenuItemAllergen[] = [
          {
            ...menuItemAllergen,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMenuItemAllergenToCollectionIfMissing(menuItemAllergenCollection, menuItemAllergen);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MenuItemAllergen to an array that doesn't contain it", () => {
        const menuItemAllergen: IMenuItemAllergen = sampleWithRequiredData;
        const menuItemAllergenCollection: IMenuItemAllergen[] = [sampleWithPartialData];
        expectedResult = service.addMenuItemAllergenToCollectionIfMissing(menuItemAllergenCollection, menuItemAllergen);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menuItemAllergen);
      });

      it('should add only unique MenuItemAllergen to an array', () => {
        const menuItemAllergenArray: IMenuItemAllergen[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const menuItemAllergenCollection: IMenuItemAllergen[] = [sampleWithRequiredData];
        expectedResult = service.addMenuItemAllergenToCollectionIfMissing(menuItemAllergenCollection, ...menuItemAllergenArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const menuItemAllergen: IMenuItemAllergen = sampleWithRequiredData;
        const menuItemAllergen2: IMenuItemAllergen = sampleWithPartialData;
        expectedResult = service.addMenuItemAllergenToCollectionIfMissing([], menuItemAllergen, menuItemAllergen2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menuItemAllergen);
        expect(expectedResult).toContain(menuItemAllergen2);
      });

      it('should accept null and undefined values', () => {
        const menuItemAllergen: IMenuItemAllergen = sampleWithRequiredData;
        expectedResult = service.addMenuItemAllergenToCollectionIfMissing([], null, menuItemAllergen, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menuItemAllergen);
      });

      it('should return initial array if no MenuItemAllergen is added', () => {
        const menuItemAllergenCollection: IMenuItemAllergen[] = [sampleWithRequiredData];
        expectedResult = service.addMenuItemAllergenToCollectionIfMissing(menuItemAllergenCollection, undefined, null);
        expect(expectedResult).toEqual(menuItemAllergenCollection);
      });
    });

    describe('compareMenuItemAllergen', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMenuItemAllergen(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 13353 };
        const entity2 = null;

        const compareResult1 = service.compareMenuItemAllergen(entity1, entity2);
        const compareResult2 = service.compareMenuItemAllergen(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 13353 };
        const entity2 = { id: 17370 };

        const compareResult1 = service.compareMenuItemAllergen(entity1, entity2);
        const compareResult2 = service.compareMenuItemAllergen(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 13353 };
        const entity2 = { id: 13353 };

        const compareResult1 = service.compareMenuItemAllergen(entity1, entity2);
        const compareResult2 = service.compareMenuItemAllergen(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
