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

describe('DiningRoom e2e test', () => {
  const diningRoomPageUrl = '/dining-room';
  const diningRoomPageUrlPattern = new RegExp('/dining-room(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const diningRoomSample = {"name":"carpool unabashedly","capacity":1886,"isActive":false};

  let diningRoom;
  // let location;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/locations',
      body: {"name":"knickers mmm floss","address":"athwart","city":"Lombard","phone":"(564) 988-0052 x4780","email":"Arnaldo79@gmail.com","latitude":9191.43,"longitude":19629.53,"reservationDurationOverride":135,"maxAdvanceBookingDaysOverride":105,"cancellationDeadlineOverride":11,"isActive":true,"createdAt":"2026-02-23T05:28:05.904Z"},
    }).then(({ body }) => {
      location = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/dining-rooms+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/dining-rooms').as('postEntityRequest');
    cy.intercept('DELETE', '/api/dining-rooms/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/restaurant-tables', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/locations', {
      statusCode: 200,
      body: [location],
    });

  });
   */

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

  /* Disabled due to incompatibility
  afterEach(() => {
    if (location) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/locations/${location.id}`,
      }).then(() => {
        location = undefined;
      });
    }
  });
   */

  it('DiningRooms menu should load DiningRooms page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('dining-room');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('DiningRoom').should('exist');
    cy.url().should('match', diningRoomPageUrlPattern);
  });

  describe('DiningRoom page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(diningRoomPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create DiningRoom page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/dining-room/new$'));
        cy.getEntityCreateUpdateHeading('DiningRoom');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', diningRoomPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/dining-rooms',
          body: {
            ...diningRoomSample,
            location: location,
          },
        }).then(({ body }) => {
          diningRoom = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/dining-rooms+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/dining-rooms?page=0&size=20>; rel="last",<http://localhost/api/dining-rooms?page=0&size=20>; rel="first"',
              },
              body: [diningRoom],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(diningRoomPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(diningRoomPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details DiningRoom page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('diningRoom');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', diningRoomPageUrlPattern);
      });

      it('edit button click should load edit DiningRoom page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DiningRoom');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', diningRoomPageUrlPattern);
      });

      it('edit button click should load edit DiningRoom page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('DiningRoom');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', diningRoomPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of DiningRoom', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('diningRoom').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', diningRoomPageUrlPattern);

        diningRoom = undefined;
      });
    });
  });

  describe('new DiningRoom page', () => {
    beforeEach(() => {
      cy.visit(`${diningRoomPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('DiningRoom');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of DiningRoom', () => {
      cy.get(`[data-cy="name"]`).type('ugh');
      cy.get(`[data-cy="name"]`).should('have.value', 'ugh');

      cy.get(`[data-cy="description"]`).type('drat within');
      cy.get(`[data-cy="description"]`).should('have.value', 'drat within');

      cy.get(`[data-cy="floor"]`).type('28659');
      cy.get(`[data-cy="floor"]`).should('have.value', '28659');

      cy.get(`[data-cy="capacity"]`).type('28368');
      cy.get(`[data-cy="capacity"]`).should('have.value', '28368');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="floorPlanUrl"]`).type('whoa premeditation after');
      cy.get(`[data-cy="floorPlanUrl"]`).should('have.value', 'whoa premeditation after');

      cy.get(`[data-cy="widthPx"]`).type('14471.49');
      cy.get(`[data-cy="widthPx"]`).should('have.value', '14471.49');

      cy.get(`[data-cy="heightPx"]`).type('14365.87');
      cy.get(`[data-cy="heightPx"]`).should('have.value', '14365.87');

      cy.get(`[data-cy="location"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        diningRoom = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', diningRoomPageUrlPattern);
    });
  });
});
