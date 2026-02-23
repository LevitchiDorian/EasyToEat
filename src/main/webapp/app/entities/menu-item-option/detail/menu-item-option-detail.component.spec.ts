import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MenuItemOptionDetailComponent } from './menu-item-option-detail.component';

describe('MenuItemOption Management Detail Component', () => {
  let comp: MenuItemOptionDetailComponent;
  let fixture: ComponentFixture<MenuItemOptionDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuItemOptionDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./menu-item-option-detail.component').then(m => m.MenuItemOptionDetailComponent),
              resolve: { menuItemOption: () => of({ id: 20913 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MenuItemOptionDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuItemOptionDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load menuItemOption on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MenuItemOptionDetailComponent);

      // THEN
      expect(instance.menuItemOption()).toEqual(expect.objectContaining({ id: 20913 }));
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
