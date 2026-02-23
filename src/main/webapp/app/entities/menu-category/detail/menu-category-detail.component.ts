import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IMenuCategory } from '../menu-category.model';

@Component({
  selector: 'jhi-menu-category-detail',
  templateUrl: './menu-category-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class MenuCategoryDetailComponent {
  menuCategory = input<IMenuCategory | null>(null);

  previousState(): void {
    window.history.back();
  }
}
