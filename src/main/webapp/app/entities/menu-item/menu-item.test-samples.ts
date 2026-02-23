import { IMenuItem, NewMenuItem } from './menu-item.model';

export const sampleWithRequiredData: IMenuItem = {
  id: 3485,
  name: 'frankly gadzooks',
  price: 6763.09,
  isAvailable: false,
  isFeatured: true,
  displayOrder: 5357,
};

export const sampleWithPartialData: IMenuItem = {
  id: 8798,
  name: 'yet',
  description: '../fake-data/blob/hipster.txt',
  price: 23838.36,
  discountedPrice: 31514.72,
  calories: 2448,
  isAvailable: false,
  isFeatured: false,
  isVegetarian: true,
  isVegan: true,
  displayOrder: 6083,
};

export const sampleWithFullData: IMenuItem = {
  id: 3737,
  name: 'playfully knowledgeable',
  description: '../fake-data/blob/hipster.txt',
  price: 32011.89,
  discountedPrice: 22559.19,
  preparationTimeMinutes: 18,
  calories: 11072,
  imageUrl: 'which unwritten meanwhile',
  isAvailable: false,
  isFeatured: false,
  isVegetarian: true,
  isVegan: false,
  isGlutenFree: true,
  spicyLevel: 2,
  displayOrder: 10680,
};

export const sampleWithNewData: NewMenuItem = {
  name: 'under',
  price: 14445.51,
  isAvailable: false,
  isFeatured: true,
  displayOrder: 8296,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
