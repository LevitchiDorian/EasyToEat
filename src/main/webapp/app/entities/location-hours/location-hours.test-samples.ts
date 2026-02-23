import { ILocationHours, NewLocationHours } from './location-hours.model';

export const sampleWithRequiredData: ILocationHours = {
  id: 29176,
  dayOfWeek: 'FRIDAY',
  openTime: 'rough',
  closeTime: 'aston',
  isClosed: false,
};

export const sampleWithPartialData: ILocationHours = {
  id: 18696,
  dayOfWeek: 'MONDAY',
  openTime: 'low t',
  closeTime: 'prude',
  isClosed: true,
  specialNote: 'quarrel huge hence',
};

export const sampleWithFullData: ILocationHours = {
  id: 16686,
  dayOfWeek: 'TUESDAY',
  openTime: 'penin',
  closeTime: 'resou',
  isClosed: false,
  specialNote: 'within digestive doubtfully',
};

export const sampleWithNewData: NewLocationHours = {
  dayOfWeek: 'WEDNESDAY',
  openTime: 'phew ',
  closeTime: 'ew',
  isClosed: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
