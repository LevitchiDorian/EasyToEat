import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDiningRoom } from '../dining-room.model';
import { DiningRoomService } from '../service/dining-room.service';

@Component({
  templateUrl: './dining-room-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DiningRoomDeleteDialogComponent {
  diningRoom?: IDiningRoom;

  protected diningRoomService = inject(DiningRoomService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.diningRoomService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
