import dayjs from 'dayjs/esm';

import { IRestaurantOrder, NewRestaurantOrder } from './restaurant-order.model';

export const sampleWithRequiredData: IRestaurantOrder = {
  id: 20515,
  orderCode: 'besides for',
  status: 'PREPARING',
  isPreOrder: false,
  subtotal: 1701.84,
  totalAmount: 12456.18,
  createdAt: dayjs('2026-02-23T06:34'),
};

export const sampleWithPartialData: IRestaurantOrder = {
  id: 20953,
  orderCode: 'gown tomatillo',
  status: 'CANCELLED',
  isPreOrder: true,
  scheduledFor: dayjs('2026-02-22T16:18'),
  subtotal: 32512.83,
  discountAmount: 23006.45,
  totalAmount: 3876.63,
  confirmedAt: dayjs('2026-02-23T09:25'),
  createdAt: dayjs('2026-02-23T03:13'),
};

export const sampleWithFullData: IRestaurantOrder = {
  id: 89,
  orderCode: 'provision geez myste',
  status: 'PREPARING',
  isPreOrder: true,
  scheduledFor: dayjs('2026-02-23T11:01'),
  subtotal: 4724,
  discountAmount: 8193.04,
  taxAmount: 30643.81,
  totalAmount: 22645.53,
  specialInstructions: '../fake-data/blob/hipster.txt',
  estimatedReadyTime: dayjs('2026-02-23T08:56'),
  confirmedAt: dayjs('2026-02-23T10:12'),
  completedAt: dayjs('2026-02-23T13:15'),
  createdAt: dayjs('2026-02-22T22:51'),
  updatedAt: dayjs('2026-02-23T02:20'),
};

export const sampleWithNewData: NewRestaurantOrder = {
  orderCode: 'ackXXX',
  status: 'READY',
  isPreOrder: true,
  subtotal: 18272.38,
  totalAmount: 11747.17,
  createdAt: dayjs('2026-02-22T19:19'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
