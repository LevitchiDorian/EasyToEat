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

describe('OrderItem e2e test', () => {
  const orderItemPageUrl = '/order-item';
  const orderItemPageUrlPattern = new RegExp('/order-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const orderItemSample = { quantity: 24055, unitPrice: 9076.39, totalPrice: 20659.01, status: 'CANCELLED' };

  let orderItem;
  let restaurantOrder;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/restaurant-orders',
      body: {
        orderCode: 'indeed',
        status: 'PREPARING',
        isPreOrder: false,
        scheduledFor: '2026-02-23T03:19:21.261Z',
        subtotal: 25829.48,
        discountAmount: 27216.57,
        taxAmount: 1253.52,
        totalAmount: 2243.31,
        specialInstructions: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        estimatedReadyTime: '2026-02-23T08:25:54.277Z',
        confirmedAt: '2026-02-23T11:19:25.175Z',
        completedAt: '2026-02-23T02:30:49.841Z',
        createdAt: '2026-02-23T08:34:02.645Z',
        updatedAt: '2026-02-23T07:31:59.252Z',
      },
    }).then(({ body }) => {
      restaurantOrder = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/order-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/order-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/order-items/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/order-item-option-selections', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/menu-items', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/restaurant-orders', {
      statusCode: 200,
      body: [restaurantOrder],
    });
  });

  afterEach(() => {
    if (orderItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/order-items/${orderItem.id}`,
      }).then(() => {
        orderItem = undefined;
      });
    }
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

  it('OrderItems menu should load OrderItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('order-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('OrderItem').should('exist');
    cy.url().should('match', orderItemPageUrlPattern);
  });

  describe('OrderItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(orderItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create OrderItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/order-item/new$'));
        cy.getEntityCreateUpdateHeading('OrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/order-items',
          body: {
            ...orderItemSample,
            order: restaurantOrder,
          },
        }).then(({ body }) => {
          orderItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/order-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/order-items?page=0&size=20>; rel="last",<http://localhost/api/order-items?page=0&size=20>; rel="first"',
              },
              body: [orderItem],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(orderItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details OrderItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('orderItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });

      it('edit button click should load edit OrderItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });

      it('edit button click should load edit OrderItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);
      });

      it('last delete button click should delete instance of OrderItem', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('orderItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemPageUrlPattern);

        orderItem = undefined;
      });
    });
  });

  describe('new OrderItem page', () => {
    beforeEach(() => {
      cy.visit(`${orderItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('OrderItem');
    });

    it('should create an instance of OrderItem', () => {
      cy.get(`[data-cy="quantity"]`).type('5477');
      cy.get(`[data-cy="quantity"]`).should('have.value', '5477');

      cy.get(`[data-cy="unitPrice"]`).type('4519.52');
      cy.get(`[data-cy="unitPrice"]`).should('have.value', '4519.52');

      cy.get(`[data-cy="totalPrice"]`).type('4091.59');
      cy.get(`[data-cy="totalPrice"]`).should('have.value', '4091.59');

      cy.get(`[data-cy="status"]`).select('PREPARING');

      cy.get(`[data-cy="specialInstructions"]`).type('teeming pry soon');
      cy.get(`[data-cy="specialInstructions"]`).should('have.value', 'teeming pry soon');

      cy.get(`[data-cy="notes"]`).type('oddly round uh-huh');
      cy.get(`[data-cy="notes"]`).should('have.value', 'oddly round uh-huh');

      cy.get(`[data-cy="order"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        orderItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', orderItemPageUrlPattern);
    });
  });
});
