import dayjs from 'dayjs/esm';

export interface IBrand {
  id: number;
  name?: string | null;
  description?: string | null;
  logoUrl?: string | null;
  coverImageUrl?: string | null;
  primaryColor?: string | null;
  secondaryColor?: string | null;
  website?: string | null;
  contactEmail?: string | null;
  contactPhone?: string | null;
  defaultReservationDuration?: number | null;
  maxAdvanceBookingDays?: number | null;
  cancellationDeadlineHours?: number | null;
  isActive?: boolean | null;
  createdAt?: dayjs.Dayjs | null;
}

export type NewBrand = Omit<IBrand, 'id'> & { id: null };
