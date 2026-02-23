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

describe('Promotion e2e test', () => {
  const promotionPageUrl = '/promotion';
  const promotionPageUrlPattern = new RegExp('/promotion(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const promotionSample = {
    code: 'upwardly',
    name: 'lest interchange',
    discountType: 'PERCENTAGE',
    discountValue: 19368.51,
    startDate: '2026-02-22',
    endDate: '2026-02-23',
    isActive: false,
  };

  let promotion;
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
        name: 'aw',
        description: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        logoUrl: 'consequently gray quick',
        coverImageUrl: 'incidentally',
        primaryColor: 'from fu',
        secondaryColor: 'phooey',
        website: 'throughout woefully apud',
        contactEmail: 'kindly yippee',
        contactPhone: 'accessorise',
        defaultReservationDuration: 389,
        maxAdvanceBookingDays: 314,
        cancellationDeadlineHours: 31,
        isActive: false,
        createdAt: '2026-02-23T07:45:36.848Z',
      },
    }).then(({ body }) => {
      brand = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/promotions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/promotions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/promotions/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/brands', {
      statusCode: 200,
      body: [brand],
    });

    cy.intercept('GET', '/api/locations', {
      statusCode: 200,
      body: [],
    });
  });

  afterEach(() => {
    if (promotion) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/promotions/${promotion.id}`,
      }).then(() => {
        promotion = undefined;
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

  it('Promotions menu should load Promotions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('promotion');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Promotion').should('exist');
    cy.url().should('match', promotionPageUrlPattern);
  });

  describe('Promotion page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(promotionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Promotion page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/promotion/new$'));
        cy.getEntityCreateUpdateHeading('Promotion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/promotions',
          body: {
            ...promotionSample,
            brand,
          },
        }).then(({ body }) => {
          promotion = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/promotions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/promotions?page=0&size=20>; rel="last",<http://localhost/api/promotions?page=0&size=20>; rel="first"',
              },
              body: [promotion],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(promotionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Promotion page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('promotion');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });

      it('edit button click should load edit Promotion page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Promotion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });

      it('edit button click should load edit Promotion page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Promotion');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);
      });

      it('last delete button click should delete instance of Promotion', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('promotion').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', promotionPageUrlPattern);

        promotion = undefined;
      });
    });
  });

  describe('new Promotion page', () => {
    beforeEach(() => {
      cy.visit(`${promotionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Promotion');
    });

    it('should create an instance of Promotion', () => {
      cy.get(`[data-cy="code"]`).type('which order nicely');
      cy.get(`[data-cy="code"]`).should('have.value', 'which order nicely');

      cy.get(`[data-cy="name"]`).type('harangue ferociously');
      cy.get(`[data-cy="name"]`).should('have.value', 'harangue ferociously');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="discountType"]`).select('PERCENTAGE');

      cy.get(`[data-cy="discountValue"]`).type('3878.96');
      cy.get(`[data-cy="discountValue"]`).should('have.value', '3878.96');

      cy.get(`[data-cy="minimumOrderAmount"]`).type('5053.93');
      cy.get(`[data-cy="minimumOrderAmount"]`).should('have.value', '5053.93');

      cy.get(`[data-cy="maxUsageCount"]`).type('21573');
      cy.get(`[data-cy="maxUsageCount"]`).should('have.value', '21573');

      cy.get(`[data-cy="currentUsageCount"]`).type('30709');
      cy.get(`[data-cy="currentUsageCount"]`).should('have.value', '30709');

      cy.get(`[data-cy="startDate"]`).type('2026-02-22');
      cy.get(`[data-cy="startDate"]`).blur();
      cy.get(`[data-cy="startDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="endDate"]`).type('2026-02-23');
      cy.get(`[data-cy="endDate"]`).blur();
      cy.get(`[data-cy="endDate"]`).should('have.value', '2026-02-23');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="brand"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        promotion = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', promotionPageUrlPattern);
    });
  });
});
