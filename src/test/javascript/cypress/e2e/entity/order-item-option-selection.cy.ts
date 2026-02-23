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

describe('OrderItemOptionSelection e2e test', () => {
  const orderItemOptionSelectionPageUrl = '/order-item-option-selection';
  const orderItemOptionSelectionPageUrlPattern = new RegExp('/order-item-option-selection(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const orderItemOptionSelectionSample = {"unitPrice":19642.67};

  let orderItemOptionSelection;
  // let orderItem;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/order-items',
      body: {"quantity":5007,"unitPrice":21697.08,"totalPrice":20532.34,"status":"PENDING","specialInstructions":"upside-down chilly evenly","notes":"per gosh eek"},
    }).then(({ body }) => {
      orderItem = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/order-item-option-selections+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/order-item-option-selections').as('postEntityRequest');
    cy.intercept('DELETE', '/api/order-item-option-selections/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/menu-item-option-values', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/order-items', {
      statusCode: 200,
      body: [orderItem],
    });

  });
   */

  afterEach(() => {
    if (orderItemOptionSelection) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/order-item-option-selections/${orderItemOptionSelection.id}`,
      }).then(() => {
        orderItemOptionSelection = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('OrderItemOptionSelections menu should load OrderItemOptionSelections page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('order-item-option-selection');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('OrderItemOptionSelection').should('exist');
    cy.url().should('match', orderItemOptionSelectionPageUrlPattern);
  });

  describe('OrderItemOptionSelection page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(orderItemOptionSelectionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create OrderItemOptionSelection page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/order-item-option-selection/new$'));
        cy.getEntityCreateUpdateHeading('OrderItemOptionSelection');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemOptionSelectionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/order-item-option-selections',
          body: {
            ...orderItemOptionSelectionSample,
            orderItem: orderItem,
          },
        }).then(({ body }) => {
          orderItemOptionSelection = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/order-item-option-selections+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [orderItemOptionSelection],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(orderItemOptionSelectionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(orderItemOptionSelectionPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details OrderItemOptionSelection page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('orderItemOptionSelection');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemOptionSelectionPageUrlPattern);
      });

      it('edit button click should load edit OrderItemOptionSelection page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderItemOptionSelection');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemOptionSelectionPageUrlPattern);
      });

      it('edit button click should load edit OrderItemOptionSelection page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('OrderItemOptionSelection');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemOptionSelectionPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of OrderItemOptionSelection', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('orderItemOptionSelection').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', orderItemOptionSelectionPageUrlPattern);

        orderItemOptionSelection = undefined;
      });
    });
  });

  describe('new OrderItemOptionSelection page', () => {
    beforeEach(() => {
      cy.visit(`${orderItemOptionSelectionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('OrderItemOptionSelection');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of OrderItemOptionSelection', () => {
      cy.get(`[data-cy="quantity"]`).type('24683');
      cy.get(`[data-cy="quantity"]`).should('have.value', '24683');

      cy.get(`[data-cy="unitPrice"]`).type('22055.76');
      cy.get(`[data-cy="unitPrice"]`).should('have.value', '22055.76');

      cy.get(`[data-cy="orderItem"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        orderItemOptionSelection = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', orderItemOptionSelectionPageUrlPattern);
    });
  });
});
