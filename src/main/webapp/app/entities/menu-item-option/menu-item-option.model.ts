import { IMenuItem } from 'app/entities/menu-item/menu-item.model';

export interface IMenuItemOption {
  id: number;
  name?: string | null;
  isRequired?: boolean | null;
  maxSelections?: number | null;
  displayOrder?: number | null;
  menuItem?: Pick<IMenuItem, 'id' | 'name'> | null;
}

export type NewMenuItemOption = Omit<IMenuItemOption, 'id'> & { id: null };
