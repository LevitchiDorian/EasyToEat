import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { ILocationMenuItemOverride } from '../location-menu-item-override.model';

@Component({
  selector: 'jhi-location-menu-item-override-detail',
  templateUrl: './location-menu-item-override-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class LocationMenuItemOverrideDetailComponent {
  locationMenuItemOverride = input<ILocationMenuItemOverride | null>(null);

  previousState(): void {
    window.history.back();
  }
}
