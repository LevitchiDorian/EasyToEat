import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPayment, NewPayment } from '../payment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPayment for edit and NewPaymentFormGroupInput for create.
 */
type PaymentFormGroupInput = IPayment | PartialWithRequiredKeyOf<NewPayment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPayment | NewPayment> = Omit<T, 'paidAt' | 'createdAt'> & {
  paidAt?: string | null;
  createdAt?: string | null;
};

type PaymentFormRawValue = FormValueOf<IPayment>;

type NewPaymentFormRawValue = FormValueOf<NewPayment>;

type PaymentFormDefaults = Pick<NewPayment, 'id' | 'paidAt' | 'createdAt'>;

type PaymentFormGroupContent = {
  id: FormControl<PaymentFormRawValue['id'] | NewPayment['id']>;
  transactionCode: FormControl<PaymentFormRawValue['transactionCode']>;
  amount: FormControl<PaymentFormRawValue['amount']>;
  method: FormControl<PaymentFormRawValue['method']>;
  status: FormControl<PaymentFormRawValue['status']>;
  paidAt: FormControl<PaymentFormRawValue['paidAt']>;
  receiptUrl: FormControl<PaymentFormRawValue['receiptUrl']>;
  notes: FormControl<PaymentFormRawValue['notes']>;
  createdAt: FormControl<PaymentFormRawValue['createdAt']>;
  processedBy: FormControl<PaymentFormRawValue['processedBy']>;
  order: FormControl<PaymentFormRawValue['order']>;
};

export type PaymentFormGroup = FormGroup<PaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaymentFormService {
  createPaymentFormGroup(payment: PaymentFormGroupInput = { id: null }): PaymentFormGroup {
    const paymentRawValue = this.convertPaymentToPaymentRawValue({
      ...this.getFormDefaults(),
      ...payment,
    });
    return new FormGroup<PaymentFormGroupContent>({
      id: new FormControl(
        { value: paymentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      transactionCode: new FormControl(paymentRawValue.transactionCode, {
        validators: [Validators.required, Validators.maxLength(100)],
      }),
      amount: new FormControl(paymentRawValue.amount, {
        validators: [Validators.required, Validators.min(0)],
      }),
      method: new FormControl(paymentRawValue.method, {
        validators: [Validators.required],
      }),
      status: new FormControl(paymentRawValue.status, {
        validators: [Validators.required],
      }),
      paidAt: new FormControl(paymentRawValue.paidAt),
      receiptUrl: new FormControl(paymentRawValue.receiptUrl, {
        validators: [Validators.maxLength(500)],
      }),
      notes: new FormControl(paymentRawValue.notes, {
        validators: [Validators.maxLength(500)],
      }),
      createdAt: new FormControl(paymentRawValue.createdAt, {
        validators: [Validators.required],
      }),
      processedBy: new FormControl(paymentRawValue.processedBy),
      order: new FormControl(paymentRawValue.order, {
        validators: [Validators.required],
      }),
    });
  }

  getPayment(form: PaymentFormGroup): IPayment | NewPayment {
    return this.convertPaymentRawValueToPayment(form.getRawValue() as PaymentFormRawValue | NewPaymentFormRawValue);
  }

  resetForm(form: PaymentFormGroup, payment: PaymentFormGroupInput): void {
    const paymentRawValue = this.convertPaymentToPaymentRawValue({ ...this.getFormDefaults(), ...payment });
    form.reset(
      {
        ...paymentRawValue,
        id: { value: paymentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PaymentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      paidAt: currentTime,
      createdAt: currentTime,
    };
  }

  private convertPaymentRawValueToPayment(rawPayment: PaymentFormRawValue | NewPaymentFormRawValue): IPayment | NewPayment {
    return {
      ...rawPayment,
      paidAt: dayjs(rawPayment.paidAt, DATE_TIME_FORMAT),
      createdAt: dayjs(rawPayment.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertPaymentToPaymentRawValue(
    payment: IPayment | (Partial<NewPayment> & PaymentFormDefaults),
  ): PaymentFormRawValue | PartialWithRequiredKeyOf<NewPaymentFormRawValue> {
    return {
      ...payment,
      paidAt: payment.paidAt ? payment.paidAt.format(DATE_TIME_FORMAT) : undefined,
      createdAt: payment.createdAt ? payment.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
