import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMenuItem, NewMenuItem } from '../menu-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMenuItem for edit and NewMenuItemFormGroupInput for create.
 */
type MenuItemFormGroupInput = IMenuItem | PartialWithRequiredKeyOf<NewMenuItem>;

type MenuItemFormDefaults = Pick<NewMenuItem, 'id' | 'isAvailable' | 'isFeatured' | 'isVegetarian' | 'isVegan' | 'isGlutenFree'>;

type MenuItemFormGroupContent = {
  id: FormControl<IMenuItem['id'] | NewMenuItem['id']>;
  name: FormControl<IMenuItem['name']>;
  description: FormControl<IMenuItem['description']>;
  price: FormControl<IMenuItem['price']>;
  discountedPrice: FormControl<IMenuItem['discountedPrice']>;
  preparationTimeMinutes: FormControl<IMenuItem['preparationTimeMinutes']>;
  calories: FormControl<IMenuItem['calories']>;
  imageUrl: FormControl<IMenuItem['imageUrl']>;
  isAvailable: FormControl<IMenuItem['isAvailable']>;
  isFeatured: FormControl<IMenuItem['isFeatured']>;
  isVegetarian: FormControl<IMenuItem['isVegetarian']>;
  isVegan: FormControl<IMenuItem['isVegan']>;
  isGlutenFree: FormControl<IMenuItem['isGlutenFree']>;
  spicyLevel: FormControl<IMenuItem['spicyLevel']>;
  displayOrder: FormControl<IMenuItem['displayOrder']>;
  category: FormControl<IMenuItem['category']>;
};

export type MenuItemFormGroup = FormGroup<MenuItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MenuItemFormService {
  createMenuItemFormGroup(menuItem: MenuItemFormGroupInput = { id: null }): MenuItemFormGroup {
    const menuItemRawValue = {
      ...this.getFormDefaults(),
      ...menuItem,
    };
    return new FormGroup<MenuItemFormGroupContent>({
      id: new FormControl(
        { value: menuItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(menuItemRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(150)],
      }),
      description: new FormControl(menuItemRawValue.description),
      price: new FormControl(menuItemRawValue.price, {
        validators: [Validators.required, Validators.min(0)],
      }),
      discountedPrice: new FormControl(menuItemRawValue.discountedPrice, {
        validators: [Validators.min(0)],
      }),
      preparationTimeMinutes: new FormControl(menuItemRawValue.preparationTimeMinutes, {
        validators: [Validators.min(0), Validators.max(180)],
      }),
      calories: new FormControl(menuItemRawValue.calories, {
        validators: [Validators.min(0)],
      }),
      imageUrl: new FormControl(menuItemRawValue.imageUrl, {
        validators: [Validators.maxLength(500)],
      }),
      isAvailable: new FormControl(menuItemRawValue.isAvailable, {
        validators: [Validators.required],
      }),
      isFeatured: new FormControl(menuItemRawValue.isFeatured, {
        validators: [Validators.required],
      }),
      isVegetarian: new FormControl(menuItemRawValue.isVegetarian),
      isVegan: new FormControl(menuItemRawValue.isVegan),
      isGlutenFree: new FormControl(menuItemRawValue.isGlutenFree),
      spicyLevel: new FormControl(menuItemRawValue.spicyLevel, {
        validators: [Validators.min(0), Validators.max(3)],
      }),
      displayOrder: new FormControl(menuItemRawValue.displayOrder, {
        validators: [Validators.required, Validators.min(0)],
      }),
      category: new FormControl(menuItemRawValue.category, {
        validators: [Validators.required],
      }),
    });
  }

  getMenuItem(form: MenuItemFormGroup): IMenuItem | NewMenuItem {
    return form.getRawValue() as IMenuItem | NewMenuItem;
  }

  resetForm(form: MenuItemFormGroup, menuItem: MenuItemFormGroupInput): void {
    const menuItemRawValue = { ...this.getFormDefaults(), ...menuItem };
    form.reset(
      {
        ...menuItemRawValue,
        id: { value: menuItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MenuItemFormDefaults {
    return {
      id: null,
      isAvailable: false,
      isFeatured: false,
      isVegetarian: false,
      isVegan: false,
      isGlutenFree: false,
    };
  }
}
