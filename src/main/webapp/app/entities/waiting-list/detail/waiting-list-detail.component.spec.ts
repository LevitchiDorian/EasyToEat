import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { WaitingListDetailComponent } from './waiting-list-detail.component';

describe('WaitingList Management Detail Component', () => {
  let comp: WaitingListDetailComponent;
  let fixture: ComponentFixture<WaitingListDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WaitingListDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./waiting-list-detail.component').then(m => m.WaitingListDetailComponent),
              resolve: { waitingList: () => of({ id: 15410 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(WaitingListDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WaitingListDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load waitingList on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', WaitingListDetailComponent);

      // THEN
      expect(instance.waitingList()).toEqual(expect.objectContaining({ id: 15410 }));
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
