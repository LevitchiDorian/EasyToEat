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

describe('Reservation e2e test', () => {
  const reservationPageUrl = '/reservation';
  const reservationPageUrlPattern = new RegExp('/reservation(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const reservationSample = {
    reservationCode: 'obnoxiously at',
    reservationDate: '2026-02-23',
    startTime: 'fatal',
    endTime: 'own',
    partySize: 20,
    status: 'CONFIRMED',
    createdAt: '2026-02-22T22:02:02.828Z',
  };

  let reservation;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/reservations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/reservations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/reservations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (reservation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/reservations/${reservation.id}`,
      }).then(() => {
        reservation = undefined;
      });
    }
  });

  it('Reservations menu should load Reservations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('reservation');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Reservation').should('exist');
    cy.url().should('match', reservationPageUrlPattern);
  });

  describe('Reservation page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(reservationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Reservation page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/reservation/new$'));
        cy.getEntityCreateUpdateHeading('Reservation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/reservations',
          body: reservationSample,
        }).then(({ body }) => {
          reservation = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/reservations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/reservations?page=0&size=20>; rel="last",<http://localhost/api/reservations?page=0&size=20>; rel="first"',
              },
              body: [reservation],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(reservationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Reservation page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('reservation');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationPageUrlPattern);
      });

      it('edit button click should load edit Reservation page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reservation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationPageUrlPattern);
      });

      it('edit button click should load edit Reservation page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reservation');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationPageUrlPattern);
      });

      it('last delete button click should delete instance of Reservation', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('reservation').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationPageUrlPattern);

        reservation = undefined;
      });
    });
  });

  describe('new Reservation page', () => {
    beforeEach(() => {
      cy.visit(`${reservationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Reservation');
    });

    it('should create an instance of Reservation', () => {
      cy.get(`[data-cy="reservationCode"]`).type('sneak yowza');
      cy.get(`[data-cy="reservationCode"]`).should('have.value', 'sneak yowza');

      cy.get(`[data-cy="reservationDate"]`).type('2026-02-22');
      cy.get(`[data-cy="reservationDate"]`).blur();
      cy.get(`[data-cy="reservationDate"]`).should('have.value', '2026-02-22');

      cy.get(`[data-cy="startTime"]`).type('crest');
      cy.get(`[data-cy="startTime"]`).should('have.value', 'crest');

      cy.get(`[data-cy="endTime"]`).type('once ');
      cy.get(`[data-cy="endTime"]`).should('have.value', 'once ');

      cy.get(`[data-cy="partySize"]`).type('48');
      cy.get(`[data-cy="partySize"]`).should('have.value', '48');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="specialRequests"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="specialRequests"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="internalNotes"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="internalNotes"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="reminderSentAt"]`).type('2026-02-22T17:00');
      cy.get(`[data-cy="reminderSentAt"]`).blur();
      cy.get(`[data-cy="reminderSentAt"]`).should('have.value', '2026-02-22T17:00');

      cy.get(`[data-cy="confirmedAt"]`).type('2026-02-22T22:29');
      cy.get(`[data-cy="confirmedAt"]`).blur();
      cy.get(`[data-cy="confirmedAt"]`).should('have.value', '2026-02-22T22:29');

      cy.get(`[data-cy="cancelledAt"]`).type('2026-02-23T06:53');
      cy.get(`[data-cy="cancelledAt"]`).blur();
      cy.get(`[data-cy="cancelledAt"]`).should('have.value', '2026-02-23T06:53');

      cy.get(`[data-cy="cancellationReason"]`).type('tensely culminate');
      cy.get(`[data-cy="cancellationReason"]`).should('have.value', 'tensely culminate');

      cy.get(`[data-cy="createdAt"]`).type('2026-02-23T08:41');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2026-02-23T08:41');

      cy.get(`[data-cy="updatedAt"]`).type('2026-02-23T04:27');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2026-02-23T04:27');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        reservation = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', reservationPageUrlPattern);
    });
  });
});
