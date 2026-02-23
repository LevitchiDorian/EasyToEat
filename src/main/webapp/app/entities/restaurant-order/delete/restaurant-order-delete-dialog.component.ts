import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRestaurantOrder } from '../restaurant-order.model';
import { RestaurantOrderService } from '../service/restaurant-order.service';

@Component({
  templateUrl: './restaurant-order-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RestaurantOrderDeleteDialogComponent {
  restaurantOrder?: IRestaurantOrder;

  protected restaurantOrderService = inject(RestaurantOrderService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.restaurantOrderService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
