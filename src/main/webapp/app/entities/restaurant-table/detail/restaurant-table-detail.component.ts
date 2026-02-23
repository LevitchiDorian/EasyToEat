import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IRestaurantTable } from '../restaurant-table.model';

@Component({
  selector: 'jhi-restaurant-table-detail',
  templateUrl: './restaurant-table-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class RestaurantTableDetailComponent {
  restaurantTable = input<IRestaurantTable | null>(null);

  previousState(): void {
    window.history.back();
  }
}
