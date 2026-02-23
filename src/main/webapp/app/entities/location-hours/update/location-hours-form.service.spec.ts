import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../location-hours.test-samples';

import { LocationHoursFormService } from './location-hours-form.service';

describe('LocationHours Form Service', () => {
  let service: LocationHoursFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LocationHoursFormService);
  });

  describe('Service methods', () => {
    describe('createLocationHoursFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLocationHoursFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dayOfWeek: expect.any(Object),
            openTime: expect.any(Object),
            closeTime: expect.any(Object),
            isClosed: expect.any(Object),
            specialNote: expect.any(Object),
            location: expect.any(Object),
          }),
        );
      });

      it('passing ILocationHours should create a new form with FormGroup', () => {
        const formGroup = service.createLocationHoursFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            dayOfWeek: expect.any(Object),
            openTime: expect.any(Object),
            closeTime: expect.any(Object),
            isClosed: expect.any(Object),
            specialNote: expect.any(Object),
            location: expect.any(Object),
          }),
        );
      });
    });

    describe('getLocationHours', () => {
      it('should return NewLocationHours for default LocationHours initial value', () => {
        const formGroup = service.createLocationHoursFormGroup(sampleWithNewData);

        const locationHours = service.getLocationHours(formGroup) as any;

        expect(locationHours).toMatchObject(sampleWithNewData);
      });

      it('should return NewLocationHours for empty LocationHours initial value', () => {
        const formGroup = service.createLocationHoursFormGroup();

        const locationHours = service.getLocationHours(formGroup) as any;

        expect(locationHours).toMatchObject({});
      });

      it('should return ILocationHours', () => {
        const formGroup = service.createLocationHoursFormGroup(sampleWithRequiredData);

        const locationHours = service.getLocationHours(formGroup) as any;

        expect(locationHours).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILocationHours should not enable id FormControl', () => {
        const formGroup = service.createLocationHoursFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLocationHours should disable id FormControl', () => {
        const formGroup = service.createLocationHoursFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
