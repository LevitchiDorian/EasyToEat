import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IOrderItemOptionSelection } from '../order-item-option-selection.model';
import { OrderItemOptionSelectionService } from '../service/order-item-option-selection.service';

@Component({
  templateUrl: './order-item-option-selection-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class OrderItemOptionSelectionDeleteDialogComponent {
  orderItemOptionSelection?: IOrderItemOptionSelection;

  protected orderItemOptionSelectionService = inject(OrderItemOptionSelectionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.orderItemOptionSelectionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
