import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../menu-item-allergen.test-samples';

import { MenuItemAllergenFormService } from './menu-item-allergen-form.service';

describe('MenuItemAllergen Form Service', () => {
  let service: MenuItemAllergenFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MenuItemAllergenFormService);
  });

  describe('Service methods', () => {
    describe('createMenuItemAllergenFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMenuItemAllergenFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            allergen: expect.any(Object),
            notes: expect.any(Object),
            menuItem: expect.any(Object),
          }),
        );
      });

      it('passing IMenuItemAllergen should create a new form with FormGroup', () => {
        const formGroup = service.createMenuItemAllergenFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            allergen: expect.any(Object),
            notes: expect.any(Object),
            menuItem: expect.any(Object),
          }),
        );
      });
    });

    describe('getMenuItemAllergen', () => {
      it('should return NewMenuItemAllergen for default MenuItemAllergen initial value', () => {
        const formGroup = service.createMenuItemAllergenFormGroup(sampleWithNewData);

        const menuItemAllergen = service.getMenuItemAllergen(formGroup) as any;

        expect(menuItemAllergen).toMatchObject(sampleWithNewData);
      });

      it('should return NewMenuItemAllergen for empty MenuItemAllergen initial value', () => {
        const formGroup = service.createMenuItemAllergenFormGroup();

        const menuItemAllergen = service.getMenuItemAllergen(formGroup) as any;

        expect(menuItemAllergen).toMatchObject({});
      });

      it('should return IMenuItemAllergen', () => {
        const formGroup = service.createMenuItemAllergenFormGroup(sampleWithRequiredData);

        const menuItemAllergen = service.getMenuItemAllergen(formGroup) as any;

        expect(menuItemAllergen).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMenuItemAllergen should not enable id FormControl', () => {
        const formGroup = service.createMenuItemAllergenFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMenuItemAllergen should disable id FormControl', () => {
        const formGroup = service.createMenuItemAllergenFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
