import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMenuCategory } from '../menu-category.model';
import { MenuCategoryService } from '../service/menu-category.service';

@Component({
  templateUrl: './menu-category-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MenuCategoryDeleteDialogComponent {
  menuCategory?: IMenuCategory;

  protected menuCategoryService = inject(MenuCategoryService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.menuCategoryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
