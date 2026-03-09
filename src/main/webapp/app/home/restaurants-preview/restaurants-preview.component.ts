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

const BRAND_META: Record<string, { cuisine: string; imageUrl: string; rating: number; address: string; hours: string }> = {
  'La Plăcinte': {
    cuisine: 'Moldovenească',
    imageUrl: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600&q=80',
    rating: 4.8,
    address: 'Bd. Ștefan cel Mare 6',
    hours: '10:00 – 23:00',
  },
  "Andy's Pizza": {
    cuisine: 'Italiană',
    imageUrl: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600&q=80',
    rating: 4.7,
    address: 'Str. Ismail 90',
    hours: '10:00 – 23:00',
  },
  'Sushi House': {
    cuisine: 'Japoneză',
    imageUrl: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=600&q=80',
    rating: 4.9,
    address: 'Str. Columna 170',
    hours: '12:00 – 23:00',
  },
  'Vatra Neamului': {
    cuisine: 'Moldovenească',
    imageUrl: 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=600&q=80',
    rating: 4.8,
    address: 'Str. Vasile Alecsandri 108',
    hours: '11:00 – 24:00',
  },
  'Green Hills': {
    cuisine: 'Vegetariană',
    imageUrl: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80',
    rating: 4.6,
    address: 'Str. București 67',
    hours: '08:00 – 22:00',
  },
};

const FALLBACK_SLOTS = [6, 4, 5, 3, 7];

@Component({
  selector: 'jhi-restaurants-preview',
  templateUrl: './restaurants-preview.component.html',
  styleUrl: './restaurants-preview.component.scss',
  imports: [RouterModule],
})
export class RestaurantsPreviewComponent implements OnInit {
  restaurants: PreviewRestaurant[] = [];

  private readonly brandService = inject(BrandService);

  ngOnInit(): void {
    this.brandService.query({ size: 3, sort: ['id,asc'] }).subscribe({
      next: res => {
        if (res.body && res.body.length > 0) {
          this.restaurants = res.body.map((b, i) => {
            const meta = BRAND_META[b.name ?? ''];
            return {
              id: b.id ?? i + 1,
              name: b.name ?? 'Restaurant',
              cuisine: meta?.cuisine ?? 'Locală',
              imageUrl:
                (b.coverImageUrl?.startsWith('http') ? b.coverImageUrl : null) ??
                meta?.imageUrl ??
                'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600&q=80',
              rating: meta?.rating ?? 4.5,
              address: meta?.address ?? 'Chișinău',
              hours: meta?.hours ?? '10:00 – 23:00',
              freeSlots: FALLBACK_SLOTS[i % FALLBACK_SLOTS.length],
            };
          });
        }
      },
      error: () => {
        this.restaurants = [];
      },
    });
  }
}
