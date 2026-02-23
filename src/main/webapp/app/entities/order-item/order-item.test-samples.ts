import { IOrderItem, NewOrderItem } from './order-item.model';

export const sampleWithRequiredData: IOrderItem = {
  id: 16549,
  quantity: 23988,
  unitPrice: 22506.51,
  totalPrice: 12961.94,
  status: 'READY',
};

export const sampleWithPartialData: IOrderItem = {
  id: 22463,
  quantity: 24134,
  unitPrice: 5412.34,
  totalPrice: 25772.33,
  status: 'READY',
};

export const sampleWithFullData: IOrderItem = {
  id: 6728,
  quantity: 17449,
  unitPrice: 20263.67,
  totalPrice: 10948.34,
  status: 'PENDING',
  specialInstructions: 'greedily duh political',
  notes: 'cruelly',
};

export const sampleWithNewData: NewOrderItem = {
  quantity: 12165,
  unitPrice: 1027.11,
  totalPrice: 12468.3,
  status: 'READY',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
