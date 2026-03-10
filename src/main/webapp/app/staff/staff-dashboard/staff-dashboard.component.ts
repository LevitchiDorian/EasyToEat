import { Component, inject, signal, computed, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { interval, Subscription } from 'rxjs';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { FloorMapComponent, FloorPlan, FloorTable } from 'app/public/floor-map/floor-map.component';
import { FloorPlanWsService } from 'app/core/floor-plan-ws/floor-plan-ws.service';

interface OrderLine {
  id: number;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  status: string;
  menuItem?: { id: number; name?: string };
}

interface TableOrderInfo {
  id: number;
  status: string;
  totalAmount?: number;
  notes?: string;
  items: OrderLine[];
}

interface ReservationInfo {
  id: number;
  reservationCode: string;
  reservationDate: string;
  startTime: string;
  endTime: string;
  partySize: number;
  status: string;
  specialRequests?: string;
  clientLogin?: string;
  clientFirstName?: string;
  clientLastName?: string;
  client?: { id: number; firstName?: string; lastName?: string };
}

interface TableInfo {
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
  reservation?: ReservationInfo;
}

interface RoomInfo {
  id: number;
  name: string;
  floor?: number;
  widthPx: number;
  heightPx: number;
  tables: TableInfo[];
}

interface FloorPlanResponse {
  locationId: number;
  locationName: string;
  locationAddress: string;
  date: string;
  rooms: RoomInfo[];
}

interface OrderItem {
  id: number;
  status: string;
  totalAmount?: number;
  notes?: string;
  client?: { id: number; firstName?: string; lastName?: string };
  table?: { id: number; tableNumber?: string };
}

interface CheckInAlert {
  reservationId: number;
  tableId: number;
  tableNumber: string;
  clientName: string;
  startTime: string;
  extendCount: number;
}

type SideTab = 'masa' | 'rezervari' | 'comenzi';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  templateUrl: './staff-dashboard.component.html',
  styleUrl: './staff-dashboard.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [CommonModule, FormsModule, RouterLink, FloorMapComponent],
})
export default class StaffDashboardComponent implements OnInit, OnDestroy {
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);
  private readonly floorPlanWs = inject(FloorPlanWsService);

  isLoading = signal(false);
  error = signal<string | null>(null);
  selectedDate = signal<string>(new Date().toISOString().substring(0, 10));
  rawResponse = signal<FloorPlanResponse | null>(null);
  floorPlan = signal<FloorPlan | null>(null);
  selectedTable = signal<TableInfo | null>(null);
  refreshCountdown = signal<number>(30);
  lastRefreshed = signal<string>('');

  activeTab = signal<SideTab>('rezervari');
  allReservations = signal<ReservationInfo[]>([]);
  isLoadingReservations = signal(false);
  activeOrders = signal<OrderItem[]>([]);
  isLoadingOrders = signal(false);

  selectedTableOrder = signal<TableOrderInfo | null>(null);
  isLoadingTableOrder = signal(false);

  checkInAlerts = signal<CheckInAlert[]>([]);

  private refreshSub?: Subscription;
  private wsSub?: Subscription;
  private minuteTick = 0;
  private readonly dismissedAlerts = new Set<number>();
  private readonly extendedAlerts = new Map<number, number>(); // reservationId → extendedUntil timestamp

  totalTables = computed(() => {
    const r = this.rawResponse();
    if (!r) return 0;
    return r.rooms.reduce((acc, room) => acc + room.tables.filter(t => t.isActive).length, 0);
  });

  availableTables = computed(() => {
    const r = this.rawResponse();
    if (!r) return 0;
    return r.rooms.reduce((acc, room) => acc + room.tables.filter(t => t.status === 'AVAILABLE' && t.isActive).length, 0);
  });

  reservedTables = computed(() => {
    const r = this.rawResponse();
    if (!r) return 0;
    return r.rooms.reduce((acc, room) => acc + room.tables.filter(t => t.status === 'RESERVED' && t.isActive).length, 0);
  });

  occupiedTables = computed(() => {
    const r = this.rawResponse();
    if (!r) return 0;
    return r.rooms.reduce((acc, room) => acc + room.tables.filter(t => t.status === 'OCCUPIED' && t.isActive).length, 0);
  });

  todayReservations = computed((): ReservationInfo[] =>
    this.allReservations()
      .filter(r => r.status !== 'CANCELLED')
      .sort((a, b) => (a.startTime ?? '').localeCompare(b.startTime ?? '')),
  );

  ngOnInit(): void {
    this.loadFloorPlan();
    this.refreshSub = interval(1000).subscribe(() => {
      const c = this.refreshCountdown() - 1;
      if (c <= 0) {
        this.refreshCountdown.set(30);
        this.loadFloorPlan(true);
      } else {
        this.refreshCountdown.set(c);
      }
      this.minuteTick++;
      if (this.minuteTick >= 60) {
        this.minuteTick = 0;
        this.checkReservationsDue();
      }
    });
  }

  ngOnDestroy(): void {
    this.refreshSub?.unsubscribe();
    this.wsSub?.unsubscribe();
  }

  loadFloorPlan(silent = false): void {
    if (!silent) this.isLoading.set(true);
    this.error.set(null);
    const url = this.configService.getEndpointFor(`api/staff/floor-plan/my?date=${this.selectedDate()}`);
    this.http.get<FloorPlanResponse>(url).subscribe({
      next: res => {
        if (!res.locationId) {
          this.error.set('Contul tău nu are o locație asignată. Contactează administratorul.');
          this.isLoading.set(false);
          return;
        }
        this.rawResponse.set(res);
        this.floorPlan.set(this.mapToFloorPlan(res));
        this.isLoading.set(false);
        this.lastRefreshed.set(new Date().toLocaleTimeString('ro-RO'));
        const prev = this.selectedTable();
        if (prev) {
          let found: TableInfo | undefined;
          for (const room of res.rooms) {
            found = room.tables.find(t => t.id === prev.id);
            if (found) break;
          }
          this.selectedTable.set(found ?? null);
        }
        this.loadSideData(res.locationId);
        this.checkReservationsDue();
        // Subscribe to real-time WebSocket updates for this location
        if (!this.wsSub) {
          this.wsSub = this.floorPlanWs.watchLocation(res.locationId).subscribe(() => {
            this.loadFloorPlan(true);
          });
        }
      },
      error: () => {
        this.error.set('Nu s-a putut încărca planul sălii. Verificați conexiunea.');
        this.isLoading.set(false);
      },
    });
  }

  private loadSideData(locationId: number): void {
    this.loadAllReservations(locationId);
    this.loadActiveOrders(locationId);
  }

  private loadAllReservations(locationId: number): void {
    this.isLoadingReservations.set(true);
    const date = this.selectedDate();
    const url = this.configService.getEndpointFor(
      `api/reservations?locationId.equals=${locationId}&reservationDate.equals=${date}&size=200&sort=startTime,asc`,
    );
    this.http.get<ReservationInfo[]>(url).subscribe({
      next: data => {
        this.allReservations.set(data);
        this.isLoadingReservations.set(false);
      },
      error: () => this.isLoadingReservations.set(false),
    });
  }

  private loadActiveOrders(locationId: number): void {
    this.isLoadingOrders.set(true);
    const url = this.configService.getEndpointFor(`api/restaurant-orders?locationId.equals=${locationId}&size=100&sort=id,desc`);
    this.http.get<OrderItem[]>(url).subscribe({
      next: data => {
        this.activeOrders.set(data.filter(o => ['PENDING', 'CONFIRMED', 'PREPARING', 'READY'].includes(o.status)));
        this.isLoadingOrders.set(false);
      },
      error: () => this.isLoadingOrders.set(false),
    });
  }

  onDateChange(newDate: string): void {
    this.selectedDate.set(newDate);
    this.loadFloorPlan();
  }

  manualRefresh(): void {
    this.refreshCountdown.set(30);
    this.loadFloorPlan();
  }

  updateTableStatus(tableId: number, currentStatus: string): void {
    const newStatus = currentStatus === 'OCCUPIED' ? 'AVAILABLE' : 'OCCUPIED';
    this.http.patch(this.configService.getEndpointFor(`api/restaurant-tables/${tableId}`), { id: tableId, status: newStatus }).subscribe({
      next: () => {
        this.rawResponse.update(r => {
          if (!r) return r;
          return {
            ...r,
            rooms: r.rooms.map(room => ({
              ...room,
              tables: room.tables.map(t => (t.id === tableId ? { ...t, status: newStatus } : t)),
            })),
          };
        });
        const sel = this.selectedTable();
        if (sel && sel.id === tableId) this.selectedTable.set({ ...sel, status: newStatus });
        const raw = this.rawResponse();
        if (raw) this.floorPlan.set(this.mapToFloorPlan(raw));
      },
      error: () => alert('Eroare la actualizare status masă.'),
    });
  }

  updateReservationStatus(resId: number, newStatus: string): void {
    this.http.patch(this.configService.getEndpointFor(`api/reservations/${resId}`), { id: resId, status: newStatus }).subscribe({
      next: () => {
        this.allReservations.update(list => list.map(r => (r.id === resId ? { ...r, status: newStatus } : r)));
        const sel = this.selectedTable();
        if (sel?.reservation && sel.reservation.id === resId) {
          this.selectedTable.set({ ...sel, reservation: { ...sel.reservation, status: newStatus } });
        }
        // Terminal actions free up the table
        if (['COMPLETED', 'NO_SHOW', 'CANCELLED'].includes(newStatus)) {
          const table = this.selectedTable();
          if (table) this.freeTable(table.id);
        }
      },
      error: () => alert('Eroare la actualizare rezervare.'),
    });
  }

  private freeTable(tableId: number): void {
    this.http.patch(this.configService.getEndpointFor(`api/restaurant-tables/${tableId}`), { id: tableId, status: 'AVAILABLE' }).subscribe({
      next: () => {
        this.rawResponse.update(r => {
          if (!r) return r;
          return {
            ...r,
            rooms: r.rooms.map(room => ({
              ...room,
              tables: room.tables.map(t => (t.id === tableId ? { ...t, status: 'AVAILABLE' } : t)),
            })),
          };
        });
        const sel = this.selectedTable();
        if (sel?.id === tableId) this.selectedTable.set({ ...sel, status: 'AVAILABLE' });
        const raw = this.rawResponse();
        if (raw) this.floorPlan.set(this.mapToFloorPlan(raw));
      },
    });
  }

  onTableSelected(table: FloorTable): void {
    const response = this.rawResponse();
    if (!response) return;
    let found: TableInfo | undefined;
    for (const room of response.rooms) {
      found = room.tables.find(t => t.id === table.id);
      if (found) break;
    }
    this.selectedTable.set(found ?? null);
    this.activeTab.set('masa');
    this.selectedTableOrder.set(null);
    if (found?.status === 'OCCUPIED') {
      this.loadTableOrder(found.id);
    } else if (found?.status === 'RESERVED' && found.reservation) {
      this.loadReservationOrder(found.reservation.id);
    }
  }

  loadTableOrder(tableId: number): void {
    this.isLoadingTableOrder.set(true);
    const url = this.configService.getEndpointFor(`api/restaurant-orders?tableId.equals=${tableId}&size=1&sort=id,desc`);
    this.http.get<any[]>(url).subscribe({
      next: orders => {
        const active = orders.filter(o => ['PENDING', 'CONFIRMED', 'PREPARING', 'READY'].includes(o.status));
        if (active.length > 0) {
          this.fetchOrderItems(active[0]);
        } else {
          this.isLoadingTableOrder.set(false);
        }
      },
      error: () => this.isLoadingTableOrder.set(false),
    });
  }

  loadReservationOrder(reservationId: number): void {
    this.isLoadingTableOrder.set(true);
    const url = this.configService.getEndpointFor(`api/restaurant-orders?reservationId.equals=${reservationId}&size=1&sort=id,desc`);
    this.http.get<any[]>(url).subscribe({
      next: orders => {
        if (orders.length > 0) {
          this.fetchOrderItems(orders[0]);
        } else {
          this.isLoadingTableOrder.set(false);
        }
      },
      error: () => this.isLoadingTableOrder.set(false),
    });
  }

  private fetchOrderItems(order: any): void {
    const url = this.configService.getEndpointFor(`api/order-items?restaurantOrderId.equals=${order.id}&size=100`);
    this.http.get<OrderLine[]>(url).subscribe({
      next: items => {
        this.selectedTableOrder.set({ id: order.id, status: order.status, totalAmount: order.totalAmount, notes: order.notes, items });
        this.isLoadingTableOrder.set(false);
      },
      error: () => this.isLoadingTableOrder.set(false),
    });
  }

  clearSelection(): void {
    this.selectedTable.set(null);
    this.selectedTableOrder.set(null);
    this.activeTab.set('rezervari');
  }

  setTab(tab: SideTab): void {
    this.activeTab.set(tab);
  }

  private checkReservationsDue(): void {
    const res = this.rawResponse();
    if (!res) return;
    const today = new Date().toISOString().substring(0, 10);
    if (this.selectedDate() !== today) return;

    const now = new Date();
    const nowMinutes = now.getHours() * 60 + now.getMinutes();

    const newAlerts: CheckInAlert[] = [];
    for (const room of res.rooms) {
      for (const table of room.tables) {
        if (table.status !== 'RESERVED' || !table.reservation) continue;
        const r = table.reservation;
        if (!['PENDING', 'CONFIRMED'].includes(r.status)) continue;
        if (this.dismissedAlerts.has(r.id)) continue;

        const [h, m] = r.startTime.split(':').map(Number);
        const startMinutes = h * 60 + m;
        if (nowMinutes < startMinutes) continue;

        // Check if extended and extension not expired yet
        const extendedUntil = this.extendedAlerts.get(r.id);
        if (extendedUntil && Date.now() < extendedUntil) continue;

        const existing = this.checkInAlerts().find(a => a.reservationId === r.id);
        if (existing) continue;

        const clientName = [r.clientFirstName, r.clientLastName].filter(Boolean).join(' ') || r.clientLogin || 'Client';
        newAlerts.push({
          reservationId: r.id,
          tableId: table.id,
          tableNumber: table.tableNumber,
          clientName,
          startTime: r.startTime,
          extendCount: this.extendedAlerts.has(r.id) ? (this.checkInAlerts().find(a => a.reservationId === r.id)?.extendCount ?? 0) : 0,
        });
      }
    }

    if (newAlerts.length > 0) {
      this.checkInAlerts.update(prev => [...prev, ...newAlerts]);
    }
  }

  alertPresent(alert: CheckInAlert): void {
    // Mark table as OCCUPIED
    this.http
      .patch(this.configService.getEndpointFor(`api/restaurant-tables/${alert.tableId}`), { id: alert.tableId, status: 'OCCUPIED' })
      .subscribe();
    this.dismissedAlerts.add(alert.reservationId);
    this.checkInAlerts.update(prev => prev.filter(a => a.reservationId !== alert.reservationId));
  }

  alertExtend(alert: CheckInAlert): void {
    const extendedUntil = Date.now() + 10 * 60 * 1000;
    this.extendedAlerts.set(alert.reservationId, extendedUntil);
    this.checkInAlerts.update(prev => prev.filter(a => a.reservationId !== alert.reservationId));

    // Update displayed arrival time (+10 min) on the selected table
    const sel = this.selectedTable();
    if (sel?.reservation?.id === alert.reservationId && sel.reservation.startTime) {
      const newStart = this.addMinutes(sel.reservation.startTime, 10);
      this.selectedTable.set({ ...sel, reservation: { ...sel.reservation, startTime: newStart } });
    }
    // Update rawResponse so the floor plan reflects the extended arrival window
    this.rawResponse.update(r => {
      if (!r) return r;
      return {
        ...r,
        rooms: r.rooms.map(room => ({
          ...room,
          tables: room.tables.map(t => {
            if (t.id === alert.tableId && t.reservation?.id === alert.reservationId && t.reservation.startTime) {
              return { ...t, reservation: { ...t.reservation, startTime: this.addMinutes(t.reservation.startTime, 10) } };
            }
            return t;
          }),
        })),
      };
    });
  }

  private addMinutes(time: string, minutes: number): string {
    if (!time) return time;
    const [h, m] = time.split(':').map(Number);
    const total = h * 60 + m + minutes;
    const hh = String(Math.floor(total / 60) % 24).padStart(2, '0');
    const mm = String(total % 60).padStart(2, '0');
    return `${hh}:${mm}`;
  }

  alertAbsent(alert: CheckInAlert): void {
    // Mark table AVAILABLE + reservation NO_SHOW
    this.http
      .patch(this.configService.getEndpointFor(`api/restaurant-tables/${alert.tableId}`), { id: alert.tableId, status: 'AVAILABLE' })
      .subscribe();
    this.http
      .patch(this.configService.getEndpointFor(`api/reservations/${alert.reservationId}`), { id: alert.reservationId, status: 'NO_SHOW' })
      .subscribe();
    this.dismissedAlerts.add(alert.reservationId);
    this.checkInAlerts.update(prev => prev.filter(a => a.reservationId !== alert.reservationId));
  }

  get locationName(): string {
    return this.rawResponse()?.locationName ?? 'Locația mea';
  }

  get locationAddress(): string {
    return this.rawResponse()?.locationAddress ?? '';
  }

  statusLabel(status: string): string {
    const map: Record<string, string> = {
      AVAILABLE: 'Disponibilă',
      RESERVED: 'Rezervată',
      OCCUPIED: 'Ocupată',
      OUT_OF_SERVICE: 'Indisponibilă',
      PENDING: 'În așteptare',
      CONFIRMED: 'Confirmată',
      COMPLETED: 'Finalizată',
      CANCELLED: 'Anulată',
      PREPARING: 'Se prepară',
      READY: 'Gata',
    };
    return map[status] ?? status;
  }

  statusClass(status: string): string {
    if (status === 'AVAILABLE' || status === 'COMPLETED') return 'sd-badge sd-badge--green';
    if (status === 'RESERVED' || status === 'PENDING' || status === 'CONFIRMED') return 'sd-badge sd-badge--amber';
    if (status === 'OCCUPIED' || status === 'PREPARING' || status === 'READY') return 'sd-badge sd-badge--blue';
    return 'sd-badge sd-badge--red';
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
      canvasWidth = Math.max(canvasWidth, 40 + w);
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
    const canvasHeight = Math.max(600, currentY + 20);
    return { canvasWidth, canvasHeight, rooms };
  }
}
