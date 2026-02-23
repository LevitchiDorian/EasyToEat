import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IMenuItemOption } from '../menu-item-option.model';

@Component({
  selector: 'jhi-menu-item-option-detail',
  templateUrl: './menu-item-option-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class MenuItemOptionDetailComponent {
  menuItemOption = input<IMenuItemOption | null>(null);

  previousState(): void {
    window.history.back();
  }
}
