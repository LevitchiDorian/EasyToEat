import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../menu-item-option-value.test-samples';

import { MenuItemOptionValueFormService } from './menu-item-option-value-form.service';

describe('MenuItemOptionValue Form Service', () => {
  let service: MenuItemOptionValueFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MenuItemOptionValueFormService);
  });

  describe('Service methods', () => {
    describe('createMenuItemOptionValueFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMenuItemOptionValueFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            priceAdjustment: expect.any(Object),
            isDefault: expect.any(Object),
            isAvailable: expect.any(Object),
            displayOrder: expect.any(Object),
            option: expect.any(Object),
          }),
        );
      });

      it('passing IMenuItemOptionValue should create a new form with FormGroup', () => {
        const formGroup = service.createMenuItemOptionValueFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            label: expect.any(Object),
            priceAdjustment: expect.any(Object),
            isDefault: expect.any(Object),
            isAvailable: expect.any(Object),
            displayOrder: expect.any(Object),
            option: expect.any(Object),
          }),
        );
      });
    });

    describe('getMenuItemOptionValue', () => {
      it('should return NewMenuItemOptionValue for default MenuItemOptionValue initial value', () => {
        const formGroup = service.createMenuItemOptionValueFormGroup(sampleWithNewData);

        const menuItemOptionValue = service.getMenuItemOptionValue(formGroup) as any;

        expect(menuItemOptionValue).toMatchObject(sampleWithNewData);
      });

      it('should return NewMenuItemOptionValue for empty MenuItemOptionValue initial value', () => {
        const formGroup = service.createMenuItemOptionValueFormGroup();

        const menuItemOptionValue = service.getMenuItemOptionValue(formGroup) as any;

        expect(menuItemOptionValue).toMatchObject({});
      });

      it('should return IMenuItemOptionValue', () => {
        const formGroup = service.createMenuItemOptionValueFormGroup(sampleWithRequiredData);

        const menuItemOptionValue = service.getMenuItemOptionValue(formGroup) as any;

        expect(menuItemOptionValue).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMenuItemOptionValue should not enable id FormControl', () => {
        const formGroup = service.createMenuItemOptionValueFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMenuItemOptionValue should disable id FormControl', () => {
        const formGroup = service.createMenuItemOptionValueFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
