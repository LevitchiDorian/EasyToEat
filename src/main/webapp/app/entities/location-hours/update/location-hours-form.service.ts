import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILocationHours, NewLocationHours } from '../location-hours.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILocationHours for edit and NewLocationHoursFormGroupInput for create.
 */
type LocationHoursFormGroupInput = ILocationHours | PartialWithRequiredKeyOf<NewLocationHours>;

type LocationHoursFormDefaults = Pick<NewLocationHours, 'id' | 'isClosed'>;

type LocationHoursFormGroupContent = {
  id: FormControl<ILocationHours['id'] | NewLocationHours['id']>;
  dayOfWeek: FormControl<ILocationHours['dayOfWeek']>;
  openTime: FormControl<ILocationHours['openTime']>;
  closeTime: FormControl<ILocationHours['closeTime']>;
  isClosed: FormControl<ILocationHours['isClosed']>;
  specialNote: FormControl<ILocationHours['specialNote']>;
  location: FormControl<ILocationHours['location']>;
};

export type LocationHoursFormGroup = FormGroup<LocationHoursFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LocationHoursFormService {
  createLocationHoursFormGroup(locationHours: LocationHoursFormGroupInput = { id: null }): LocationHoursFormGroup {
    const locationHoursRawValue = {
      ...this.getFormDefaults(),
      ...locationHours,
    };
    return new FormGroup<LocationHoursFormGroupContent>({
      id: new FormControl(
        { value: locationHoursRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      dayOfWeek: new FormControl(locationHoursRawValue.dayOfWeek, {
        validators: [Validators.required],
      }),
      openTime: new FormControl(locationHoursRawValue.openTime, {
        validators: [Validators.required, Validators.maxLength(5)],
      }),
      closeTime: new FormControl(locationHoursRawValue.closeTime, {
        validators: [Validators.required, Validators.maxLength(5)],
      }),
      isClosed: new FormControl(locationHoursRawValue.isClosed, {
        validators: [Validators.required],
      }),
      specialNote: new FormControl(locationHoursRawValue.specialNote, {
        validators: [Validators.maxLength(255)],
      }),
      location: new FormControl(locationHoursRawValue.location, {
        validators: [Validators.required],
      }),
    });
  }

  getLocationHours(form: LocationHoursFormGroup): ILocationHours | NewLocationHours {
    return form.getRawValue() as ILocationHours | NewLocationHours;
  }

  resetForm(form: LocationHoursFormGroup, locationHours: LocationHoursFormGroupInput): void {
    const locationHoursRawValue = { ...this.getFormDefaults(), ...locationHours };
    form.reset(
      {
        ...locationHoursRawValue,
        id: { value: locationHoursRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): LocationHoursFormDefaults {
    return {
      id: null,
      isClosed: false,
    };
  }
}
