import { Component, inject, signal, computed, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { AccountRoleService } from 'app/core/profile/account-role.service';
import { OrdersWsService } from 'app/core/orders-ws/orders-ws.service';

export interface ChefOrderItem {
  id: number;
  quantity: number;
  menuItemName: string;
  specialInstructions?: string;
  status: string;
}

export interface ChefOrder {
  id: number;
  orderCode: string;
  status: 'SUBMITTED' | 'PREPARING' | 'READY' | 'SERVED' | 'CANCELLED';
  createdAt: string;
  updatedAt?: string;
  locationId: number;
  tableNumber?: string;
  isPreOrder?: boolean;
  reservationDate?: string;
  reservationStartTime?: string;
  items: ChefOrderItem[];
}

@Component({
  selector: 'app-chef-dashboard',
  standalone: true,
  templateUrl: './chef-dashboard.component.html',
  styleUrl: './chef-dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule],
})
export default class ChefDashboardComponent implements OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly accountRoleService = inject(AccountRoleService);
  private readonly ordersWs = inject(OrdersWsService);

  isLoading = signal(false);
  allOrders = signal<ChefOrder[]>([]);

  // Drag state
  draggedOrderId: number | null = null;
  dragOverColumn = signal<string | null>(null);

  preparingModal = signal<ChefOrder | null>(null);

  private wsSub?: Subscription;
  private autoServeTick?: ReturnType<typeof setInterval>;
  private periodicRefreshTick?: ReturnType<typeof setInterval>;

  submitted = computed(() =>
    this.allOrders()
      .filter(o => o.status === 'SUBMITTED')
      .sort(this.byScheduledTime),
  );
  preparing = computed(() =>
    this.allOrders()
      .filter(o => o.status === 'PREPARING')
      .sort(this.byScheduledTime),
  );
  ready = computed(() =>
    this.allOrders()
      .filter(o => o.status === 'READY')
      .sort(this.byScheduledTime),
  );

  private readonly byScheduledTime = (a: ChefOrder, b: ChefOrder): number => {
    const keyA = a.reservationDate && a.reservationStartTime ? `${a.reservationDate}T${a.reservationStartTime}` : (a.createdAt ?? '');
    const keyB = b.reservationDate && b.reservationStartTime ? `${b.reservationDate}T${b.reservationStartTime}` : (b.createdAt ?? '');
    return keyA.localeCompare(keyB);
  };

  constructor() {
    this.loadOrders();
    // Fallback: refresh every 15 s in case a WS message is missed
    this.periodicRefreshTick = setInterval(() => this.loadOrders(true), 15_000);
    // Auto-advance READY orders to SERVED after 10 minutes
    this.autoServeTick = setInterval(() => this.checkAutoServe(), 60_000);
  }

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
    clearInterval(this.autoServeTick);
    clearInterval(this.periodicRefreshTick);
  }

  loadOrders(silent = false): void {
    if (!silent) this.isLoading.set(true);
    const locId = this.accountRoleService.locationId();
    const url = this.configService.getEndpointFor('api/chef/orders');
    this.http.get<ChefOrder[]>(url).subscribe({
      next: orders => {
        this.allOrders.set(orders);
        this.isLoading.set(false);
        // Set up WS once we know the location (from profile or first order)
        if (!this.wsSub) {
          const wsLocId = locId ?? (orders.length > 0 ? orders[0].locationId : null);
          if (wsLocId) {
            this.wsSub = this.ordersWs.watchLocation(wsLocId).subscribe(() => this.loadOrders(true));
          }
        }
      },
      error: () => this.isLoading.set(false),
    });
  }

  private checkAutoServe(): void {
    const now = Date.now();
    for (const order of this.allOrders()) {
      if (order.status !== 'READY') continue;
      const readyAt = new Date(order.updatedAt ?? order.createdAt).getTime();
      if (now - readyAt >= 10 * 60 * 1000) {
        this.serveOrder(order.id);
      }
    }
  }

  // ── Drag & Drop ─────────────────────────────────────────────────────────────

  onDragStart(orderId: number): void {
    this.draggedOrderId = orderId;
  }
  onDragEnd(): void {
    this.draggedOrderId = null;
    this.dragOverColumn.set(null);
  }
  onDragOver(event: DragEvent, column: string): void {
    event.preventDefault();
    this.dragOverColumn.set(column);
  }
  onDragLeave(): void {
    this.dragOverColumn.set(null);
  }

  onDrop(event: DragEvent, targetStatus: 'PREPARING' | 'READY'): void {
    event.preventDefault();
    this.dragOverColumn.set(null);
    if (this.draggedOrderId === null) return;
    const order = this.allOrders().find(o => o.id === this.draggedOrderId);
    if (!order) return;
    const valid =
      (order.status === 'SUBMITTED' && targetStatus === 'PREPARING') || (order.status === 'PREPARING' && targetStatus === 'READY');
    if (!valid) return;
    this.updateStatus(order.id, targetStatus);
    this.draggedOrderId = null;
  }

  updateStatus(orderId: number, newStatus: 'PREPARING' | 'READY'): void {
    this.allOrders.update(orders =>
      orders.map(o => (o.id === orderId ? { ...o, status: newStatus, updatedAt: new Date().toISOString() } : o)),
    );
    this.http.patch(this.configService.getEndpointFor(`api/chef/orders/${orderId}/status`), { status: newStatus }).subscribe({
      error: () => this.loadOrders(true),
    });
  }

  serveOrder(orderId: number): void {
    // Remove from board immediately
    this.allOrders.update(orders => orders.filter(o => o.id !== orderId));
    this.http.patch(this.configService.getEndpointFor(`api/chef/orders/${orderId}/status`), { status: 'SERVED' }).subscribe({
      error: () => this.loadOrders(true),
    });
  }

  // ── Preparing modal ──────────────────────────────────────────────────────────

  openPreparingModal(order: ChefOrder, event: MouseEvent): void {
    event.stopPropagation();
    this.preparingModal.set({ ...order, items: order.items.map(i => ({ ...i })) });
  }

  closePreparingModal(): void {
    this.preparingModal.set(null);
  }

  markItemReady(itemId: number, orderId: number): void {
    const update = (items: ChefOrderItem[]) => items.map(i => (i.id === itemId ? { ...i, status: 'READY' } : i));
    this.preparingModal.update(m => (m ? { ...m, items: update(m.items) } : null));
    this.allOrders.update(orders => orders.map(o => (o.id === orderId ? { ...o, items: update(o.items) } : o)));
    this.http.patch(this.configService.getEndpointFor(`api/chef/items/${itemId}/ready`), {}).subscribe({
      error: () => this.loadOrders(true),
    });
  }

  // ── Helpers ─────────────────────────────────────────────────────────────────

  elapsedLabel(dateStr: string): string {
    if (!dateStr) return '';
    const diff = Math.floor((Date.now() - new Date(dateStr).getTime()) / 1000);
    if (diff < 60) return `${diff}s`;
    const m = Math.floor(diff / 60);
    if (m < 60) return `${m}m`;
    return `${Math.floor(m / 60)}h ${m % 60}m`;
  }

  isUrgent(createdAt: string, status: string): boolean {
    if (status !== 'SUBMITTED' && status !== 'PREPARING') return false;
    return Math.floor((Date.now() - new Date(createdAt).getTime()) / 60000) >= 10;
  }

  readyElapsed(order: ChefOrder): string {
    return this.elapsedLabel(order.updatedAt ?? order.createdAt);
  }

  isReadyOld(order: ChefOrder): boolean {
    const readyAt = new Date(order.updatedAt ?? order.createdAt).getTime();
    return Date.now() - readyAt >= 5 * 60 * 1000; // warn after 5 min in READY
  }

  minutesUntilArrival(order: ChefOrder): number {
    if (!order.reservationStartTime) return 999;
    const [h, m] = order.reservationStartTime.split(':').map(Number);
    const now = new Date();
    const arrivalMinutes = h * 60 + m;
    const nowMinutes = now.getHours() * 60 + now.getMinutes();
    return arrivalMinutes - nowMinutes;
  }
}
