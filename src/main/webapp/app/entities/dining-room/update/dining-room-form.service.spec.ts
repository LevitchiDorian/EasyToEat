import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../dining-room.test-samples';

import { DiningRoomFormService } from './dining-room-form.service';

describe('DiningRoom Form Service', () => {
  let service: DiningRoomFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DiningRoomFormService);
  });

  describe('Service methods', () => {
    describe('createDiningRoomFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDiningRoomFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            floor: expect.any(Object),
            capacity: expect.any(Object),
            isActive: expect.any(Object),
            floorPlanUrl: expect.any(Object),
            widthPx: expect.any(Object),
            heightPx: expect.any(Object),
            location: expect.any(Object),
          }),
        );
      });

      it('passing IDiningRoom should create a new form with FormGroup', () => {
        const formGroup = service.createDiningRoomFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            floor: expect.any(Object),
            capacity: expect.any(Object),
            isActive: expect.any(Object),
            floorPlanUrl: expect.any(Object),
            widthPx: expect.any(Object),
            heightPx: expect.any(Object),
            location: expect.any(Object),
          }),
        );
      });
    });

    describe('getDiningRoom', () => {
      it('should return NewDiningRoom for default DiningRoom initial value', () => {
        const formGroup = service.createDiningRoomFormGroup(sampleWithNewData);

        const diningRoom = service.getDiningRoom(formGroup) as any;

        expect(diningRoom).toMatchObject(sampleWithNewData);
      });

      it('should return NewDiningRoom for empty DiningRoom initial value', () => {
        const formGroup = service.createDiningRoomFormGroup();

        const diningRoom = service.getDiningRoom(formGroup) as any;

        expect(diningRoom).toMatchObject({});
      });

      it('should return IDiningRoom', () => {
        const formGroup = service.createDiningRoomFormGroup(sampleWithRequiredData);

        const diningRoom = service.getDiningRoom(formGroup) as any;

        expect(diningRoom).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDiningRoom should not enable id FormControl', () => {
        const formGroup = service.createDiningRoomFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDiningRoom should disable id FormControl', () => {
        const formGroup = service.createDiningRoomFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
