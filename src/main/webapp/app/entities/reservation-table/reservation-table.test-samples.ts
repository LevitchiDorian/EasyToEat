import dayjs from 'dayjs/esm';

import { IReservationTable, NewReservationTable } from './reservation-table.model';

export const sampleWithRequiredData: IReservationTable = {
  id: 8560,
  assignedAt: dayjs('2026-02-23T12:50'),
};

export const sampleWithPartialData: IReservationTable = {
  id: 18358,
  assignedAt: dayjs('2026-02-22T20:58'),
  notes: 'psst',
};

export const sampleWithFullData: IReservationTable = {
  id: 21941,
  assignedAt: dayjs('2026-02-23T12:30'),
  notes: 'woefully propound',
};

export const sampleWithNewData: NewReservationTable = {
  assignedAt: dayjs('2026-02-23T03:42'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
