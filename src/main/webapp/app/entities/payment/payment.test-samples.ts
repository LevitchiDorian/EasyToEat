import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 4942,
  transactionCode: 'on huddle',
  amount: 4830.24,
  method: 'CARD',
  status: 'FAILED',
  createdAt: dayjs('2026-02-22T15:46'),
};

export const sampleWithPartialData: IPayment = {
  id: 18677,
  transactionCode: 'dally debut',
  amount: 3366.95,
  method: 'CASH',
  status: 'PAID',
  receiptUrl: 'unzip transom round',
  createdAt: dayjs('2026-02-22T19:29'),
};

export const sampleWithFullData: IPayment = {
  id: 28239,
  transactionCode: 'hourly unwieldy',
  amount: 3889.72,
  method: 'CARD',
  status: 'FAILED',
  paidAt: dayjs('2026-02-23T13:00'),
  receiptUrl: 'remark yum tame',
  notes: 'encouragement alligator oof',
  createdAt: dayjs('2026-02-23T05:31'),
};

export const sampleWithNewData: NewPayment = {
  transactionCode: 'within',
  amount: 6944.47,
  method: 'CASH',
  status: 'FAILED',
  createdAt: dayjs('2026-02-23T02:12'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
