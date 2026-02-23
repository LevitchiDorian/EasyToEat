import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IOrderItem, NewOrderItem } from '../order-item.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrderItem for edit and NewOrderItemFormGroupInput for create.
 */
type OrderItemFormGroupInput = IOrderItem | PartialWithRequiredKeyOf<NewOrderItem>;

type OrderItemFormDefaults = Pick<NewOrderItem, 'id'>;

type OrderItemFormGroupContent = {
  id: FormControl<IOrderItem['id'] | NewOrderItem['id']>;
  quantity: FormControl<IOrderItem['quantity']>;
  unitPrice: FormControl<IOrderItem['unitPrice']>;
  totalPrice: FormControl<IOrderItem['totalPrice']>;
  status: FormControl<IOrderItem['status']>;
  specialInstructions: FormControl<IOrderItem['specialInstructions']>;
  notes: FormControl<IOrderItem['notes']>;
  menuItem: FormControl<IOrderItem['menuItem']>;
  order: FormControl<IOrderItem['order']>;
};

export type OrderItemFormGroup = FormGroup<OrderItemFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrderItemFormService {
  createOrderItemFormGroup(orderItem: OrderItemFormGroupInput = { id: null }): OrderItemFormGroup {
    const orderItemRawValue = {
      ...this.getFormDefaults(),
      ...orderItem,
    };
    return new FormGroup<OrderItemFormGroupContent>({
      id: new FormControl(
        { value: orderItemRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantity: new FormControl(orderItemRawValue.quantity, {
        validators: [Validators.required, Validators.min(1)],
      }),
      unitPrice: new FormControl(orderItemRawValue.unitPrice, {
        validators: [Validators.required, Validators.min(0)],
      }),
      totalPrice: new FormControl(orderItemRawValue.totalPrice, {
        validators: [Validators.required, Validators.min(0)],
      }),
      status: new FormControl(orderItemRawValue.status, {
        validators: [Validators.required],
      }),
      specialInstructions: new FormControl(orderItemRawValue.specialInstructions, {
        validators: [Validators.maxLength(500)],
      }),
      notes: new FormControl(orderItemRawValue.notes, {
        validators: [Validators.maxLength(255)],
      }),
      menuItem: new FormControl(orderItemRawValue.menuItem),
      order: new FormControl(orderItemRawValue.order, {
        validators: [Validators.required],
      }),
    });
  }

  getOrderItem(form: OrderItemFormGroup): IOrderItem | NewOrderItem {
    return form.getRawValue() as IOrderItem | NewOrderItem;
  }

  resetForm(form: OrderItemFormGroup, orderItem: OrderItemFormGroupInput): void {
    const orderItemRawValue = { ...this.getFormDefaults(), ...orderItem };
    form.reset(
      {
        ...orderItemRawValue,
        id: { value: orderItemRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): OrderItemFormDefaults {
    return {
      id: null,
    };
  }
}
