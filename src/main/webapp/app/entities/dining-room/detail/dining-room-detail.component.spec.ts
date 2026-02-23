import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DiningRoomDetailComponent } from './dining-room-detail.component';

describe('DiningRoom Management Detail Component', () => {
  let comp: DiningRoomDetailComponent;
  let fixture: ComponentFixture<DiningRoomDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DiningRoomDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./dining-room-detail.component').then(m => m.DiningRoomDetailComponent),
              resolve: { diningRoom: () => of({ id: 3993 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DiningRoomDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DiningRoomDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load diningRoom on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DiningRoomDetailComponent);

      // THEN
      expect(instance.diningRoom()).toEqual(expect.objectContaining({ id: 3993 }));
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
