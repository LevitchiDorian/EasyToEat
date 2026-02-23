import dayjs from 'dayjs/esm';

import { IPromotion, NewPromotion } from './promotion.model';

export const sampleWithRequiredData: IPromotion = {
  id: 12646,
  code: 'clear aha',
  name: 'inasmuch er',
  discountType: 'FIXED_AMOUNT',
  discountValue: 4285.3,
  startDate: dayjs('2026-02-23'),
  endDate: dayjs('2026-02-23'),
  isActive: true,
};

export const sampleWithPartialData: IPromotion = {
  id: 32127,
  code: 'gulp reproach or',
  name: 'bah concerning',
  description: '../fake-data/blob/hipster.txt',
  discountType: 'PERCENTAGE',
  discountValue: 24112.41,
  minimumOrderAmount: 19924.53,
  maxUsageCount: 31207,
  startDate: dayjs('2026-02-22'),
  endDate: dayjs('2026-02-23'),
  isActive: true,
};

export const sampleWithFullData: IPromotion = {
  id: 16704,
  code: 'pish',
  name: 'daintily hmph fictionalize',
  description: '../fake-data/blob/hipster.txt',
  discountType: 'PERCENTAGE',
  discountValue: 20501.69,
  minimumOrderAmount: 27105.62,
  maxUsageCount: 3266,
  currentUsageCount: 4498,
  startDate: dayjs('2026-02-22'),
  endDate: dayjs('2026-02-22'),
  isActive: false,
};

export const sampleWithNewData: NewPromotion = {
  code: 'quizzically pomelo',
  name: 'openly yowza knavishly',
  discountType: 'FIXED_AMOUNT',
  discountValue: 15775.68,
  startDate: dayjs('2026-02-22'),
  endDate: dayjs('2026-02-23'),
  isActive: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
