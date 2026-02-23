import { IDiningRoom, NewDiningRoom } from './dining-room.model';

export const sampleWithRequiredData: IDiningRoom = {
  id: 4460,
  name: 'pilot',
  capacity: 21277,
  isActive: true,
};

export const sampleWithPartialData: IDiningRoom = {
  id: 26832,
  name: 'lock hose',
  description: 'beside worriedly thigh',
  floor: 29159,
  capacity: 9735,
  isActive: false,
};

export const sampleWithFullData: IDiningRoom = {
  id: 21018,
  name: 'well-documented',
  description: 'classic magnificent',
  floor: 29446,
  capacity: 30222,
  isActive: false,
  floorPlanUrl: 'replicate',
  widthPx: 18690.36,
  heightPx: 11299.94,
};

export const sampleWithNewData: NewDiningRoom = {
  name: 'including',
  capacity: 30958,
  isActive: false,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
