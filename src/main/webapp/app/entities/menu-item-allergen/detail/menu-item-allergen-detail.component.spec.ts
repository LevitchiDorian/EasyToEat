import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { MenuItemAllergenDetailComponent } from './menu-item-allergen-detail.component';

describe('MenuItemAllergen Management Detail Component', () => {
  let comp: MenuItemAllergenDetailComponent;
  let fixture: ComponentFixture<MenuItemAllergenDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MenuItemAllergenDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./menu-item-allergen-detail.component').then(m => m.MenuItemAllergenDetailComponent),
              resolve: { menuItemAllergen: () => of({ id: 13353 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(MenuItemAllergenDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuItemAllergenDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load menuItemAllergen on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', MenuItemAllergenDetailComponent);

      // THEN
      expect(instance.menuItemAllergen()).toEqual(expect.objectContaining({ id: 13353 }));
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
