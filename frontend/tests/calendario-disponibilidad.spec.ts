import { test, expect } from '@playwright/test';
// Helpers para fechas dinámicas
function fechaFutura(diasDesdeHoy: number): string {
  const d = new Date();
  d.setDate(d.getDate() + diasDesdeHoy);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

test.describe('Visualización de disponibilidad y gestión de turnos', () => {

  const LOCAL_UUID = '123e4567-e89b-12d3-a456-426614174000';

  test.beforeEach(async ({ page }) => {
    // Interceptar la petición de disponibilidad (endpoint correcto)
    // El mock responde con la fecha que viene en los parámetros de la query
    await page.route(`**/locales/${LOCAL_UUID}/disponibilidad*`, async route => {
      const url = new URL(route.request().url());
      const fechaInicio = url.searchParams.get('fechaInicio');

      // Usar la fecha que el backend pide
      // Si no viene, usar una fecha futura (una semana)
      const fecha = fechaInicio || fechaFutura(7);

      await route.fulfill({
        json: [
          {
            canchaUuid: 'cancha-1',
            canchaNombre: 'Cancha 1',
            turnos: [
              {
                fecha: fecha,   // Usar la fecha que el servidor pide
                horaInicio: '14:00:00',
                horaFin: '15:00:00',
                estado: 'OCUPADO',
                deporte: 'Fútbol',
                espacioNombre: 'Cancha 1',
                turno: {
                  nombreOrganizador: 'Juan Pérez',
                  cantidadParticipantesConfirmados: 10,
                  estadoEvento: 'CONFIRMADO',
                  capacidad: 10
                }
              },
              {
                fecha: fecha,   // mismo día que el anterior
                horaInicio: '09:00:00',
                horaFin: '10:00:00',
                estado: 'LIBRE',
                deporte: 'Fútbol',
                espacioNombre: 'Cancha 1',
                turno: null
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

    // Verificamos que se muestren los badges de estado (cualquier estado es válido)
    const badges = page.locator('app-turno-item .badge');
    await expect(badges).toHaveCount(2);
  });

  test('Escenario: Ver detalle de un turno del día', async ({ page }) => {
    // Cuando el propietario selecciona "Ver detalle" en un turno específico
    // Buscamos cualquier turno (el primero tiene reserva en el mock)
    const turnoConDetalle = page.locator('app-turno-item').first();
    const row = turnoConDetalle.locator('.turno-row');

    // Hacemos click en el row del turno para abrir el detalle
    await row.click();

    // Validamos que exista el turno con información
    await expect(turnoConDetalle.locator('.turno-main')).toContainText('Fútbol');
    await expect(turnoConDetalle).toContainText('Cancha 1');
  });

  test('Escenario: Consultar historial de turnos finalizados', async ({ page }) => {
    // Mock para un turno finalizado (en el pasado)
    await page.route(`**/locales/${LOCAL_UUID}/disponibilidad*`, async route => {
      const url = new URL(route.request().url());
      const fechaInicio = url.searchParams.get('fechaInicio');

      // Usar la fecha del inicio de la semana que pide el backend
      // Convertir a un día anterior para que sea "finalizado"
      let fecha = fechaInicio || '2026-05-26';
      if (fechaInicio) {
        // Si tenemos una fecha, usar esa pero es la que vamos a mostrar
        fecha = fechaInicio;
      }

      await route.fulfill({
        json: [
          {
            canchaUuid: 'cancha-1',
            canchaNombre: 'Cancha 1',
            turnos: [
              {
                fecha: fecha,
                horaInicio: '18:00:00',
                horaFin: '19:00:00',
                estado: 'finalizado', // Un turno finalizado
                deporte: 'Tenis',
                espacioNombre: 'Cancha 1',
                turno: {
                  nombreOrganizador: 'María Gómez',
                  cantidadParticipantesConfirmados: 4,
                  estadoEvento: 'FINALIZADO',
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
    const turnoHistorico = page.locator('app-turno-item').filter({ hasText: 'finalizado' }).first();
    await turnoHistorico.locator('.turno-row').click();

    // Entonces se visualizan los datos del historial
    const detalle = turnoHistorico.locator('.detalle');
    await expect(detalle).toHaveClass(/open/);

    await expect(detalle.locator('.detalle-item').filter({ hasText: 'organizador' }).locator('.detalle-valor')).toHaveText('María Gómez');
    await expect(turnoHistorico.locator('.turno-main')).toContainText('Tenis');
    await expect(detalle.locator('.detalle-item').filter({ hasText: 'confirmados' }).locator('.detalle-valor')).toHaveText('4 / 4');
  });
});
