import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';

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

const MOCK_RESTAURANTS: RestaurantCard[] = [
  {
    id: 1,
    name: 'Sushi Master',
    cuisine: 'Japoneză',
    cuisineKey: 'japoneza',
    description: 'Cel mai fresh sushi din Chișinău. Bucătari japonezi, ingrediente premium și o experiență culinară de neuitat.',
    imageUrl: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=700&q=80',
    rating: 4.9,
    address: 'Str. Columna 45',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 1,
  },
  {
    id: 2,
    name: 'La Plăcinte',
    cuisine: 'Moldovenească',
    cuisineKey: 'moldoveneasca',
    description:
      'Bucătărie tradițională moldovenească cu rețete autentice transmise din generație în generație. Savurați cele mai bune plăcinte.',
    imageUrl: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=700&q=80',
    rating: 4.8,
    address: 'Str. București 67',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 2,
  },
  {
    id: 3,
    name: 'Steakhouse Premium',
    cuisine: 'Americană',
    cuisineKey: 'americana',
    description: 'Specialități din carne de vită maturată, preparată la grătar. Atmosferă elegantă și servicii impecabile.',
    imageUrl: 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=700&q=80',
    rating: 4.7,
    address: 'Bd. Dacia 12',
    city: 'Chișinău',
    isOpen: false,
    locationsCount: 1,
  },
  {
    id: 4,
    name: 'Ristorante Bella Italia',
    cuisine: 'Italiană',
    cuisineKey: 'italiana',
    description: 'Autentica bucătărie italiană preparată de bucătari experimentați. Paste proaspete, pizza pe vatră și vinuri selecte.',
    imageUrl: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=700&q=80',
    rating: 4.6,
    address: 'Bd. Ștefan cel Mare 123',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 1,
  },
  {
    id: 5,
    name: 'Garden Bistro',
    cuisine: 'Vegetariană',
    cuisineKey: 'vegetariana',
    description: 'Meniu vegetarian și vegan cu ingrediente bio locale. Terasă verde în centrul orașului.',
    imageUrl: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=700&q=80',
    rating: 4.5,
    address: 'Str. Puskin 24',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 1,
  },
];

const CUISINES = [
  { key: 'toate', label: 'Toate' },
  { key: 'japoneza', label: 'Japoneză' },
  { key: 'moldoveneasca', label: 'Moldovenească' },
  { key: 'americana', label: 'Americană' },
  { key: 'italiana', label: 'Italiană' },
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

  allRestaurants = signal<RestaurantCard[]>(MOCK_RESTAURANTS);
  isLoading = signal(false);

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

  ngOnInit(): void {}

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
