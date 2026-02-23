import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IOrderItemOptionSelection, NewOrderItemOptionSelection } from '../order-item-option-selection.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrderItemOptionSelection for edit and NewOrderItemOptionSelectionFormGroupInput for create.
 */
type OrderItemOptionSelectionFormGroupInput = IOrderItemOptionSelection | PartialWithRequiredKeyOf<NewOrderItemOptionSelection>;

type OrderItemOptionSelectionFormDefaults = Pick<NewOrderItemOptionSelection, 'id'>;

type OrderItemOptionSelectionFormGroupContent = {
  id: FormControl<IOrderItemOptionSelection['id'] | NewOrderItemOptionSelection['id']>;
  quantity: FormControl<IOrderItemOptionSelection['quantity']>;
  unitPrice: FormControl<IOrderItemOptionSelection['unitPrice']>;
  optionValue: FormControl<IOrderItemOptionSelection['optionValue']>;
  orderItem: FormControl<IOrderItemOptionSelection['orderItem']>;
};

export type OrderItemOptionSelectionFormGroup = FormGroup<OrderItemOptionSelectionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrderItemOptionSelectionFormService {
  createOrderItemOptionSelectionFormGroup(
    orderItemOptionSelection: OrderItemOptionSelectionFormGroupInput = { id: null },
  ): OrderItemOptionSelectionFormGroup {
    const orderItemOptionSelectionRawValue = {
      ...this.getFormDefaults(),
      ...orderItemOptionSelection,
    };
    return new FormGroup<OrderItemOptionSelectionFormGroupContent>({
      id: new FormControl(
        { value: orderItemOptionSelectionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantity: new FormControl(orderItemOptionSelectionRawValue.quantity, {
        validators: [Validators.min(1)],
      }),
      unitPrice: new FormControl(orderItemOptionSelectionRawValue.unitPrice, {
        validators: [Validators.required, Validators.min(0)],
      }),
      optionValue: new FormControl(orderItemOptionSelectionRawValue.optionValue),
      orderItem: new FormControl(orderItemOptionSelectionRawValue.orderItem, {
        validators: [Validators.required],
      }),
    });
  }

  getOrderItemOptionSelection(form: OrderItemOptionSelectionFormGroup): IOrderItemOptionSelection | NewOrderItemOptionSelection {
    return form.getRawValue() as IOrderItemOptionSelection | NewOrderItemOptionSelection;
  }

  resetForm(form: OrderItemOptionSelectionFormGroup, orderItemOptionSelection: OrderItemOptionSelectionFormGroupInput): void {
    const orderItemOptionSelectionRawValue = { ...this.getFormDefaults(), ...orderItemOptionSelection };
    form.reset(
      {
        ...orderItemOptionSelectionRawValue,
        id: { value: orderItemOptionSelectionRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): OrderItemOptionSelectionFormDefaults {
    return {
      id: null,
    };
  }
}
