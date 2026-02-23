import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { LocationMenuItemOverrideDetailComponent } from './location-menu-item-override-detail.component';

describe('LocationMenuItemOverride Management Detail Component', () => {
  let comp: LocationMenuItemOverrideDetailComponent;
  let fixture: ComponentFixture<LocationMenuItemOverrideDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocationMenuItemOverrideDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () =>
                import('./location-menu-item-override-detail.component').then(m => m.LocationMenuItemOverrideDetailComponent),
              resolve: { locationMenuItemOverride: () => of({ id: 27299 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(LocationMenuItemOverrideDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LocationMenuItemOverrideDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load locationMenuItemOverride on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LocationMenuItemOverrideDetailComponent);

      // THEN
      expect(instance.locationMenuItemOverride()).toEqual(expect.objectContaining({ id: 27299 }));
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
