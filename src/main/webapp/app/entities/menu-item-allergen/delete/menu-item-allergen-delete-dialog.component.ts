import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMenuItemAllergen } from '../menu-item-allergen.model';
import { MenuItemAllergenService } from '../service/menu-item-allergen.service';

@Component({
  templateUrl: './menu-item-allergen-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MenuItemAllergenDeleteDialogComponent {
  menuItemAllergen?: IMenuItemAllergen;

  protected menuItemAllergenService = inject(MenuItemAllergenService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.menuItemAllergenService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
