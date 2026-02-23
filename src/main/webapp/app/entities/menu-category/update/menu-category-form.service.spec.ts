import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../menu-category.test-samples';

import { MenuCategoryFormService } from './menu-category-form.service';

describe('MenuCategory Form Service', () => {
  let service: MenuCategoryFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MenuCategoryFormService);
  });

  describe('Service methods', () => {
    describe('createMenuCategoryFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createMenuCategoryFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            imageUrl: expect.any(Object),
            displayOrder: expect.any(Object),
            isActive: expect.any(Object),
            parent: expect.any(Object),
            brand: expect.any(Object),
          }),
        );
      });

      it('passing IMenuCategory should create a new form with FormGroup', () => {
        const formGroup = service.createMenuCategoryFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            imageUrl: expect.any(Object),
            displayOrder: expect.any(Object),
            isActive: expect.any(Object),
            parent: expect.any(Object),
            brand: expect.any(Object),
          }),
        );
      });
    });

    describe('getMenuCategory', () => {
      it('should return NewMenuCategory for default MenuCategory initial value', () => {
        const formGroup = service.createMenuCategoryFormGroup(sampleWithNewData);

        const menuCategory = service.getMenuCategory(formGroup) as any;

        expect(menuCategory).toMatchObject(sampleWithNewData);
      });

      it('should return NewMenuCategory for empty MenuCategory initial value', () => {
        const formGroup = service.createMenuCategoryFormGroup();

        const menuCategory = service.getMenuCategory(formGroup) as any;

        expect(menuCategory).toMatchObject({});
      });

      it('should return IMenuCategory', () => {
        const formGroup = service.createMenuCategoryFormGroup(sampleWithRequiredData);

        const menuCategory = service.getMenuCategory(formGroup) as any;

        expect(menuCategory).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IMenuCategory should not enable id FormControl', () => {
        const formGroup = service.createMenuCategoryFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewMenuCategory should disable id FormControl', () => {
        const formGroup = service.createMenuCategoryFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
