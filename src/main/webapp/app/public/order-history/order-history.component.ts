import { Component, OnInit, inject, signal } from '@angular/core';
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
    if (!confirm('Ești sigur că vrei să anulezi această rezervare?')) return;
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
