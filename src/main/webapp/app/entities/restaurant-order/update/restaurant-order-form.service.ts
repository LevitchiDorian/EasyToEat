import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IRestaurantOrder, NewRestaurantOrder } from '../restaurant-order.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRestaurantOrder for edit and NewRestaurantOrderFormGroupInput for create.
 */
type RestaurantOrderFormGroupInput = IRestaurantOrder | PartialWithRequiredKeyOf<NewRestaurantOrder>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IRestaurantOrder | NewRestaurantOrder> = Omit<
  T,
  'scheduledFor' | 'estimatedReadyTime' | 'confirmedAt' | 'completedAt' | 'createdAt' | 'updatedAt'
> & {
  scheduledFor?: string | null;
  estimatedReadyTime?: string | null;
  confirmedAt?: string | null;
  completedAt?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
};

type RestaurantOrderFormRawValue = FormValueOf<IRestaurantOrder>;

type NewRestaurantOrderFormRawValue = FormValueOf<NewRestaurantOrder>;

type RestaurantOrderFormDefaults = Pick<
  NewRestaurantOrder,
  'id' | 'isPreOrder' | 'scheduledFor' | 'estimatedReadyTime' | 'confirmedAt' | 'completedAt' | 'createdAt' | 'updatedAt'
>;

type RestaurantOrderFormGroupContent = {
  id: FormControl<RestaurantOrderFormRawValue['id'] | NewRestaurantOrder['id']>;
  orderCode: FormControl<RestaurantOrderFormRawValue['orderCode']>;
  status: FormControl<RestaurantOrderFormRawValue['status']>;
  isPreOrder: FormControl<RestaurantOrderFormRawValue['isPreOrder']>;
  scheduledFor: FormControl<RestaurantOrderFormRawValue['scheduledFor']>;
  subtotal: FormControl<RestaurantOrderFormRawValue['subtotal']>;
  discountAmount: FormControl<RestaurantOrderFormRawValue['discountAmount']>;
  taxAmount: FormControl<RestaurantOrderFormRawValue['taxAmount']>;
  totalAmount: FormControl<RestaurantOrderFormRawValue['totalAmount']>;
  specialInstructions: FormControl<RestaurantOrderFormRawValue['specialInstructions']>;
  estimatedReadyTime: FormControl<RestaurantOrderFormRawValue['estimatedReadyTime']>;
  confirmedAt: FormControl<RestaurantOrderFormRawValue['confirmedAt']>;
  completedAt: FormControl<RestaurantOrderFormRawValue['completedAt']>;
  createdAt: FormControl<RestaurantOrderFormRawValue['createdAt']>;
  updatedAt: FormControl<RestaurantOrderFormRawValue['updatedAt']>;
  location: FormControl<RestaurantOrderFormRawValue['location']>;
  client: FormControl<RestaurantOrderFormRawValue['client']>;
  assignedWaiter: FormControl<RestaurantOrderFormRawValue['assignedWaiter']>;
  table: FormControl<RestaurantOrderFormRawValue['table']>;
  promotion: FormControl<RestaurantOrderFormRawValue['promotion']>;
  reservation: FormControl<RestaurantOrderFormRawValue['reservation']>;
};

export type RestaurantOrderFormGroup = FormGroup<RestaurantOrderFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RestaurantOrderFormService {
  createRestaurantOrderFormGroup(restaurantOrder: RestaurantOrderFormGroupInput = { id: null }): RestaurantOrderFormGroup {
    const restaurantOrderRawValue = this.convertRestaurantOrderToRestaurantOrderRawValue({
      ...this.getFormDefaults(),
      ...restaurantOrder,
    });
    return new FormGroup<RestaurantOrderFormGroupContent>({
      id: new FormControl(
        { value: restaurantOrderRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      orderCode: new FormControl(restaurantOrderRawValue.orderCode, {
        validators: [Validators.required, Validators.minLength(6), Validators.maxLength(20)],
      }),
      status: new FormControl(restaurantOrderRawValue.status, {
        validators: [Validators.required],
      }),
      isPreOrder: new FormControl(restaurantOrderRawValue.isPreOrder, {
        validators: [Validators.required],
      }),
      scheduledFor: new FormControl(restaurantOrderRawValue.scheduledFor),
      subtotal: new FormControl(restaurantOrderRawValue.subtotal, {
        validators: [Validators.required, Validators.min(0)],
      }),
      discountAmount: new FormControl(restaurantOrderRawValue.discountAmount, {
        validators: [Validators.min(0)],
      }),
      taxAmount: new FormControl(restaurantOrderRawValue.taxAmount, {
        validators: [Validators.min(0)],
      }),
      totalAmount: new FormControl(restaurantOrderRawValue.totalAmount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      specialInstructions: new FormControl(restaurantOrderRawValue.specialInstructions),
      estimatedReadyTime: new FormControl(restaurantOrderRawValue.estimatedReadyTime),
      confirmedAt: new FormControl(restaurantOrderRawValue.confirmedAt),
      completedAt: new FormControl(restaurantOrderRawValue.completedAt),
      createdAt: new FormControl(restaurantOrderRawValue.createdAt, {
        validators: [Validators.required],
      }),
      updatedAt: new FormControl(restaurantOrderRawValue.updatedAt),
      location: new FormControl(restaurantOrderRawValue.location),
      client: new FormControl(restaurantOrderRawValue.client),
      assignedWaiter: new FormControl(restaurantOrderRawValue.assignedWaiter),
      table: new FormControl(restaurantOrderRawValue.table),
      promotion: new FormControl(restaurantOrderRawValue.promotion),
      reservation: new FormControl(restaurantOrderRawValue.reservation),
    });
  }

  getRestaurantOrder(form: RestaurantOrderFormGroup): IRestaurantOrder | NewRestaurantOrder {
    return this.convertRestaurantOrderRawValueToRestaurantOrder(
      form.getRawValue() as RestaurantOrderFormRawValue | NewRestaurantOrderFormRawValue,
    );
  }

  resetForm(form: RestaurantOrderFormGroup, restaurantOrder: RestaurantOrderFormGroupInput): void {
    const restaurantOrderRawValue = this.convertRestaurantOrderToRestaurantOrderRawValue({ ...this.getFormDefaults(), ...restaurantOrder });
    form.reset(
      {
        ...restaurantOrderRawValue,
        id: { value: restaurantOrderRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RestaurantOrderFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isPreOrder: false,
      scheduledFor: currentTime,
      estimatedReadyTime: currentTime,
      confirmedAt: currentTime,
      completedAt: currentTime,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertRestaurantOrderRawValueToRestaurantOrder(
    rawRestaurantOrder: RestaurantOrderFormRawValue | NewRestaurantOrderFormRawValue,
  ): IRestaurantOrder | NewRestaurantOrder {
    return {
      ...rawRestaurantOrder,
      scheduledFor: dayjs(rawRestaurantOrder.scheduledFor, DATE_TIME_FORMAT),
      estimatedReadyTime: dayjs(rawRestaurantOrder.estimatedReadyTime, DATE_TIME_FORMAT),
      confirmedAt: dayjs(rawRestaurantOrder.confirmedAt, DATE_TIME_FORMAT),
      completedAt: dayjs(rawRestaurantOrder.completedAt, DATE_TIME_FORMAT),
      createdAt: dayjs(rawRestaurantOrder.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawRestaurantOrder.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertRestaurantOrderToRestaurantOrderRawValue(
    restaurantOrder: IRestaurantOrder | (Partial<NewRestaurantOrder> & RestaurantOrderFormDefaults),
  ): RestaurantOrderFormRawValue | PartialWithRequiredKeyOf<NewRestaurantOrderFormRawValue> {
    return {
      ...restaurantOrder,
      scheduledFor: restaurantOrder.scheduledFor ? restaurantOrder.scheduledFor.format(DATE_TIME_FORMAT) : undefined,
      estimatedReadyTime: restaurantOrder.estimatedReadyTime ? restaurantOrder.estimatedReadyTime.format(DATE_TIME_FORMAT) : undefined,
      confirmedAt: restaurantOrder.confirmedAt ? restaurantOrder.confirmedAt.format(DATE_TIME_FORMAT) : undefined,
      completedAt: restaurantOrder.completedAt ? restaurantOrder.completedAt.format(DATE_TIME_FORMAT) : undefined,
      createdAt: restaurantOrder.createdAt ? restaurantOrder.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: restaurantOrder.updatedAt ? restaurantOrder.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
