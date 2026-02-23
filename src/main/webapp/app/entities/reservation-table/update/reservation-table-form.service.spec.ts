import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../reservation-table.test-samples';

import { ReservationTableFormService } from './reservation-table-form.service';

describe('ReservationTable Form Service', () => {
  let service: ReservationTableFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReservationTableFormService);
  });

  describe('Service methods', () => {
    describe('createReservationTableFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReservationTableFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            assignedAt: expect.any(Object),
            notes: expect.any(Object),
            table: expect.any(Object),
            reservation: expect.any(Object),
          }),
        );
      });

      it('passing IReservationTable should create a new form with FormGroup', () => {
        const formGroup = service.createReservationTableFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            assignedAt: expect.any(Object),
            notes: expect.any(Object),
            table: expect.any(Object),
            reservation: expect.any(Object),
          }),
        );
      });
    });

    describe('getReservationTable', () => {
      it('should return NewReservationTable for default ReservationTable initial value', () => {
        const formGroup = service.createReservationTableFormGroup(sampleWithNewData);

        const reservationTable = service.getReservationTable(formGroup) as any;

        expect(reservationTable).toMatchObject(sampleWithNewData);
      });

      it('should return NewReservationTable for empty ReservationTable initial value', () => {
        const formGroup = service.createReservationTableFormGroup();

        const reservationTable = service.getReservationTable(formGroup) as any;

        expect(reservationTable).toMatchObject({});
      });

      it('should return IReservationTable', () => {
        const formGroup = service.createReservationTableFormGroup(sampleWithRequiredData);

        const reservationTable = service.getReservationTable(formGroup) as any;

        expect(reservationTable).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReservationTable should not enable id FormControl', () => {
        const formGroup = service.createReservationTableFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewReservationTable should disable id FormControl', () => {
        const formGroup = service.createReservationTableFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
