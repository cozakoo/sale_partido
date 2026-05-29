import { test, expect } from '@playwright/test';

test.describe('Visualización de disponibilidad y gestión de turnos', () => {

  const LOCAL_UUID = '123e4567-e89b-12d3-a456-426614174000';

  test.beforeEach(async ({ page }) => {
    // Interceptar la petición de disponibilidad (endpoint correcto)
    await page.route(`**/locales/${LOCAL_UUID}/disponibilidad*`, async route => {
      await route.fulfill({
        json: [
          {
            canchaUuid: 'cancha-1',
            canchaNombre: 'Cancha 1',
            turnos: [
              {
                fecha: '2026-05-25',
                horaInicio: '10:00:00',
                horaFin: '11:00:00',
                estado: 'OCUPADO',
                deporte: 'Fútbol',
                espacioNombre: 'Cancha 1',
                reserva: {
                  uuid: 'res-1',
                  nombreOrganizador: 'Juan Pérez',
                  deporte: 'Fútbol',
                  cantidadParticipantesConfirmados: 10,
                  estadoEvento: 'CONFIRMADO',
                  capacidad: 10
                }
              },
              {
                fecha: '2026-05-25',
                horaInicio: '11:00:00',
                horaFin: '12:00:00',
                estado: 'LIBRE',
                deporte: 'Fútbol',
                espacioNombre: 'Cancha 1',
                reserva: null
              }
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
    // Cuando el propietario presiona el botón "Ver disponibilidad" para una semana determinada
    // Entonces el sistema muestra los horarios ocupados y disponibles de la semana seleccionada
    // Validamos que se muestren los 7 días de la semana (siempre mapea a 7 días)
    const dias = page.locator('.dia-card');
    await expect(dias).toHaveCount(7); // mapearASemana() siempre genera 7 días

    // Validamos que existan turnos en los días con datos
    const turnosAadir = page.locator('app-turno-item');
    await expect(turnosAadir).toHaveCount(2); // Mock proporciona 2 turnos totales

    // Escenario: Ver disponibilidad diaria
    // Los días vienen expandidos por defecto
    const primerTurno = turnosAadir.nth(0);
    await expect(primerTurno).toBeVisible();

    // Verificamos que se muestren estados en minúsculas (ocupado, libre)
    await expect(turnosAadir.nth(0).locator('.badge')).toContainText(/ocupado/i);
    await expect(turnosAadir.nth(1).locator('.badge')).toContainText(/libre/i);
  });

  test('Escenario: Ver detalle de un turno del día', async ({ page }) => {
    // Cuando el propietario selecciona "Ver detalle" en un turno específico
    const turnoOcupado = page.locator('app-turno-item').filter({ hasText: 'ocupado' }).first();
    const row = turnoOcupado.locator('.turno-row');

    // Hacemos click en el row del turno para abrir el detalle
    await row.click();

    const detalle = turnoOcupado.locator('.detalle');

    await expect(detalle).toHaveClass(/open/);

    await expect(detalle.locator('.detalle-item').filter({ hasText: 'organizador' }).locator('.detalle-valor')).toHaveText('Juan Pérez');
    await expect(detalle.locator('.detalle-item').filter({ hasText: 'estado evento' }).locator('.detalle-valor')).toHaveText('confirmado', { ignoreCase: true });
    await expect(detalle.locator('.detalle-item').filter({ hasText: 'confirmados' }).locator('.detalle-valor')).toHaveText('10 / 10');
    // El deporte se muestra en el main del row
    await expect(turnoOcupado.locator('.turno-main')).toContainText('Fútbol');
  });

  test('Escenario: Consultar historial de turnos finalizados', async ({ page }) => {
    // Hacemos un mock específico simulando un turno finalizado (ocupado en el pasado)
    // Usamos una fecha de la semana actual para que sea visible en el calendario
    await page.route(`**/locales/${LOCAL_UUID}/disponibilidad*`, async route => {
      await route.fulfill({
        json: [
          {
            canchaUuid: 'cancha-1',
            canchaNombre: 'Cancha 1',
            turnos: [
              {
                fecha: '2026-05-26', // Lunes de la semana actual (hace poco)
                horaInicio: '18:00:00',
                horaFin: '19:00:00',
                estado: 'OCUPADO', // Un turno finalizado es un turno que estuvo ocupado
                deporte: 'Tenis',
                reserva: {
                  uuid: 'res-2',
                  nombreOrganizador: 'María Gómez',
                  deporte: 'Tenis',
                  cantidadParticipantesConfirmados: 4,
                  estadoEvento: 'FINALIZADO', // Estado de evento finalizado
                  capacidad: 4
                }
              }
            ]
          }
        ]
      });
    });

    await Promise.all([
      page.waitForResponse(res => res.url().includes(`/locales/${LOCAL_UUID}/disponibilidad`)),
      page.goto(`http://localhost:4200/locales/${LOCAL_UUID}/calendario`),
    ]);

    // Cuando el propietario selecciona un turno ocupado en una fecha pasada (historial)
    const turnoHistorico = page.locator('app-turno-item').filter({ hasText: 'ocupado' }).first();
    await turnoHistorico.locator('.turno-row').click();

    // Entonces se visualizan los datos del historial
    const detalle = turnoHistorico.locator('.detalle');
    await expect(detalle).toHaveClass(/open/);

    await expect(detalle.locator('.detalle-item').filter({ hasText: 'organizador' }).locator('.detalle-valor')).toHaveText('María Gómez');
    await expect(turnoHistorico.locator('.turno-main')).toContainText('Tenis');
    await expect(detalle.locator('.detalle-item').filter({ hasText: 'confirmados' }).locator('.detalle-valor')).toHaveText('4 / 4');
  });
});
