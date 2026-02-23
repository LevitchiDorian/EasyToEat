import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IWaitingList } from '../waiting-list.model';

@Component({
  selector: 'jhi-waiting-list-detail',
  templateUrl: './waiting-list-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class WaitingListDetailComponent {
  waitingList = input<IWaitingList | null>(null);

  previousState(): void {
    window.history.back();
  }
}
