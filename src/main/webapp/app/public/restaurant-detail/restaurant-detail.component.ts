import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { Subscription, forkJoin } from 'rxjs';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

import { FloorMapComponent, FloorPlan, FloorTable, FloorDecoration, DEMO_FLOOR_PLAN } from '../floor-map/floor-map.component';
import { BookingWizardComponent } from '../booking-wizard/booking-wizard.component';
import { RestaurantCard } from '../restaurants/restaurants.component';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AccountService } from 'app/core/auth/account.service';
import { FloorPlanWsService } from 'app/core/floor-plan-ws/floor-plan-ws.service';

interface MenuCategory {
  id: number;
  name: string;
  displayOrder?: number;
  isActive?: boolean;
  brand?: { id: number };
}

interface MenuItem {
  id: number;
  name: string;
  description?: string;
  price: number;
  prepTimeMinutes?: number;
  isVegetarian?: boolean;
  isVegan?: boolean;
  isGlutenFree?: boolean;
  spicyLevel?: number;
  isAvailable?: boolean;
  category?: { id: number };
}

interface LocationCard {
  id: number;
  name: string;
  address: string;
  city: string;
  phone: string;
  hours: string;
  isOpen: boolean;
}

interface FloorPlanResponse {
  locationId: number;
  locationName: string;
  locationAddress: string;
  date: string;
  rooms: {
    id: number;
    name: string;
    floor?: number;
    widthPx: number;
    heightPx: number;
    decorationsJson?: string;
    tables: {
      id: number;
      tableNumber: string;
      shape: string;
      minCapacity: number;
      maxCapacity: number;
      positionX: number;
      positionY: number;
      widthPx: number;
      heightPx: number;
      rotation: number;
      status: string;
      isActive: boolean;
      notes?: string;
    }[];
  }[];
}

const RESTAURANT_DATA: Record<number, RestaurantCard & { locations: LocationCard[]; fullDescription: string }> = {
  1: {
    id: 1,
    name: 'La Plăcinte',
    cuisine: 'Moldovenească',
    cuisineKey: 'moldoveneasca',
    description: 'Lanț de restaurante tradiționale moldovenești.',
    fullDescription:
      'La Plăcinte este un lanț de restaurante moldovenești autentic, cu o tradiție de peste 20 de ani. Rețetele transmise din generație în generație, ingredientele locale și ospitalitatea caldă îți garantează o experiență culinară de neuitat. Vino să savurezi cele mai bune plăcinte, zeamă de pui și mâncăruri tradiționale moldovenești.',
    imageUrl: 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=1200&q=80',
    rating: 4.8,
    address: 'Bd. Ștefan cel Mare 6',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 2,
    locations: [
      {
        id: 1,
        name: 'La Plăcinte — Centru',
        address: 'Bd. Ștefan cel Mare 6',
        city: 'Chișinău',
        phone: '+373 22 001 001',
        hours: '10:00 – 23:00',
        isOpen: true,
      },
      {
        id: 2,
        name: 'La Plăcinte — Botanica',
        address: 'Str. Titulescu 1/1',
        city: 'Chișinău',
        phone: '+373 22 001 002',
        hours: '10:00 – 23:00',
        isOpen: true,
      },
    ],
  },
  2: {
    id: 2,
    name: "Andy's Pizza",
    cuisine: 'Italiană',
    cuisineKey: 'italiana',
    description: 'Cel mai popular lanț de pizzerii din Moldova.',
    fullDescription:
      "Andy's Pizza este destinația nr. 1 pentru iubitorii de pizza în Moldova. Cu rețete italiene adaptate gustului local, ingrediente proaspete și un serviciu rapid, fiecare vizită este o plăcere. Încercați pizza Andy Special sau clasica Margherita — ambele sunt spectaculoase!",
    imageUrl: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=1200&q=80',
    rating: 4.7,
    address: 'Str. Ismail 90',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 2,
    locations: [
      {
        id: 3,
        name: "Andy's Pizza — Centru",
        address: 'Str. Ismail 90',
        city: 'Chișinău',
        phone: '+373 22 002 001',
        hours: '10:00 – 23:00',
        isOpen: true,
      },
      {
        id: 4,
        name: "Andy's Pizza — Rîșcani",
        address: 'Bd. Moscova 14',
        city: 'Chișinău',
        phone: '+373 22 002 002',
        hours: '10:00 – 23:00',
        isOpen: true,
      },
    ],
  },
  3: {
    id: 3,
    name: 'Sushi House',
    cuisine: 'Japoneză',
    cuisineKey: 'japoneza',
    description: 'Restaurant japonez premium cu sushi proaspăt.',
    fullDescription:
      'Sushi House este destinația perfectă pentru iubitorii de bucătărie japoneză autentică. Bucătarii noștri cu experiență internațională pregătesc zilnic ingrediente proaspete. Rolls-urile noastre sunt preparate la comandă, cu pește importat direct de la furnizori certificați. Atmosfera modernă și serviciul impecabil fac fiecare vizită memorabilă.',
    imageUrl: 'https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=1200&q=80',
    rating: 4.9,
    address: 'Str. Columna 170',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 1,
    locations: [
      {
        id: 5,
        name: 'Sushi House — Centru',
        address: 'Str. Columna 170',
        city: 'Chișinău',
        phone: '+373 22 003 001',
        hours: '12:00 – 23:00',
        isOpen: true,
      },
    ],
  },
  4: {
    id: 4,
    name: 'Vatra Neamului',
    cuisine: 'Moldovenească',
    cuisineKey: 'moldoveneasca',
    description: 'Restaurant tradițional moldovenesc cu specific național.',
    fullDescription:
      'Vatra Neamului este restaurantul care celebrează tradițiile și bucătăria autentică moldovenească. Cu muzică live în fiecare vineri și sâmbătă, sarmale tradiționale, mici la grătar și vinuri moldovenești selecte, restaurantul nostru este locul ideal pentru ocazii speciale și evenimente de familie.',
    imageUrl: 'https://images.unsplash.com/photo-1546833999-b9f581a1996d?w=1200&q=80',
    rating: 4.8,
    address: 'Str. Vasile Alecsandri 108',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 1,
    locations: [
      {
        id: 6,
        name: 'Vatra Neamului — Centru',
        address: 'Str. Vasile Alecsandri 108',
        city: 'Chișinău',
        phone: '+373 22 004 001',
        hours: '11:00 – 24:00',
        isOpen: true,
      },
    ],
  },
  5: {
    id: 5,
    name: 'Green Hills',
    cuisine: 'Vegetariană',
    cuisineKey: 'vegetariana',
    description: 'Café și restaurant sănătos cu mâncare bio.',
    fullDescription:
      'Green Hills este paradisul iubitorilor de mâncare sănătoasă din Chișinău. Ingrediente bio locale, rețete creative și sucuri naturale presate la rece fac din Green Hills alegerea perfectă pentru un stil de viață echilibrat. Buddha bowls, smoothie-uri energizante și mic dejun delicios vă așteaptă în fiecare zi.',
    imageUrl: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=1200&q=80',
    rating: 4.6,
    address: 'Str. București 67',
    city: 'Chișinău',
    isOpen: true,
    locationsCount: 2,
    locations: [
      {
        id: 7,
        name: 'Green Hills — Centru',
        address: 'Str. București 67',
        city: 'Chișinău',
        phone: '+373 22 005 001',
        hours: '08:00 – 22:00',
        isOpen: true,
      },
      {
        id: 8,
        name: 'Green Hills — Telecentru',
        address: 'Bd. Dacia 23',
        city: 'Chișinău',
        phone: '+373 22 005 002',
        hours: '08:00 – 22:00',
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
  imports: [RouterModule, CommonModule, FormsModule, FloorMapComponent, BookingWizardComponent],
})
export default class RestaurantDetailComponent implements OnInit, OnDestroy {
  restaurant = signal<(RestaurantCard & { locations: LocationCard[]; fullDescription: string }) | null>(null);
  selectedLocationId = signal<number | null>(null);
  activeTab = signal<DetailTab>('mese');
  selectedTable = signal<FloorTable | null>(null);

  isLoadingFloorPlan = signal(false);
  floorPlan = signal<FloorPlan | null>(null);
  rawFloorPlan = signal<FloorPlanResponse | null>(null);

  menuCategories = signal<MenuCategory[]>([]);
  menuItems = signal<MenuItem[]>([]);
  isLoadingMenu = signal(false);
  menuLoaded = signal(false);
  expandedCategoryId = signal<number | null>(null);
  menuSearch = '';
  menuSort: 'name' | 'price_asc' | 'price_desc' = 'name';

  showWizard = signal(false);

  today = new Date().toISOString().substring(0, 10);
  reservationDate = new Date().toISOString().substring(0, 10);

  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly accountService = inject(AccountService);
  private readonly floorPlanWs = inject(FloorPlanWsService);

  private wsSub?: Subscription;

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    const data = RESTAURANT_DATA[id] ?? RESTAURANT_DATA[1];
    this.restaurant.set(data);
    if (data.locations.length > 0) {
      this.selectedLocationId.set(data.locations[0].id);
      this.loadFloorPlan(data.locations[0].id);
    }
  }

  openBookingWizard(): void {
    if (!this.accountService.isAuthenticated()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url } });
      return;
    }
    this.showWizard.set(true);
  }

  selectLocation(id: number): void {
    this.selectedLocationId.set(id);
    this.selectedTable.set(null);
    this.loadFloorPlan(id);
  }

  loadFloorPlan(locationId: number): void {
    // Re-subscribe to correct location on every load (handles location switches)
    this.wsSub?.unsubscribe();
    this.wsSub = this.floorPlanWs.watchLocation(locationId).subscribe(() => this.loadFloorPlan(locationId));

    this.isLoadingFloorPlan.set(true);
    this.floorPlan.set(null);
    const url = this.configService.getEndpointFor(`api/public/floor-plan/${locationId}?date=${this.reservationDate}`);
    this.http.get<FloorPlanResponse>(url).subscribe({
      next: res => {
        this.rawFloorPlan.set(res);
        const mapped = this.mapToFloorPlan(res);
        this.floorPlan.set(mapped.rooms.length > 0 ? mapped : DEMO_FLOOR_PLAN);
        this.isLoadingFloorPlan.set(false);
      },
      error: () => {
        this.floorPlan.set(DEMO_FLOOR_PLAN);
        this.isLoadingFloorPlan.set(false);
      },
    });
  }

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
  }

  onDateChange(): void {
    const locId = this.selectedLocationId();
    if (locId) this.loadFloorPlan(locId);
  }

  setTab(tab: DetailTab): void {
    this.activeTab.set(tab);
    if (tab === 'meniu' && !this.menuLoaded()) {
      const brandId = this.restaurant()?.id;
      if (brandId) this.loadMenu(brandId);
    }
  }

  loadMenu(brandId: number): void {
    this.isLoadingMenu.set(true);
    const catUrl = this.configService.getEndpointFor('api/menu-categories?size=200');
    const itemUrl = this.configService.getEndpointFor('api/menu-items?size=500');
    forkJoin({
      cats: this.http.get<MenuCategory[]>(catUrl),
      items: this.http.get<MenuItem[]>(itemUrl),
    }).subscribe({
      next: ({ cats, items }) => {
        const filtered = cats.filter(c => c.brand?.id === brandId && c.isActive !== false);
        filtered.sort((a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0));
        this.menuCategories.set(filtered);
        if (filtered.length > 0) this.expandedCategoryId.set(filtered[0].id);
        const catIds = new Set(filtered.map(c => c.id));
        this.menuItems.set(items.filter(i => i.category?.id !== undefined && catIds.has(i.category.id) && i.isAvailable !== false));
        this.isLoadingMenu.set(false);
        this.menuLoaded.set(true);
      },
      error: () => {
        this.isLoadingMenu.set(false);
        this.menuLoaded.set(true);
      },
    });
  }

  itemsForCategory(categoryId: number): MenuItem[] {
    const q = this.menuSearch.toLowerCase().trim();
    let items = this.menuItems().filter(i => i.category?.id === categoryId);
    if (q) items = items.filter(i => i.name.toLowerCase().includes(q) || (i.description ?? '').toLowerCase().includes(q));
    if (this.menuSort === 'price_asc') items = [...items].sort((a, b) => a.price - b.price);
    else if (this.menuSort === 'price_desc') items = [...items].sort((a, b) => b.price - a.price);
    else items = [...items].sort((a, b) => a.name.localeCompare(b.name));
    return items;
  }

  visibleCategories(): MenuCategory[] {
    const q = this.menuSearch.toLowerCase().trim();
    if (!q) return this.menuCategories();
    return this.menuCategories().filter(c => this.itemsForCategory(c.id).length > 0 || c.name.toLowerCase().includes(q));
  }

  getCategoryImage(categoryName: string): string {
    const name = categoryName.toLowerCase();
    const MAP: [string, string][] = [
      ['plăcinte', 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=600&q=80'],
      ['supe', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=600&q=80'],
      ['pizza', 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=600&q=80'],
      ['paste', 'https://images.unsplash.com/photo-1598866594240-2d2b7a40db02?w=600&q=80'],
      ['sushi', 'https://images.unsplash.com/photo-1617196034183-421b4040ed20?w=600&q=80'],
      ['ramen', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=600&q=80'],
      ['salat', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=600&q=80'],
      ['desert', 'https://images.unsplash.com/photo-1559181567-c3190438e2f8?w=600&q=80'],
      ['smoothie', 'https://images.unsplash.com/photo-1505252585461-04db1eb84625?w=600&q=80'],
      ['cafea', 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=600&q=80'],
      ['grătar', 'https://images.unsplash.com/photo-1544025162-d76694265947?w=600&q=80'],
      ['carne', 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&q=80'],
      ['asiatic', 'https://images.unsplash.com/photo-1555993539-1732b0258235?w=600&q=80'],
      ['băuturi', 'https://images.unsplash.com/photo-1544145945-f90425340c7e?w=600&q=80'],
    ];
    for (const [key, url] of MAP) {
      if (name.includes(key)) return url;
    }
    return 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600&q=80';
  }

  formatPrice(price: number): string {
    return `${price.toFixed(0)} MDL`;
  }

  toggleCategory(id: number): void {
    this.expandedCategoryId.set(this.expandedCategoryId() === id ? null : id);
  }

  onTableSelected(table: FloorTable): void {
    if (table.status === 'RESERVED' || table.status === 'OCCUPIED') return;
    this.selectedTable.set(table);
  }

  onWizardClosed(): void {
    this.showWizard.set(false);
  }

  onWizardDone(): void {
    const locId = this.selectedLocationId();
    if (locId) this.loadFloorPlan(locId);
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

  private mapToFloorPlan(res: FloorPlanResponse): FloorPlan {
    let canvasWidth = 800;
    let currentY = 20;
    const GAP = 24;

    const rooms = res.rooms.map(room => {
      const w = room.widthPx ?? 700;
      const h = room.heightPx ?? 400;
      const posX = 20;
      const posY = currentY;
      currentY += h + GAP;
      canvasWidth = Math.max(canvasWidth, posX + w + 20);

      let decorations: FloorDecoration[] = [];
      if (room.decorationsJson) {
        try {
          decorations = JSON.parse(room.decorationsJson) ?? [];
        } catch {
          decorations = [];
        }
      }

      return {
        id: room.id,
        name: room.name,
        posX,
        posY,
        width: w,
        height: h,
        decorations,
        tables: room.tables.map(t => ({
          id: t.id,
          tableNumber: t.tableNumber,
          shape: (t.shape as 'ROUND' | 'SQUARE' | 'RECTANGLE') ?? 'RECTANGLE',
          minCapacity: t.minCapacity ?? 1,
          maxCapacity: t.maxCapacity ?? 4,
          positionX: t.positionX ?? 30,
          positionY: t.positionY ?? 30,
          widthPx: t.widthPx ?? 80,
          heightPx: t.heightPx ?? 80,
          rotation: t.rotation ?? 0,
          status: (t.status as 'AVAILABLE' | 'RESERVED' | 'OCCUPIED' | 'OUT_OF_SERVICE') ?? 'AVAILABLE',
          isActive: t.isActive ?? true,
          notes: t.notes,
        })),
      };
    });

    const canvasHeight = Math.max(600, currentY + 20);
    return { canvasWidth, canvasHeight, rooms };
  }
}
