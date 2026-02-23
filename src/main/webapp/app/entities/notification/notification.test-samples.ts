import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 10110,
  type: 'ORDER_STATUS_CHANGED',
  channel: 'EMAIL',
  body: '../fake-data/blob/hipster.txt',
  isRead: true,
  createdAt: dayjs('2026-02-23T11:03'),
};

export const sampleWithPartialData: INotification = {
  id: 19694,
  type: 'RESERVATION_CONFIRMED',
  channel: 'EMAIL',
  body: '../fake-data/blob/hipster.txt',
  isRead: false,
  readAt: dayjs('2026-02-23T05:05'),
  createdAt: dayjs('2026-02-23T00:54'),
};

export const sampleWithFullData: INotification = {
  id: 5787,
  type: 'ORDER_READY',
  channel: 'PUSH',
  subject: 'promptly fit leading',
  body: '../fake-data/blob/hipster.txt',
  isRead: true,
  sentAt: dayjs('2026-02-23T09:05'),
  readAt: dayjs('2026-02-23T15:26'),
  createdAt: dayjs('2026-02-23T07:11'),
};

export const sampleWithNewData: NewNotification = {
  type: 'REMINDER',
  channel: 'PUSH',
  body: '../fake-data/blob/hipster.txt',
  isRead: false,
  createdAt: dayjs('2026-02-22T18:51'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
