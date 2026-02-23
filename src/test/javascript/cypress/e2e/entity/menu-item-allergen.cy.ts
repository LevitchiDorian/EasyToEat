import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('MenuItemAllergen e2e test', () => {
  const menuItemAllergenPageUrl = '/menu-item-allergen';
  const menuItemAllergenPageUrlPattern = new RegExp('/menu-item-allergen(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const menuItemAllergenSample = {"allergen":"DAIRY"};

  let menuItemAllergen;
  // let menuItem;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/menu-items',
      body: {"name":"eyebrow","description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","price":7571.15,"discountedPrice":27270.19,"preparationTimeMinutes":42,"calories":20583,"imageUrl":"subsidy minus consequently","isAvailable":true,"isFeatured":false,"isVegetarian":false,"isVegan":false,"isGlutenFree":false,"spicyLevel":0,"displayOrder":26886},
    }).then(({ body }) => {
      menuItem = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/menu-item-allergens+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/menu-item-allergens').as('postEntityRequest');
    cy.intercept('DELETE', '/api/menu-item-allergens/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/menu-items', {
      statusCode: 200,
      body: [menuItem],
    });

  });
   */

  afterEach(() => {
    if (menuItemAllergen) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/menu-item-allergens/${menuItemAllergen.id}`,
      }).then(() => {
        menuItemAllergen = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (menuItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/menu-items/${menuItem.id}`,
      }).then(() => {
        menuItem = undefined;
      });
    }
  });
   */

  it('MenuItemAllergens menu should load MenuItemAllergens page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('menu-item-allergen');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MenuItemAllergen').should('exist');
    cy.url().should('match', menuItemAllergenPageUrlPattern);
  });

  describe('MenuItemAllergen page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(menuItemAllergenPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MenuItemAllergen page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/menu-item-allergen/new$'));
        cy.getEntityCreateUpdateHeading('MenuItemAllergen');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemAllergenPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/menu-item-allergens',
          body: {
            ...menuItemAllergenSample,
            menuItem: menuItem,
          },
        }).then(({ body }) => {
          menuItemAllergen = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/menu-item-allergens+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [menuItemAllergen],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(menuItemAllergenPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(menuItemAllergenPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details MenuItemAllergen page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('menuItemAllergen');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemAllergenPageUrlPattern);
      });

      it('edit button click should load edit MenuItemAllergen page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MenuItemAllergen');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemAllergenPageUrlPattern);
      });

      it('edit button click should load edit MenuItemAllergen page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MenuItemAllergen');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemAllergenPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of MenuItemAllergen', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('menuItemAllergen').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemAllergenPageUrlPattern);

        menuItemAllergen = undefined;
      });
    });
  });

  describe('new MenuItemAllergen page', () => {
    beforeEach(() => {
      cy.visit(`${menuItemAllergenPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MenuItemAllergen');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of MenuItemAllergen', () => {
      cy.get(`[data-cy="allergen"]`).select('EGGS');

      cy.get(`[data-cy="notes"]`).type('than unrealistic besides');
      cy.get(`[data-cy="notes"]`).should('have.value', 'than unrealistic besides');

      cy.get(`[data-cy="menuItem"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        menuItemAllergen = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', menuItemAllergenPageUrlPattern);
    });
  });
});
