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

describe('MenuItem e2e test', () => {
  const menuItemPageUrl = '/menu-item';
  const menuItemPageUrlPattern = new RegExp('/menu-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const menuItemSample = {"name":"char oxygenate","price":19282.81,"isAvailable":false,"isFeatured":false,"displayOrder":32071};

  let menuItem;
  // let menuCategory;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/menu-categories',
      body: {"name":"entice sternly immaculate","description":"ah unearth","imageUrl":"hence oof","displayOrder":18181,"isActive":true},
    }).then(({ body }) => {
      menuCategory = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/menu-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/menu-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/menu-items/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/menu-item-allergens', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/menu-item-options', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/menu-categories', {
      statusCode: 200,
      body: [menuCategory],
    });

  });
   */

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

  /* Disabled due to incompatibility
  afterEach(() => {
    if (menuCategory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/menu-categories/${menuCategory.id}`,
      }).then(() => {
        menuCategory = undefined;
      });
    }
  });
   */

  it('MenuItems menu should load MenuItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('menu-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MenuItem').should('exist');
    cy.url().should('match', menuItemPageUrlPattern);
  });

  describe('MenuItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(menuItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MenuItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/menu-item/new$'));
        cy.getEntityCreateUpdateHeading('MenuItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/menu-items',
          body: {
            ...menuItemSample,
            category: menuCategory,
          },
        }).then(({ body }) => {
          menuItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/menu-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/menu-items?page=0&size=20>; rel="last",<http://localhost/api/menu-items?page=0&size=20>; rel="first"',
              },
              body: [menuItem],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(menuItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(menuItemPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details MenuItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('menuItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemPageUrlPattern);
      });

      it('edit button click should load edit MenuItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MenuItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemPageUrlPattern);
      });

      it('edit button click should load edit MenuItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MenuItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of MenuItem', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('menuItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuItemPageUrlPattern);

        menuItem = undefined;
      });
    });
  });

  describe('new MenuItem page', () => {
    beforeEach(() => {
      cy.visit(`${menuItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MenuItem');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of MenuItem', () => {
      cy.get(`[data-cy="name"]`).type('elevation uselessly');
      cy.get(`[data-cy="name"]`).should('have.value', 'elevation uselessly');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="price"]`).type('8385.99');
      cy.get(`[data-cy="price"]`).should('have.value', '8385.99');

      cy.get(`[data-cy="discountedPrice"]`).type('15583.22');
      cy.get(`[data-cy="discountedPrice"]`).should('have.value', '15583.22');

      cy.get(`[data-cy="preparationTimeMinutes"]`).type('45');
      cy.get(`[data-cy="preparationTimeMinutes"]`).should('have.value', '45');

      cy.get(`[data-cy="calories"]`).type('17505');
      cy.get(`[data-cy="calories"]`).should('have.value', '17505');

      cy.get(`[data-cy="imageUrl"]`).type('hmph');
      cy.get(`[data-cy="imageUrl"]`).should('have.value', 'hmph');

      cy.get(`[data-cy="isAvailable"]`).should('not.be.checked');
      cy.get(`[data-cy="isAvailable"]`).click();
      cy.get(`[data-cy="isAvailable"]`).should('be.checked');

      cy.get(`[data-cy="isFeatured"]`).should('not.be.checked');
      cy.get(`[data-cy="isFeatured"]`).click();
      cy.get(`[data-cy="isFeatured"]`).should('be.checked');

      cy.get(`[data-cy="isVegetarian"]`).should('not.be.checked');
      cy.get(`[data-cy="isVegetarian"]`).click();
      cy.get(`[data-cy="isVegetarian"]`).should('be.checked');

      cy.get(`[data-cy="isVegan"]`).should('not.be.checked');
      cy.get(`[data-cy="isVegan"]`).click();
      cy.get(`[data-cy="isVegan"]`).should('be.checked');

      cy.get(`[data-cy="isGlutenFree"]`).should('not.be.checked');
      cy.get(`[data-cy="isGlutenFree"]`).click();
      cy.get(`[data-cy="isGlutenFree"]`).should('be.checked');

      cy.get(`[data-cy="spicyLevel"]`).type('0');
      cy.get(`[data-cy="spicyLevel"]`).should('have.value', '0');

      cy.get(`[data-cy="displayOrder"]`).type('29815');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '29815');

      cy.get(`[data-cy="category"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        menuItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', menuItemPageUrlPattern);
    });
  });
});
