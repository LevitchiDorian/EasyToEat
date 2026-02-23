import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { OrderItemOptionSelectionDetailComponent } from './order-item-option-selection-detail.component';

describe('OrderItemOptionSelection Management Detail Component', () => {
  let comp: OrderItemOptionSelectionDetailComponent;
  let fixture: ComponentFixture<OrderItemOptionSelectionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderItemOptionSelectionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () =>
                import('./order-item-option-selection-detail.component').then(m => m.OrderItemOptionSelectionDetailComponent),
              resolve: { orderItemOptionSelection: () => of({ id: 17155 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(OrderItemOptionSelectionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrderItemOptionSelectionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load orderItemOptionSelection on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', OrderItemOptionSelectionDetailComponent);

      // THEN
      expect(instance.orderItemOptionSelection()).toEqual(expect.objectContaining({ id: 17155 }));
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
