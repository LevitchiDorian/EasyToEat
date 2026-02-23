import dayjs from 'dayjs/esm';
import { IRestaurantTable } from 'app/entities/restaurant-table/restaurant-table.model';
import { IReservation } from 'app/entities/reservation/reservation.model';

export interface IReservationTable {
  id: number;
  assignedAt?: dayjs.Dayjs | null;
  notes?: string | null;
  table?: Pick<IRestaurantTable, 'id' | 'tableNumber'> | null;
  reservation?: Pick<IReservation, 'id'> | null;
}

export type NewReservationTable = Omit<IReservationTable, 'id'> & { id: null };
