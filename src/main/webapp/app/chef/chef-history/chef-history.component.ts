import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { ChefOrder } from '../chef-dashboard/chef-dashboard.component';

@Component({
  selector: 'app-chef-history',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chef-history.component.html',
  styleUrl: './chef-history.component.scss',
})
export default class ChefHistoryComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);

  selectedDate = signal<string>(new Date().toISOString().substring(0, 10));
  orders = signal<ChefOrder[]>([]);
  isLoading = signal(false);

  servedOrders = () => this.orders().filter(o => o.status === 'SERVED');
  cancelledOrders = () => this.orders().filter(o => o.status === 'CANCELLED');

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    const url = this.configService.getEndpointFor(`api/chef/orders/history?date=${this.selectedDate()}`);
    this.http.get<ChefOrder[]>(url).subscribe({
      next: data => {
        this.orders.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  onDateChange(val: string): void {
    this.selectedDate.set(val);
    this.load();
  }

  elapsedLabel(dateStr: string): string {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleTimeString('ro-RO', { hour: '2-digit', minute: '2-digit' });
  }

  statusLabel(status: string): string {
    return status === 'SERVED' ? 'Servit' : status === 'CANCELLED' ? 'Anulat' : status;
  }
}
