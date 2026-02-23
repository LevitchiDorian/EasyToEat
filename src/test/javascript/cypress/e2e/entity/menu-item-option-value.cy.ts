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

describe('MenuItemOptionValue e2e test', () => {
  const menuItemOptionValuePageUrl = '/menu-item-option-value';
  const menuItemOptionValuePageUrlPattern = new RegExp('/menu-item-option-value(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const menuItemOptionValueSample = {"label":"extension duh colorfully","priceAdjustment":30826.86,"isDefault":true,"isAvailable":false};

  let menuItemOptionValue;
  // let menuItemOption;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/menu-item-options',
      body: {"name":"bulky","isRequired":true,"maxSelections":31426,"displayOrder":31591},
    }).then(({ body }) => {
      menuItemOption = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/menu-item-option-values+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/menu-item-option-values').as('postEntityRequest');
    cy.intercept('DELETE', '/api/menu-item-option-values/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/menu-item-options', {
      statusCode: 200,
      body: [menuItemOption],
    });

  });
   */

  afterEach(() => {
    if (menuItemOptionValue) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/menu-item-option-values/${menuItemOptionValue.id}`,
      }).then(() => {
        menuItemOptionValue = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('MenuItemOptionValues menu should load MenuItemOptionValues page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('menu-item-option-value');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MenuItemOptionValue').should('exist');
    cy.url().should('match', menuItemOptionValuePageUrlPattern);
  });

  describe('MenuItemOptionValue page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(menuItemOptionValuePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MenuItemOptionValue page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/menu-item-option-value/new$'));
        cy.getEntityCreateUpdateHeading('MenuItemOptionValue');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemOptionValuePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/menu-item-option-values',
          body: {
            ...menuItemOptionValueSample,
            option: menuItemOption,
          },
        }).then(({ body }) => {
          menuItemOptionValue = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/menu-item-option-values+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [menuItemOptionValue],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(menuItemOptionValuePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(menuItemOptionValuePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details MenuItemOptionValue page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('menuItemOptionValue');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemOptionValuePageUrlPattern);
      });

      it('edit button click should load edit MenuItemOptionValue page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MenuItemOptionValue');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemOptionValuePageUrlPattern);
      });

      it('edit button click should load edit MenuItemOptionValue page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MenuItemOptionValue');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemOptionValuePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of MenuItemOptionValue', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('menuItemOptionValue').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemOptionValuePageUrlPattern);

        menuItemOptionValue = undefined;
      });
    });
  });

  describe('new MenuItemOptionValue page', () => {
    beforeEach(() => {
      cy.visit(`${menuItemOptionValuePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MenuItemOptionValue');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of MenuItemOptionValue', () => {
      cy.get(`[data-cy="label"]`).type('suddenly');
      cy.get(`[data-cy="label"]`).should('have.value', 'suddenly');

      cy.get(`[data-cy="priceAdjustment"]`).type('29810.41');
      cy.get(`[data-cy="priceAdjustment"]`).should('have.value', '29810.41');

      cy.get(`[data-cy="isDefault"]`).should('not.be.checked');
      cy.get(`[data-cy="isDefault"]`).click();
      cy.get(`[data-cy="isDefault"]`).should('be.checked');

      cy.get(`[data-cy="isAvailable"]`).should('not.be.checked');
      cy.get(`[data-cy="isAvailable"]`).click();
      cy.get(`[data-cy="isAvailable"]`).should('be.checked');

      cy.get(`[data-cy="displayOrder"]`).type('17272');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '17272');

      cy.get(`[data-cy="option"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        menuItemOptionValue = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', menuItemOptionValuePageUrlPattern);
    });
  });
});
