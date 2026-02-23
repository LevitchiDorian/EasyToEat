import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IMenuItem } from '../menu-item.model';
import { MenuItemService } from '../service/menu-item.service';

@Component({
  templateUrl: './menu-item-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class MenuItemDeleteDialogComponent {
  menuItem?: IMenuItem;

  protected menuItemService = inject(MenuItemService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.menuItemService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
