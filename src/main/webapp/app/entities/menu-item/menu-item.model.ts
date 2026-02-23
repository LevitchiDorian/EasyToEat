import { IMenuCategory } from 'app/entities/menu-category/menu-category.model';

export interface IMenuItem {
  id: number;
  name?: string | null;
  description?: string | null;
  price?: number | null;
  discountedPrice?: number | null;
  preparationTimeMinutes?: number | null;
  calories?: number | null;
  imageUrl?: string | null;
  isAvailable?: boolean | null;
  isFeatured?: boolean | null;
  isVegetarian?: boolean | null;
  isVegan?: boolean | null;
  isGlutenFree?: boolean | null;
  spicyLevel?: number | null;
  displayOrder?: number | null;
  category?: Pick<IMenuCategory, 'id' | 'name'> | null;
}

export type NewMenuItem = Omit<IMenuItem, 'id'> & { id: null };
