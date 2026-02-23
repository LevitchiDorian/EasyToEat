import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMenuCategory, NewMenuCategory } from '../menu-category.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMenuCategory for edit and NewMenuCategoryFormGroupInput for create.
 */
type MenuCategoryFormGroupInput = IMenuCategory | PartialWithRequiredKeyOf<NewMenuCategory>;

type MenuCategoryFormDefaults = Pick<NewMenuCategory, 'id' | 'isActive'>;

type MenuCategoryFormGroupContent = {
  id: FormControl<IMenuCategory['id'] | NewMenuCategory['id']>;
  name: FormControl<IMenuCategory['name']>;
  description: FormControl<IMenuCategory['description']>;
  imageUrl: FormControl<IMenuCategory['imageUrl']>;
  displayOrder: FormControl<IMenuCategory['displayOrder']>;
  isActive: FormControl<IMenuCategory['isActive']>;
  parent: FormControl<IMenuCategory['parent']>;
  brand: FormControl<IMenuCategory['brand']>;
};

export type MenuCategoryFormGroup = FormGroup<MenuCategoryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MenuCategoryFormService {
  createMenuCategoryFormGroup(menuCategory: MenuCategoryFormGroupInput = { id: null }): MenuCategoryFormGroup {
    const menuCategoryRawValue = {
      ...this.getFormDefaults(),
      ...menuCategory,
    };
    return new FormGroup<MenuCategoryFormGroupContent>({
      id: new FormControl(
        { value: menuCategoryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(menuCategoryRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      description: new FormControl(menuCategoryRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      imageUrl: new FormControl(menuCategoryRawValue.imageUrl, {
        validators: [Validators.maxLength(500)],
      }),
      displayOrder: new FormControl(menuCategoryRawValue.displayOrder, {
        validators: [Validators.required, Validators.min(0)],
      }),
      isActive: new FormControl(menuCategoryRawValue.isActive, {
        validators: [Validators.required],
      }),
      parent: new FormControl(menuCategoryRawValue.parent),
      brand: new FormControl(menuCategoryRawValue.brand, {
        validators: [Validators.required],
      }),
    });
  }

  getMenuCategory(form: MenuCategoryFormGroup): IMenuCategory | NewMenuCategory {
    return form.getRawValue() as IMenuCategory | NewMenuCategory;
  }

  resetForm(form: MenuCategoryFormGroup, menuCategory: MenuCategoryFormGroupInput): void {
    const menuCategoryRawValue = { ...this.getFormDefaults(), ...menuCategory };
    form.reset(
      {
        ...menuCategoryRawValue,
        id: { value: menuCategoryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MenuCategoryFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
