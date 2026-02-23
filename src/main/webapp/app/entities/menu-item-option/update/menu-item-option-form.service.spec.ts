import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../menu-item-option.test-samples';

import { MenuItemOptionFormService } from './menu-item-option-form.service';

describe('MenuItemOption Form Service', () => {
  let service: MenuItemOptionFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MenuItemOptionFormService);
  });

  describe('Service methods', () => {
    describe('createMenuItemOptionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMenuItemOptionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            isRequired: expect.any(Object),
            maxSelections: expect.any(Object),
            displayOrder: expect.any(Object),
            menuItem: expect.any(Object),
          }),
        );
      });

      it('passing IMenuItemOption should create a new form with FormGroup', () => {
        const formGroup = service.createMenuItemOptionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            isRequired: expect.any(Object),
            maxSelections: expect.any(Object),
            displayOrder: expect.any(Object),
            menuItem: expect.any(Object),
          }),
        );
      });
    });

    describe('getMenuItemOption', () => {
      it('should return NewMenuItemOption for default MenuItemOption initial value', () => {
        const formGroup = service.createMenuItemOptionFormGroup(sampleWithNewData);

        const menuItemOption = service.getMenuItemOption(formGroup) as any;

        expect(menuItemOption).toMatchObject(sampleWithNewData);
      });

      it('should return NewMenuItemOption for empty MenuItemOption initial value', () => {
        const formGroup = service.createMenuItemOptionFormGroup();

        const menuItemOption = service.getMenuItemOption(formGroup) as any;

        expect(menuItemOption).toMatchObject({});
      });

      it('should return IMenuItemOption', () => {
        const formGroup = service.createMenuItemOptionFormGroup(sampleWithRequiredData);

        const menuItemOption = service.getMenuItemOption(formGroup) as any;

        expect(menuItemOption).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMenuItemOption should not enable id FormControl', () => {
        const formGroup = service.createMenuItemOptionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMenuItemOption should disable id FormControl', () => {
        const formGroup = service.createMenuItemOptionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
