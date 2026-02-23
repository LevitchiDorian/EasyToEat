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

describe('MenuCategory e2e test', () => {
  const menuCategoryPageUrl = '/menu-category';
  const menuCategoryPageUrlPattern = new RegExp('/menu-category(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const menuCategorySample = { name: 'brr however until', displayOrder: 24893, isActive: false };

  let menuCategory;
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
        name: 'specific duh mash',
        description: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        logoUrl: 'likable given sweetly',
        coverImageUrl: 'comestible',
        primaryColor: 'mismatc',
        secondaryColor: 'until s',
        website: 'incinerate urgently rubbery',
        contactEmail: 'stoop',
        contactPhone: 'other hopeful',
        defaultReservationDuration: 72,
        maxAdvanceBookingDays: 294,
        cancellationDeadlineHours: 72,
        isActive: true,
        createdAt: '2026-02-22T16:03:43.236Z',
      },
    }).then(({ body }) => {
      brand = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/menu-categories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/menu-categories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/menu-categories/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/menu-items', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/menu-categories', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/brands', {
      statusCode: 200,
      body: [brand],
    });
  });

  afterEach(() => {
    if (menuCategory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/menu-categories/${menuCategory.id}`,
      }).then(() => {
        menuCategory = undefined;
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

  it('MenuCategories menu should load MenuCategories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('menu-category');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MenuCategory').should('exist');
    cy.url().should('match', menuCategoryPageUrlPattern);
  });

  describe('MenuCategory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(menuCategoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MenuCategory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/menu-category/new$'));
        cy.getEntityCreateUpdateHeading('MenuCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuCategoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/menu-categories',
          body: {
            ...menuCategorySample,
            brand,
          },
        }).then(({ body }) => {
          menuCategory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/menu-categories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/menu-categories?page=0&size=20>; rel="last",<http://localhost/api/menu-categories?page=0&size=20>; rel="first"',
              },
              body: [menuCategory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(menuCategoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MenuCategory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('menuCategory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuCategoryPageUrlPattern);
      });

      it('edit button click should load edit MenuCategory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MenuCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuCategoryPageUrlPattern);
      });

      it('edit button click should load edit MenuCategory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MenuCategory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuCategoryPageUrlPattern);
      });

      it('last delete button click should delete instance of MenuCategory', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('menuCategory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', menuCategoryPageUrlPattern);

        menuCategory = undefined;
      });
    });
  });

  describe('new MenuCategory page', () => {
    beforeEach(() => {
      cy.visit(`${menuCategoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MenuCategory');
    });

    it('should create an instance of MenuCategory', () => {
      cy.get(`[data-cy="name"]`).type('whose brook knife');
      cy.get(`[data-cy="name"]`).should('have.value', 'whose brook knife');

      cy.get(`[data-cy="description"]`).type('instead sign afterwards');
      cy.get(`[data-cy="description"]`).should('have.value', 'instead sign afterwards');

      cy.get(`[data-cy="imageUrl"]`).type('needy beyond assist');
      cy.get(`[data-cy="imageUrl"]`).should('have.value', 'needy beyond assist');

      cy.get(`[data-cy="displayOrder"]`).type('21171');
      cy.get(`[data-cy="displayOrder"]`).should('have.value', '21171');

      cy.get(`[data-cy="isActive"]`).should('not.be.checked');
      cy.get(`[data-cy="isActive"]`).click();
      cy.get(`[data-cy="isActive"]`).should('be.checked');

      cy.get(`[data-cy="brand"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        menuCategory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', menuCategoryPageUrlPattern);
    });
  });
});
