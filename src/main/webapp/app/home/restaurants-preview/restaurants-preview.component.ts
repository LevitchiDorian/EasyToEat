import { Component, OnInit, inject } from '@angular/core';
import { RouterModule } from '@angular/router';

import { BrandService } from 'app/entities/brand/service/brand.service';

interface PreviewRestaurant {
  id: number;
  name: string;
  cuisine: string;
  imageUrl: string;
  rating: number;
  address: string;
  hours: string;
  freeSlots: number;
}

const MOCK_RESTAURANTS: PreviewRestaurant[] = [
  {
    id: 1,
    name: 'La Plăcinte',
    cuisine: 'Moldovenească',
    imageUrl: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600&q=80',
    rating: 4.8,
    address: 'Str. București 67',
    hours: '08:00 - 23:00',
    freeSlots: 5,
  },
  {
    id: 2,
    name: 'Ristorante Italiano',
    cuisine: 'Italiană',
    imageUrl: 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=600&q=80',
    rating: 4.6,
    address: 'Bd. Ștefan cel Mare 123',
    hours: '11:00 - 23:00',
    freeSlots: 3,
  },
  {
    id: 3,
    name: 'Sushi Master',
    cuisine: 'Japoneză',
    imageUrl: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=600&q=80',
    rating: 4.9,
    address: 'Str. Columna 45',
    hours: '12:00 - 22:00',
    freeSlots: 8,
  },
];

@Component({
  selector: 'jhi-restaurants-preview',
  templateUrl: './restaurants-preview.component.html',
  styleUrl: './restaurants-preview.component.scss',
  imports: [RouterModule],
})
export class RestaurantsPreviewComponent implements OnInit {
  restaurants: PreviewRestaurant[] = MOCK_RESTAURANTS;

  private readonly brandService = inject(BrandService);

  ngOnInit(): void {
    this.brandService.query({ size: 3, sort: ['id,desc'] }).subscribe({
      next: res => {
        if (res.body && res.body.length > 0) {
          this.restaurants = res.body.map((b, i) => ({
            id: b.id ?? i + 1,
            name: b.name ?? 'Restaurant',
            cuisine: 'Locală',
            imageUrl: b.coverImageUrl ?? MOCK_RESTAURANTS[i % MOCK_RESTAURANTS.length].imageUrl,
            rating: Math.round((4.5 + Math.random() * 0.5) * 10) / 10,
            address: 'Chișinău',
            hours: '10:00 - 23:00',
            freeSlots: Math.floor(Math.random() * 8) + 1,
          }));
        }
      },
      error: () => {
        this.restaurants = MOCK_RESTAURANTS;
      },
    });
  }
}
