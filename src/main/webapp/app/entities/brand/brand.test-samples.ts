import dayjs from 'dayjs/esm';

import { IBrand, NewBrand } from './brand.model';

export const sampleWithRequiredData: IBrand = {
  id: 20780,
  name: 'sandy cheerfully unto',
  contactEmail: 'usher metabolite towards',
  contactPhone: 'almost impressive wh',
  defaultReservationDuration: 19,
  maxAdvanceBookingDays: 156,
  cancellationDeadlineHours: 58,
  isActive: false,
  createdAt: dayjs('2026-02-23T11:55'),
};

export const sampleWithPartialData: IBrand = {
  id: 887,
  name: 'daily',
  secondaryColor: 'yuppify',
  website: 'raw blight quick',
  contactEmail: 'through duh soulful',
  contactPhone: 'joyous duh',
  defaultReservationDuration: 372,
  maxAdvanceBookingDays: 113,
  cancellationDeadlineHours: 44,
  isActive: true,
  createdAt: dayjs('2026-02-22T17:04'),
};

export const sampleWithFullData: IBrand = {
  id: 9187,
  name: 'er',
  description: '../fake-data/blob/hipster.txt',
  logoUrl: 'shell drum',
  coverImageUrl: 'eek',
  primaryColor: 'outrage',
  secondaryColor: 'that',
  website: 'boldly er typify',
  contactEmail: 'which up redress',
  contactPhone: 'after oof drat',
  defaultReservationDuration: 55,
  maxAdvanceBookingDays: 318,
  cancellationDeadlineHours: 42,
  isActive: true,
  createdAt: dayjs('2026-02-23T04:50'),
};

export const sampleWithNewData: NewBrand = {
  name: 'delete',
  contactEmail: 'synergy grouper windy',
  contactPhone: 'longboat scenario',
  defaultReservationDuration: 88,
  maxAdvanceBookingDays: 332,
  cancellationDeadlineHours: 56,
  isActive: false,
  createdAt: dayjs('2026-02-23T08:35'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
