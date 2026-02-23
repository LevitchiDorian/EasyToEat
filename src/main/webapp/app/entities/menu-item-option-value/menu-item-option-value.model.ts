import { IMenuItemOption } from 'app/entities/menu-item-option/menu-item-option.model';

export interface IMenuItemOptionValue {
  id: number;
  label?: string | null;
  priceAdjustment?: number | null;
  isDefault?: boolean | null;
  isAvailable?: boolean | null;
  displayOrder?: number | null;
  option?: Pick<IMenuItemOption, 'id' | 'name'> | null;
}

export type NewMenuItemOptionValue = Omit<IMenuItemOptionValue, 'id'> & { id: null };
