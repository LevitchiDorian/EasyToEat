import dayjs from 'dayjs/esm';
import { ILocation } from 'app/entities/location/location.model';
import { IUser } from 'app/entities/user/user.model';
import { IDiningRoom } from 'app/entities/dining-room/dining-room.model';
import { ReservationStatus } from 'app/entities/enumerations/reservation-status.model';

export interface IReservation {
  id: number;
  reservationCode?: string | null;
  reservationDate?: dayjs.Dayjs | null;
  startTime?: string | null;
  endTime?: string | null;
  partySize?: number | null;
  status?: keyof typeof ReservationStatus | null;
  specialRequests?: string | null;
  internalNotes?: string | null;
  reminderSentAt?: dayjs.Dayjs | null;
  confirmedAt?: dayjs.Dayjs | null;
  cancelledAt?: dayjs.Dayjs | null;
  cancellationReason?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  location?: Pick<ILocation, 'id' | 'name'> | null;
  client?: Pick<IUser, 'id' | 'login'> | null;
  room?: Pick<IDiningRoom, 'id' | 'name'> | null;
}

export type NewReservation = Omit<IReservation, 'id'> & { id: null };
