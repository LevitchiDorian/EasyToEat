import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IDiningRoom } from '../dining-room.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../dining-room.test-samples';

import { DiningRoomService } from './dining-room.service';

const requireRestSample: IDiningRoom = {
  ...sampleWithRequiredData,
};

describe('DiningRoom Service', () => {
  let service: DiningRoomService;
  let httpMock: HttpTestingController;
  let expectedResult: IDiningRoom | IDiningRoom[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DiningRoomService);
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

    it('should create a DiningRoom', () => {
      const diningRoom = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(diningRoom).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DiningRoom', () => {
      const diningRoom = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(diningRoom).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DiningRoom', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DiningRoom', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DiningRoom', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDiningRoomToCollectionIfMissing', () => {
      it('should add a DiningRoom to an empty array', () => {
        const diningRoom: IDiningRoom = sampleWithRequiredData;
        expectedResult = service.addDiningRoomToCollectionIfMissing([], diningRoom);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(diningRoom);
      });

      it('should not add a DiningRoom to an array that contains it', () => {
        const diningRoom: IDiningRoom = sampleWithRequiredData;
        const diningRoomCollection: IDiningRoom[] = [
          {
            ...diningRoom,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDiningRoomToCollectionIfMissing(diningRoomCollection, diningRoom);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DiningRoom to an array that doesn't contain it", () => {
        const diningRoom: IDiningRoom = sampleWithRequiredData;
        const diningRoomCollection: IDiningRoom[] = [sampleWithPartialData];
        expectedResult = service.addDiningRoomToCollectionIfMissing(diningRoomCollection, diningRoom);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(diningRoom);
      });

      it('should add only unique DiningRoom to an array', () => {
        const diningRoomArray: IDiningRoom[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const diningRoomCollection: IDiningRoom[] = [sampleWithRequiredData];
        expectedResult = service.addDiningRoomToCollectionIfMissing(diningRoomCollection, ...diningRoomArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const diningRoom: IDiningRoom = sampleWithRequiredData;
        const diningRoom2: IDiningRoom = sampleWithPartialData;
        expectedResult = service.addDiningRoomToCollectionIfMissing([], diningRoom, diningRoom2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(diningRoom);
        expect(expectedResult).toContain(diningRoom2);
      });

      it('should accept null and undefined values', () => {
        const diningRoom: IDiningRoom = sampleWithRequiredData;
        expectedResult = service.addDiningRoomToCollectionIfMissing([], null, diningRoom, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(diningRoom);
      });

      it('should return initial array if no DiningRoom is added', () => {
        const diningRoomCollection: IDiningRoom[] = [sampleWithRequiredData];
        expectedResult = service.addDiningRoomToCollectionIfMissing(diningRoomCollection, undefined, null);
        expect(expectedResult).toEqual(diningRoomCollection);
      });
    });

    describe('compareDiningRoom', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDiningRoom(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 3993 };
        const entity2 = null;

        const compareResult1 = service.compareDiningRoom(entity1, entity2);
        const compareResult2 = service.compareDiningRoom(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 3993 };
        const entity2 = { id: 12477 };

        const compareResult1 = service.compareDiningRoom(entity1, entity2);
        const compareResult2 = service.compareDiningRoom(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 3993 };
        const entity2 = { id: 3993 };

        const compareResult1 = service.compareDiningRoom(entity1, entity2);
        const compareResult2 = service.compareDiningRoom(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
