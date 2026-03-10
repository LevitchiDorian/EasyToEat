import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { IReservation } from 'app/entities/reservation/reservation.model';
import { IRestaurantOrder } from 'app/entities/restaurant-order/restaurant-order.model';
import { ReservationService } from 'app/entities/reservation/service/reservation.service';
import { RestaurantOrderService } from 'app/entities/restaurant-order/service/restaurant-order.service';
import { AccountService } from 'app/core/auth/account.service';
import dayjs from 'dayjs/esm';

type Tab = 'reservations' | 'orders';

interface SimpleMenuItem {
  id: number;
  name: string;
  price: number;
  categoryName?: string;
}

interface PickerEntry {
  item: SimpleMenuItem;
  qty: number;
}

@Component({
  selector: 'jhi-order-history',
  standalone: true,
  imports: [RouterModule, DecimalPipe],
  templateUrl: './order-history.component.html',
  styleUrl: './order-history.component.scss',
})
export default class OrderHistoryComponent implements OnInit {
  activeTab = signal<Tab>('reservations');
  reservations = signal<IReservation[]>([]);
  orders = signal<IRestaurantOrder[]>([]);
  isLoading = signal(true);
  error = signal(false);

  // Add-items picker state
  pickerOrderId = signal<number | null>(null);
  pickerLocationId = signal<number | null>(null);
  menuItems = signal<SimpleMenuItem[]>([]);
  isLoadingMenu = signal(false);
  pickerEntries = signal<PickerEntry[]>([]);
  isAddingItems = signal(false);
  addSuccess = signal(false);

  pickerTotal = computed(() => this.pickerEntries().reduce((s, e) => s + e.item.price * e.qty, 0));

  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly reservationService = inject(ReservationService);
  private readonly orderService = inject(RestaurantOrderService);
  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (!account) {
        this.router.navigate(['/login'], { queryParams: { returnUrl: '/istoricul-meu' } });
        return;
      }
      this.http.get<any>('/api/account').subscribe({
        next: acc => this.loadHistory(acc.id),
        error: () => {
          this.error.set(true);
          this.isLoading.set(false);
        },
      });
    });
  }

  private loadHistory(userId: number): void {
    forkJoin({
      reservations: this.reservationService.query({ 'clientId.equals': userId, sort: 'createdAt,desc', size: 100 }),
      orders: this.orderService.query({ 'clientId.equals': userId, sort: 'createdAt,desc', size: 100 }),
    }).subscribe({
      next: ({ reservations, orders }) => {
        this.reservations.set(reservations.body ?? []);
        this.orders.set(orders.body ?? []);
        this.isLoading.set(false);
      },
      error: () => {
        this.error.set(true);
        this.isLoading.set(false);
      },
    });
  }

  setTab(tab: Tab): void {
    this.activeTab.set(tab);
  }

  isActiveOrder(order: IRestaurantOrder): boolean {
    return ['PENDING', 'CONFIRMED', 'PREPARING', 'READY'].includes(order.status ?? '');
  }

  openPicker(order: IRestaurantOrder): void {
    const locationId = (order as any).location?.id ?? null;
    this.pickerOrderId.set(order.id);
    this.pickerLocationId.set(locationId);
    this.pickerEntries.set([]);
    this.addSuccess.set(false);
    this.isLoadingMenu.set(true);
    const url = locationId
      ? this.configService.getEndpointFor(
          `api/menu-items?locationId.equals=${locationId}&isAvailable.equals=true&size=200&sort=displayOrder,asc`,
        )
      : this.configService.getEndpointFor(`api/menu-items?isAvailable.equals=true&size=200&sort=displayOrder,asc`);
    this.http.get<any[]>(url).subscribe({
      next: items => {
        this.menuItems.set(items.map(i => ({ id: i.id, name: i.name ?? 'Produs', price: i.price ?? 0, categoryName: i.category?.name })));
        this.isLoadingMenu.set(false);
      },
      error: () => this.isLoadingMenu.set(false),
    });
  }

  closePicker(): void {
    this.pickerOrderId.set(null);
  }

  pickerQty(itemId: number): number {
    return this.pickerEntries().find(e => e.item.id === itemId)?.qty ?? 0;
  }

  adjustQty(item: SimpleMenuItem, delta: number): void {
    const entries = [...this.pickerEntries()];
    const idx = entries.findIndex(e => e.item.id === item.id);
    if (idx === -1) {
      if (delta > 0) entries.push({ item, qty: delta });
    } else {
      const newQty = entries[idx].qty + delta;
      if (newQty <= 0) {
        entries.splice(idx, 1);
      } else {
        entries[idx] = { ...entries[idx], qty: newQty };
      }
    }
    this.pickerEntries.set(entries);
  }

  confirmAddItems(): void {
    const orderId = this.pickerOrderId();
    if (!orderId || this.pickerEntries().length === 0) return;
    this.isAddingItems.set(true);
    const posts = this.pickerEntries().map(e =>
      this.http.post(this.configService.getEndpointFor('api/order-items'), {
        restaurantOrder: { id: orderId },
        menuItem: { id: e.item.id },
        quantity: e.qty,
        unitPrice: e.item.price,
        totalPrice: e.item.price * e.qty,
        status: 'PENDING',
      }),
    );
    forkJoin(posts).subscribe({
      next: () => {
        this.isAddingItems.set(false);
        this.addSuccess.set(true);
        this.pickerEntries.set([]);
        // Refresh the order total by reloading
        this.http.get<any>('/api/account').subscribe({ next: acc => this.loadHistory(acc.id) });
      },
      error: () => this.isAddingItems.set(false),
    });
  }

  reservationStatusLabel(status: string | null | undefined): string {
    switch (status) {
      case 'PENDING':
        return 'În așteptare';
      case 'CONFIRMED':
        return 'Confirmată';
      case 'CANCELLED':
        return 'Anulată';
      case 'COMPLETED':
        return 'Finalizată';
      case 'NO_SHOW':
        return 'Neprezentare';
      default:
        return status ?? '—';
    }
  }

  orderStatusLabel(status: string | null | undefined): string {
    switch (status) {
      case 'PENDING':
        return 'În așteptare';
      case 'CONFIRMED':
        return 'Confirmată';
      case 'PREPARING':
        return 'Se prepară';
      case 'READY':
        return 'Gata';
      case 'DELIVERED':
        return 'Livrată';
      case 'CANCELLED':
        return 'Anulată';
      case 'COMPLETED':
        return 'Finalizată';
      default:
        return status ?? '—';
    }
  }

  orderTypeLabel(order: IRestaurantOrder): string {
    return order.isPreOrder ? 'Pre-comandă' : 'Comandă';
  }

  canCancelReservation(res: IReservation): boolean {
    if (res.status === 'CANCELLED' || res.status === 'COMPLETED' || res.status === 'NO_SHOW') return false;
    if (!res.reservationDate || !res.startTime) return true;
    const dateStr = res.reservationDate.format('YYYY-MM-DD');
    const resDateTime = dayjs(`${dateStr}T${res.startTime}`);
    return resDateTime.diff(dayjs(), 'hour') >= 2;
  }

  cancelReservation(res: IReservation): void {
    this.http.patch(this.configService.getEndpointFor(`api/reservations/${res.id}`), { id: res.id, status: 'CANCELLED' }).subscribe({
      next: () => {
        this.reservations.update(list => list.map(r => (r.id === res.id ? { ...r, status: 'CANCELLED' } : r)));
      },
    });
  }

  statusMod(status: string | null | undefined): string {
    switch (status) {
      case 'CONFIRMED':
        return 'success';
      case 'COMPLETED':
        return 'success';
      case 'DELIVERED':
        return 'success';
      case 'PENDING':
        return 'warning';
      case 'PREPARING':
        return 'warning';
      case 'READY':
        return 'warning';
      case 'CANCELLED':
        return 'danger';
      case 'NO_SHOW':
        return 'danger';
      default:
        return 'neutral';
    }
  }
}
