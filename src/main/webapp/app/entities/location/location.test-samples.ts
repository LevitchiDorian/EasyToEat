import dayjs from 'dayjs/esm';

import { ILocation, NewLocation } from './location.model';

export const sampleWithRequiredData: ILocation = {
  id: 12482,
  name: 'and',
  address: 'drowse unto considering',
  city: 'West Jedchester',
  phone: '1-329-448-9164 x677',
  email: 'Isom.Feil4@hotmail.com',
  isActive: false,
  createdAt: dayjs('2026-02-23T07:54'),
};

export const sampleWithPartialData: ILocation = {
  id: 4414,
  name: 'modulo',
  address: 'scenario',
  city: 'New Hayleeboro',
  phone: '(287) 237-7525',
  email: 'Dangelo_Roberts@hotmail.com',
  longitude: 325.92,
  reservationDurationOverride: 230,
  isActive: false,
  createdAt: dayjs('2026-02-23T11:46'),
};

export const sampleWithFullData: ILocation = {
  id: 22007,
  name: 'abseil given',
  address: 'underneath emerge excluding',
  city: 'Port Peyton',
  phone: '888-497-8561 x96614',
  email: 'Jayda62@hotmail.com',
  latitude: 26052.08,
  longitude: 1410.31,
  reservationDurationOverride: 271,
  maxAdvanceBookingDaysOverride: 32,
  cancellationDeadlineOverride: 58,
  isActive: false,
  createdAt: dayjs('2026-02-23T06:29'),
};

export const sampleWithNewData: NewLocation = {
  name: 'cake',
  address: 'plus train ornate',
  city: 'Kovacekworth',
  phone: '1-417-758-2828 x1360',
  email: 'Destini93@yahoo.com',
  isActive: true,
  createdAt: dayjs('2026-02-22T23:41'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
