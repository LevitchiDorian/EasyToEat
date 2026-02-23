import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ILocationHours } from '../location-hours.model';

@Component({
  selector: 'jhi-location-hours-detail',
  templateUrl: './location-hours-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class LocationHoursDetailComponent {
  locationHours = input<ILocationHours | null>(null);

  previousState(): void {
    window.history.back();
  }
}
