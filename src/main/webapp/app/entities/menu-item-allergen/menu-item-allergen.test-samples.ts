import { IMenuItemAllergen, NewMenuItemAllergen } from './menu-item-allergen.model';

export const sampleWithRequiredData: IMenuItemAllergen = {
  id: 28108,
  allergen: 'NUTS',
};

export const sampleWithPartialData: IMenuItemAllergen = {
  id: 7720,
  allergen: 'SHELLFISH',
  notes: 'bolster',
};

export const sampleWithFullData: IMenuItemAllergen = {
  id: 11857,
  allergen: 'GLUTEN',
  notes: 'easily',
};

export const sampleWithNewData: NewMenuItemAllergen = {
  allergen: 'GLUTEN',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
