import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IWaitingList, NewWaitingList } from '../waiting-list.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IWaitingList for edit and NewWaitingListFormGroupInput for create.
 */
type WaitingListFormGroupInput = IWaitingList | PartialWithRequiredKeyOf<NewWaitingList>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IWaitingList | NewWaitingList> = Omit<T, 'expiresAt' | 'createdAt'> & {
  expiresAt?: string | null;
  createdAt?: string | null;
};

type WaitingListFormRawValue = FormValueOf<IWaitingList>;

type NewWaitingListFormRawValue = FormValueOf<NewWaitingList>;

type WaitingListFormDefaults = Pick<NewWaitingList, 'id' | 'isNotified' | 'expiresAt' | 'createdAt'>;

type WaitingListFormGroupContent = {
  id: FormControl<WaitingListFormRawValue['id'] | NewWaitingList['id']>;
  requestedDate: FormControl<WaitingListFormRawValue['requestedDate']>;
  requestedTime: FormControl<WaitingListFormRawValue['requestedTime']>;
  partySize: FormControl<WaitingListFormRawValue['partySize']>;
  notes: FormControl<WaitingListFormRawValue['notes']>;
  isNotified: FormControl<WaitingListFormRawValue['isNotified']>;
  expiresAt: FormControl<WaitingListFormRawValue['expiresAt']>;
  createdAt: FormControl<WaitingListFormRawValue['createdAt']>;
  location: FormControl<WaitingListFormRawValue['location']>;
  client: FormControl<WaitingListFormRawValue['client']>;
  room: FormControl<WaitingListFormRawValue['room']>;
};

export type WaitingListFormGroup = FormGroup<WaitingListFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class WaitingListFormService {
  createWaitingListFormGroup(waitingList: WaitingListFormGroupInput = { id: null }): WaitingListFormGroup {
    const waitingListRawValue = this.convertWaitingListToWaitingListRawValue({
      ...this.getFormDefaults(),
      ...waitingList,
    });
    return new FormGroup<WaitingListFormGroupContent>({
      id: new FormControl(
        { value: waitingListRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      requestedDate: new FormControl(waitingListRawValue.requestedDate, {
        validators: [Validators.required],
      }),
      requestedTime: new FormControl(waitingListRawValue.requestedTime, {
        validators: [Validators.required, Validators.maxLength(5)],
      }),
      partySize: new FormControl(waitingListRawValue.partySize, {
        validators: [Validators.required, Validators.min(1)],
      }),
      notes: new FormControl(waitingListRawValue.notes, {
        validators: [Validators.maxLength(500)],
      }),
      isNotified: new FormControl(waitingListRawValue.isNotified, {
        validators: [Validators.required],
      }),
      expiresAt: new FormControl(waitingListRawValue.expiresAt),
      createdAt: new FormControl(waitingListRawValue.createdAt, {
        validators: [Validators.required],
      }),
      location: new FormControl(waitingListRawValue.location),
      client: new FormControl(waitingListRawValue.client),
      room: new FormControl(waitingListRawValue.room),
    });
  }

  getWaitingList(form: WaitingListFormGroup): IWaitingList | NewWaitingList {
    return this.convertWaitingListRawValueToWaitingList(form.getRawValue() as WaitingListFormRawValue | NewWaitingListFormRawValue);
  }

  resetForm(form: WaitingListFormGroup, waitingList: WaitingListFormGroupInput): void {
    const waitingListRawValue = this.convertWaitingListToWaitingListRawValue({ ...this.getFormDefaults(), ...waitingList });
    form.reset(
      {
        ...waitingListRawValue,
        id: { value: waitingListRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): WaitingListFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isNotified: false,
      expiresAt: currentTime,
      createdAt: currentTime,
    };
  }

  private convertWaitingListRawValueToWaitingList(
    rawWaitingList: WaitingListFormRawValue | NewWaitingListFormRawValue,
  ): IWaitingList | NewWaitingList {
    return {
      ...rawWaitingList,
      expiresAt: dayjs(rawWaitingList.expiresAt, DATE_TIME_FORMAT),
      createdAt: dayjs(rawWaitingList.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertWaitingListToWaitingListRawValue(
    waitingList: IWaitingList | (Partial<NewWaitingList> & WaitingListFormDefaults),
  ): WaitingListFormRawValue | PartialWithRequiredKeyOf<NewWaitingListFormRawValue> {
    return {
      ...waitingList,
      expiresAt: waitingList.expiresAt ? waitingList.expiresAt.format(DATE_TIME_FORMAT) : undefined,
      createdAt: waitingList.createdAt ? waitingList.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
