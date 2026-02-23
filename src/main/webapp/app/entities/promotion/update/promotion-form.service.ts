import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IPromotion, NewPromotion } from '../promotion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPromotion for edit and NewPromotionFormGroupInput for create.
 */
type PromotionFormGroupInput = IPromotion | PartialWithRequiredKeyOf<NewPromotion>;

type PromotionFormDefaults = Pick<NewPromotion, 'id' | 'isActive'>;

type PromotionFormGroupContent = {
  id: FormControl<IPromotion['id'] | NewPromotion['id']>;
  code: FormControl<IPromotion['code']>;
  name: FormControl<IPromotion['name']>;
  description: FormControl<IPromotion['description']>;
  discountType: FormControl<IPromotion['discountType']>;
  discountValue: FormControl<IPromotion['discountValue']>;
  minimumOrderAmount: FormControl<IPromotion['minimumOrderAmount']>;
  maxUsageCount: FormControl<IPromotion['maxUsageCount']>;
  currentUsageCount: FormControl<IPromotion['currentUsageCount']>;
  startDate: FormControl<IPromotion['startDate']>;
  endDate: FormControl<IPromotion['endDate']>;
  isActive: FormControl<IPromotion['isActive']>;
  brand: FormControl<IPromotion['brand']>;
  location: FormControl<IPromotion['location']>;
};

export type PromotionFormGroup = FormGroup<PromotionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PromotionFormService {
  createPromotionFormGroup(promotion: PromotionFormGroupInput = { id: null }): PromotionFormGroup {
    const promotionRawValue = {
      ...this.getFormDefaults(),
      ...promotion,
    };
    return new FormGroup<PromotionFormGroupContent>({
      id: new FormControl(
        { value: promotionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(promotionRawValue.code, {
        validators: [Validators.required, Validators.minLength(3), Validators.maxLength(50)],
      }),
      name: new FormControl(promotionRawValue.name, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      description: new FormControl(promotionRawValue.description),
      discountType: new FormControl(promotionRawValue.discountType, {
        validators: [Validators.required],
      }),
      discountValue: new FormControl(promotionRawValue.discountValue, {
        validators: [Validators.required, Validators.min(0)],
      }),
      minimumOrderAmount: new FormControl(promotionRawValue.minimumOrderAmount, {
        validators: [Validators.min(0)],
      }),
      maxUsageCount: new FormControl(promotionRawValue.maxUsageCount, {
        validators: [Validators.min(1)],
      }),
      currentUsageCount: new FormControl(promotionRawValue.currentUsageCount, {
        validators: [Validators.min(0)],
      }),
      startDate: new FormControl(promotionRawValue.startDate, {
        validators: [Validators.required],
      }),
      endDate: new FormControl(promotionRawValue.endDate, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(promotionRawValue.isActive, {
        validators: [Validators.required],
      }),
      brand: new FormControl(promotionRawValue.brand, {
        validators: [Validators.required],
      }),
      location: new FormControl(promotionRawValue.location),
    });
  }

  getPromotion(form: PromotionFormGroup): IPromotion | NewPromotion {
    return form.getRawValue() as IPromotion | NewPromotion;
  }

  resetForm(form: PromotionFormGroup, promotion: PromotionFormGroupInput): void {
    const promotionRawValue = { ...this.getFormDefaults(), ...promotion };
    form.reset(
      {
        ...promotionRawValue,
        id: { value: promotionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PromotionFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
