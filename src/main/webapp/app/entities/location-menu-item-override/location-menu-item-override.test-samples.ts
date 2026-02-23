import { ILocationMenuItemOverride, NewLocationMenuItemOverride } from './location-menu-item-override.model';

export const sampleWithRequiredData: ILocationMenuItemOverride = {
  id: 25018,
  isAvailableAtLocation: true,
};

export const sampleWithPartialData: ILocationMenuItemOverride = {
  id: 1141,
  isAvailableAtLocation: false,
  preparationTimeOverride: 1,
  notes: 'sternly',
};

export const sampleWithFullData: ILocationMenuItemOverride = {
  id: 3922,
  isAvailableAtLocation: false,
  priceOverride: 10342.4,
  preparationTimeOverride: 146,
  notes: 'up',
};

export const sampleWithNewData: NewLocationMenuItemOverride = {
  isAvailableAtLocation: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
