import dayjs from 'dayjs/esm';

import { IReservation, NewReservation } from './reservation.model';

export const sampleWithRequiredData: IReservation = {
  id: 30105,
  reservationCode: 'now seal',
  reservationDate: dayjs('2026-02-22'),
  startTime: 'fooey',
  endTime: 'vainl',
  partySize: 32,
  status: 'COMPLETED',
  createdAt: dayjs('2026-02-22T15:50'),
};

export const sampleWithPartialData: IReservation = {
  id: 9932,
  reservationCode: 'knifeX',
  reservationDate: dayjs('2026-02-23'),
  startTime: 'boo r',
  endTime: 'unawa',
  partySize: 30,
  status: 'PENDING',
  specialRequests: '../fake-data/blob/hipster.txt',
  reminderSentAt: dayjs('2026-02-22T16:59'),
  confirmedAt: dayjs('2026-02-23T03:36'),
  cancellationReason: 'yahoo jealously',
  createdAt: dayjs('2026-02-23T02:43'),
  updatedAt: dayjs('2026-02-22T18:25'),
};

export const sampleWithFullData: IReservation = {
  id: 27073,
  reservationCode: 'paceXX',
  reservationDate: dayjs('2026-02-23'),
  startTime: 'peter',
  endTime: 'hefty',
  partySize: 11,
  status: 'COMPLETED',
  specialRequests: '../fake-data/blob/hipster.txt',
  internalNotes: '../fake-data/blob/hipster.txt',
  reminderSentAt: dayjs('2026-02-23T05:00'),
  confirmedAt: dayjs('2026-02-23T06:07'),
  cancelledAt: dayjs('2026-02-23T04:08'),
  cancellationReason: 'misreport',
  createdAt: dayjs('2026-02-23T08:38'),
  updatedAt: dayjs('2026-02-23T09:10'),
};

export const sampleWithNewData: NewReservation = {
  reservationCode: 'stealthily hexagon',
  reservationDate: dayjs('2026-02-23'),
  startTime: 'regar',
  endTime: 'inwar',
  partySize: 16,
  status: 'COMPLETED',
  createdAt: dayjs('2026-02-22T15:54'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
