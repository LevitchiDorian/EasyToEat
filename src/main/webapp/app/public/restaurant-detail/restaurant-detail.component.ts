import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { FloorMapComponent, DEMO_FLOOR_PLAN, FloorTable } from '../floor-map/floor-map.component';
import { RestaurantCard } from '../restaurants/restaurants.component';

interface LocationCard {
  id: number;
  name: string;
  address: string;
  city: string;
  phone: string;
  hours: string;
  isOpen: boolean;
  latitude?: number;
  longitude?: number;
}

const MOCK_DETAILS: Record<number, RestaurantCard & { locations: LocationCard[]; fullDescription: string }> = {
  1: {
    id: 1,
    name: 'Sushi Master',
    cuisine: 'Japoneză',
    cuisineKey: 'japoneza',
    description: 'Cel mai fresh sushi din Chișinău.',
    fullDescription:
      'Sushi Master este destinația perfectă pentru iubitorii de bucătărie japoneză autentică. Bucătarii noștri cu experiență internațională pregătesc zilnic ingrediente proaspete importate direct din Japonia. Atmosfera modernă și serviciul impecabil fac fiecare vizită memorabilă.',
    imageUrl: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=1200&q=80',
    rating: 4.9,
    address: 'Str. Columna 45',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 1,
    locations: [
      {
        id: 1,
        name: 'Sushi Master — Centru',
        address: 'Str. Columna 45',
        city: 'Chișinău',
        phone: '+373 22 123 456',
        hours: '12:00 – 22:00',
        isOpen: true,
        latitude: 47.0245,
        longitude: 28.8324,
      },
    ],
  },
  2: {
    id: 2,
    name: 'La Plăcinte',
    cuisine: 'Moldovenească',
    cuisineKey: 'moldoveneasca',
    description: 'Bucătărie tradițională moldovenească.',
    fullDescription:
      'La Plăcinte este un lanț de restaurante moldovenești autentic, cu o tradiție de peste 20 de ani. Rețetele transmise din generație în generație, ingredientele locale și ospitalitatea caldă îți garantează o experiență culinară de neuitat.',
    imageUrl: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=1200&q=80',
    rating: 4.8,
    address: 'Str. București 67',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 2,
    locations: [
      {
        id: 2,
        name: 'La Plăcinte — București',
        address: 'Str. București 67',
        city: 'Chișinău',
        phone: '+373 22 234 567',
        hours: '08:00 – 23:00',
        isOpen: true,
      },
      {
        id: 3,
        name: 'La Plăcinte — Botanica',
        address: 'Str. Sarmizegetusa 12',
        city: 'Chișinău',
        phone: '+373 22 345 678',
        hours: '08:00 – 23:00',
        isOpen: true,
      },
    ],
  },
  3: {
    id: 3,
    name: 'Steakhouse Premium',
    cuisine: 'Americană',
    cuisineKey: 'americana',
    description: 'Specialități din carne de vită maturată.',
    fullDescription:
      'Steakhouse Premium oferă cea mai bună carne de vită maturată din Moldova. Cu un interior elegant și un serviciu de top, restaurantul nostru este locul ideal pentru ocazii speciale și business dinners.',
    imageUrl: 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=1200&q=80',
    rating: 4.7,
    address: 'Bd. Dacia 12',
    city: 'Chișinău',
    isOpen: false,
    locationsCount: 1,
    locations: [
      {
        id: 4,
        name: 'Steakhouse Premium — Centru',
        address: 'Bd. Dacia 12',
        city: 'Chișinău',
        phone: '+373 22 456 789',
        hours: '13:00 – 24:00',
        isOpen: false,
      },
    ],
  },
  4: {
    id: 4,
    name: 'Ristorante Bella Italia',
    cuisine: 'Italiană',
    cuisineKey: 'italiana',
    description: 'Autentica bucătărie italiană.',
    fullDescription:
      'Ristorante Bella Italia aduce un strop de Italia în Chișinău. Paste proaspete, pizza pe vatră de lemn și un vin selectat cu grijă din cele mai renumite podgorii italiene. Chef-ul nostru, cu 15 ani de experiență în Italia, garantează autenticitatea fiecărui fel.',
    imageUrl: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=1200&q=80',
    rating: 4.6,
    address: 'Bd. Ștefan cel Mare 123',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 1,
    locations: [
      {
        id: 5,
        name: 'Ristorante Bella Italia — Centru',
        address: 'Bd. Ștefan cel Mare 123',
        city: 'Chișinău',
        phone: '+373 22 567 890',
        hours: '11:00 – 23:00',
        isOpen: true,
      },
    ],
  },
  5: {
    id: 5,
    name: 'Garden Bistro',
    cuisine: 'Vegetariană',
    cuisineKey: 'vegetariana',
    description: 'Meniu vegetarian și vegan cu ingrediente bio.',
    fullDescription:
      'Garden Bistro este paradisul iubitorilor de mâncare sănătoasă. Ingrediente bio locale, rețete creative și o terasă verde în inima orașului. Alegem sustenabilitatea în fiecare detaliu — de la ambalaje la aprovizionare.',
    imageUrl: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=1200&q=80',
    rating: 4.5,
    address: 'Str. Puskin 24',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 1,
    locations: [
      {
        id: 6,
        name: 'Garden Bistro — Centru',
        address: 'Str. Puskin 24',
        city: 'Chișinău',
        phone: '+373 22 678 901',
        hours: '09:00 – 21:00',
        isOpen: true,
      },
    ],
  },
};

type DetailTab = 'mese' | 'meniu' | 'recenzii';

@Component({
  selector: 'app-restaurant-detail',
  templateUrl: './restaurant-detail.component.html',
  styleUrl: './restaurant-detail.component.scss',
  imports: [RouterModule, CommonModule, FormsModule, FloorMapComponent],
})
export default class RestaurantDetailComponent implements OnInit {
  restaurant = signal<(RestaurantCard & { locations: LocationCard[]; fullDescription: string }) | null>(null);
  selectedLocationId = signal<number | null>(null);
  activeTab = signal<DetailTab>('mese');
  selectedTable = signal<FloorTable | null>(null);

  reservationDate = '';
  partySize = 2;
  orderType: 'masa' | 'livrare' | 'pachet' = 'masa';

  floorPlan = DEMO_FLOOR_PLAN;

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    const data = MOCK_DETAILS[id] ?? MOCK_DETAILS[1];
    this.restaurant.set(data);
    if (data.locations.length > 0) {
      this.selectedLocationId.set(data.locations[0].id);
    }
  }

  selectLocation(id: number): void {
    this.selectedLocationId.set(id);
    this.selectedTable.set(null);
  }

  setTab(tab: DetailTab): void {
    this.activeTab.set(tab);
  }

  onTableSelected(table: FloorTable): void {
    this.selectedTable.set(table);
  }

  confirmReservation(): void {
    const r = this.restaurant();
    if (!r || !this.selectedTable()) return;
    alert(
      `Rezervare confirmată!\nRestaurant: ${r.name}\nMasa: ${this.selectedTable()!.tableNumber}\nData: ${this.reservationDate}\nPersoane: ${this.partySize}`,
    );
  }

  goBack(): void {
    this.router.navigate(['/restaurante']);
  }

  stars(rating: number): number[] {
    return Array.from({ length: Math.round(rating) });
  }

  get selectedLocation(): LocationCard | undefined {
    return this.restaurant()?.locations.find(l => l.id === this.selectedLocationId());
  }

  openGoogleMaps(loc: LocationCard): void {
    const query = encodeURIComponent(`${loc.address}, ${loc.city}`);
    window.open(`https://www.google.com/maps/search/${query}`, '_blank');
  }
}
