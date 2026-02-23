import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { IReservationTable } from '../reservation-table.model';

@Component({
  selector: 'jhi-reservation-table-detail',
  templateUrl: './reservation-table-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class ReservationTableDetailComponent {
  reservationTable = input<IReservationTable | null>(null);

  previousState(): void {
    window.history.back();
  }
}
