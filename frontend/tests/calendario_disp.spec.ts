import { test, expect } from '@playwright/test';

test.describe('Visualización de disponibilidad y gestión de turnos', () => {

  const LOCAL_UUID = '123e4567-e89b-12d3-a456-426614174000';

  test.beforeEach(async ({ page }) => {
    // Interceptar la petición de calendario semanal
    await page.route(`**/locales/${LOCAL_UUID}/calendario/semana*`, async route => {
      await route.fulfill({
        json: [
          {
            fecha: '2026-05-25',
            turnos: [
              {
                id: 't1',
                horaInicio: '10:00',
                horaFin: '11:00',
                estado: 'Ocupado',
                deporte: 'Fútbol',
                espacioNombre: 'Cancha 1',
                reserva: {
                  organizadorNombre: 'Juan Pérez',
                  cantidadConfirmados: 10,
                  capacidadTotal: 10,
                  estadoEvento: 'Confirmado'
                }
              },
              {
                id: 't2',
                horaInicio: '11:00',
                horaFin: '12:00',
                estado: 'Disponible',
                deporte: 'Fútbol',
                espacioNombre: 'Cancha 1',
                reserva: null
              }
            ]
          },
          {
            fecha: '2026-05-26',
            turnos: []
          }
        ]
      });
    });

    await page.goto(`http://localhost:4200/locales/${LOCAL_UUID}/calendario`);
  });

  test('Escenario: Ver disponibilidad semanal y diaria', async ({ page }) => {
    // Cuando el propietario presiona el botón "Ver disponibilidad" para una semana determinada
    // Entonces el sistema muestra los horarios ocupados y disponibles de la semana seleccionada
    // Validamos que se muestren las fechas en los headers
    const dias = page.locator('.dia-card');
    await expect(dias).toHaveCount(2); // Mock devuelve 2 días (Lunes y Martes)

    // Validamos que el primer día tenga turnos
    const primerDia = dias.nth(0);
    await expect(primerDia.locator('.count-badge')).toContainText('2 turnos');

    // Escenario: Ver disponibilidad diaria
    // Al expandir/abrir un día específico
    // En este diseño de frontend, los días vienen expandidos por defecto isDiaAbierto(i) no devuelve explícitamente false
    // pero podemos forzar un click para probar el toggle, o simplemente ver su contenido
    const turnosPrimerDia = primerDia.locator('.turnos app-turno-item');
    await expect(turnosPrimerDia).toHaveCount(2);
    
    // Verificamos que se muestren ocupados y disponibles
    await expect(turnosPrimerDia.nth(0).locator('.badge')).toHaveText('Ocupado');
    await expect(turnosPrimerDia.nth(1).locator('.badge')).toHaveText('Disponible');
  });

  test('Escenario: Ver detalle de un turno del día', async ({ page }) => {
    // Cuando el propietario selecciona "Ver detalle" en un turno específico
    const turnoOcupado = page.locator('app-turno-item').filter({ hasText: 'Ocupado' }).first();
    const row = turnoOcupado.locator('.turno-row');
    
    // Hacemos click en el row del turno para abrir el detalle
    await row.click();

    // Entonces se visualizan los siguientes datos:
    // | campo | nombre del organizador | deporte | participantes confirmados | estado del evento |
    const detalle = turnoOcupado.locator('.detalle');
    await expect(detalle).toHaveClass(/open/);

    await expect(detalle.locator('.detalle-item').filter({ hasText: 'organizador' }).locator('.detalle-valor')).toHaveText('Juan Pérez');
    await expect(detalle.locator('.detalle-item').filter({ hasText: 'estado evento' }).locator('.detalle-valor')).toHaveText('Confirmado');
    await expect(detalle.locator('.detalle-item').filter({ hasText: 'confirmados' }).locator('.detalle-valor')).toHaveText('10 / 10');
    // El deporte se muestra en el main del row
    await expect(turnoOcupado.locator('.turno-main')).toContainText('Fútbol');
  });

  test('Escenario: Consultar historial de turnos finalizados', async ({ page }) => {
    // Hacemos un mock específico simulando una fecha en el pasado
    await page.route(`**/locales/${LOCAL_UUID}/calendario/semana*`, async route => {
      await route.fulfill({
        json: [
          {
            fecha: '2026-05-18', // Fecha de la semana pasada
            turnos: [
              {
                id: 't3',
                horaInicio: '18:00',
                horaFin: '19:00',
                estado: 'Finalizado',
                deporte: 'Tenis',
                espacioNombre: 'Cancha 1',
                reserva: {
                  organizadorNombre: 'María Gómez',
                  cantidadConfirmados: 4,
                  capacidadTotal: 4,
                  estadoEvento: 'Finalizado'
                }
              }
            ]
          }
        ]
      });
    });

    // Recargar la página para aplicar el nuevo mock
    await page.goto(`http://localhost:4200/locales/${LOCAL_UUID}/calendario`);

    // Cuando el propietario selecciona un partido finalizado en una fecha pasada
    const turnoFinalizado = page.locator('app-turno-item').filter({ hasText: 'Finalizado' }).first();
    await turnoFinalizado.locator('.turno-row').click();

    // Entonces se visualizan los datos del historial
    const detalle = turnoFinalizado.locator('.detalle');
    await expect(detalle).toHaveClass(/open/);

    await expect(detalle.locator('.detalle-item').filter({ hasText: 'organizador' }).locator('.detalle-valor')).toHaveText('María Gómez');
    await expect(turnoFinalizado.locator('.turno-main')).toContainText('Tenis');
    await expect(detalle.locator('.detalle-item').filter({ hasText: 'confirmados' }).locator('.detalle-valor')).toHaveText('4 / 4');
  });

});
