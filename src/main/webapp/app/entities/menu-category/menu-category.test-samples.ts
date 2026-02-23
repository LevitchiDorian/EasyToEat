import { IMenuCategory, NewMenuCategory } from './menu-category.model';

export const sampleWithRequiredData: IMenuCategory = {
  id: 5530,
  name: 'pfft',
  displayOrder: 21056,
  isActive: true,
};

export const sampleWithPartialData: IMenuCategory = {
  id: 12461,
  name: 'why gadzooks phony',
  imageUrl: 'often ah',
  displayOrder: 14598,
  isActive: true,
};

export const sampleWithFullData: IMenuCategory = {
  id: 25541,
  name: 'exactly',
  description: 'eek',
  imageUrl: 'yuck excellent fabricate',
  displayOrder: 22713,
  isActive: true,
};

export const sampleWithNewData: NewMenuCategory = {
  name: 'conceptualize vaguely',
  displayOrder: 19332,
  isActive: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
