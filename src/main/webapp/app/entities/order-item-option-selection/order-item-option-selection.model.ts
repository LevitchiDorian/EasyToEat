import { IMenuItemOptionValue } from 'app/entities/menu-item-option-value/menu-item-option-value.model';
import { IOrderItem } from 'app/entities/order-item/order-item.model';

export interface IOrderItemOptionSelection {
  id: number;
  quantity?: number | null;
  unitPrice?: number | null;
  optionValue?: Pick<IMenuItemOptionValue, 'id' | 'label'> | null;
  orderItem?: Pick<IOrderItem, 'id'> | null;
}

export type NewOrderItemOptionSelection = Omit<IOrderItemOptionSelection, 'id'> & { id: null };
