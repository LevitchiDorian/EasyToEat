import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IRestaurantTable } from '../restaurant-table.model';
import { RestaurantTableService } from '../service/restaurant-table.service';

@Component({
  templateUrl: './restaurant-table-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class RestaurantTableDeleteDialogComponent {
  restaurantTable?: IRestaurantTable;

  protected restaurantTableService = inject(RestaurantTableService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.restaurantTableService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
