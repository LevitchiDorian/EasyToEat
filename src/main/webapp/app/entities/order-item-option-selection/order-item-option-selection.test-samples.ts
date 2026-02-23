import { IOrderItemOptionSelection, NewOrderItemOptionSelection } from './order-item-option-selection.model';

export const sampleWithRequiredData: IOrderItemOptionSelection = {
  id: 17449,
  unitPrice: 28663.22,
};

export const sampleWithPartialData: IOrderItemOptionSelection = {
  id: 16186,
  quantity: 22492,
  unitPrice: 14956.01,
};

export const sampleWithFullData: IOrderItemOptionSelection = {
  id: 5004,
  quantity: 24001,
  unitPrice: 3677.16,
};

export const sampleWithNewData: NewOrderItemOptionSelection = {
  unitPrice: 21360.04,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
