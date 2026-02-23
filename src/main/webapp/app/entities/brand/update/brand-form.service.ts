import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBrand, NewBrand } from '../brand.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBrand for edit and NewBrandFormGroupInput for create.
 */
type BrandFormGroupInput = IBrand | PartialWithRequiredKeyOf<NewBrand>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBrand | NewBrand> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type BrandFormRawValue = FormValueOf<IBrand>;

type NewBrandFormRawValue = FormValueOf<NewBrand>;

type BrandFormDefaults = Pick<NewBrand, 'id' | 'isActive' | 'createdAt'>;

type BrandFormGroupContent = {
  id: FormControl<BrandFormRawValue['id'] | NewBrand['id']>;
  name: FormControl<BrandFormRawValue['name']>;
  description: FormControl<BrandFormRawValue['description']>;
  logoUrl: FormControl<BrandFormRawValue['logoUrl']>;
  coverImageUrl: FormControl<BrandFormRawValue['coverImageUrl']>;
  primaryColor: FormControl<BrandFormRawValue['primaryColor']>;
  secondaryColor: FormControl<BrandFormRawValue['secondaryColor']>;
  website: FormControl<BrandFormRawValue['website']>;
  contactEmail: FormControl<BrandFormRawValue['contactEmail']>;
  contactPhone: FormControl<BrandFormRawValue['contactPhone']>;
  defaultReservationDuration: FormControl<BrandFormRawValue['defaultReservationDuration']>;
  maxAdvanceBookingDays: FormControl<BrandFormRawValue['maxAdvanceBookingDays']>;
  cancellationDeadlineHours: FormControl<BrandFormRawValue['cancellationDeadlineHours']>;
  isActive: FormControl<BrandFormRawValue['isActive']>;
  createdAt: FormControl<BrandFormRawValue['createdAt']>;
};

export type BrandFormGroup = FormGroup<BrandFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BrandFormService {
  createBrandFormGroup(brand: BrandFormGroupInput = { id: null }): BrandFormGroup {
    const brandRawValue = this.convertBrandToBrandRawValue({
      ...this.getFormDefaults(),
      ...brand,
    });
    return new FormGroup<BrandFormGroupContent>({
      id: new FormControl(
        { value: brandRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(brandRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      description: new FormControl(brandRawValue.description),
      logoUrl: new FormControl(brandRawValue.logoUrl, {
        validators: [Validators.maxLength(500)],
      }),
      coverImageUrl: new FormControl(brandRawValue.coverImageUrl, {
        validators: [Validators.maxLength(500)],
      }),
      primaryColor: new FormControl(brandRawValue.primaryColor, {
        validators: [Validators.maxLength(7)],
      }),
      secondaryColor: new FormControl(brandRawValue.secondaryColor, {
        validators: [Validators.maxLength(7)],
      }),
      website: new FormControl(brandRawValue.website, {
        validators: [Validators.maxLength(255)],
      }),
      contactEmail: new FormControl(brandRawValue.contactEmail, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      contactPhone: new FormControl(brandRawValue.contactPhone, {
        validators: [Validators.required, Validators.maxLength(20)],
      }),
      defaultReservationDuration: new FormControl(brandRawValue.defaultReservationDuration, {
        validators: [Validators.required, Validators.min(15), Validators.max(480)],
      }),
      maxAdvanceBookingDays: new FormControl(brandRawValue.maxAdvanceBookingDays, {
        validators: [Validators.required, Validators.min(1), Validators.max(365)],
      }),
      cancellationDeadlineHours: new FormControl(brandRawValue.cancellationDeadlineHours, {
        validators: [Validators.required, Validators.min(0), Validators.max(72)],
      }),
      isActive: new FormControl(brandRawValue.isActive, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(brandRawValue.createdAt, {
        validators: [Validators.required],
      }),
    });
  }

  getBrand(form: BrandFormGroup): IBrand | NewBrand {
    return this.convertBrandRawValueToBrand(form.getRawValue() as BrandFormRawValue | NewBrandFormRawValue);
  }

  resetForm(form: BrandFormGroup, brand: BrandFormGroupInput): void {
    const brandRawValue = this.convertBrandToBrandRawValue({ ...this.getFormDefaults(), ...brand });
    form.reset(
      {
        ...brandRawValue,
        id: { value: brandRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): BrandFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isActive: false,
      createdAt: currentTime,
    };
  }

  private convertBrandRawValueToBrand(rawBrand: BrandFormRawValue | NewBrandFormRawValue): IBrand | NewBrand {
    return {
      ...rawBrand,
      createdAt: dayjs(rawBrand.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertBrandToBrandRawValue(
    brand: IBrand | (Partial<NewBrand> & BrandFormDefaults),
  ): BrandFormRawValue | PartialWithRequiredKeyOf<NewBrandFormRawValue> {
    return {
      ...brand,
      createdAt: brand.createdAt ? brand.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
