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

describe('RestaurantTable e2e test', () => {
  const restaurantTablePageUrl = '/restaurant-table';
  const restaurantTablePageUrlPattern = new RegExp('/restaurant-table(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const restaurantTableSample = {"tableNumber":"apropos mmm","shape":"SQUARE","minCapacity":9252,"maxCapacity":7446,"status":"OCCUPIED","isActive":false};

  let restaurantTable;
  // let diningRoom;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/dining-rooms',
      body: {"name":"abaft loudly","description":"quart brr","floor":21853,"capacity":12737,"isActive":false,"floorPlanUrl":"out","widthPx":29607.85,"heightPx":31205.63},
    }).then(({ body }) => {
      diningRoom = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/restaurant-tables+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/restaurant-tables').as('postEntityRequest');
    cy.intercept('DELETE', '/api/restaurant-tables/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/dining-rooms', {
      statusCode: 200,
      body: [diningRoom],
    });

  });
   */

  afterEach(() => {
    if (restaurantTable) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/restaurant-tables/${restaurantTable.id}`,
      }).then(() => {
        restaurantTable = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (diningRoom) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/dining-rooms/${diningRoom.id}`,
      }).then(() => {
        diningRoom = undefined;
      });
    }
  });
   */

  it('RestaurantTables menu should load RestaurantTables page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('restaurant-table');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RestaurantTable').should('exist');
    cy.url().should('match', restaurantTablePageUrlPattern);
  });

  describe('RestaurantTable page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(restaurantTablePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create RestaurantTable page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/restaurant-table/new$'));
        cy.getEntityCreateUpdateHeading('RestaurantTable');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurantTablePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/restaurant-tables',
          body: {
            ...restaurantTableSample,
            room: diningRoom,
          },
        }).then(({ body }) => {
          restaurantTable = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/restaurant-tables+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/restaurant-tables?page=0&size=20>; rel="last",<http://localhost/api/restaurant-tables?page=0&size=20>; rel="first"',
              },
              body: [restaurantTable],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(restaurantTablePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(restaurantTablePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details RestaurantTable page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('restaurantTable');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurantTablePageUrlPattern);
      });

      it('edit button click should load edit RestaurantTable page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RestaurantTable');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurantTablePageUrlPattern);
      });

      it('edit button click should load edit RestaurantTable page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('RestaurantTable');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurantTablePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of RestaurantTable', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('restaurantTable').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', restaurantTablePageUrlPattern);

        restaurantTable = undefined;
      });
    });
  });

  describe('new RestaurantTable page', () => {
    beforeEach(() => {
      cy.visit(`${restaurantTablePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('RestaurantTable');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of RestaurantTable', () => {
      cy.get(`[data-cy="tableNumber"]`).type('strictly');
      cy.get(`[data-cy="tableNumber"]`).should('have.value', 'strictly');

      cy.get(`[data-cy="shape"]`).select('RECTANGLE');

      cy.get(`[data-cy="minCapacity"]`).type('16174');
      cy.get(`[data-cy="minCapacity"]`).should('have.value', '16174');

      cy.get(`[data-cy="maxCapacity"]`).type('6744');
      cy.get(`[data-cy="maxCapacity"]`).should('have.value', '6744');

      cy.get(`[data-cy="positionX"]`).type('25399.46');
      cy.get(`[data-cy="positionX"]`).should('have.value', '25399.46');

      cy.get(`[data-cy="positionY"]`).type('30220.67');
      cy.get(`[data-cy="positionY"]`).should('have.value', '30220.67');

      cy.get(`[data-cy="widthPx"]`).type('25085.9');
      cy.get(`[data-cy="widthPx"]`).should('have.value', '25085.9');

      cy.get(`[data-cy="heightPx"]`).type('10837.37');
      cy.get(`[data-cy="heightPx"]`).should('have.value', '10837.37');

      cy.get(`[data-cy="rotation"]`).type('4863.94');
      cy.get(`[data-cy="rotation"]`).should('have.value', '4863.94');

      cy.get(`[data-cy="status"]`).select('AVAILABLE');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="notes"]`).type('bleak onto amid');
      cy.get(`[data-cy="notes"]`).should('have.value', 'bleak onto amid');

      cy.get(`[data-cy="room"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        restaurantTable = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', restaurantTablePageUrlPattern);
    });
  });
});
