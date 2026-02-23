import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMenuItemOptionValue, NewMenuItemOptionValue } from '../menu-item-option-value.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMenuItemOptionValue for edit and NewMenuItemOptionValueFormGroupInput for create.
 */
type MenuItemOptionValueFormGroupInput = IMenuItemOptionValue | PartialWithRequiredKeyOf<NewMenuItemOptionValue>;

type MenuItemOptionValueFormDefaults = Pick<NewMenuItemOptionValue, 'id' | 'isDefault' | 'isAvailable'>;

type MenuItemOptionValueFormGroupContent = {
  id: FormControl<IMenuItemOptionValue['id'] | NewMenuItemOptionValue['id']>;
  label: FormControl<IMenuItemOptionValue['label']>;
  priceAdjustment: FormControl<IMenuItemOptionValue['priceAdjustment']>;
  isDefault: FormControl<IMenuItemOptionValue['isDefault']>;
  isAvailable: FormControl<IMenuItemOptionValue['isAvailable']>;
  displayOrder: FormControl<IMenuItemOptionValue['displayOrder']>;
  option: FormControl<IMenuItemOptionValue['option']>;
};

export type MenuItemOptionValueFormGroup = FormGroup<MenuItemOptionValueFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MenuItemOptionValueFormService {
  createMenuItemOptionValueFormGroup(menuItemOptionValue: MenuItemOptionValueFormGroupInput = { id: null }): MenuItemOptionValueFormGroup {
    const menuItemOptionValueRawValue = {
      ...this.getFormDefaults(),
      ...menuItemOptionValue,
    };
    return new FormGroup<MenuItemOptionValueFormGroupContent>({
      id: new FormControl(
        { value: menuItemOptionValueRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      label: new FormControl(menuItemOptionValueRawValue.label, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      priceAdjustment: new FormControl(menuItemOptionValueRawValue.priceAdjustment, {
        validators: [Validators.required],
      }),
      isDefault: new FormControl(menuItemOptionValueRawValue.isDefault, {
        validators: [Validators.required],
      }),
      isAvailable: new FormControl(menuItemOptionValueRawValue.isAvailable, {
        validators: [Validators.required],
      }),
      displayOrder: new FormControl(menuItemOptionValueRawValue.displayOrder, {
        validators: [Validators.min(0)],
      }),
      option: new FormControl(menuItemOptionValueRawValue.option, {
        validators: [Validators.required],
      }),
    });
  }

  getMenuItemOptionValue(form: MenuItemOptionValueFormGroup): IMenuItemOptionValue | NewMenuItemOptionValue {
    return form.getRawValue() as IMenuItemOptionValue | NewMenuItemOptionValue;
  }

  resetForm(form: MenuItemOptionValueFormGroup, menuItemOptionValue: MenuItemOptionValueFormGroupInput): void {
    const menuItemOptionValueRawValue = { ...this.getFormDefaults(), ...menuItemOptionValue };
    form.reset(
      {
        ...menuItemOptionValueRawValue,
        id: { value: menuItemOptionValueRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MenuItemOptionValueFormDefaults {
    return {
      id: null,
      isDefault: false,
      isAvailable: false,
    };
  }
}
