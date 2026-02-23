import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../waiting-list.test-samples';

import { WaitingListFormService } from './waiting-list-form.service';

describe('WaitingList Form Service', () => {
  let service: WaitingListFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WaitingListFormService);
  });

  describe('Service methods', () => {
    describe('createWaitingListFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createWaitingListFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            requestedDate: expect.any(Object),
            requestedTime: expect.any(Object),
            partySize: expect.any(Object),
            notes: expect.any(Object),
            isNotified: expect.any(Object),
            expiresAt: expect.any(Object),
            createdAt: expect.any(Object),
            location: expect.any(Object),
            client: expect.any(Object),
            room: expect.any(Object),
          }),
        );
      });

      it('passing IWaitingList should create a new form with FormGroup', () => {
        const formGroup = service.createWaitingListFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            requestedDate: expect.any(Object),
            requestedTime: expect.any(Object),
            partySize: expect.any(Object),
            notes: expect.any(Object),
            isNotified: expect.any(Object),
            expiresAt: expect.any(Object),
            createdAt: expect.any(Object),
            location: expect.any(Object),
            client: expect.any(Object),
            room: expect.any(Object),
          }),
        );
      });
    });

    describe('getWaitingList', () => {
      it('should return NewWaitingList for default WaitingList initial value', () => {
        const formGroup = service.createWaitingListFormGroup(sampleWithNewData);

        const waitingList = service.getWaitingList(formGroup) as any;

        expect(waitingList).toMatchObject(sampleWithNewData);
      });

      it('should return NewWaitingList for empty WaitingList initial value', () => {
        const formGroup = service.createWaitingListFormGroup();

        const waitingList = service.getWaitingList(formGroup) as any;

        expect(waitingList).toMatchObject({});
      });

      it('should return IWaitingList', () => {
        const formGroup = service.createWaitingListFormGroup(sampleWithRequiredData);

        const waitingList = service.getWaitingList(formGroup) as any;

        expect(waitingList).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IWaitingList should not enable id FormControl', () => {
        const formGroup = service.createWaitingListFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewWaitingList should disable id FormControl', () => {
        const formGroup = service.createWaitingListFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
