import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReservationTable, NewReservationTable } from '../reservation-table.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReservationTable for edit and NewReservationTableFormGroupInput for create.
 */
type ReservationTableFormGroupInput = IReservationTable | PartialWithRequiredKeyOf<NewReservationTable>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReservationTable | NewReservationTable> = Omit<T, 'assignedAt'> & {
  assignedAt?: string | null;
};

type ReservationTableFormRawValue = FormValueOf<IReservationTable>;

type NewReservationTableFormRawValue = FormValueOf<NewReservationTable>;

type ReservationTableFormDefaults = Pick<NewReservationTable, 'id' | 'assignedAt'>;

type ReservationTableFormGroupContent = {
  id: FormControl<ReservationTableFormRawValue['id'] | NewReservationTable['id']>;
  assignedAt: FormControl<ReservationTableFormRawValue['assignedAt']>;
  notes: FormControl<ReservationTableFormRawValue['notes']>;
  table: FormControl<ReservationTableFormRawValue['table']>;
  reservation: FormControl<ReservationTableFormRawValue['reservation']>;
};

export type ReservationTableFormGroup = FormGroup<ReservationTableFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReservationTableFormService {
  createReservationTableFormGroup(reservationTable: ReservationTableFormGroupInput = { id: null }): ReservationTableFormGroup {
    const reservationTableRawValue = this.convertReservationTableToReservationTableRawValue({
      ...this.getFormDefaults(),
      ...reservationTable,
    });
    return new FormGroup<ReservationTableFormGroupContent>({
      id: new FormControl(
        { value: reservationTableRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      assignedAt: new FormControl(reservationTableRawValue.assignedAt, {
        validators: [Validators.required],
      }),
      notes: new FormControl(reservationTableRawValue.notes, {
        validators: [Validators.maxLength(255)],
      }),
      table: new FormControl(reservationTableRawValue.table),
      reservation: new FormControl(reservationTableRawValue.reservation, {
        validators: [Validators.required],
      }),
    });
  }

  getReservationTable(form: ReservationTableFormGroup): IReservationTable | NewReservationTable {
    return this.convertReservationTableRawValueToReservationTable(
      form.getRawValue() as ReservationTableFormRawValue | NewReservationTableFormRawValue,
    );
  }

  resetForm(form: ReservationTableFormGroup, reservationTable: ReservationTableFormGroupInput): void {
    const reservationTableRawValue = this.convertReservationTableToReservationTableRawValue({
      ...this.getFormDefaults(),
      ...reservationTable,
    });
    form.reset(
      {
        ...reservationTableRawValue,
        id: { value: reservationTableRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReservationTableFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      assignedAt: currentTime,
    };
  }

  private convertReservationTableRawValueToReservationTable(
    rawReservationTable: ReservationTableFormRawValue | NewReservationTableFormRawValue,
  ): IReservationTable | NewReservationTable {
    return {
      ...rawReservationTable,
      assignedAt: dayjs(rawReservationTable.assignedAt, DATE_TIME_FORMAT),
    };
  }

  private convertReservationTableToReservationTableRawValue(
    reservationTable: IReservationTable | (Partial<NewReservationTable> & ReservationTableFormDefaults),
  ): ReservationTableFormRawValue | PartialWithRequiredKeyOf<NewReservationTableFormRawValue> {
    return {
      ...reservationTable,
      assignedAt: reservationTable.assignedAt ? reservationTable.assignedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
