import dayjs from 'dayjs/esm';

import { IReview, NewReview } from './review.model';

export const sampleWithRequiredData: IReview = {
  id: 6640,
  overallRating: 2,
  isApproved: true,
  isAnonymous: false,
  createdAt: dayjs('2026-02-23T05:13'),
};

export const sampleWithPartialData: IReview = {
  id: 549,
  overallRating: 3,
  serviceRating: 5,
  ambienceRating: 2,
  comment: '../fake-data/blob/hipster.txt',
  isApproved: false,
  isAnonymous: false,
  createdAt: dayjs('2026-02-22T23:04'),
};

export const sampleWithFullData: IReview = {
  id: 30916,
  overallRating: 4,
  foodRating: 2,
  serviceRating: 5,
  ambienceRating: 3,
  comment: '../fake-data/blob/hipster.txt',
  isApproved: true,
  isAnonymous: true,
  createdAt: dayjs('2026-02-22T16:30'),
};

export const sampleWithNewData: NewReview = {
  overallRating: 2,
  isApproved: false,
  isAnonymous: true,
  createdAt: dayjs('2026-02-23T09:17'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
