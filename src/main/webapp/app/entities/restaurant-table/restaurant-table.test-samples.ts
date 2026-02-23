import { IRestaurantTable, NewRestaurantTable } from './restaurant-table.model';

export const sampleWithRequiredData: IRestaurantTable = {
  id: 31790,
  tableNumber: 'from gee',
  shape: 'RECTANGLE',
  minCapacity: 18419,
  maxCapacity: 23367,
  status: 'AVAILABLE',
  isActive: true,
};

export const sampleWithPartialData: IRestaurantTable = {
  id: 4375,
  tableNumber: 'or toady following',
  shape: 'SQUARE',
  minCapacity: 1849,
  maxCapacity: 22730,
  positionX: 10831.66,
  rotation: 7051.88,
  status: 'OUT_OF_SERVICE',
  isActive: true,
  notes: 'who',
};

export const sampleWithFullData: IRestaurantTable = {
  id: 25873,
  tableNumber: 'jump abaft into',
  shape: 'SQUARE',
  minCapacity: 31662,
  maxCapacity: 5583,
  positionX: 17110.85,
  positionY: 11642.15,
  widthPx: 18143.23,
  heightPx: 20712.41,
  rotation: 2891.68,
  status: 'OCCUPIED',
  isActive: true,
  notes: 'righteously aboard cultivated',
};

export const sampleWithNewData: NewRestaurantTable = {
  tableNumber: 'forearm capitalize',
  shape: 'SQUARE',
  minCapacity: 3254,
  maxCapacity: 2833,
  status: 'OCCUPIED',
  isActive: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
