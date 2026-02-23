import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IRestaurantTable, NewRestaurantTable } from '../restaurant-table.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IRestaurantTable for edit and NewRestaurantTableFormGroupInput for create.
 */
type RestaurantTableFormGroupInput = IRestaurantTable | PartialWithRequiredKeyOf<NewRestaurantTable>;

type RestaurantTableFormDefaults = Pick<NewRestaurantTable, 'id' | 'isActive'>;

type RestaurantTableFormGroupContent = {
  id: FormControl<IRestaurantTable['id'] | NewRestaurantTable['id']>;
  tableNumber: FormControl<IRestaurantTable['tableNumber']>;
  shape: FormControl<IRestaurantTable['shape']>;
  minCapacity: FormControl<IRestaurantTable['minCapacity']>;
  maxCapacity: FormControl<IRestaurantTable['maxCapacity']>;
  positionX: FormControl<IRestaurantTable['positionX']>;
  positionY: FormControl<IRestaurantTable['positionY']>;
  widthPx: FormControl<IRestaurantTable['widthPx']>;
  heightPx: FormControl<IRestaurantTable['heightPx']>;
  rotation: FormControl<IRestaurantTable['rotation']>;
  status: FormControl<IRestaurantTable['status']>;
  isActive: FormControl<IRestaurantTable['isActive']>;
  notes: FormControl<IRestaurantTable['notes']>;
  room: FormControl<IRestaurantTable['room']>;
};

export type RestaurantTableFormGroup = FormGroup<RestaurantTableFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class RestaurantTableFormService {
  createRestaurantTableFormGroup(restaurantTable: RestaurantTableFormGroupInput = { id: null }): RestaurantTableFormGroup {
    const restaurantTableRawValue = {
      ...this.getFormDefaults(),
      ...restaurantTable,
    };
    return new FormGroup<RestaurantTableFormGroupContent>({
      id: new FormControl(
        { value: restaurantTableRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      tableNumber: new FormControl(restaurantTableRawValue.tableNumber, {
        validators: [Validators.required, Validators.minLength(1), Validators.maxLength(20)],
      }),
      shape: new FormControl(restaurantTableRawValue.shape, {
        validators: [Validators.required],
      }),
      minCapacity: new FormControl(restaurantTableRawValue.minCapacity, {
        validators: [Validators.required, Validators.min(1)],
      }),
      maxCapacity: new FormControl(restaurantTableRawValue.maxCapacity, {
        validators: [Validators.required, Validators.min(1)],
      }),
      positionX: new FormControl(restaurantTableRawValue.positionX),
      positionY: new FormControl(restaurantTableRawValue.positionY),
      widthPx: new FormControl(restaurantTableRawValue.widthPx),
      heightPx: new FormControl(restaurantTableRawValue.heightPx),
      rotation: new FormControl(restaurantTableRawValue.rotation),
      status: new FormControl(restaurantTableRawValue.status, {
        validators: [Validators.required],
      }),
      isActive: new FormControl(restaurantTableRawValue.isActive, {
        validators: [Validators.required],
      }),
      notes: new FormControl(restaurantTableRawValue.notes, {
        validators: [Validators.maxLength(500)],
      }),
      room: new FormControl(restaurantTableRawValue.room, {
        validators: [Validators.required],
      }),
    });
  }

  getRestaurantTable(form: RestaurantTableFormGroup): IRestaurantTable | NewRestaurantTable {
    return form.getRawValue() as IRestaurantTable | NewRestaurantTable;
  }

  resetForm(form: RestaurantTableFormGroup, restaurantTable: RestaurantTableFormGroupInput): void {
    const restaurantTableRawValue = { ...this.getFormDefaults(), ...restaurantTable };
    form.reset(
      {
        ...restaurantTableRawValue,
        id: { value: restaurantTableRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): RestaurantTableFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
