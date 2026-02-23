import dayjs from 'dayjs/esm';

import { IWaitingList, NewWaitingList } from './waiting-list.model';

export const sampleWithRequiredData: IWaitingList = {
  id: 27973,
  requestedDate: dayjs('2026-02-22'),
  requestedTime: 'drat ',
  partySize: 16233,
  isNotified: false,
  createdAt: dayjs('2026-02-22T19:26'),
};

export const sampleWithPartialData: IWaitingList = {
  id: 28701,
  requestedDate: dayjs('2026-02-23'),
  requestedTime: 'defia',
  partySize: 26616,
  isNotified: false,
  createdAt: dayjs('2026-02-23T14:04'),
};

export const sampleWithFullData: IWaitingList = {
  id: 6403,
  requestedDate: dayjs('2026-02-22'),
  requestedTime: 'bump ',
  partySize: 15299,
  notes: 'crackle excited than',
  isNotified: false,
  expiresAt: dayjs('2026-02-22T20:42'),
  createdAt: dayjs('2026-02-23T04:04'),
};

export const sampleWithNewData: NewWaitingList = {
  requestedDate: dayjs('2026-02-23'),
  requestedTime: 'towar',
  partySize: 16572,
  isNotified: true,
  createdAt: dayjs('2026-02-23T05:07'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
