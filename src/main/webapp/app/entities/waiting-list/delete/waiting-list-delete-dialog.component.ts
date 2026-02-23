import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IWaitingList } from '../waiting-list.model';
import { WaitingListService } from '../service/waiting-list.service';

@Component({
  templateUrl: './waiting-list-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class WaitingListDeleteDialogComponent {
  waitingList?: IWaitingList;

  protected waitingListService = inject(WaitingListService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.waitingListService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
