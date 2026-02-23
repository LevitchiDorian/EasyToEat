import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IReservationTable } from '../reservation-table.model';
import { ReservationTableService } from '../service/reservation-table.service';

@Component({
  templateUrl: './reservation-table-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ReservationTableDeleteDialogComponent {
  reservationTable?: IReservationTable;

  protected reservationTableService = inject(ReservationTableService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.reservationTableService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
