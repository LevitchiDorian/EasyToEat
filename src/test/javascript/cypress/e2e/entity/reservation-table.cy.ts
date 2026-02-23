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

describe('ReservationTable e2e test', () => {
  const reservationTablePageUrl = '/reservation-table';
  const reservationTablePageUrlPattern = new RegExp('/reservation-table(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const reservationTableSample = { assignedAt: '2026-02-23T06:50:24.141Z' };

  let reservationTable;
  let reservation;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/reservations',
      body: {
        reservationCode: 'well-groomed misrepo',
        reservationDate: '2026-02-23',
        startTime: 'phooe',
        endTime: 'pillo',
        partySize: 14,
        status: 'NO_SHOW',
        specialRequests: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        internalNotes: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
        reminderSentAt: '2026-02-23T01:09:44.562Z',
        confirmedAt: '2026-02-23T12:07:25.944Z',
        cancelledAt: '2026-02-23T12:48:43.678Z',
        cancellationReason: 'scar orient petal',
        createdAt: '2026-02-23T02:05:41.242Z',
        updatedAt: '2026-02-23T11:58:06.964Z',
      },
    }).then(({ body }) => {
      reservation = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/reservation-tables+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/reservation-tables').as('postEntityRequest');
    cy.intercept('DELETE', '/api/reservation-tables/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/restaurant-tables', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/reservations', {
      statusCode: 200,
      body: [reservation],
    });
  });

  afterEach(() => {
    if (reservationTable) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/reservation-tables/${reservationTable.id}`,
      }).then(() => {
        reservationTable = undefined;
      });
    }
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

  it('ReservationTables menu should load ReservationTables page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('reservation-table');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ReservationTable').should('exist');
    cy.url().should('match', reservationTablePageUrlPattern);
  });

  describe('ReservationTable page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(reservationTablePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ReservationTable page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/reservation-table/new$'));
        cy.getEntityCreateUpdateHeading('ReservationTable');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationTablePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/reservation-tables',
          body: {
            ...reservationTableSample,
            reservation,
          },
        }).then(({ body }) => {
          reservationTable = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/reservation-tables+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [reservationTable],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(reservationTablePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ReservationTable page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('reservationTable');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationTablePageUrlPattern);
      });

      it('edit button click should load edit ReservationTable page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReservationTable');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationTablePageUrlPattern);
      });

      it('edit button click should load edit ReservationTable page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ReservationTable');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationTablePageUrlPattern);
      });

      it('last delete button click should delete instance of ReservationTable', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('reservationTable').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationTablePageUrlPattern);

        reservationTable = undefined;
      });
    });
  });

  describe('new ReservationTable page', () => {
    beforeEach(() => {
      cy.visit(`${reservationTablePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ReservationTable');
    });

    it('should create an instance of ReservationTable', () => {
      cy.get(`[data-cy="assignedAt"]`).type('2026-02-23T03:55');
      cy.get(`[data-cy="assignedAt"]`).blur();
      cy.get(`[data-cy="assignedAt"]`).should('have.value', '2026-02-23T03:55');

      cy.get(`[data-cy="notes"]`).type('equatorial');
      cy.get(`[data-cy="notes"]`).should('have.value', 'equatorial');

      cy.get(`[data-cy="reservation"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        reservationTable = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', reservationTablePageUrlPattern);
    });
  });
});
