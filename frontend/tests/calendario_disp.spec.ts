import { test, expect } from '@playwright/test';

test.describe('Visualización de disponibilidad y gestión de turnos', () => {

  const LOCAL_UUID = '123e4567-e89b-12d3-a456-426614174000';
  const RUTA_API = `**/locales/${LOCAL_UUID}/disponibilidad*`;

  function getFechaHoy(): string {
    const d = new Date();
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  }

  function crearTurnoMock(overrides = {}) {
    return {
      fecha: getFechaHoy(),
      horaInicio: '23:00:00',
      horaFin: '23:59:00',
      estado: 'RESERVADO',
      deporte: 'Fútbol',
      espacioNombre: 'Cancha 1',
      reserva: {
        uuid: 'reserva-uuid-1',
        nombreOrganizador: 'Juan Pérez',
        deporte: 'Fútbol',
        cantidadParticipantesConfirmados: 10,
        capacidad: 10,
        estadoEvento: 'CONFIRMADO'
      },
      ...overrides
    };
  }

  test.beforeEach(async ({ page }) => {

    await page.route(RUTA_API, async route => {
      await route.fulfill({
        json: [
          {
            canchaUuid: 'cancha-uuid-1',
            canchaNombre: 'Cancha 1',
            turnos: [
              crearTurnoMock(),
              crearTurnoMock({
                horaInicio: '11:00:00',
                horaFin: '12:00:00',
                estado: 'LIBRE',
                reserva: null
              })
            ]
          }
        ]
      });
    });

    await Promise.all([
      page.waitForResponse(res => res.url().includes(`/locales/${LOCAL_UUID}/disponibilidad`)),
      page.goto(`http://localhost:4200/locales/${LOCAL_UUID}/calendario`),
    ]);
  });

  test('Escenario: Ver disponibilidad semanal y diaria', async ({ page }) => {

    const dias = page.locator('[data-testid="dia-card"]');

    await expect(dias).toHaveCount(7);

    const diaConTurnos = page
    .locator('[data-testid="dia-card"]')
    .filter({ has: page.locator('.count-badge') })
    .first();

  await expect(diaConTurnos.locator('.count-badge')).toContainText('2 turnos');

  const turnosPrimerDia = diaConTurnos.locator('app-turno-item');

  await expect(turnosPrimerDia).toHaveCount(2);

  await expect(turnosPrimerDia.nth(0)).toContainText(/libre/i);
  await expect(turnosPrimerDia.nth(1)).toContainText(/ocupado/i);
  });

  test('Escenario: Ver detalle de un turno del día', async ({ page }) => {

    const turnoOcupado = page
      .locator('app-turno-item')
      .filter({ hasText: /ocupado/i })
      .first();

    await turnoOcupado.locator('.turno-row').click();

    const detalle = turnoOcupado.locator('.detalle');

    await expect(detalle).toHaveClass(/open/);

    await expect(
      detalle
        .locator('.detalle-item')
        .filter({ hasText: /organizador/i })
        .locator('.detalle-valor')
    ).toHaveText('Juan Pérez');

    await expect(
      detalle
        .locator('.detalle-item')
        .filter({ hasText: /estado evento/i })
        .locator('.detalle-valor')
    ).toHaveText(/confirmado/i);

    await expect(
      detalle
        .locator('.detalle-item')
        .filter({ hasText: /confirmados/i })
        .locator('.detalle-valor')
    ).toHaveText('10 / 10');

    await expect(
      turnoOcupado.locator('.turno-main')
    ).toContainText('Fútbol');
  });

  test('Escenario: Consultar historial de turnos finalizados', async ({ page }) => {

    await page.unroute(RUTA_API);

    await page.route(RUTA_API, async route => {
      await route.fulfill({
        json: [
          {
            canchaUuid: 'cancha-uuid-1',
            canchaNombre: 'Cancha 1',
            turnos: [
              crearTurnoMock({
                horaInicio: '00:00:00',
                horaFin: '00:30:00',
                deporte: 'Tenis',
                reserva: {
                  uuid: 'reserva-uuid-2',
                  nombreOrganizador: 'María Gómez',
                  deporte: 'Tenis',
                  cantidadParticipantesConfirmados: 4,
                  capacidad: 4,
                  estadoEvento: 'CONFIRMADO'
                }
              })
            ]
          }
        ]
      });
    });

    await Promise.all([
      page.waitForResponse(res => res.url().includes(`/locales/${LOCAL_UUID}/disponibilidad`)),
      page.goto(`http://localhost:4200/locales/${LOCAL_UUID}/calendario`),
    ]);

    const turnoFinalizado = page.locator('[data-testid="turno-item"]').first();

    await expect(turnoFinalizado.locator('.badge'))
      .toContainText(/finalizado/i);

    await turnoFinalizado.click();

    const detalle = page.locator('[data-testid="detalle-turno"]').first();

    await expect(detalle).toHaveClass(/open/);

    await expect(
      detalle
        .locator('.detalle-item')
        .filter({ hasText: /organizador/i })
        .locator('.detalle-valor')
    ).toHaveText('María Gómez');

    await expect(
      turnoFinalizado.locator('.turno-main')
    ).toContainText('Tenis');

    await expect(
      detalle
        .locator('.detalle-item')
        .filter({ hasText: /confirmados/i })
        .locator('.detalle-valor')
    ).toHaveText('4 / 4');
  });
});