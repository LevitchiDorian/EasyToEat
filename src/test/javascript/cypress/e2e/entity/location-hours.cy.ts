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

describe('LocationHours e2e test', () => {
  const locationHoursPageUrl = '/location-hours';
  const locationHoursPageUrlPattern = new RegExp('/location-hours(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const locationHoursSample = {"dayOfWeek":"MONDAY","openTime":"helpl","closeTime":"yum","isClosed":true};

  let locationHours;
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
      body: {"name":"so","address":"ew wedge rightfully","city":"Sunrise","phone":"484.600.7954 x914","email":"Ariane.Howell@yahoo.com","latitude":27414.8,"longitude":13677.46,"reservationDurationOverride":291,"maxAdvanceBookingDaysOverride":302,"cancellationDeadlineOverride":34,"isActive":false,"createdAt":"2026-02-22T23:23:19.181Z"},
    }).then(({ body }) => {
      location = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/location-hours+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/location-hours').as('postEntityRequest');
    cy.intercept('DELETE', '/api/location-hours/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/locations', {
      statusCode: 200,
      body: [location],
    });

  });
   */

  afterEach(() => {
    if (locationHours) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/location-hours/${locationHours.id}`,
      }).then(() => {
        locationHours = undefined;
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

  it('LocationHours menu should load LocationHours page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('location-hours');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('LocationHours').should('exist');
    cy.url().should('match', locationHoursPageUrlPattern);
  });

  describe('LocationHours page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(locationHoursPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create LocationHours page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/location-hours/new$'));
        cy.getEntityCreateUpdateHeading('LocationHours');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationHoursPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/location-hours',
          body: {
            ...locationHoursSample,
            location: location,
          },
        }).then(({ body }) => {
          locationHours = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/location-hours+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [locationHours],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(locationHoursPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(locationHoursPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details LocationHours page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('locationHours');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationHoursPageUrlPattern);
      });

      it('edit button click should load edit LocationHours page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LocationHours');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationHoursPageUrlPattern);
      });

      it('edit button click should load edit LocationHours page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LocationHours');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationHoursPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of LocationHours', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('locationHours').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationHoursPageUrlPattern);

        locationHours = undefined;
      });
    });
  });

  describe('new LocationHours page', () => {
    beforeEach(() => {
      cy.visit(`${locationHoursPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('LocationHours');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of LocationHours', () => {
      cy.get(`[data-cy="dayOfWeek"]`).select('MONDAY');

      cy.get(`[data-cy="openTime"]`).type('and u');
      cy.get(`[data-cy="openTime"]`).should('have.value', 'and u');

      cy.get(`[data-cy="closeTime"]`).type('geez ');
      cy.get(`[data-cy="closeTime"]`).should('have.value', 'geez ');

      cy.get(`[data-cy="isClosed"]`).should('not.be.checked');
      cy.get(`[data-cy="isClosed"]`).click();
      cy.get(`[data-cy="isClosed"]`).should('be.checked');

      cy.get(`[data-cy="specialNote"]`).type('which woefully');
      cy.get(`[data-cy="specialNote"]`).should('have.value', 'which woefully');

      cy.get(`[data-cy="location"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        locationHours = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', locationHoursPageUrlPattern);
    });
  });
});
