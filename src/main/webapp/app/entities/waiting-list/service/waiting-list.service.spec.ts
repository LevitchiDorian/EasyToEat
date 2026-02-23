import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IWaitingList } from '../waiting-list.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../waiting-list.test-samples';

import { RestWaitingList, WaitingListService } from './waiting-list.service';

const requireRestSample: RestWaitingList = {
  ...sampleWithRequiredData,
  requestedDate: sampleWithRequiredData.requestedDate?.format(DATE_FORMAT),
  expiresAt: sampleWithRequiredData.expiresAt?.toJSON(),
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('WaitingList Service', () => {
  let service: WaitingListService;
  let httpMock: HttpTestingController;
  let expectedResult: IWaitingList | IWaitingList[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(WaitingListService);
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

    it('should create a WaitingList', () => {
      const waitingList = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(waitingList).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a WaitingList', () => {
      const waitingList = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(waitingList).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a WaitingList', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of WaitingList', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a WaitingList', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addWaitingListToCollectionIfMissing', () => {
      it('should add a WaitingList to an empty array', () => {
        const waitingList: IWaitingList = sampleWithRequiredData;
        expectedResult = service.addWaitingListToCollectionIfMissing([], waitingList);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(waitingList);
      });

      it('should not add a WaitingList to an array that contains it', () => {
        const waitingList: IWaitingList = sampleWithRequiredData;
        const waitingListCollection: IWaitingList[] = [
          {
            ...waitingList,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addWaitingListToCollectionIfMissing(waitingListCollection, waitingList);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a WaitingList to an array that doesn't contain it", () => {
        const waitingList: IWaitingList = sampleWithRequiredData;
        const waitingListCollection: IWaitingList[] = [sampleWithPartialData];
        expectedResult = service.addWaitingListToCollectionIfMissing(waitingListCollection, waitingList);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(waitingList);
      });

      it('should add only unique WaitingList to an array', () => {
        const waitingListArray: IWaitingList[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const waitingListCollection: IWaitingList[] = [sampleWithRequiredData];
        expectedResult = service.addWaitingListToCollectionIfMissing(waitingListCollection, ...waitingListArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const waitingList: IWaitingList = sampleWithRequiredData;
        const waitingList2: IWaitingList = sampleWithPartialData;
        expectedResult = service.addWaitingListToCollectionIfMissing([], waitingList, waitingList2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(waitingList);
        expect(expectedResult).toContain(waitingList2);
      });

      it('should accept null and undefined values', () => {
        const waitingList: IWaitingList = sampleWithRequiredData;
        expectedResult = service.addWaitingListToCollectionIfMissing([], null, waitingList, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(waitingList);
      });

      it('should return initial array if no WaitingList is added', () => {
        const waitingListCollection: IWaitingList[] = [sampleWithRequiredData];
        expectedResult = service.addWaitingListToCollectionIfMissing(waitingListCollection, undefined, null);
        expect(expectedResult).toEqual(waitingListCollection);
      });
    });

    describe('compareWaitingList', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareWaitingList(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15410 };
        const entity2 = null;

        const compareResult1 = service.compareWaitingList(entity1, entity2);
        const compareResult2 = service.compareWaitingList(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15410 };
        const entity2 = { id: 28180 };

        const compareResult1 = service.compareWaitingList(entity1, entity2);
        const compareResult2 = service.compareWaitingList(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15410 };
        const entity2 = { id: 15410 };

        const compareResult1 = service.compareWaitingList(entity1, entity2);
        const compareResult2 = service.compareWaitingList(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
