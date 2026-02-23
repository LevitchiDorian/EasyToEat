import { IMenuItem } from 'app/entities/menu-item/menu-item.model';
import { AllergenType } from 'app/entities/enumerations/allergen-type.model';

export interface IMenuItemAllergen {
  id: number;
  allergen?: keyof typeof AllergenType | null;
  notes?: string | null;
  menuItem?: Pick<IMenuItem, 'id' | 'name'> | null;
}

export type NewMenuItemAllergen = Omit<IMenuItemAllergen, 'id'> & { id: null };
