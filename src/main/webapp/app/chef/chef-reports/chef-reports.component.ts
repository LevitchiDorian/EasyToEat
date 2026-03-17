import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ApplicationConfigService } from 'app/core/config/application-config.service';

interface ReportDTO {
  totalOrders: number;
  servedOrders: number;
  cancelledOrders: number;
  totalRevenue: number;
  avgOrderValue: number;
}

type Period = 'day' | 'week' | 'month';

@Component({
  selector: 'app-chef-reports',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chef-reports.component.html',
  styleUrl: './chef-reports.component.scss',
})
export default class ChefReportsComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly configService = inject(ApplicationConfigService);

  period = signal<Period>('day');
  report = signal<ReportDTO | null>(null);
  isLoading = signal(false);

  ngOnInit(): void {
    this.load();
  }

  setPeriod(p: Period): void {
    this.period.set(p);
    this.load();
  }

  load(): void {
    this.isLoading.set(true);
    const url = this.configService.getEndpointFor(`api/chef/orders/report?period=${this.period()}`);
    this.http.get<ReportDTO>(url).subscribe({
      next: data => {
        this.report.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false),
    });
  }

  periodLabel(): string {
    const map: Record<Period, string> = { day: 'azi', week: 'ultimele 7 zile', month: 'ultimele 30 zile' };
    return map[this.period()];
  }

  successRate(): number {
    const r = this.report();
    if (!r || r.totalOrders === 0) return 0;
    return Math.round((r.servedOrders / r.totalOrders) * 100);
  }
}
