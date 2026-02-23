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

describe('MenuItemOption e2e test', () => {
  const menuItemOptionPageUrl = '/menu-item-option';
  const menuItemOptionPageUrlPattern = new RegExp('/menu-item-option(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const menuItemOptionSample = {"name":"speedy","isRequired":false};

  let menuItemOption;
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
      body: {"name":"though circa","description":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=","price":32225.38,"discountedPrice":24621.36,"preparationTimeMinutes":0,"calories":2200,"imageUrl":"for tenderly mutate","isAvailable":true,"isFeatured":false,"isVegetarian":true,"isVegan":false,"isGlutenFree":false,"spicyLevel":2,"displayOrder":31493},
    }).then(({ body }) => {
      menuItem = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/menu-item-options+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/menu-item-options').as('postEntityRequest');
    cy.intercept('DELETE', '/api/menu-item-options/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/menu-item-option-values', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/menu-items', {
      statusCode: 200,
      body: [menuItem],
    });

  });
   */

  afterEach(() => {
    if (menuItemOption) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/menu-item-options/${menuItemOption.id}`,
      }).then(() => {
        menuItemOption = undefined;
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

  it('MenuItemOptions menu should load MenuItemOptions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('menu-item-option');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MenuItemOption').should('exist');
    cy.url().should('match', menuItemOptionPageUrlPattern);
  });

  describe('MenuItemOption page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(menuItemOptionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MenuItemOption page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/menu-item-option/new$'));
        cy.getEntityCreateUpdateHeading('MenuItemOption');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemOptionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/menu-item-options',
          body: {
            ...menuItemOptionSample,
            menuItem: menuItem,
          },
        }).then(({ body }) => {
          menuItemOption = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/menu-item-options+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [menuItemOption],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(menuItemOptionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(menuItemOptionPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details MenuItemOption page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('menuItemOption');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemOptionPageUrlPattern);
      });

      it('edit button click should load edit MenuItemOption page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MenuItemOption');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemOptionPageUrlPattern);
      });

      it('edit button click should load edit MenuItemOption page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MenuItemOption');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemOptionPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of MenuItemOption', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('menuItemOption').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemOptionPageUrlPattern);

        menuItemOption = undefined;
      });
    });
  });

  describe('new MenuItemOption page', () => {
    beforeEach(() => {
      cy.visit(`${menuItemOptionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MenuItemOption');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of MenuItemOption', () => {
      cy.get(`[data-cy="name"]`).type('drat since video');
      cy.get(`[data-cy="name"]`).should('have.value', 'drat since video');

      cy.get(`[data-cy="isRequired"]`).should('not.be.checked');
      cy.get(`[data-cy="isRequired"]`).click();
      cy.get(`[data-cy="isRequired"]`).should('be.checked');

      cy.get(`[data-cy="maxSelections"]`).type('24640');
      cy.get(`[data-cy="maxSelections"]`).should('have.value', '24640');

      cy.get(`[data-cy="displayOrder"]`).type('19991');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '19991');

      cy.get(`[data-cy="menuItem"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        menuItemOption = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', menuItemOptionPageUrlPattern);
    });
  });
});
