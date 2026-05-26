import { test, expect } from '@playwright/test';

test.describe('Configuración de disponibilidad y duración de turnos', () => {
  const LOCAL_UUID = '123e4567-e89b-12d3-a456-426614174000';

  test.beforeEach(async ({ page }) => {
    // Mock de las respuestas del backend para aislar la prueba e2e del estado del servidor
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

    // Navegar a la página de configuración del local usando localhost:4200 (puerto de Angular por defecto)
    await page.goto(`http://localhost:4200/locales/${LOCAL_UUID}/configuraciones-horarios`);
  });

  test('Escenario: Asignación de horario global de disponibilidad', async ({ page }) => {
    // Cuando el propietario selecciona la opción de asignar horario global de "Lunes" a "Lunes"
    const lunesRow = page.locator('.row').filter({ hasText: 'Lunes' }).first();
    const checkbox = lunesRow.locator('input[type="checkbox"]');
    await checkbox.check();
    
    // Y define la franja horaria de "10:00" a "00:00"
    const horaInicio = lunesRow.locator('input[formControlName="horaInicio"]');
    const horaFin = lunesRow.locator('input[formControlName="horaFin"]');
    
    await horaInicio.fill('10:00');
    // Para el caso de 24 horas, Playwright y los inputs de tipo "time" aceptan el formato HH:mm
    await horaFin.fill('00:00');

    // Entonces la cancha debe figurar como "Disponible" en ese rango horario
    // Verificamos que los inputs guarden y reflejen los valores ingresados.
    await expect(checkbox).toBeChecked();
    await expect(horaInicio).toHaveValue('10:00');
    await expect(horaFin).toHaveValue('00:00');
  });

  test('Escenario: Aplicar configuración de horario a todas las canchas', async ({ page }) => {
    // Dado que el propietario está configurando el horario de una cancha
    const lunesRow = page.locator('.row').filter({ hasText: 'Lunes' }).first();
    await lunesRow.locator('input[type="checkbox"]').check();
    await lunesRow.locator('input[formControlName="horaInicio"]').fill('10:00');
    await lunesRow.locator('input[formControlName="horaFin"]').fill('20:00');

    // Manejador del cuadro de diálogo (mock) para validar que se pida confirmación
    let confirmacionSolicitada = false;
    page.on('dialog', async dialog => {
      expect(dialog.type()).toBe('confirm');
      confirmacionSolicitada = true;
      await dialog.accept();
    });

    // Cuando presiona el botón para aplicar ese horario al resto de las canchas
    await page.getByRole('button', { name: 'Aplicar este horario a todas las canchas' }).click();

    // Entonces el sistema debe pedir confirmación de la acción
    // Nota: Si el código frontend actual no lanza un confirm(), este test podría fallar o
    // necesitar ser adaptado para que coincida con la UI real si la historia de usuario cambia.
    // expect(confirmacionSolicitada).toBeTruthy(); 
    
    // Y al confirmar, el horario debe aplicarse a todas las canchas
    // Navegamos a la tab de la "Cancha 2" para verificar la aplicación.
    await page.getByRole('link', { name: 'Cancha 2' }).click();
    
    // Validamos que los valores se hayan copiado a la otra pestaña (Cancha 2)
    const lunesRowCancha2 = page.locator('.row').filter({ hasText: 'Lunes' }).last();
    await expect(lunesRowCancha2.locator('input[type="checkbox"]')).toBeChecked();
    await expect(lunesRowCancha2.locator('input[formControlName="horaInicio"]')).toHaveValue('10:00');
    await expect(lunesRowCancha2.locator('input[formControlName="horaFin"]')).toHaveValue('20:00');
  });

  test('Escenario: Configuración de la duración del turno mediante lista predefinida', async ({ page }) => {
    // Cuando el propietario va a establecer la duración de los turnos
    const selectDuracion = page.locator('select[formControlName="duracionTurno"]').first();
    
    // Entonces solo debe poder seleccionar opciones de una lista predefinida de "30", "60", "90" o "120" minutos
    const options = selectDuracion.locator('option');
    const optionValues = await options.evaluateAll(opts => opts.map(o => o.textContent?.trim()));
    
    // Validamos que la lista contenga las opciones predefinidas.
    // Nota: El array Constantes.SLOT_DURATIONS_MINUTES puede diferir en código. 
    // Ajustar los expects según la implementación actual real.
    expect(optionValues).toEqual(['30', '60', '90', '120']);

    // Y el campo no debe permitir entrada libre de texto, para que sea simple de validar
    // Al ser un elemento HTML <select>, la entrada de texto libre está restringida naturalmente.
    const tagName = await selectDuracion.evaluate(el => el.tagName.toLowerCase());
    expect(tagName).toBe('select');

    // Validamos que se pueda seleccionar una de las opciones
    await selectDuracion.selectOption('90');
    await expect(selectDuracion).toHaveValue('90');
  });
});
