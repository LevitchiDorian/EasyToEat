import { IMenuItemOption, NewMenuItemOption } from './menu-item-option.model';

export const sampleWithRequiredData: IMenuItemOption = {
  id: 16098,
  name: 'optimistically',
  isRequired: false,
};

export const sampleWithPartialData: IMenuItemOption = {
  id: 29991,
  name: 'bitterly till',
  isRequired: false,
};

export const sampleWithFullData: IMenuItemOption = {
  id: 29561,
  name: 'anxiously',
  isRequired: false,
  maxSelections: 16882,
  displayOrder: 27772,
};

export const sampleWithNewData: NewMenuItemOption = {
  name: 'kiddingly',
  isRequired: true,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
