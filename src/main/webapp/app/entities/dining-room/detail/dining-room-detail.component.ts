import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IDiningRoom } from '../dining-room.model';

@Component({
  selector: 'jhi-dining-room-detail',
  templateUrl: './dining-room-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class DiningRoomDetailComponent {
  diningRoom = input<IDiningRoom | null>(null);

  previousState(): void {
    window.history.back();
  }
}
