import { ILocation } from 'app/entities/location/location.model';
import { DayOfWeek } from 'app/entities/enumerations/day-of-week.model';

export interface ILocationHours {
  id: number;
  dayOfWeek?: keyof typeof DayOfWeek | null;
  openTime?: string | null;
  closeTime?: string | null;
  isClosed?: boolean | null;
  specialNote?: string | null;
  location?: Pick<ILocation, 'id' | 'name'> | null;
}

export type NewLocationHours = Omit<ILocationHours, 'id'> & { id: null };
