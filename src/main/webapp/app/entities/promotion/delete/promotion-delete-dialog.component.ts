import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPromotion } from '../promotion.model';
import { PromotionService } from '../service/promotion.service';

@Component({
  templateUrl: './promotion-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PromotionDeleteDialogComponent {
  promotion?: IPromotion;

  protected promotionService = inject(PromotionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.promotionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
