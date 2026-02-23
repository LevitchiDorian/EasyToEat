import dayjs from 'dayjs/esm';

import { IUserProfile, NewUserProfile } from './user-profile.model';

export const sampleWithRequiredData: IUserProfile = {
  id: 4033,
  role: 'CLIENT',
  createdAt: dayjs('2026-02-23T01:04'),
};

export const sampleWithPartialData: IUserProfile = {
  id: 22011,
  phone: '514.627.5759 x09805',
  avatarUrl: 'unpleasant yearn',
  role: 'MANAGER',
  preferredLanguage: 'dishonor q',
  createdAt: dayjs('2026-02-23T10:20'),
};

export const sampleWithFullData: IUserProfile = {
  id: 9570,
  phone: '932-280-3506 x38036',
  avatarUrl: 'afterwards interestingly',
  role: 'MANAGER',
  preferredLanguage: 'festival f',
  receiveEmailNotifications: false,
  receivePushNotifications: false,
  loyaltyPoints: 28439,
  createdAt: dayjs('2026-02-23T04:32'),
};

export const sampleWithNewData: NewUserProfile = {
  role: 'CLIENT',
  createdAt: dayjs('2026-02-23T12:16'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
