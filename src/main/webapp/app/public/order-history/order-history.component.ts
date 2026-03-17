import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router, RouterModule } from '@angular/router';
import { forkJoin, Subscription } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

import { IReservation } from 'app/entities/reservation/reservation.model';
import { IRestaurantOrder } from 'app/entities/restaurant-order/restaurant-order.model';
import { ReservationService } from 'app/entities/reservation/service/reservation.service';
import { RestaurantOrderService } from 'app/entities/restaurant-order/service/restaurant-order.service';
import { AccountService } from 'app/core/auth/account.service';
import { OrdersWsService } from 'app/core/orders-ws/orders-ws.service';
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

export interface Visit {
  reservation: IReservation;
  orders: IRestaurantOrder[];
  total: number;
  isActive: boolean;
  locationId: number | null;
  locationName: string;
}

@Component({
  selector: 'jhi-order-history',
  standalone: true,
  imports: [RouterModule, DecimalPipe],
  templateUrl: './order-history.component.html',
  styleUrl: './order-history.component.scss',
})
export default class OrderHistoryComponent implements OnInit, OnDestroy {
  activeTab = signal<Tab>('reservations');
  reservations = signal<IReservation[]>([]);
  orders = signal<IRestaurantOrder[]>([]);
  isLoading = signal(true);
  error = signal(false);

  // Computed visit groupings
  activeVisits = computed<Visit[]>(() => {
    const today = dayjs().format('YYYY-MM-DD');
    return this.reservations()
      .filter(r => (r.status === 'CONFIRMED' || r.status === 'PENDING') && r.reservationDate?.format('YYYY-MM-DD') === today)
      .map(r => this.toVisit(r, true));
  });

  pastVisits = computed<Visit[]>(() => {
    const today = dayjs().format('YYYY-MM-DD');
    return this.reservations()
      .filter(r => {
        const isToday = r.reservationDate?.format('YYYY-MM-DD') === today;
        const isActive = r.status === 'CONFIRMED' || r.status === 'PENDING';
        return !(isActive && isToday);
      })
      .filter(r => this.ordersForReservation(r.id).length > 0)
      .map(r => this.toVisit(r, false));
  });

  standaloneOrders = computed<IRestaurantOrder[]>(() => this.orders().filter(o => !o.reservation?.id));

  // New-order picker (creates a new order per visit)
  pickerReservationId = signal<number | null>(null);
  pickerLocationId = signal<number | null>(null);
  menuItems = signal<SimpleMenuItem[]>([]);
  isLoadingMenu = signal(false);
  pickerEntries = signal<PickerEntry[]>([]);
  isSubmittingOrder = signal(false);
  orderSuccess = signal(false);

  pickerTotal = computed(() => this.pickerEntries().reduce((s, e) => s + e.item.price * e.qty, 0));

  private billRequestedIds = signal<Set<number>>(new Set());

  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly reservationService = inject(ReservationService);
  private readonly orderService = inject(RestaurantOrderService);
  private readonly accountService = inject(AccountService);
  private readonly ordersWs = inject(OrdersWsService);
  private readonly router = inject(Router);

  private currentUserId: number | null = null;
  private wsSubs: Subscription[] = [];

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => {
      if (!account) {
        this.router.navigate(['/login'], { queryParams: { returnUrl: '/istoricul-meu' } });
        return;
      }
      this.http.get<any>('/api/account').subscribe({
        next: acc => {
          this.currentUserId = acc.id;
          this.loadHistory(acc.id);
        },
        error: () => {
          this.error.set(true);
          this.isLoading.set(false);
        },
      });
    });
  }

  ngOnDestroy(): void {
    this.wsSubs.forEach(s => s.unsubscribe());
  }

  // ── Data loading ─────────────────────────────────────────────────────────────

  private loadHistory(userId: number): void {
    forkJoin({
      reservations: this.reservationService.query({ 'clientId.equals': userId, sort: 'reservationDate,desc', size: 100 }),
      orders: this.orderService.query({ 'clientId.equals': userId, sort: 'createdAt,desc', size: 100 }),
    }).subscribe({
      next: ({ reservations, orders }) => {
        this.reservations.set(reservations.body ?? []);
        this.orders.set(orders.body ?? []);
        this.isLoading.set(false);
        this.subscribeToWS();
      },
      error: () => {
        this.error.set(true);
        this.isLoading.set(false);
      },
    });
  }

  private subscribeToWS(): void {
    this.wsSubs.forEach(s => s.unsubscribe());
    this.wsSubs = [];
    const locationIds = new Set<number>();
    // Reservations always have location populated — use as primary source
    for (const res of this.reservations()) {
      const locId = (res as any).location?.id as number | undefined;
      if (locId) locationIds.add(locId);
    }
    // Orders as fallback
    for (const order of this.orders()) {
      const locId = order.location?.id as number | undefined;
      if (locId) locationIds.add(locId);
    }
    locationIds.forEach(locId => {
      this.wsSubs.push(this.ordersWs.watchLocation(locId).subscribe(() => this.reloadOrders()));
    });
  }

  private reloadOrders(): void {
    if (this.currentUserId === null) return;
    this.orderService.query({ 'clientId.equals': this.currentUserId, sort: 'createdAt,desc', size: 100 }).subscribe({
      next: res => {
        this.orders.set(res.body ?? []);
        this.subscribeToWS();
      },
    });
  }

  // ── Visit helpers ─────────────────────────────────────────────────────────────

  private ordersForReservation(resId: number): IRestaurantOrder[] {
    return this.orders().filter(o => o.reservation?.id === resId);
  }

  private toVisit(res: IReservation, isActive: boolean): Visit {
    const visitOrders = this.ordersForReservation(res.id);
    return {
      reservation: res,
      orders: visitOrders,
      total: visitOrders.reduce((s, o) => s + (o.totalAmount ?? 0), 0),
      isActive,
      locationId: (res as any).location?.id ?? null,
      locationName: res.location?.name ?? 'Restaurant',
    };
  }

  // ── New-order picker ──────────────────────────────────────────────────────────

  openNewOrderPicker(visit: Visit): void {
    this.pickerReservationId.set(visit.reservation.id);
    this.pickerLocationId.set(visit.locationId);
    this.pickerEntries.set([]);
    this.orderSuccess.set(false);
    this.isLoadingMenu.set(true);
    const locId = visit.locationId;
    const url = locId
      ? this.configService.getEndpointFor(
          `api/menu-items?locationId.equals=${locId}&isAvailable.equals=true&size=200&sort=displayOrder,asc`,
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
    this.pickerReservationId.set(null);
    this.orderSuccess.set(false);
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
      if (newQty <= 0) entries.splice(idx, 1);
      else entries[idx] = { ...entries[idx], qty: newQty };
    }
    this.pickerEntries.set(entries);
  }

  confirmNewOrder(): void {
    const resId = this.pickerReservationId();
    const locId = this.pickerLocationId();
    if (!resId || !locId || !this.currentUserId || this.pickerEntries().length === 0) return;

    this.isSubmittingOrder.set(true);
    const total = this.pickerTotal();
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let code = 'ORD-';
    for (let i = 0; i < 6; i++) code += chars[Math.floor(Math.random() * chars.length)];

    this.http
      .post<{ id: number }>(this.configService.getEndpointFor('api/restaurant-orders'), {
        orderCode: code,
        status: 'SUBMITTED',
        isPreOrder: false,
        createdAt: new Date().toISOString(),
        subtotal: total,
        totalAmount: total,
        location: { id: locId },
        client: { id: this.currentUserId },
        reservation: { id: resId },
      })
      .subscribe({
        next: order => {
          const itemPosts = this.pickerEntries().map(e =>
            this.http.post(this.configService.getEndpointFor('api/order-items'), {
              order: { id: order.id },
              menuItem: { id: e.item.id },
              quantity: e.qty,
              unitPrice: e.item.price,
              totalPrice: e.item.price * e.qty,
              status: 'PENDING',
            }),
          );
          forkJoin(itemPosts).subscribe({
            next: () => {
              this.http.post(this.configService.getEndpointFor(`api/restaurant-orders/${order.id}/finalize`), {}).subscribe();
              this.isSubmittingOrder.set(false);
              this.orderSuccess.set(true);
              this.pickerEntries.set([]);
              this.reloadOrders();
            },
            error: () => this.isSubmittingOrder.set(false),
          });
        },
        error: () => this.isSubmittingOrder.set(false),
      });
  }

  // ── Bill request ──────────────────────────────────────────────────────────────

  isBillRequested(reservationId: number): boolean {
    return this.billRequestedIds().has(reservationId);
  }

  requestBill(reservationId: number): void {
    this.billRequestedIds.update(s => new Set(s).add(reservationId));
    this.http.post(this.configService.getEndpointFor(`api/reservations/${reservationId}/request-bill`), {}).subscribe();
  }

  // ── Cancel order ──────────────────────────────────────────────────────────────

  canCancelOrder(order: IRestaurantOrder): boolean {
    return order.status === 'SUBMITTED';
  }

  cancelOrder(order: IRestaurantOrder): void {
    const locationId = (order as any).location?.id as number | undefined;
    this.http
      .patch(this.configService.getEndpointFor(`api/restaurant-orders/${order.id}`), { id: order.id, status: 'CANCELLED' })
      .subscribe({
        next: () => {
          this.orders.update(list => list.map(o => (o.id === order.id ? { ...o, status: 'CANCELLED' } : o)));
          if (locationId) {
            this.http.post(this.configService.getEndpointFor(`api/restaurant-orders/${order.id}/finalize`), {}).subscribe();
          }
        },
      });
  }

  // ── Reservation cancel ────────────────────────────────────────────────────────

  setTab(tab: Tab): void {
    this.activeTab.set(tab);
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

  // ── Labels ────────────────────────────────────────────────────────────────────

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
      case 'SUBMITTED':
        return 'La bucătărie';
      case 'PREPARING':
        return 'Se prepară';
      case 'READY':
        return 'Gata de servire';
      case 'SERVED':
        return 'Servită';
      case 'CANCELLED':
        return 'Anulată';
      case 'CONFIRMED':
        return 'Confirmată';
      default:
        return status ?? '—';
    }
  }

  orderTypeLabel(order: IRestaurantOrder): string {
    return order.isPreOrder ? 'Pre-comandă' : 'Comandă';
  }

  statusMod(status: string | null | undefined): string {
    switch (status) {
      case 'CONFIRMED':
        return 'success';
      case 'COMPLETED':
        return 'success';
      case 'DELIVERED':
        return 'success';
      case 'SERVED':
        return 'success';
      case 'SUBMITTED':
        return 'warning';
      case 'PREPARING':
        return 'warning';
      case 'READY':
        return 'ready';
      case 'CANCELLED':
        return 'danger';
      case 'NO_SHOW':
        return 'danger';
      default:
        return 'neutral';
    }
  }
}
