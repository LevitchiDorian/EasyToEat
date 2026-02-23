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

describe('WaitingList e2e test', () => {
  const waitingListPageUrl = '/waiting-list';
  const waitingListPageUrlPattern = new RegExp('/waiting-list(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const waitingListSample = {
    requestedDate: '2026-02-22',
    requestedTime: 'furth',
    partySize: 6114,
    isNotified: true,
    createdAt: '2026-02-23T08:35:36.977Z',
  };

  let waitingList;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/waiting-lists+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/waiting-lists').as('postEntityRequest');
    cy.intercept('DELETE', '/api/waiting-lists/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (waitingList) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/waiting-lists/${waitingList.id}`,
      }).then(() => {
        waitingList = undefined;
      });
    }
  });

  it('WaitingLists menu should load WaitingLists page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('waiting-list');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('WaitingList').should('exist');
    cy.url().should('match', waitingListPageUrlPattern);
  });

  describe('WaitingList page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(waitingListPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create WaitingList page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/waiting-list/new$'));
        cy.getEntityCreateUpdateHeading('WaitingList');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waitingListPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/waiting-lists',
          body: waitingListSample,
        }).then(({ body }) => {
          waitingList = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/waiting-lists+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/waiting-lists?page=0&size=20>; rel="last",<http://localhost/api/waiting-lists?page=0&size=20>; rel="first"',
              },
              body: [waitingList],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(waitingListPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details WaitingList page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('waitingList');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waitingListPageUrlPattern);
      });

      it('edit button click should load edit WaitingList page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WaitingList');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waitingListPageUrlPattern);
      });

      it('edit button click should load edit WaitingList page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('WaitingList');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waitingListPageUrlPattern);
      });

      it('last delete button click should delete instance of WaitingList', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('waitingList').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', waitingListPageUrlPattern);

        waitingList = undefined;
      });
    });
  });

  describe('new WaitingList page', () => {
    beforeEach(() => {
      cy.visit(`${waitingListPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('WaitingList');
    });

    it('should create an instance of WaitingList', () => {
      cy.get(`[data-cy="requestedDate"]`).type('2026-02-22');
      cy.get(`[data-cy="requestedDate"]`).blur();
      cy.get(`[data-cy="requestedDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="requestedTime"]`).type('after');
      cy.get(`[data-cy="requestedTime"]`).should('have.value', 'after');

      cy.get(`[data-cy="partySize"]`).type('25816');
      cy.get(`[data-cy="partySize"]`).should('have.value', '25816');

      cy.get(`[data-cy="notes"]`).type('too bind throughout');
      cy.get(`[data-cy="notes"]`).should('have.value', 'too bind throughout');

      cy.get(`[data-cy="isNotified"]`).should('not.be.checked');
      cy.get(`[data-cy="isNotified"]`).click();
      cy.get(`[data-cy="isNotified"]`).should('be.checked');

      cy.get(`[data-cy="expiresAt"]`).type('2026-02-23T09:15');
      cy.get(`[data-cy="expiresAt"]`).blur();
      cy.get(`[data-cy="expiresAt"]`).should('have.value', '2026-02-23T09:15');

      cy.get(`[data-cy="createdAt"]`).type('2026-02-22T19:02');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2026-02-22T19:02');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        waitingList = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', waitingListPageUrlPattern);
    });
  });
});
