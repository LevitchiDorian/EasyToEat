import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IMenuItemAllergen } from '../menu-item-allergen.model';

@Component({
  selector: 'jhi-menu-item-allergen-detail',
  templateUrl: './menu-item-allergen-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class MenuItemAllergenDetailComponent {
  menuItemAllergen = input<IMenuItemAllergen | null>(null);

  previousState(): void {
    window.history.back();
  }
}
