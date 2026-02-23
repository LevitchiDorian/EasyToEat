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

describe('LocationMenuItemOverride e2e test', () => {
  const locationMenuItemOverridePageUrl = '/location-menu-item-override';
  const locationMenuItemOverridePageUrlPattern = new RegExp('/location-menu-item-override(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const locationMenuItemOverrideSample = {"isAvailableAtLocation":true};

  let locationMenuItemOverride;
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
      body: {"name":"duh via uh-huh","address":"mid","city":"Sallyside","phone":"1-590-902-6040 x6254","email":"Joshuah_Donnelly73@gmail.com","latitude":5599.27,"longitude":13564.37,"reservationDurationOverride":401,"maxAdvanceBookingDaysOverride":2,"cancellationDeadlineOverride":18,"isActive":true,"createdAt":"2026-02-23T05:01:21.436Z"},
    }).then(({ body }) => {
      location = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/location-menu-item-overrides+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/location-menu-item-overrides').as('postEntityRequest');
    cy.intercept('DELETE', '/api/location-menu-item-overrides/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/menu-items', {
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
    if (locationMenuItemOverride) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/location-menu-item-overrides/${locationMenuItemOverride.id}`,
      }).then(() => {
        locationMenuItemOverride = undefined;
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

  it('LocationMenuItemOverrides menu should load LocationMenuItemOverrides page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('location-menu-item-override');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('LocationMenuItemOverride').should('exist');
    cy.url().should('match', locationMenuItemOverridePageUrlPattern);
  });

  describe('LocationMenuItemOverride page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(locationMenuItemOverridePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create LocationMenuItemOverride page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/location-menu-item-override/new$'));
        cy.getEntityCreateUpdateHeading('LocationMenuItemOverride');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationMenuItemOverridePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/location-menu-item-overrides',
          body: {
            ...locationMenuItemOverrideSample,
            location: location,
          },
        }).then(({ body }) => {
          locationMenuItemOverride = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/location-menu-item-overrides+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/location-menu-item-overrides?page=0&size=20>; rel="last",<http://localhost/api/location-menu-item-overrides?page=0&size=20>; rel="first"',
              },
              body: [locationMenuItemOverride],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(locationMenuItemOverridePageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(locationMenuItemOverridePageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details LocationMenuItemOverride page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('locationMenuItemOverride');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationMenuItemOverridePageUrlPattern);
      });

      it('edit button click should load edit LocationMenuItemOverride page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LocationMenuItemOverride');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationMenuItemOverridePageUrlPattern);
      });

      it('edit button click should load edit LocationMenuItemOverride page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LocationMenuItemOverride');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationMenuItemOverridePageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of LocationMenuItemOverride', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('locationMenuItemOverride').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationMenuItemOverridePageUrlPattern);

        locationMenuItemOverride = undefined;
      });
    });
  });

  describe('new LocationMenuItemOverride page', () => {
    beforeEach(() => {
      cy.visit(`${locationMenuItemOverridePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('LocationMenuItemOverride');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of LocationMenuItemOverride', () => {
      cy.get(`[data-cy="isAvailableAtLocation"]`).should('not.be.checked');
      cy.get(`[data-cy="isAvailableAtLocation"]`).click();
      cy.get(`[data-cy="isAvailableAtLocation"]`).should('be.checked');

      cy.get(`[data-cy="priceOverride"]`).type('21546.21');
      cy.get(`[data-cy="priceOverride"]`).should('have.value', '21546.21');

      cy.get(`[data-cy="preparationTimeOverride"]`).type('22');
      cy.get(`[data-cy="preparationTimeOverride"]`).should('have.value', '22');

      cy.get(`[data-cy="notes"]`).type('hm ack wrongly');
      cy.get(`[data-cy="notes"]`).should('have.value', 'hm ack wrongly');

      cy.get(`[data-cy="location"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        locationMenuItemOverride = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', locationMenuItemOverridePageUrlPattern);
    });
  });
});
