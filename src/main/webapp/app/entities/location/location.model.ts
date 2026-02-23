import dayjs from 'dayjs/esm';
import { IBrand } from 'app/entities/brand/brand.model';

export interface ILocation {
  id: number;
  name?: string | null;
  address?: string | null;
  city?: string | null;
  phone?: string | null;
  email?: string | null;
  latitude?: number | null;
  longitude?: number | null;
  reservationDurationOverride?: number | null;
  maxAdvanceBookingDaysOverride?: number | null;
  cancellationDeadlineOverride?: number | null;
  isActive?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
  brand?: Pick<IBrand, 'id' | 'name'> | null;
}

export type NewLocation = Omit<ILocation, 'id'> & { id: null };
