import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReservationTableDetailComponent } from './reservation-table-detail.component';

describe('ReservationTable Management Detail Component', () => {
  let comp: ReservationTableDetailComponent;
  let fixture: ComponentFixture<ReservationTableDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReservationTableDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./reservation-table-detail.component').then(m => m.ReservationTableDetailComponent),
              resolve: { reservationTable: () => of({ id: 202 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ReservationTableDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReservationTableDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load reservationTable on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ReservationTableDetailComponent);

      // THEN
      expect(instance.reservationTable()).toEqual(expect.objectContaining({ id: 202 }));
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
