import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDiningRoom, NewDiningRoom } from '../dining-room.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDiningRoom for edit and NewDiningRoomFormGroupInput for create.
 */
type DiningRoomFormGroupInput = IDiningRoom | PartialWithRequiredKeyOf<NewDiningRoom>;

type DiningRoomFormDefaults = Pick<NewDiningRoom, 'id' | 'isActive'>;

type DiningRoomFormGroupContent = {
  id: FormControl<IDiningRoom['id'] | NewDiningRoom['id']>;
  name: FormControl<IDiningRoom['name']>;
  description: FormControl<IDiningRoom['description']>;
  floor: FormControl<IDiningRoom['floor']>;
  capacity: FormControl<IDiningRoom['capacity']>;
  isActive: FormControl<IDiningRoom['isActive']>;
  floorPlanUrl: FormControl<IDiningRoom['floorPlanUrl']>;
  widthPx: FormControl<IDiningRoom['widthPx']>;
  heightPx: FormControl<IDiningRoom['heightPx']>;
  location: FormControl<IDiningRoom['location']>;
};

export type DiningRoomFormGroup = FormGroup<DiningRoomFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DiningRoomFormService {
  createDiningRoomFormGroup(diningRoom: DiningRoomFormGroupInput = { id: null }): DiningRoomFormGroup {
    const diningRoomRawValue = {
      ...this.getFormDefaults(),
      ...diningRoom,
    };
    return new FormGroup<DiningRoomFormGroupContent>({
      id: new FormControl(
        { value: diningRoomRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      name: new FormControl(diningRoomRawValue.name, {
        validators: [Validators.required, Validators.minLength(2), Validators.maxLength(100)],
      }),
      description: new FormControl(diningRoomRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      floor: new FormControl(diningRoomRawValue.floor),
      capacity: new FormControl(diningRoomRawValue.capacity, {
        validators: [Validators.required, Validators.min(1)],
      }),
      isActive: new FormControl(diningRoomRawValue.isActive, {
        validators: [Validators.required],
      }),
      floorPlanUrl: new FormControl(diningRoomRawValue.floorPlanUrl, {
        validators: [Validators.maxLength(500)],
      }),
      widthPx: new FormControl(diningRoomRawValue.widthPx),
      heightPx: new FormControl(diningRoomRawValue.heightPx),
      location: new FormControl(diningRoomRawValue.location, {
        validators: [Validators.required],
      }),
    });
  }

  getDiningRoom(form: DiningRoomFormGroup): IDiningRoom | NewDiningRoom {
    return form.getRawValue() as IDiningRoom | NewDiningRoom;
  }

  resetForm(form: DiningRoomFormGroup, diningRoom: DiningRoomFormGroupInput): void {
    const diningRoomRawValue = { ...this.getFormDefaults(), ...diningRoom };
    form.reset(
      {
        ...diningRoomRawValue,
        id: { value: diningRoomRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DiningRoomFormDefaults {
    return {
      id: null,
      isActive: false,
    };
  }
}
