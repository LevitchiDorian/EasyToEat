import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IMenuItemOption } from '../menu-item-option.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../menu-item-option.test-samples';

import { MenuItemOptionService } from './menu-item-option.service';

const requireRestSample: IMenuItemOption = {
  ...sampleWithRequiredData,
};

describe('MenuItemOption Service', () => {
  let service: MenuItemOptionService;
  let httpMock: HttpTestingController;
  let expectedResult: IMenuItemOption | IMenuItemOption[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(MenuItemOptionService);
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

    it('should create a MenuItemOption', () => {
      const menuItemOption = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(menuItemOption).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a MenuItemOption', () => {
      const menuItemOption = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(menuItemOption).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a MenuItemOption', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of MenuItemOption', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a MenuItemOption', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMenuItemOptionToCollectionIfMissing', () => {
      it('should add a MenuItemOption to an empty array', () => {
        const menuItemOption: IMenuItemOption = sampleWithRequiredData;
        expectedResult = service.addMenuItemOptionToCollectionIfMissing([], menuItemOption);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menuItemOption);
      });

      it('should not add a MenuItemOption to an array that contains it', () => {
        const menuItemOption: IMenuItemOption = sampleWithRequiredData;
        const menuItemOptionCollection: IMenuItemOption[] = [
          {
            ...menuItemOption,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMenuItemOptionToCollectionIfMissing(menuItemOptionCollection, menuItemOption);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a MenuItemOption to an array that doesn't contain it", () => {
        const menuItemOption: IMenuItemOption = sampleWithRequiredData;
        const menuItemOptionCollection: IMenuItemOption[] = [sampleWithPartialData];
        expectedResult = service.addMenuItemOptionToCollectionIfMissing(menuItemOptionCollection, menuItemOption);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menuItemOption);
      });

      it('should add only unique MenuItemOption to an array', () => {
        const menuItemOptionArray: IMenuItemOption[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const menuItemOptionCollection: IMenuItemOption[] = [sampleWithRequiredData];
        expectedResult = service.addMenuItemOptionToCollectionIfMissing(menuItemOptionCollection, ...menuItemOptionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const menuItemOption: IMenuItemOption = sampleWithRequiredData;
        const menuItemOption2: IMenuItemOption = sampleWithPartialData;
        expectedResult = service.addMenuItemOptionToCollectionIfMissing([], menuItemOption, menuItemOption2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menuItemOption);
        expect(expectedResult).toContain(menuItemOption2);
      });

      it('should accept null and undefined values', () => {
        const menuItemOption: IMenuItemOption = sampleWithRequiredData;
        expectedResult = service.addMenuItemOptionToCollectionIfMissing([], null, menuItemOption, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menuItemOption);
      });

      it('should return initial array if no MenuItemOption is added', () => {
        const menuItemOptionCollection: IMenuItemOption[] = [sampleWithRequiredData];
        expectedResult = service.addMenuItemOptionToCollectionIfMissing(menuItemOptionCollection, undefined, null);
        expect(expectedResult).toEqual(menuItemOptionCollection);
      });
    });

    describe('compareMenuItemOption', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMenuItemOption(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 20913 };
        const entity2 = null;

        const compareResult1 = service.compareMenuItemOption(entity1, entity2);
        const compareResult2 = service.compareMenuItemOption(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 20913 };
        const entity2 = { id: 195 };

        const compareResult1 = service.compareMenuItemOption(entity1, entity2);
        const compareResult2 = service.compareMenuItemOption(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 20913 };
        const entity2 = { id: 20913 };

        const compareResult1 = service.compareMenuItemOption(entity1, entity2);
        const compareResult2 = service.compareMenuItemOption(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
