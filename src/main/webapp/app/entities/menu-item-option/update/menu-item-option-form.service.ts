import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IMenuItemOption, NewMenuItemOption } from '../menu-item-option.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMenuItemOption for edit and NewMenuItemOptionFormGroupInput for create.
 */
type MenuItemOptionFormGroupInput = IMenuItemOption | PartialWithRequiredKeyOf<NewMenuItemOption>;

type MenuItemOptionFormDefaults = Pick<NewMenuItemOption, 'id' | 'isRequired'>;

type MenuItemOptionFormGroupContent = {
  id: FormControl<IMenuItemOption['id'] | NewMenuItemOption['id']>;
  name: FormControl<IMenuItemOption['name']>;
  isRequired: FormControl<IMenuItemOption['isRequired']>;
  maxSelections: FormControl<IMenuItemOption['maxSelections']>;
  displayOrder: FormControl<IMenuItemOption['displayOrder']>;
  menuItem: FormControl<IMenuItemOption['menuItem']>;
};

export type MenuItemOptionFormGroup = FormGroup<MenuItemOptionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MenuItemOptionFormService {
  createMenuItemOptionFormGroup(menuItemOption: MenuItemOptionFormGroupInput = { id: null }): MenuItemOptionFormGroup {
    const menuItemOptionRawValue = {
      ...this.getFormDefaults(),
      ...menuItemOption,
    };
    return new FormGroup<MenuItemOptionFormGroupContent>({
      id: new FormControl(
        { value: menuItemOptionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(menuItemOptionRawValue.name, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      isRequired: new FormControl(menuItemOptionRawValue.isRequired, {
        validators: [Validators.required],
      }),
      maxSelections: new FormControl(menuItemOptionRawValue.maxSelections, {
        validators: [Validators.min(1)],
      }),
      displayOrder: new FormControl(menuItemOptionRawValue.displayOrder, {
        validators: [Validators.min(0)],
      }),
      menuItem: new FormControl(menuItemOptionRawValue.menuItem, {
        validators: [Validators.required],
      }),
    });
  }

  getMenuItemOption(form: MenuItemOptionFormGroup): IMenuItemOption | NewMenuItemOption {
    return form.getRawValue() as IMenuItemOption | NewMenuItemOption;
  }

  resetForm(form: MenuItemOptionFormGroup, menuItemOption: MenuItemOptionFormGroupInput): void {
    const menuItemOptionRawValue = { ...this.getFormDefaults(), ...menuItemOption };
    form.reset(
      {
        ...menuItemOptionRawValue,
        id: { value: menuItemOptionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): MenuItemOptionFormDefaults {
    return {
      id: null,
      isRequired: false,
    };
  }
}
