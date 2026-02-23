import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMenuItemOption } from '../menu-item-option.model';
import { MenuItemOptionService } from '../service/menu-item-option.service';

@Component({
  templateUrl: './menu-item-option-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MenuItemOptionDeleteDialogComponent {
  menuItemOption?: IMenuItemOption;

  protected menuItemOptionService = inject(MenuItemOptionService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.menuItemOptionService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
