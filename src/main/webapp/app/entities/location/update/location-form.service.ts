import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ILocation, NewLocation } from '../location.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILocation for edit and NewLocationFormGroupInput for create.
 */
type LocationFormGroupInput = ILocation | PartialWithRequiredKeyOf<NewLocation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ILocation | NewLocation> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type LocationFormRawValue = FormValueOf<ILocation>;

type NewLocationFormRawValue = FormValueOf<NewLocation>;

type LocationFormDefaults = Pick<NewLocation, 'id' | 'isActive' | 'createdAt'>;

type LocationFormGroupContent = {
  id: FormControl<LocationFormRawValue['id'] | NewLocation['id']>;
  name: FormControl<LocationFormRawValue['name']>;
  address: FormControl<LocationFormRawValue['address']>;
  city: FormControl<LocationFormRawValue['city']>;
  phone: FormControl<LocationFormRawValue['phone']>;
  email: FormControl<LocationFormRawValue['email']>;
  latitude: FormControl<LocationFormRawValue['latitude']>;
  longitude: FormControl<LocationFormRawValue['longitude']>;
  reservationDurationOverride: FormControl<LocationFormRawValue['reservationDurationOverride']>;
  maxAdvanceBookingDaysOverride: FormControl<LocationFormRawValue['maxAdvanceBookingDaysOverride']>;
  cancellationDeadlineOverride: FormControl<LocationFormRawValue['cancellationDeadlineOverride']>;
  isActive: FormControl<LocationFormRawValue['isActive']>;
  createdAt: FormControl<LocationFormRawValue['createdAt']>;
  brand: FormControl<LocationFormRawValue['brand']>;
};

export type LocationFormGroup = FormGroup<LocationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LocationFormService {
  createLocationFormGroup(location: LocationFormGroupInput = { id: null }): LocationFormGroup {
    const locationRawValue = this.convertLocationToLocationRawValue({
      ...this.getFormDefaults(),
      ...location,
    });
    return new FormGroup<LocationFormGroupContent>({
      id: new FormControl(
        { value: locationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(locationRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      address: new FormControl(locationRawValue.address, {
        validators: [Validators.required, Validators.maxLength(255)],
      }),
      city: new FormControl(locationRawValue.city, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      phone: new FormControl(locationRawValue.phone, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      email: new FormControl(locationRawValue.email, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      latitude: new FormControl(locationRawValue.latitude),
      longitude: new FormControl(locationRawValue.longitude),
      reservationDurationOverride: new FormControl(locationRawValue.reservationDurationOverride, {
        validators: [Validators.min(15), Validators.max(480)],
      }),
      maxAdvanceBookingDaysOverride: new FormControl(locationRawValue.maxAdvanceBookingDaysOverride, {
        validators: [Validators.min(1), Validators.max(365)],
      }),
      cancellationDeadlineOverride: new FormControl(locationRawValue.cancellationDeadlineOverride, {
        validators: [Validators.min(0), Validators.max(72)],
      }),
      isActive: new FormControl(locationRawValue.isActive, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(locationRawValue.createdAt, {
        validators: [Validators.required],
      }),
      brand: new FormControl(locationRawValue.brand, {
        validators: [Validators.required],
      }),
    });
  }

  getLocation(form: LocationFormGroup): ILocation | NewLocation {
    return this.convertLocationRawValueToLocation(form.getRawValue() as LocationFormRawValue | NewLocationFormRawValue);
  }

  resetForm(form: LocationFormGroup, location: LocationFormGroupInput): void {
    const locationRawValue = this.convertLocationToLocationRawValue({ ...this.getFormDefaults(), ...location });
    form.reset(
      {
        ...locationRawValue,
        id: { value: locationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): LocationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isActive: false,
      createdAt: currentTime,
    };
  }

  private convertLocationRawValueToLocation(rawLocation: LocationFormRawValue | NewLocationFormRawValue): ILocation | NewLocation {
    return {
      ...rawLocation,
      createdAt: dayjs(rawLocation.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertLocationToLocationRawValue(
    location: ILocation | (Partial<NewLocation> & LocationFormDefaults),
  ): LocationFormRawValue | PartialWithRequiredKeyOf<NewLocationFormRawValue> {
    return {
      ...location,
      createdAt: location.createdAt ? location.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
