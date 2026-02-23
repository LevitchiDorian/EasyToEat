import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReservation, NewReservation } from '../reservation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReservation for edit and NewReservationFormGroupInput for create.
 */
type ReservationFormGroupInput = IReservation | PartialWithRequiredKeyOf<NewReservation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReservation | NewReservation> = Omit<
  T,
  'reminderSentAt' | 'confirmedAt' | 'cancelledAt' | 'createdAt' | 'updatedAt'
> & {
  reminderSentAt?: string | null;
  confirmedAt?: string | null;
  cancelledAt?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
};

type ReservationFormRawValue = FormValueOf<IReservation>;

type NewReservationFormRawValue = FormValueOf<NewReservation>;

type ReservationFormDefaults = Pick<NewReservation, 'id' | 'reminderSentAt' | 'confirmedAt' | 'cancelledAt' | 'createdAt' | 'updatedAt'>;

type ReservationFormGroupContent = {
  id: FormControl<ReservationFormRawValue['id'] | NewReservation['id']>;
  reservationCode: FormControl<ReservationFormRawValue['reservationCode']>;
  reservationDate: FormControl<ReservationFormRawValue['reservationDate']>;
  startTime: FormControl<ReservationFormRawValue['startTime']>;
  endTime: FormControl<ReservationFormRawValue['endTime']>;
  partySize: FormControl<ReservationFormRawValue['partySize']>;
  status: FormControl<ReservationFormRawValue['status']>;
  specialRequests: FormControl<ReservationFormRawValue['specialRequests']>;
  internalNotes: FormControl<ReservationFormRawValue['internalNotes']>;
  reminderSentAt: FormControl<ReservationFormRawValue['reminderSentAt']>;
  confirmedAt: FormControl<ReservationFormRawValue['confirmedAt']>;
  cancelledAt: FormControl<ReservationFormRawValue['cancelledAt']>;
  cancellationReason: FormControl<ReservationFormRawValue['cancellationReason']>;
  createdAt: FormControl<ReservationFormRawValue['createdAt']>;
  updatedAt: FormControl<ReservationFormRawValue['updatedAt']>;
  location: FormControl<ReservationFormRawValue['location']>;
  client: FormControl<ReservationFormRawValue['client']>;
  room: FormControl<ReservationFormRawValue['room']>;
};

export type ReservationFormGroup = FormGroup<ReservationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReservationFormService {
  createReservationFormGroup(reservation: ReservationFormGroupInput = { id: null }): ReservationFormGroup {
    const reservationRawValue = this.convertReservationToReservationRawValue({
      ...this.getFormDefaults(),
      ...reservation,
    });
    return new FormGroup<ReservationFormGroupContent>({
      id: new FormControl(
        { value: reservationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      reservationCode: new FormControl(reservationRawValue.reservationCode, {
        validators: [Validators.required, Validators.minLength(6), Validators.maxLength(20)],
      }),
      reservationDate: new FormControl(reservationRawValue.reservationDate, {
        validators: [Validators.required],
      }),
      startTime: new FormControl(reservationRawValue.startTime, {
        validators: [Validators.required, Validators.maxLength(5)],
      }),
      endTime: new FormControl(reservationRawValue.endTime, {
        validators: [Validators.required, Validators.maxLength(5)],
      }),
      partySize: new FormControl(reservationRawValue.partySize, {
        validators: [Validators.required, Validators.min(1), Validators.max(50)],
      }),
      status: new FormControl(reservationRawValue.status, {
        validators: [Validators.required],
      }),
      specialRequests: new FormControl(reservationRawValue.specialRequests),
      internalNotes: new FormControl(reservationRawValue.internalNotes),
      reminderSentAt: new FormControl(reservationRawValue.reminderSentAt),
      confirmedAt: new FormControl(reservationRawValue.confirmedAt),
      cancelledAt: new FormControl(reservationRawValue.cancelledAt),
      cancellationReason: new FormControl(reservationRawValue.cancellationReason, {
        validators: [Validators.maxLength(500)],
      }),
      createdAt: new FormControl(reservationRawValue.createdAt, {
        validators: [Validators.required],
      }),
      updatedAt: new FormControl(reservationRawValue.updatedAt),
      location: new FormControl(reservationRawValue.location),
      client: new FormControl(reservationRawValue.client),
      room: new FormControl(reservationRawValue.room),
    });
  }

  getReservation(form: ReservationFormGroup): IReservation | NewReservation {
    return this.convertReservationRawValueToReservation(form.getRawValue() as ReservationFormRawValue | NewReservationFormRawValue);
  }

  resetForm(form: ReservationFormGroup, reservation: ReservationFormGroupInput): void {
    const reservationRawValue = this.convertReservationToReservationRawValue({ ...this.getFormDefaults(), ...reservation });
    form.reset(
      {
        ...reservationRawValue,
        id: { value: reservationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): ReservationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      reminderSentAt: currentTime,
      confirmedAt: currentTime,
      cancelledAt: currentTime,
      createdAt: currentTime,
      updatedAt: currentTime,
    };
  }

  private convertReservationRawValueToReservation(
    rawReservation: ReservationFormRawValue | NewReservationFormRawValue,
  ): IReservation | NewReservation {
    return {
      ...rawReservation,
      reminderSentAt: dayjs(rawReservation.reminderSentAt, DATE_TIME_FORMAT),
      confirmedAt: dayjs(rawReservation.confirmedAt, DATE_TIME_FORMAT),
      cancelledAt: dayjs(rawReservation.cancelledAt, DATE_TIME_FORMAT),
      createdAt: dayjs(rawReservation.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawReservation.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertReservationToReservationRawValue(
    reservation: IReservation | (Partial<NewReservation> & ReservationFormDefaults),
  ): ReservationFormRawValue | PartialWithRequiredKeyOf<NewReservationFormRawValue> {
    return {
      ...reservation,
      reminderSentAt: reservation.reminderSentAt ? reservation.reminderSentAt.format(DATE_TIME_FORMAT) : undefined,
      confirmedAt: reservation.confirmedAt ? reservation.confirmedAt.format(DATE_TIME_FORMAT) : undefined,
      cancelledAt: reservation.cancelledAt ? reservation.cancelledAt.format(DATE_TIME_FORMAT) : undefined,
      createdAt: reservation.createdAt ? reservation.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: reservation.updatedAt ? reservation.updatedAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
