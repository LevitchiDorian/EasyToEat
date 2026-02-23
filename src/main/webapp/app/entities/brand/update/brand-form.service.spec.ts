import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../brand.test-samples';

import { BrandFormService } from './brand-form.service';

describe('Brand Form Service', () => {
  let service: BrandFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BrandFormService);
  });

  describe('Service methods', () => {
    describe('createBrandFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBrandFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            logoUrl: expect.any(Object),
            coverImageUrl: expect.any(Object),
            primaryColor: expect.any(Object),
            secondaryColor: expect.any(Object),
            website: expect.any(Object),
            contactEmail: expect.any(Object),
            contactPhone: expect.any(Object),
            defaultReservationDuration: expect.any(Object),
            maxAdvanceBookingDays: expect.any(Object),
            cancellationDeadlineHours: expect.any(Object),
            isActive: expect.any(Object),
            createdAt: expect.any(Object),
          }),
        );
      });

      it('passing IBrand should create a new form with FormGroup', () => {
        const formGroup = service.createBrandFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            logoUrl: expect.any(Object),
            coverImageUrl: expect.any(Object),
            primaryColor: expect.any(Object),
            secondaryColor: expect.any(Object),
            website: expect.any(Object),
            contactEmail: expect.any(Object),
            contactPhone: expect.any(Object),
            defaultReservationDuration: expect.any(Object),
            maxAdvanceBookingDays: expect.any(Object),
            cancellationDeadlineHours: expect.any(Object),
            isActive: expect.any(Object),
            createdAt: expect.any(Object),
          }),
        );
      });
    });

    describe('getBrand', () => {
      it('should return NewBrand for default Brand initial value', () => {
        const formGroup = service.createBrandFormGroup(sampleWithNewData);

        const brand = service.getBrand(formGroup) as any;

        expect(brand).toMatchObject(sampleWithNewData);
      });

      it('should return NewBrand for empty Brand initial value', () => {
        const formGroup = service.createBrandFormGroup();

        const brand = service.getBrand(formGroup) as any;

        expect(brand).toMatchObject({});
      });

      it('should return IBrand', () => {
        const formGroup = service.createBrandFormGroup(sampleWithRequiredData);

        const brand = service.getBrand(formGroup) as any;

        expect(brand).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBrand should not enable id FormControl', () => {
        const formGroup = service.createBrandFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBrand should disable id FormControl', () => {
        const formGroup = service.createBrandFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
