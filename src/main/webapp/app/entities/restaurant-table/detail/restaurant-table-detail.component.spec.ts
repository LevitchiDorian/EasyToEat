import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { RestaurantTableDetailComponent } from './restaurant-table-detail.component';

describe('RestaurantTable Management Detail Component', () => {
  let comp: RestaurantTableDetailComponent;
  let fixture: ComponentFixture<RestaurantTableDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RestaurantTableDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./restaurant-table-detail.component').then(m => m.RestaurantTableDetailComponent),
              resolve: { restaurantTable: () => of({ id: 23892 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RestaurantTableDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RestaurantTableDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load restaurantTable on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RestaurantTableDetailComponent);

      // THEN
      expect(instance.restaurantTable()).toEqual(expect.objectContaining({ id: 23892 }));
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
