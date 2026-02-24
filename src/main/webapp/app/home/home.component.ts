import { Component } from '@angular/core';

import { HeroSectionComponent } from './hero-section/hero-section.component';
import { FeaturesSectionComponent } from './features-section/features-section.component';
import { RestaurantsPreviewComponent } from './restaurants-preview/restaurants-preview.component';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
  imports: [HeroSectionComponent, FeaturesSectionComponent, RestaurantsPreviewComponent],
})
export default class HomeComponent {}
