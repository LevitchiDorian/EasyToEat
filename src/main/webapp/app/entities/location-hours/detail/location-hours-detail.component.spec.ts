import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { LocationHoursDetailComponent } from './location-hours-detail.component';

describe('LocationHours Management Detail Component', () => {
  let comp: LocationHoursDetailComponent;
  let fixture: ComponentFixture<LocationHoursDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocationHoursDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./location-hours-detail.component').then(m => m.LocationHoursDetailComponent),
              resolve: { locationHours: () => of({ id: 18708 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(LocationHoursDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationHoursDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load locationHours on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LocationHoursDetailComponent);

      // THEN
      expect(instance.locationHours()).toEqual(expect.objectContaining({ id: 18708 }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
