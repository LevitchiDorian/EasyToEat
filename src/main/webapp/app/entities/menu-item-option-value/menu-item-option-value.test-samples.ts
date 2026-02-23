import { IMenuItemOptionValue, NewMenuItemOptionValue } from './menu-item-option-value.model';

export const sampleWithRequiredData: IMenuItemOptionValue = {
  id: 13614,
  label: 'windy',
  priceAdjustment: 24944.2,
  isDefault: true,
  isAvailable: true,
};

export const sampleWithPartialData: IMenuItemOptionValue = {
  id: 5423,
  label: 'energetically',
  priceAdjustment: 12347.59,
  isDefault: true,
  isAvailable: true,
};

export const sampleWithFullData: IMenuItemOptionValue = {
  id: 22123,
  label: 'sans ornate inventory',
  priceAdjustment: 30304.8,
  isDefault: true,
  isAvailable: false,
  displayOrder: 26286,
};

export const sampleWithNewData: NewMenuItemOptionValue = {
  label: 'apropos',
  priceAdjustment: 1765.32,
  isDefault: false,
  isAvailable: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
