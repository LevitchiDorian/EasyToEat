import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { INotification, NewNotification } from '../notification.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts INotification for edit and NewNotificationFormGroupInput for create.
 */
type NotificationFormGroupInput = INotification | PartialWithRequiredKeyOf<NewNotification>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends INotification | NewNotification> = Omit<T, 'sentAt' | 'readAt' | 'createdAt'> & {
  sentAt?: string | null;
  readAt?: string | null;
  createdAt?: string | null;
};

type NotificationFormRawValue = FormValueOf<INotification>;

type NewNotificationFormRawValue = FormValueOf<NewNotification>;

type NotificationFormDefaults = Pick<NewNotification, 'id' | 'isRead' | 'sentAt' | 'readAt' | 'createdAt'>;

type NotificationFormGroupContent = {
  id: FormControl<NotificationFormRawValue['id'] | NewNotification['id']>;
  type: FormControl<NotificationFormRawValue['type']>;
  channel: FormControl<NotificationFormRawValue['channel']>;
  subject: FormControl<NotificationFormRawValue['subject']>;
  body: FormControl<NotificationFormRawValue['body']>;
  isRead: FormControl<NotificationFormRawValue['isRead']>;
  sentAt: FormControl<NotificationFormRawValue['sentAt']>;
  readAt: FormControl<NotificationFormRawValue['readAt']>;
  createdAt: FormControl<NotificationFormRawValue['createdAt']>;
  recipient: FormControl<NotificationFormRawValue['recipient']>;
  location: FormControl<NotificationFormRawValue['location']>;
  reservation: FormControl<NotificationFormRawValue['reservation']>;
  order: FormControl<NotificationFormRawValue['order']>;
};

export type NotificationFormGroup = FormGroup<NotificationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class NotificationFormService {
  createNotificationFormGroup(notification: NotificationFormGroupInput = { id: null }): NotificationFormGroup {
    const notificationRawValue = this.convertNotificationToNotificationRawValue({
      ...this.getFormDefaults(),
      ...notification,
    });
    return new FormGroup<NotificationFormGroupContent>({
      id: new FormControl(
        { value: notificationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      type: new FormControl(notificationRawValue.type, {
        validators: [Validators.required],
      }),
      channel: new FormControl(notificationRawValue.channel, {
        validators: [Validators.required],
      }),
      subject: new FormControl(notificationRawValue.subject, {
        validators: [Validators.maxLength(255)],
      }),
      body: new FormControl(notificationRawValue.body, {
        validators: [Validators.required],
      }),
      isRead: new FormControl(notificationRawValue.isRead, {
        validators: [Validators.required],
      }),
      sentAt: new FormControl(notificationRawValue.sentAt),
      readAt: new FormControl(notificationRawValue.readAt),
      createdAt: new FormControl(notificationRawValue.createdAt, {
        validators: [Validators.required],
      }),
      recipient: new FormControl(notificationRawValue.recipient),
      location: new FormControl(notificationRawValue.location),
      reservation: new FormControl(notificationRawValue.reservation),
      order: new FormControl(notificationRawValue.order),
    });
  }

  getNotification(form: NotificationFormGroup): INotification | NewNotification {
    return this.convertNotificationRawValueToNotification(form.getRawValue() as NotificationFormRawValue | NewNotificationFormRawValue);
  }

  resetForm(form: NotificationFormGroup, notification: NotificationFormGroupInput): void {
    const notificationRawValue = this.convertNotificationToNotificationRawValue({ ...this.getFormDefaults(), ...notification });
    form.reset(
      {
        ...notificationRawValue,
        id: { value: notificationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): NotificationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      isRead: false,
      sentAt: currentTime,
      readAt: currentTime,
      createdAt: currentTime,
    };
  }

  private convertNotificationRawValueToNotification(
    rawNotification: NotificationFormRawValue | NewNotificationFormRawValue,
  ): INotification | NewNotification {
    return {
      ...rawNotification,
      sentAt: dayjs(rawNotification.sentAt, DATE_TIME_FORMAT),
      readAt: dayjs(rawNotification.readAt, DATE_TIME_FORMAT),
      createdAt: dayjs(rawNotification.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertNotificationToNotificationRawValue(
    notification: INotification | (Partial<NewNotification> & NotificationFormDefaults),
  ): NotificationFormRawValue | PartialWithRequiredKeyOf<NewNotificationFormRawValue> {
    return {
      ...notification,
      sentAt: notification.sentAt ? notification.sentAt.format(DATE_TIME_FORMAT) : undefined,
      readAt: notification.readAt ? notification.readAt.format(DATE_TIME_FORMAT) : undefined,
      createdAt: notification.createdAt ? notification.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
