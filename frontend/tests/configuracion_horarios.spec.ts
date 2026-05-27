import { test, expect } from '@playwright/test';

test.describe('Configuración de disponibilidad y duración de turnos', () => {
  const LOCAL_UUID = '123e4567-e89b-12d3-a456-426614174000';

  test.beforeEach(async ({ page }) => {
    // Mock de las respuestas del backend
    await page.route(`**/locales/${LOCAL_UUID}`, async route => {
      await route.fulfill({
        json: { uuid: LOCAL_UUID, nombre: 'Local de Prueba' }
      });
    });

    await page.route(`**/locales/${LOCAL_UUID}/canchas?view=detalle`, async route => {
      await route.fulfill({
        json: [
          { uuid: 'cancha-1', nombre: 'Cancha 1', configuracionesHorarios: [] },
          { uuid: 'cancha-2', nombre: 'Cancha 2', configuracionesHorarios: [] }
        ]
      });
    });

    await page.goto(`http://localhost:4200/locales/${LOCAL_UUID}/configuraciones-horarios`);
  });

  test('Escenario: Asignación de horario global de disponibilidad', async ({ page }) => {
    const lunesRow = page.locator('.row').filter({ hasText: 'Lunes' }).first();
    await lunesRow.locator('input[type="checkbox"]').check();
    await lunesRow.locator('input[formControlName="horaInicio"]').fill('10:00');
    await lunesRow.locator('input[formControlName="horaFin"]').fill('00:00');

    await expect(lunesRow.locator('input[type="checkbox"]')).toBeChecked();
    await expect(lunesRow.locator('input[formControlName="horaInicio"]')).toHaveValue('10:00');
    await expect(lunesRow.locator('input[formControlName="horaFin"]')).toHaveValue('00:00');
  });

  test('Escenario: Aplicar configuración de horario a todas las canchas', async ({ page }) => {
    const lunesRow = page.locator('.row').filter({ hasText: 'Lunes' }).first();
    await lunesRow.locator('input[type="checkbox"]').check();
    await lunesRow.locator('input[formControlName="horaInicio"]').fill('10:00');
    await lunesRow.locator('input[formControlName="horaFin"]').fill('20:00');

    page.on('dialog', dialog => dialog.accept());
    await page.getByRole('button', { name: 'Aplicar este horario a todas las canchas' }).click();

    await page.getByRole('link', { name: 'Cancha 2' }).click();
    const lunesRowCancha2 = page.locator('.row').filter({ hasText: 'Lunes' }).last();
    await expect(lunesRowCancha2.locator('input[type="checkbox"]')).toBeChecked();
    await expect(lunesRowCancha2.locator('input[formControlName="horaInicio"]')).toHaveValue('10:00');
  });

  test('Escenario: Configuración de la duración del turno mediante lista predefinida', async ({ page }) => {
    const selectDuracion = page.locator('select[formControlName="duracionTurno"]').first();
    const options = await selectDuracion.locator('option').evaluateAll(opts => opts.map(o => o.textContent?.trim()));
    
    expect(options).toEqual(expect.arrayContaining(['30', '60', '90', '120']));
    await selectDuracion.selectOption('90');
    await expect(selectDuracion).toHaveValue('90');
  });

  test('Escenario: Validación de hora de apertura menor a hora de cierre', async ({ page }) => {
    const lunesRow = page.locator('.row').filter({ hasText: 'Lunes' }).first();
    await lunesRow.locator('input[formControlName="horaInicio"]').fill('22:00');
    await lunesRow.locator('input[formControlName="horaFin"]').fill('08:00');
    
    // Asumiendo que el botón se deshabilita si los datos son inválidos
    await expect(page.getByRole('button', { name: 'Guardar' })).toBeDisabled();
  });

  test('Escenario: Advertencia de coherencia de horario vs duración', async ({ page }) => {
    await page.locator('select[formControlName="duracionTurno"]').selectOption('60');
    await page.locator('input[formControlName="horaInicio"]').first().fill('10:00');
    await page.locator('input[formControlName="horaFin"]').first().fill('10:45'); // Incoherente con 60 min
    
    await expect(page.locator('.advertencia-coherencia')).toBeVisible();
  });

  test('Escenario: Validación de formato 24 horas', async ({ page }) => {
    const horaInicio = page.locator('input[formControlName="horaInicio"]').first();
    // Verificamos que el atributo type sea time (que maneja 24hs nativamente)
    await expect(horaInicio).toHaveAttribute('type', 'time');
  });
});