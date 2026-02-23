import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ILocation } from 'app/entities/location/location.model';
import { IReservation } from 'app/entities/reservation/reservation.model';
import { IRestaurantOrder } from 'app/entities/restaurant-order/restaurant-order.model';
import { NotificationType } from 'app/entities/enumerations/notification-type.model';
import { NotificationChannel } from 'app/entities/enumerations/notification-channel.model';

export interface INotification {
  id: number;
  type?: keyof typeof NotificationType | null;
  channel?: keyof typeof NotificationChannel | null;
  subject?: string | null;
  body?: string | null;
  isRead?: boolean | null;
  sentAt?: dayjs.Dayjs | null;
  readAt?: dayjs.Dayjs | null;
  createdAt?: dayjs.Dayjs | null;
  recipient?: Pick<IUser, 'id' | 'login'> | null;
  location?: Pick<ILocation, 'id' | 'name'> | null;
  reservation?: Pick<IReservation, 'id' | 'reservationCode'> | null;
  order?: Pick<IRestaurantOrder, 'id' | 'orderCode'> | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
