import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../location-menu-item-override.test-samples';

import { LocationMenuItemOverrideFormService } from './location-menu-item-override-form.service';

describe('LocationMenuItemOverride Form Service', () => {
  let service: LocationMenuItemOverrideFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LocationMenuItemOverrideFormService);
  });

  describe('Service methods', () => {
    describe('createLocationMenuItemOverrideFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLocationMenuItemOverrideFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            isAvailableAtLocation: expect.any(Object),
            priceOverride: expect.any(Object),
            preparationTimeOverride: expect.any(Object),
            notes: expect.any(Object),
            menuItem: expect.any(Object),
            location: expect.any(Object),
          }),
        );
      });

      it('passing ILocationMenuItemOverride should create a new form with FormGroup', () => {
        const formGroup = service.createLocationMenuItemOverrideFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            isAvailableAtLocation: expect.any(Object),
            priceOverride: expect.any(Object),
            preparationTimeOverride: expect.any(Object),
            notes: expect.any(Object),
            menuItem: expect.any(Object),
            location: expect.any(Object),
          }),
        );
      });
    });

    describe('getLocationMenuItemOverride', () => {
      it('should return NewLocationMenuItemOverride for default LocationMenuItemOverride initial value', () => {
        const formGroup = service.createLocationMenuItemOverrideFormGroup(sampleWithNewData);

        const locationMenuItemOverride = service.getLocationMenuItemOverride(formGroup) as any;

        expect(locationMenuItemOverride).toMatchObject(sampleWithNewData);
      });

      it('should return NewLocationMenuItemOverride for empty LocationMenuItemOverride initial value', () => {
        const formGroup = service.createLocationMenuItemOverrideFormGroup();

        const locationMenuItemOverride = service.getLocationMenuItemOverride(formGroup) as any;

        expect(locationMenuItemOverride).toMatchObject({});
      });

      it('should return ILocationMenuItemOverride', () => {
        const formGroup = service.createLocationMenuItemOverrideFormGroup(sampleWithRequiredData);

        const locationMenuItemOverride = service.getLocationMenuItemOverride(formGroup) as any;

        expect(locationMenuItemOverride).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILocationMenuItemOverride should not enable id FormControl', () => {
        const formGroup = service.createLocationMenuItemOverrideFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLocationMenuItemOverride should disable id FormControl', () => {
        const formGroup = service.createLocationMenuItemOverrideFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
