import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MenuItemOptionValueDetailComponent } from './menu-item-option-value-detail.component';

describe('MenuItemOptionValue Management Detail Component', () => {
  let comp: MenuItemOptionValueDetailComponent;
  let fixture: ComponentFixture<MenuItemOptionValueDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuItemOptionValueDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./menu-item-option-value-detail.component').then(m => m.MenuItemOptionValueDetailComponent),
              resolve: { menuItemOptionValue: () => of({ id: 31564 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MenuItemOptionValueDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuItemOptionValueDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load menuItemOptionValue on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MenuItemOptionValueDetailComponent);

      // THEN
      expect(instance.menuItemOptionValue()).toEqual(expect.objectContaining({ id: 31564 }));
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
