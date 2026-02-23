import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMenuItemOptionValue } from '../menu-item-option-value.model';
import { MenuItemOptionValueService } from '../service/menu-item-option-value.service';

@Component({
  templateUrl: './menu-item-option-value-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MenuItemOptionValueDeleteDialogComponent {
  menuItemOptionValue?: IMenuItemOptionValue;

  protected menuItemOptionValueService = inject(MenuItemOptionValueService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.menuItemOptionValueService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
