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

describe('Brand e2e test', () => {
  const brandPageUrl = '/brand';
  const brandPageUrlPattern = new RegExp('/brand(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const brandSample = {
    name: 'excepting er',
    contactEmail: 'unlawful',
    contactPhone: 'leading triumphantly',
    defaultReservationDuration: 255,
    maxAdvanceBookingDays: 303,
    cancellationDeadlineHours: 17,
    isActive: true,
    createdAt: '2026-02-23T14:27:44.991Z',
  };

  let brand;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/brands+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/brands').as('postEntityRequest');
    cy.intercept('DELETE', '/api/brands/*').as('deleteEntityRequest');
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

  it('Brands menu should load Brands page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('brand');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Brand').should('exist');
    cy.url().should('match', brandPageUrlPattern);
  });

  describe('Brand page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(brandPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Brand page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/brand/new$'));
        cy.getEntityCreateUpdateHeading('Brand');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', brandPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/brands',
          body: brandSample,
        }).then(({ body }) => {
          brand = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/brands+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/brands?page=0&size=20>; rel="last",<http://localhost/api/brands?page=0&size=20>; rel="first"',
              },
              body: [brand],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(brandPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Brand page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('brand');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', brandPageUrlPattern);
      });

      it('edit button click should load edit Brand page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Brand');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', brandPageUrlPattern);
      });

      it('edit button click should load edit Brand page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Brand');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', brandPageUrlPattern);
      });

      it('last delete button click should delete instance of Brand', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('brand').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', brandPageUrlPattern);

        brand = undefined;
      });
    });
  });

  describe('new Brand page', () => {
    beforeEach(() => {
      cy.visit(`${brandPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Brand');
    });

    it('should create an instance of Brand', () => {
      cy.get(`[data-cy="name"]`).type('statement since');
      cy.get(`[data-cy="name"]`).should('have.value', 'statement since');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="logoUrl"]`).type('offensively');
      cy.get(`[data-cy="logoUrl"]`).should('have.value', 'offensively');

      cy.get(`[data-cy="coverImageUrl"]`).type('physically noon into');
      cy.get(`[data-cy="coverImageUrl"]`).should('have.value', 'physically noon into');

      cy.get(`[data-cy="primaryColor"]`).type('a even ');
      cy.get(`[data-cy="primaryColor"]`).should('have.value', 'a even ');

      cy.get(`[data-cy="secondaryColor"]`).type('grouper');
      cy.get(`[data-cy="secondaryColor"]`).should('have.value', 'grouper');

      cy.get(`[data-cy="website"]`).type('innovate');
      cy.get(`[data-cy="website"]`).should('have.value', 'innovate');

      cy.get(`[data-cy="contactEmail"]`).type('likely');
      cy.get(`[data-cy="contactEmail"]`).should('have.value', 'likely');

      cy.get(`[data-cy="contactPhone"]`).type('from');
      cy.get(`[data-cy="contactPhone"]`).should('have.value', 'from');

      cy.get(`[data-cy="defaultReservationDuration"]`).type('302');
      cy.get(`[data-cy="defaultReservationDuration"]`).should('have.value', '302');

      cy.get(`[data-cy="maxAdvanceBookingDays"]`).type('262');
      cy.get(`[data-cy="maxAdvanceBookingDays"]`).should('have.value', '262');

      cy.get(`[data-cy="cancellationDeadlineHours"]`).type('5');
      cy.get(`[data-cy="cancellationDeadlineHours"]`).should('have.value', '5');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="createdAt"]`).type('2026-02-22T21:59');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2026-02-22T21:59');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        brand = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', brandPageUrlPattern);
    });
  });
});
