import { Component, Input, Output, EventEmitter, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

import { FloorMapComponent, FloorPlan, FloorTable } from '../floor-map/floor-map.component';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { RestaurantCard } from '../restaurants/restaurants.component';

export type WizardStep = 'CHOICE' | 'RESERVATION_TYPE' | 'FLOOR_MAP' | 'FORM' | 'MENU' | 'SUMMARY' | 'SUCCESS';
export type BookingType = 'RESERVATION' | 'DELIVERY' | 'TAKEAWAY';
export type ReservationType = 'TABLE' | 'TABLE_MENU' | 'BANQUET';

export interface CartItem {
  item: WizardMenuItem;
  quantity: number;
}

interface WizardMenuItem {
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

interface WizardMenuCategory {
  id: number;
  name: string;
  displayOrder?: number;
  isActive?: boolean;
  brand?: { id: number };
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

@Component({
  selector: 'app-booking-wizard',
  standalone: true,
  templateUrl: './booking-wizard.component.html',
  styleUrl: './booking-wizard.component.scss',
  imports: [CommonModule, FormsModule, FloorMapComponent],
})
export class BookingWizardComponent implements OnInit {
  @Input() restaurant!: RestaurantCard & {
    locations: { id: number; name: string; address: string; city: string; phone: string; hours: string; isOpen: boolean }[];
    fullDescription: string;
  };
  @Input() locationId!: number;
  @Input() brandId!: number;
  @Output() closed = new EventEmitter<void>();
  @Output() reservationDone = new EventEmitter<void>();

  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);

  // Wizard state
  step = signal<WizardStep>('CHOICE');
  bookingType = signal<BookingType | null>(null);
  reservationType = signal<ReservationType | null>(null);

  // Floor plan
  floorPlan = signal<FloorPlan | null>(null);
  isLoadingFloorPlan = signal(false);
  selectedTable = signal<FloorTable | null>(null);
  selectedRoomId = signal<number | null>(null);
  selectedRoomName = signal<string>('');

  // Menu
  menuCategories = signal<WizardMenuCategory[]>([]);
  menuItems = signal<WizardMenuItem[]>([]);
  isLoadingMenu = signal(false);
  menuLoaded = signal(false);
  expandedCategoryId = signal<number | null>(null);
  cart = signal<CartItem[]>([]);
  menuSearch = '';

  // Form fields (pre-filled from account)
  firstName = '';
  lastName = '';
  email = '';
  phone = '';
  reservationDate = new Date().toISOString().substring(0, 10);
  startTime = '19:00';
  endTime = '20:30';
  partySize: any = 2;
  address = '';
  scheduledDate = new Date().toISOString().substring(0, 10);
  scheduledTime = '12:00';
  eventType = 'aniversare';
  specialRequests = '';

  // Hours status
  isCheckingHours = signal(false);
  locationIsOpen = signal<boolean | null>(null);
  locationOpenTime = signal<string | null>(null);
  locationCloseTime = signal<string | null>(null);

  // Submit state
  isSubmitting = signal(false);
  confirmationCode = signal<string | null>(null);
  error = signal<string | null>(null);

  today = new Date().toISOString().substring(0, 10);
  accountId: number | null = null;

  // Touched tracking (mark on blur)
  touched: Record<string, boolean> = {};

  // Calendar state
  calOpen = false;
  calTarget: 'reservation' | 'scheduled' = 'reservation';
  calYear = new Date().getFullYear();
  calMonth = new Date().getMonth();

  // Party size validation
  partySizeError = '';
  tableConflictError = '';

  ngOnInit(): void {
    this.loadAccount();
    this.checkLocationHours();
  }

  private checkLocationHours(): void {
    this.isCheckingHours.set(true);
    this.http
      .get<{
        isOpen: boolean;
        openTime: string | null;
        closeTime: string | null;
      }>(this.configService.getEndpointFor(`api/public/locations/${this.locationId}/hours-status`))
      .subscribe({
        next: res => {
          this.locationIsOpen.set(res.isOpen);
          this.locationOpenTime.set(res.openTime);
          this.locationCloseTime.set(res.closeTime);
          this.isCheckingHours.set(false);
        },
        error: () => {
          this.locationIsOpen.set(true); // assume open on error
          this.isCheckingHours.set(false);
        },
      });
  }

  private loadAccount(): void {
    this.http
      .get<{ id: number; email: string; firstName: string; lastName: string }>(this.configService.getEndpointFor('api/account'))
      .subscribe({
        next: acc => {
          this.accountId = acc.id;
          this.email = acc.email ?? '';
          this.firstName = acc.firstName ?? '';
          this.lastName = acc.lastName ?? '';
        },
      });
  }

  // ─── Calendar ─────────────────────────────────────────────────────

  openCal(target: 'reservation' | 'scheduled'): void {
    this.calTarget = target;
    const dateStr = target === 'reservation' ? this.reservationDate : this.scheduledDate;
    if (dateStr) {
      const [y, m] = dateStr.split('-').map(Number);
      this.calYear = y;
      this.calMonth = m - 1;
    }
    this.calOpen = !this.calOpen;
  }

  closeCal(): void {
    this.calOpen = false;
  }

  calPrev(): void {
    if (this.calMonth === 0) {
      this.calMonth = 11;
      this.calYear--;
    } else this.calMonth--;
  }

  calNext(): void {
    if (this.calMonth === 11) {
      this.calMonth = 0;
      this.calYear++;
    } else this.calMonth++;
  }

  calMonthLabel(): string {
    const months = [
      'Ianuarie',
      'Februarie',
      'Martie',
      'Aprilie',
      'Mai',
      'Iunie',
      'Iulie',
      'August',
      'Septembrie',
      'Octombrie',
      'Noiembrie',
      'Decembrie',
    ];
    return `${months[this.calMonth]} ${this.calYear}`;
  }

  calDays(): { day: number | null; disabled: boolean; today: boolean; selected: boolean }[] {
    const firstDay = new Date(this.calYear, this.calMonth, 1).getDay();
    const startPad = (firstDay + 6) % 7; // Monday-first
    const daysInMonth = new Date(this.calYear, this.calMonth + 1, 0).getDate();
    const todayStr = new Date().toISOString().substring(0, 10);
    const selectedStr = this.calTarget === 'reservation' ? this.reservationDate : this.scheduledDate;
    const result: { day: number | null; disabled: boolean; today: boolean; selected: boolean }[] = [];
    for (let i = 0; i < startPad; i++) result.push({ day: null, disabled: true, today: false, selected: false });
    for (let d = 1; d <= daysInMonth; d++) {
      const mm = String(this.calMonth + 1).padStart(2, '0');
      const dd = String(d).padStart(2, '0');
      const str = `${this.calYear}-${mm}-${dd}`;
      result.push({ day: d, disabled: str < todayStr, today: str === todayStr, selected: str === selectedStr });
    }
    return result;
  }

  calSelect(day: number | null): void {
    if (!day) return;
    const mm = String(this.calMonth + 1).padStart(2, '0');
    const dd = String(day).padStart(2, '0');
    const str = `${this.calYear}-${mm}-${dd}`;
    if (str < new Date().toISOString().substring(0, 10)) return;
    if (this.calTarget === 'reservation') {
      this.reservationDate = str;
      if (this.step() === 'FLOOR_MAP') this.reloadFloorPlan();
    } else {
      this.scheduledDate = str;
    }
    this.calOpen = false;
    this.touched['reservationDate'] = true;
    this.touched['scheduledDate'] = true;
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return 'Selectează data';
    const [y, m, d] = dateStr.split('-').map(Number);
    const months = ['Ian', 'Feb', 'Mar', 'Apr', 'Mai', 'Iun', 'Iul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return `${d} ${months[m - 1]} ${y}`;
  }

  validatePartySize(): void {
    const val = Number(this.partySize);
    const table = this.selectedTable();
    const rt = this.reservationType();
    if (!val || val < 1) {
      this.partySizeError = 'Introduceți numărul de persoane.';
    } else if (rt !== 'BANQUET' && table && val > table.maxCapacity) {
      this.partySizeError = `Capacitate maximă a mesei: ${table.maxCapacity} persoane.`;
    } else if (rt !== 'BANQUET' && table && val < table.minCapacity) {
      this.partySizeError = `Capacitate minimă a mesei: ${table.minCapacity} persoane.`;
    } else {
      this.partySizeError = '';
    }
  }

  // ─── Navigation ───────────────────────────────────────────────────

  chooseBookingType(type: BookingType): void {
    this.bookingType.set(type);
    if (type === 'RESERVATION') {
      this.step.set('RESERVATION_TYPE');
    } else {
      this.loadMenu();
      this.step.set('MENU');
    }
  }

  chooseReservationType(type: ReservationType): void {
    this.reservationType.set(type);
    if (type === 'BANQUET') {
      this.step.set('FORM');
    } else {
      this.loadFloorPlan();
      this.step.set('FLOOR_MAP');
    }
  }

  next(): void {
    const s = this.step();
    const bt = this.bookingType();
    const rt = this.reservationType();

    if (s === 'FLOOR_MAP') {
      this.step.set('FORM');
    } else if (s === 'FORM') {
      if (rt === 'TABLE_MENU') {
        this.loadMenu();
        this.step.set('MENU');
      } else {
        this.step.set('SUMMARY');
      }
    } else if (s === 'MENU') {
      if (bt === 'RESERVATION') {
        this.step.set('SUMMARY');
      } else {
        // DELIVERY / TAKEAWAY: MENU → FORM
        this.step.set('FORM');
      }
    } else if (s === 'SUMMARY') {
      this.submit();
    }
  }

  back(): void {
    const s = this.step();
    const bt = this.bookingType();
    const rt = this.reservationType();

    if (s === 'RESERVATION_TYPE') {
      this.step.set('CHOICE');
    } else if (s === 'FLOOR_MAP') {
      this.step.set('RESERVATION_TYPE');
    } else if (s === 'FORM') {
      if (rt === 'BANQUET') {
        this.step.set('RESERVATION_TYPE');
      } else if (rt === 'TABLE' || rt === 'TABLE_MENU') {
        this.step.set('FLOOR_MAP');
      } else {
        // DELIVERY / TAKEAWAY
        this.step.set('MENU');
      }
    } else if (s === 'MENU') {
      if (bt === 'RESERVATION') {
        this.step.set('FORM');
      } else {
        this.step.set('CHOICE');
      }
    } else if (s === 'SUMMARY') {
      if (rt === 'TABLE_MENU') {
        this.step.set('MENU');
      } else if (bt === 'DELIVERY' || bt === 'TAKEAWAY') {
        this.step.set('FORM');
      } else {
        this.step.set('FORM');
      }
    }
  }

  close(): void {
    this.closed.emit();
  }

  progressWidth(): string {
    const steps: WizardStep[] = ['CHOICE', 'RESERVATION_TYPE', 'FLOOR_MAP', 'FORM', 'MENU', 'SUMMARY', 'SUCCESS'];
    const idx = steps.indexOf(this.step());
    return `${Math.round(((idx + 1) / steps.length) * 100)}%`;
  }

  // ─── Floor plan ───────────────────────────────────────────────────

  loadFloorPlan(): void {
    this.isLoadingFloorPlan.set(true);
    this.floorPlan.set(null);
    this.selectedTable.set(null);
    const url = this.configService.getEndpointFor(`api/public/floor-plan/${this.locationId}?date=${this.reservationDate}`);
    this.http.get<FloorPlanResponse>(url).subscribe({
      next: res => {
        this.floorPlan.set(this.mapToFloorPlan(res));
        this.isLoadingFloorPlan.set(false);
      },
      error: () => {
        this.isLoadingFloorPlan.set(false);
      },
    });
  }

  reloadFloorPlan(): void {
    if (this.step() === 'FLOOR_MAP') this.loadFloorPlan();
  }

  onTableSelected(table: FloorTable): void {
    if (table.status === 'RESERVED' || table.status === 'OCCUPIED' || table.status === 'OUT_OF_SERVICE') return;
    this.selectedTable.set(table);
    this.tableConflictError = '';
    const fp = this.floorPlan();
    if (fp) {
      for (const room of fp.rooms) {
        if (room.tables.some(t => t.id === table.id)) {
          this.selectedRoomId.set(room.id);
          this.selectedRoomName.set(room.name);
          break;
        }
      }
    }
  }

  private mapToFloorPlan(res: FloorPlanResponse): FloorPlan {
    let canvasWidth = 800;
    let currentY = 20;
    const GAP = 24;
    const rooms = res.rooms.map(room => {
      const w = room.widthPx ?? 700;
      const h = room.heightPx ?? 400;
      const posY = currentY;
      currentY += h + GAP;
      canvasWidth = Math.max(canvasWidth, 20 + w + 20);
      return {
        id: room.id,
        name: room.name,
        posX: 20,
        posY,
        width: w,
        height: h,
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
    return { canvasWidth, canvasHeight: Math.max(600, currentY + 20), rooms };
  }

  // ─── Menu & Cart ──────────────────────────────────────────────────

  loadMenu(): void {
    if (this.menuLoaded()) return;
    this.isLoadingMenu.set(true);
    const catUrl = this.configService.getEndpointFor('api/menu-categories?size=200');
    const itemUrl = this.configService.getEndpointFor('api/menu-items?size=500');
    this.http.get<WizardMenuCategory[]>(catUrl).subscribe({
      next: cats => {
        const filtered = cats.filter(c => c.brand?.id === this.brandId && c.isActive !== false);
        filtered.sort((a, b) => (a.displayOrder ?? 0) - (b.displayOrder ?? 0));
        this.menuCategories.set(filtered);
        if (filtered.length > 0) this.expandedCategoryId.set(filtered[0].id);
        this.http.get<WizardMenuItem[]>(itemUrl).subscribe({
          next: items => {
            const catIds = new Set(filtered.map(c => c.id));
            this.menuItems.set(items.filter(i => i.category?.id !== undefined && catIds.has(i.category!.id) && i.isAvailable !== false));
            this.isLoadingMenu.set(false);
            this.menuLoaded.set(true);
          },
          error: () => {
            this.isLoadingMenu.set(false);
            this.menuLoaded.set(true);
          },
        });
      },
      error: () => {
        this.isLoadingMenu.set(false);
        this.menuLoaded.set(true);
      },
    });
  }

  itemsForCategory(categoryId: number): WizardMenuItem[] {
    const q = this.menuSearch.toLowerCase().trim();
    let items = this.menuItems().filter(i => i.category?.id === categoryId);
    if (q) items = items.filter(i => i.name.toLowerCase().includes(q) || (i.description ?? '').toLowerCase().includes(q));
    return items;
  }

  toggleCategory(id: number): void {
    this.expandedCategoryId.set(this.expandedCategoryId() === id ? null : id);
  }

  addToCart(item: WizardMenuItem): void {
    const current = this.cart();
    const idx = current.findIndex(ci => ci.item.id === item.id);
    if (idx >= 0) {
      const updated = [...current];
      updated[idx] = { ...updated[idx], quantity: updated[idx].quantity + 1 };
      this.cart.set(updated);
    } else {
      this.cart.set([...current, { item, quantity: 1 }]);
    }
  }

  removeFromCart(item: WizardMenuItem): void {
    const current = this.cart();
    const idx = current.findIndex(ci => ci.item.id === item.id);
    if (idx >= 0) {
      if (current[idx].quantity > 1) {
        const updated = [...current];
        updated[idx] = { ...updated[idx], quantity: updated[idx].quantity - 1 };
        this.cart.set(updated);
      } else {
        this.cart.set(current.filter(ci => ci.item.id !== item.id));
      }
    }
  }

  getQuantity(itemId: number): number {
    return this.cart().find(ci => ci.item.id === itemId)?.quantity ?? 0;
  }

  cartTotal(): number {
    return this.cart().reduce((sum, ci) => sum + ci.item.price * ci.quantity, 0);
  }

  cartItemCount(): number {
    return this.cart().reduce((sum, ci) => sum + ci.quantity, 0);
  }

  formatPrice(price: number): string {
    return `${price.toFixed(0)} MDL`;
  }

  getCategoryImage(categoryName: string): string {
    const name = categoryName.toLowerCase();
    const MAP: [string, string][] = [
      ['plăcinte', 'https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=400&q=70'],
      ['supe', 'https://images.unsplash.com/photo-1547592166-23ac45744acd?w=400&q=70'],
      ['pizza', 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=400&q=70'],
      ['sushi', 'https://images.unsplash.com/photo-1617196034183-421b4040ed20?w=400&q=70'],
      ['ramen', 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400&q=70'],
      ['salat', 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=400&q=70'],
      ['desert', 'https://images.unsplash.com/photo-1559181567-c3190438e2f8?w=400&q=70'],
      ['cafea', 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&q=70'],
      ['carne', 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&q=70'],
    ];
    for (const [key, url] of MAP) {
      if (name.includes(key)) return url;
    }
    return 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=400&q=70';
  }

  // ─── Form helpers ─────────────────────────────────────────────────

  get isFormValid(): boolean {
    const base = !!(this.firstName.trim() && this.lastName.trim() && this.email.trim() && this.phone.trim());
    if (!base) return false;
    if (this.partySizeError) return false;
    const bt = this.bookingType();
    if (bt === 'DELIVERY') return !!(this.scheduledDate && this.address.trim());
    if (bt === 'TAKEAWAY') return !!this.scheduledDate;
    return !!this.reservationDate;
  }

  get isFloorMapNextEnabled(): boolean {
    return this.selectedTable() !== null;
  }

  get bookingTypeLabel(): string {
    const bt = this.bookingType();
    if (bt === 'DELIVERY') return 'Livrare la domiciliu';
    if (bt === 'TAKEAWAY') return 'Ridicare la pachet';
    const rt = this.reservationType();
    if (rt === 'TABLE') return 'Rezervare masă';
    if (rt === 'TABLE_MENU') return 'Rezervare masă + pre-comandă';
    if (rt === 'BANQUET') return 'Banchet / Eveniment';
    return 'Rezervare';
  }

  get locationName(): string {
    return this.restaurant.locations.find(l => l.id === this.locationId)?.name ?? this.restaurant.name;
  }

  get locationAddress(): string {
    const loc = this.restaurant.locations.find(l => l.id === this.locationId);
    return loc ? `${loc.address}, ${loc.city}` : '';
  }

  // ─── Submit ───────────────────────────────────────────────────────

  private addMinutes(time: string, minutes: number): string {
    const [h, m] = time.split(':').map(Number);
    const total = h * 60 + m + minutes;
    const hh = String(Math.floor(total / 60) % 24).padStart(2, '0');
    const mm = String(total % 60).padStart(2, '0');
    return `${hh}:${mm}`;
  }

  private generateCode(prefix: string): string {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let code = prefix + '-';
    for (let i = 0; i < 6; i++) {
      code += chars[Math.floor(Math.random() * chars.length)];
    }
    return code;
  }

  async submit(): Promise<void> {
    if (!this.accountId) {
      this.error.set('Trebuie să fiți autentificat pentru a continua.');
      return;
    }
    this.isSubmitting.set(true);
    this.error.set(null);

    try {
      const bt = this.bookingType();
      const rt = this.reservationType();

      if (bt === 'RESERVATION') {
        // Double-booking check: reload floor plan and verify table is still AVAILABLE
        const tableId = this.selectedTable()?.id;
        if (tableId) {
          const freshFp = await firstValueFrom(
            this.http.get<FloorPlanResponse>(
              this.configService.getEndpointFor(`api/public/floor-plan/${this.locationId}?date=${this.reservationDate}`),
            ),
          );
          let tableStillAvailable = false;
          for (const room of freshFp.rooms) {
            const t = room.tables.find(t => t.id === tableId);
            if (t) {
              tableStillAvailable = t.status === 'AVAILABLE';
              break;
            }
          }
          if (!tableStillAvailable) {
            this.floorPlan.set(this.mapToFloorPlan(freshFp));
            this.selectedTable.set(null);
            this.isSubmitting.set(false);
            this.step.set('FLOOR_MAP');
            this.tableConflictError = 'Masa selectată a fost rezervată de altcineva. Te rog alege altă masă disponibilă.';
            return;
          }
        }

        const resCode = this.generateCode('RES');
        const autoEndTime = this.addMinutes(this.startTime, 90);
        const resBody: Record<string, unknown> = {
          reservationCode: resCode,
          reservationDate: this.reservationDate,
          startTime: this.startTime,
          endTime: autoEndTime,
          partySize: Number(this.partySize),
          status: 'PENDING',
          createdAt: new Date().toISOString(),
          specialRequests: this.buildSpecialRequests(),
          location: { id: this.locationId },
          room: this.selectedRoomId() ? { id: this.selectedRoomId() } : null,
          client: { id: this.accountId },
        };

        const reservation = await firstValueFrom(
          this.http.post<{ id: number; reservationCode: string }>(this.configService.getEndpointFor('api/reservations'), resBody),
        );

        // Link the selected table to the reservation so the floor plan shows it as RESERVED
        if (tableId) {
          await firstValueFrom(
            this.http.post(this.configService.getEndpointFor('api/reservation-tables'), {
              assignedAt: new Date().toISOString(),
              reservation: { id: reservation.id },
              table: { id: tableId },
            }),
          );
        }

        if (rt === 'TABLE_MENU' && this.cart().length > 0) {
          const orderCode = this.generateCode('ORD');
          const total = this.cartTotal();
          const orderBody: Record<string, unknown> = {
            orderCode,
            status: 'SUBMITTED',
            isPreOrder: true,
            createdAt: new Date().toISOString(),
            subtotal: total,
            totalAmount: total,
            specialInstructions: this.specialRequests || null,
            location: { id: this.locationId },
            client: { id: this.accountId },
            reservation: { id: reservation.id },
          };
          const order = await firstValueFrom(
            this.http.post<{ id: number; orderCode: string }>(this.configService.getEndpointFor('api/restaurant-orders'), orderBody),
          );
          await this.createOrderItems(order.id);
          await firstValueFrom(this.http.post(this.configService.getEndpointFor(`api/restaurant-orders/${order.id}/finalize`), {}));
        }

        this.confirmationCode.set(reservation.reservationCode ?? resCode);
      } else {
        // DELIVERY or TAKEAWAY
        const orderCode = this.generateCode('ORD');
        const total = this.cartTotal();
        const scheduledFor = `${this.scheduledDate}T${this.scheduledTime}:00Z`;
        const instructions =
          bt === 'DELIVERY'
            ? `Adresă: ${this.address}${this.specialRequests ? '\n' + this.specialRequests : ''}`
            : this.specialRequests || null;

        const orderBody: Record<string, unknown> = {
          orderCode,
          status: 'SUBMITTED',
          isPreOrder: true,
          createdAt: new Date().toISOString(),
          scheduledFor,
          subtotal: total,
          totalAmount: total,
          specialInstructions: instructions,
          location: { id: this.locationId },
          client: { id: this.accountId },
        };
        const order = await firstValueFrom(
          this.http.post<{ id: number; orderCode: string }>(this.configService.getEndpointFor('api/restaurant-orders'), orderBody),
        );
        await this.createOrderItems(order.id);
        await firstValueFrom(this.http.post(this.configService.getEndpointFor(`api/restaurant-orders/${order.id}/finalize`), {}));
        this.confirmationCode.set(order.orderCode ?? orderCode);
      }

      this.step.set('SUCCESS');
      this.reservationDone.emit();
    } catch (err: any) {
      const detail = err?.error?.detail ?? err?.error?.title ?? err?.message ?? 'Eroare necunoscută';
      console.error('[BookingWizard] submit error:', err);
      this.error.set(`Eroare: ${detail}`);
    } finally {
      this.isSubmitting.set(false);
    }
  }

  private async createOrderItems(orderId: number): Promise<void> {
    for (const ci of this.cart()) {
      const unitPrice = ci.item.price;
      await firstValueFrom(
        this.http.post(this.configService.getEndpointFor('api/order-items'), {
          quantity: Number(ci.quantity),
          unitPrice,
          totalPrice: unitPrice * ci.quantity,
          status: 'PENDING',
          order: { id: orderId },
          menuItem: { id: ci.item.id },
        }),
      );
    }
  }

  private buildSpecialRequests(): string {
    const parts: string[] = [];
    if (this.phone) parts.push(`Tel: ${this.phone}`);
    if (this.reservationType() === 'BANQUET') {
      parts.push(`Tip eveniment: ${this.eventType}`);
    }
    if (this.specialRequests) parts.push(this.specialRequests);
    return parts.join(' | ') || '';
  }
}
