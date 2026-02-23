import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IMenuItemOptionValue } from '../menu-item-option-value.model';

@Component({
  selector: 'jhi-menu-item-option-value-detail',
  templateUrl: './menu-item-option-value-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class MenuItemOptionValueDetailComponent {
  menuItemOptionValue = input<IMenuItemOptionValue | null>(null);

  previousState(): void {
    window.history.back();
  }
}
