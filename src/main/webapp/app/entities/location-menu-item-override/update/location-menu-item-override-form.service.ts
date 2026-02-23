import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILocationMenuItemOverride, NewLocationMenuItemOverride } from '../location-menu-item-override.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILocationMenuItemOverride for edit and NewLocationMenuItemOverrideFormGroupInput for create.
 */
type LocationMenuItemOverrideFormGroupInput = ILocationMenuItemOverride | PartialWithRequiredKeyOf<NewLocationMenuItemOverride>;

type LocationMenuItemOverrideFormDefaults = Pick<NewLocationMenuItemOverride, 'id' | 'isAvailableAtLocation'>;

type LocationMenuItemOverrideFormGroupContent = {
  id: FormControl<ILocationMenuItemOverride['id'] | NewLocationMenuItemOverride['id']>;
  isAvailableAtLocation: FormControl<ILocationMenuItemOverride['isAvailableAtLocation']>;
  priceOverride: FormControl<ILocationMenuItemOverride['priceOverride']>;
  preparationTimeOverride: FormControl<ILocationMenuItemOverride['preparationTimeOverride']>;
  notes: FormControl<ILocationMenuItemOverride['notes']>;
  menuItem: FormControl<ILocationMenuItemOverride['menuItem']>;
  location: FormControl<ILocationMenuItemOverride['location']>;
};

export type LocationMenuItemOverrideFormGroup = FormGroup<LocationMenuItemOverrideFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LocationMenuItemOverrideFormService {
  createLocationMenuItemOverrideFormGroup(
    locationMenuItemOverride: LocationMenuItemOverrideFormGroupInput = { id: null },
  ): LocationMenuItemOverrideFormGroup {
    const locationMenuItemOverrideRawValue = {
      ...this.getFormDefaults(),
      ...locationMenuItemOverride,
    };
    return new FormGroup<LocationMenuItemOverrideFormGroupContent>({
      id: new FormControl(
        { value: locationMenuItemOverrideRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      isAvailableAtLocation: new FormControl(locationMenuItemOverrideRawValue.isAvailableAtLocation, {
        validators: [Validators.required],
      }),
      priceOverride: new FormControl(locationMenuItemOverrideRawValue.priceOverride, {
        validators: [Validators.min(0)],
      }),
      preparationTimeOverride: new FormControl(locationMenuItemOverrideRawValue.preparationTimeOverride, {
        validators: [Validators.min(0), Validators.max(180)],
      }),
      notes: new FormControl(locationMenuItemOverrideRawValue.notes, {
        validators: [Validators.maxLength(500)],
      }),
      menuItem: new FormControl(locationMenuItemOverrideRawValue.menuItem),
      location: new FormControl(locationMenuItemOverrideRawValue.location, {
        validators: [Validators.required],
      }),
    });
  }

  getLocationMenuItemOverride(form: LocationMenuItemOverrideFormGroup): ILocationMenuItemOverride | NewLocationMenuItemOverride {
    return form.getRawValue() as ILocationMenuItemOverride | NewLocationMenuItemOverride;
  }

  resetForm(form: LocationMenuItemOverrideFormGroup, locationMenuItemOverride: LocationMenuItemOverrideFormGroupInput): void {
    const locationMenuItemOverrideRawValue = { ...this.getFormDefaults(), ...locationMenuItemOverride };
    form.reset(
      {
        ...locationMenuItemOverrideRawValue,
        id: { value: locationMenuItemOverrideRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): LocationMenuItemOverrideFormDefaults {
    return {
      id: null,
      isAvailableAtLocation: false,
    };
  }
}
