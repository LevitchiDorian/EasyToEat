import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { IRestaurantOrder } from 'app/entities/restaurant-order/restaurant-order.model';
import { OrderItemStatus } from 'app/entities/enumerations/order-item-status.model';

export interface IOrderItem {
  id: number;
  quantity?: number | null;
  unitPrice?: number | null;
  totalPrice?: number | null;
  status?: keyof typeof OrderItemStatus | null;
  specialInstructions?: string | null;
  notes?: string | null;
  menuItem?: Pick<IMenuItem, 'id' | 'name'> | null;
  order?: Pick<IRestaurantOrder, 'id'> | null;
}

export type NewOrderItem = Omit<IOrderItem, 'id'> & { id: null };
