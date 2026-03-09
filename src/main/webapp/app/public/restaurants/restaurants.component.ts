import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { BrandService } from 'app/entities/brand/service/brand.service';

export interface RestaurantCard {
  id: number;
  name: string;
  cuisine: string;
  cuisineKey: string;
  description: string;
  imageUrl: string;
  rating: number;
  address: string;
  city: string;
  isOpen: boolean;
  locationsCount: number;
}

const BRAND_META: Record<
  string,
  { cuisine: string; cuisineKey: string; description: string; imageUrl: string; rating: number; address: string; locationsCount: number }
> = {
  'La Plăcinte': {
    cuisine: 'Moldovenească',
    cuisineKey: 'moldoveneasca',
    description: 'Lanț de restaurante tradiționale moldovenești, renumit pentru plăcinte, mâncăruri de casă și atmosferă caldă, autentică.',
    imageUrl: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=700&q=80',
    rating: 4.8,
    address: 'Bd. Ștefan cel Mare 6',
    locationsCount: 2,
  },
  "Andy's Pizza": {
    cuisine: 'Italiană',
    cuisineKey: 'italiana',
    description: 'Cel mai popular lanț de pizzerii din Moldova, cu rețete italiene adaptate gustului local și ingrediente proaspete.',
    imageUrl: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=700&q=80',
    rating: 4.7,
    address: 'Str. Ismail 90',
    locationsCount: 2,
  },
  'Sushi House': {
    cuisine: 'Japoneză',
    cuisineKey: 'japoneza',
    description: 'Restaurant japonez premium cu sushi proaspăt, ramen autentic și specialități asiatice în inima Chișinăului.',
    imageUrl: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=700&q=80',
    rating: 4.9,
    address: 'Str. Columna 170',
    locationsCount: 1,
  },
  'Vatra Neamului': {
    cuisine: 'Moldovenească',
    cuisineKey: 'moldoveneasca',
    description: 'Restaurant tradițional moldovenesc cu specific național, muzică live în weekend și bucătărie autentică.',
    imageUrl: 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=700&q=80',
    rating: 4.8,
    address: 'Str. Vasile Alecsandri 108',
    locationsCount: 1,
  },
  'Green Hills': {
    cuisine: 'Vegetariană',
    cuisineKey: 'vegetariana',
    description: 'Café și restaurant sănătos cu mâncare bio, sucuri naturale presate la rece și o atmosferă relaxantă.',
    imageUrl: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=700&q=80',
    rating: 4.6,
    address: 'Str. București 67',
    locationsCount: 2,
  },
};

const CUISINES = [
  { key: 'toate', label: 'Toate' },
  { key: 'moldoveneasca', label: 'Moldovenească' },
  { key: 'italiana', label: 'Italiană' },
  { key: 'japoneza', label: 'Japoneză' },
  { key: 'vegetariana', label: 'Vegetariană' },
];

const RATINGS = [
  { value: 0, label: 'Toate' },
  { value: 3, label: '3+' },
  { value: 3.5, label: '3.5+' },
  { value: 4, label: '4+' },
  { value: 4.5, label: '4.5+' },
];

@Component({
  selector: 'app-restaurants',
  templateUrl: './restaurants.component.html',
  styleUrl: './restaurants.component.scss',
  imports: [RouterModule, CommonModule, FormsModule],
})
export default class RestaurantsComponent implements OnInit, OnDestroy {
  cuisines = CUISINES;
  ratingOptions = RATINGS;

  selectedCuisine = signal('toate');
  selectedRating = signal(0);
  searchQuery = signal('');

  allRestaurants = signal<RestaurantCard[]>([]);
  isLoading = signal(true);

  filtered = computed(() => {
    const q = this.searchQuery().toLowerCase();
    const cus = this.selectedCuisine();
    const rat = this.selectedRating();

    return this.allRestaurants().filter(r => {
      const matchQ = !q || r.name.toLowerCase().includes(q) || r.cuisine.toLowerCase().includes(q);
      const matchC = cus === 'toate' || r.cuisineKey === cus;
      const matchR = r.rating >= rat;
      return matchQ && matchC && matchR;
    });
  });

  private readonly destroy$ = new Subject<void>();
  private readonly brandService = inject(BrandService);

  ngOnInit(): void {
    this.brandService
      .query({ size: 20, sort: ['id,asc'] })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: res => {
          if (res.body && res.body.length > 0) {
            const cards: RestaurantCard[] = res.body.map(b => {
              const meta = BRAND_META[b.name ?? ''];
              return {
                id: b.id ?? 0,
                name: b.name ?? 'Restaurant',
                cuisine: meta?.cuisine ?? 'Locală',
                cuisineKey: meta?.cuisineKey ?? 'locale',
                description: b.description ?? meta?.description ?? '',
                imageUrl:
                  (b.coverImageUrl?.startsWith('http') ? b.coverImageUrl : null) ??
                  meta?.imageUrl ??
                  'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=700&q=80',
                rating: meta?.rating ?? 4.5,
                address: meta?.address ?? 'Chișinău',
                city: 'Chișinău',
                isOpen: true,
                locationsCount: meta?.locationsCount ?? 1,
              };
            });
            this.allRestaurants.set(cards);
          }
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
        },
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSearch(value: string): void {
    this.searchQuery.set(value);
  }

  selectCuisine(key: string): void {
    this.selectedCuisine.set(key);
  }

  selectRating(value: number): void {
    this.selectedRating.set(value);
  }

  stars(rating: number): number[] {
    return Array.from({ length: Math.floor(rating) });
  }
}
