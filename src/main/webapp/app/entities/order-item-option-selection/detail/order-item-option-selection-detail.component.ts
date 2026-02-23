import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IOrderItemOptionSelection } from '../order-item-option-selection.model';

@Component({
  selector: 'jhi-order-item-option-selection-detail',
  templateUrl: './order-item-option-selection-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class OrderItemOptionSelectionDetailComponent {
  orderItemOptionSelection = input<IOrderItemOptionSelection | null>(null);

  previousState(): void {
    window.history.back();
  }
}
