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

describe('RestaurantOrder e2e test', () => {
  const restaurantOrderPageUrl = '/restaurant-order';
  const restaurantOrderPageUrlPattern = new RegExp('/restaurant-order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const restaurantOrderSample = {
    orderCode: 'traffic',
    status: 'CANCELLED',
    isPreOrder: false,
    subtotal: 16516.22,
    totalAmount: 10459.14,
    createdAt: '2026-02-23T08:45:06.795Z',
  };

  let restaurantOrder;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/restaurant-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/restaurant-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/restaurant-orders/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (restaurantOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/restaurant-orders/${restaurantOrder.id}`,
      }).then(() => {
        restaurantOrder = undefined;
      });
    }
  });

  it('RestaurantOrders menu should load RestaurantOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('restaurant-order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RestaurantOrder').should('exist');
    cy.url().should('match', restaurantOrderPageUrlPattern);
  });

  describe('RestaurantOrder page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(restaurantOrderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create RestaurantOrder page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/restaurant-order/new$'));
        cy.getEntityCreateUpdateHeading('RestaurantOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurantOrderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/restaurant-orders',
          body: restaurantOrderSample,
        }).then(({ body }) => {
          restaurantOrder = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/restaurant-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/restaurant-orders?page=0&size=20>; rel="last",<http://localhost/api/restaurant-orders?page=0&size=20>; rel="first"',
              },
              body: [restaurantOrder],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(restaurantOrderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details RestaurantOrder page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('restaurantOrder');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurantOrderPageUrlPattern);
      });

      it('edit button click should load edit RestaurantOrder page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RestaurantOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurantOrderPageUrlPattern);
      });

      it('edit button click should load edit RestaurantOrder page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RestaurantOrder');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurantOrderPageUrlPattern);
      });

      it('last delete button click should delete instance of RestaurantOrder', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('restaurantOrder').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurantOrderPageUrlPattern);

        restaurantOrder = undefined;
      });
    });
  });

  describe('new RestaurantOrder page', () => {
    beforeEach(() => {
      cy.visit(`${restaurantOrderPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('RestaurantOrder');
    });

    it('should create an instance of RestaurantOrder', () => {
      cy.get(`[data-cy="orderCode"]`).type('phrase kaleidoscopic');
      cy.get(`[data-cy="orderCode"]`).should('have.value', 'phrase kaleidoscopic');

      cy.get(`[data-cy="status"]`).select('SERVED');

      cy.get(`[data-cy="isPreOrder"]`).should('not.be.checked');
      cy.get(`[data-cy="isPreOrder"]`).click();
      cy.get(`[data-cy="isPreOrder"]`).should('be.checked');

      cy.get(`[data-cy="scheduledFor"]`).type('2026-02-23T11:10');
      cy.get(`[data-cy="scheduledFor"]`).blur();
      cy.get(`[data-cy="scheduledFor"]`).should('have.value', '2026-02-23T11:10');

      cy.get(`[data-cy="subtotal"]`).type('10597.06');
      cy.get(`[data-cy="subtotal"]`).should('have.value', '10597.06');

      cy.get(`[data-cy="discountAmount"]`).type('15438.01');
      cy.get(`[data-cy="discountAmount"]`).should('have.value', '15438.01');

      cy.get(`[data-cy="taxAmount"]`).type('29099.23');
      cy.get(`[data-cy="taxAmount"]`).should('have.value', '29099.23');

      cy.get(`[data-cy="totalAmount"]`).type('3162.58');
      cy.get(`[data-cy="totalAmount"]`).should('have.value', '3162.58');

      cy.get(`[data-cy="specialInstructions"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="specialInstructions"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="estimatedReadyTime"]`).type('2026-02-23T05:47');
      cy.get(`[data-cy="estimatedReadyTime"]`).blur();
      cy.get(`[data-cy="estimatedReadyTime"]`).should('have.value', '2026-02-23T05:47');

      cy.get(`[data-cy="confirmedAt"]`).type('2026-02-23T11:42');
      cy.get(`[data-cy="confirmedAt"]`).blur();
      cy.get(`[data-cy="confirmedAt"]`).should('have.value', '2026-02-23T11:42');

      cy.get(`[data-cy="completedAt"]`).type('2026-02-23T15:17');
      cy.get(`[data-cy="completedAt"]`).blur();
      cy.get(`[data-cy="completedAt"]`).should('have.value', '2026-02-23T15:17');

      cy.get(`[data-cy="createdAt"]`).type('2026-02-23T07:59');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2026-02-23T07:59');

      cy.get(`[data-cy="updatedAt"]`).type('2026-02-23T09:48');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2026-02-23T09:48');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        restaurantOrder = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', restaurantOrderPageUrlPattern);
    });
  });
});
