import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMenuItemAllergen, NewMenuItemAllergen } from '../menu-item-allergen.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMenuItemAllergen for edit and NewMenuItemAllergenFormGroupInput for create.
 */
type MenuItemAllergenFormGroupInput = IMenuItemAllergen | PartialWithRequiredKeyOf<NewMenuItemAllergen>;

type MenuItemAllergenFormDefaults = Pick<NewMenuItemAllergen, 'id'>;

type MenuItemAllergenFormGroupContent = {
  id: FormControl<IMenuItemAllergen['id'] | NewMenuItemAllergen['id']>;
  allergen: FormControl<IMenuItemAllergen['allergen']>;
  notes: FormControl<IMenuItemAllergen['notes']>;
  menuItem: FormControl<IMenuItemAllergen['menuItem']>;
};

export type MenuItemAllergenFormGroup = FormGroup<MenuItemAllergenFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MenuItemAllergenFormService {
  createMenuItemAllergenFormGroup(menuItemAllergen: MenuItemAllergenFormGroupInput = { id: null }): MenuItemAllergenFormGroup {
    const menuItemAllergenRawValue = {
      ...this.getFormDefaults(),
      ...menuItemAllergen,
    };
    return new FormGroup<MenuItemAllergenFormGroupContent>({
      id: new FormControl(
        { value: menuItemAllergenRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      allergen: new FormControl(menuItemAllergenRawValue.allergen, {
        validators: [Validators.required],
      }),
      notes: new FormControl(menuItemAllergenRawValue.notes, {
        validators: [Validators.maxLength(255)],
      }),
      menuItem: new FormControl(menuItemAllergenRawValue.menuItem, {
        validators: [Validators.required],
      }),
    });
  }

  getMenuItemAllergen(form: MenuItemAllergenFormGroup): IMenuItemAllergen | NewMenuItemAllergen {
    return form.getRawValue() as IMenuItemAllergen | NewMenuItemAllergen;
  }

  resetForm(form: MenuItemAllergenFormGroup, menuItemAllergen: MenuItemAllergenFormGroupInput): void {
    const menuItemAllergenRawValue = { ...this.getFormDefaults(), ...menuItemAllergen };
    form.reset(
      {
        ...menuItemAllergenRawValue,
        id: { value: menuItemAllergenRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MenuItemAllergenFormDefaults {
    return {
      id: null,
    };
  }
}
