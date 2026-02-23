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

describe('Location e2e test', () => {
  const locationPageUrl = '/location';
  const locationPageUrlPattern = new RegExp('/location(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const locationSample = {
    name: 'throughout officially quizzically',
    address: 'phooey clamor a',
    city: 'Port Dante',
    phone: '501.401.6123 x736',
    email: 'Carley.Beatty6@hotmail.com',
    isActive: false,
    createdAt: '2026-02-22T18:12:29.618Z',
  };

  let location;
  let brand;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/brands',
      body: {
        name: 'intrepid',
        description: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        logoUrl: 'likewise',
        coverImageUrl: 'fibre unbalance',
        primaryColor: 'pile bo',
        secondaryColor: 'worth g',
        website: 'slowly',
        contactEmail: 'compromise beep',
        contactPhone: 'from summer',
        defaultReservationDuration: 59,
        maxAdvanceBookingDays: 87,
        cancellationDeadlineHours: 57,
        isActive: true,
        createdAt: '2026-02-23T11:53:24.651Z',
      },
    }).then(({ body }) => {
      brand = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/locations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/locations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/locations/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/location-hours', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/dining-rooms', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/promotions', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/location-menu-item-overrides', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/brands', {
      statusCode: 200,
      body: [brand],
    });
  });

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

  afterEach(() => {
    if (brand) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/brands/${brand.id}`,
      }).then(() => {
        brand = undefined;
      });
    }
  });

  it('Locations menu should load Locations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('location');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Location').should('exist');
    cy.url().should('match', locationPageUrlPattern);
  });

  describe('Location page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(locationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Location page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/location/new$'));
        cy.getEntityCreateUpdateHeading('Location');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/locations',
          body: {
            ...locationSample,
            brand,
          },
        }).then(({ body }) => {
          location = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/locations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/locations?page=0&size=20>; rel="last",<http://localhost/api/locations?page=0&size=20>; rel="first"',
              },
              body: [location],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(locationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Location page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('location');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationPageUrlPattern);
      });

      it('edit button click should load edit Location page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Location');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationPageUrlPattern);
      });

      it('edit button click should load edit Location page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Location');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationPageUrlPattern);
      });

      it('last delete button click should delete instance of Location', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('location').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', locationPageUrlPattern);

        location = undefined;
      });
    });
  });

  describe('new Location page', () => {
    beforeEach(() => {
      cy.visit(`${locationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Location');
    });

    it('should create an instance of Location', () => {
      cy.get(`[data-cy="name"]`).type('accredit meh');
      cy.get(`[data-cy="name"]`).should('have.value', 'accredit meh');

      cy.get(`[data-cy="address"]`).type('soup alongside intensely');
      cy.get(`[data-cy="address"]`).should('have.value', 'soup alongside intensely');

      cy.get(`[data-cy="city"]`).type('New Zoieville');
      cy.get(`[data-cy="city"]`).should('have.value', 'New Zoieville');

      cy.get(`[data-cy="phone"]`).type('592-271-6188 x789');
      cy.get(`[data-cy="phone"]`).should('have.value', '592-271-6188 x789');

      cy.get(`[data-cy="email"]`).type('Peyton.Kshlerin14@yahoo.com');
      cy.get(`[data-cy="email"]`).should('have.value', 'Peyton.Kshlerin14@yahoo.com');

      cy.get(`[data-cy="latitude"]`).type('17638.53');
      cy.get(`[data-cy="latitude"]`).should('have.value', '17638.53');

      cy.get(`[data-cy="longitude"]`).type('17657.84');
      cy.get(`[data-cy="longitude"]`).should('have.value', '17657.84');

      cy.get(`[data-cy="reservationDurationOverride"]`).type('166');
      cy.get(`[data-cy="reservationDurationOverride"]`).should('have.value', '166');

      cy.get(`[data-cy="maxAdvanceBookingDaysOverride"]`).type('237');
      cy.get(`[data-cy="maxAdvanceBookingDaysOverride"]`).should('have.value', '237');

      cy.get(`[data-cy="cancellationDeadlineOverride"]`).type('32');
      cy.get(`[data-cy="cancellationDeadlineOverride"]`).should('have.value', '32');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="createdAt"]`).type('2026-02-22T21:57');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2026-02-22T21:57');

      cy.get(`[data-cy="brand"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        location = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', locationPageUrlPattern);
    });
  });
});
