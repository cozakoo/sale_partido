import { test, expect, Page } from '@playwright/test';

// ─────────────────────────────────────────────────────────────────────────────
// E3-H01 | Creación de un evento deportivo
// ─────────────────────────────────────────────────────────────────────────────

interface CreacionConfig {
  localPreseleccionado?: boolean;
  capacidadCancha?: number;
  fecha?: string;
  hora?: string;
}

async function cargarEscenario(page: Page, cfg: CreacionConfig): Promise<void> {
  const localValido = cfg.localPreseleccionado !== false;
  
  await page.route('http://localhost:8080/eventos*', async route => {
    if (route.request().method() === 'POST') {
      await route.fulfill({
        status: 201,
        json: { mensaje: 'Evento registrado exitosamente' }
      });
    } else {
      await route.continue();
    }
  });

  if (localValido) {
    const params = new URLSearchParams();
    if (cfg.fecha) params.append('fecha', cfg.fecha);
    if (cfg.hora) params.append('hora', cfg.hora);
    if (cfg.capacidadCancha) params.append('capacidad', cfg.capacidadCancha.toString());
    params.append('localId', 'mock-local-123'); // Example ID
    
    await page.goto(`http://localhost:4200/eventos/crear?${params.toString()}`);
  } else {
    await page.goto('http://localhost:4200/eventos/crear');
  }
}

test.describe('E3-H01 | Flujos Principales y Alternativos', () => {
  test.beforeEach(async ({ page }) => {
    await cargarEscenario(page, { localPreseleccionado: true, capacidadCancha: 10 });
  });

  test('Creación exitosa: Ejemplo 1 (Cerrado)', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-maximo"]', '10');
    await page.fill('[data-testid="input-cupo-minimo"]', '10');
    await page.selectOption('[data-testid="select-tipo-ingreso"]', 'Cerrado');
    await page.fill('[data-testid="input-tiempo-cancelacion"]', '1');
    await page.selectOption('[data-testid="select-nivel-habilidad"]', 'Sin especificar');

    await page.click('[data-testid="btn-confirmar"]');

    await expect(page.locator('[data-testid="mensaje-exito"]')).toBeVisible();
    const resultado = page.locator('[data-testid="resultado-evento"]');
    await expect(resultado).toHaveAttribute('data-tipo-ingreso', 'Cerrado');
    await expect(resultado).toHaveAttribute('data-nivel-habilidad', 'Sin especificar');
  });

  test('Creación exitosa: Ejemplo 2 (Abierto con Nivel Intermedio)', async ({ page }) => {
    await cargarEscenario(page, { localPreseleccionado: true, capacidadCancha: 10, fecha: '2026-11-20', hora: '20:00' });
    await page.fill('[data-testid="input-cupo-maximo"]', '10');
    await page.fill('[data-testid="input-cupo-minimo"]', '8');
    await page.selectOption('[data-testid="select-tipo-ingreso"]', 'Abierto');
    await page.fill('[data-testid="input-tiempo-cancelacion"]', '24');
    await page.selectOption('[data-testid="select-nivel-habilidad"]', 'Intermedio');

    await page.click('[data-testid="btn-confirmar"]');

    const resultado = page.locator('[data-testid="resultado-evento"]');
    await expect(resultado).toHaveAttribute('data-tipo-ingreso', 'Abierto');
    await expect(resultado).toHaveAttribute('data-nivel-habilidad', 'Intermedio');
  });
});

test.describe('E3-H01 | Casos de Borde: Errores de Validación', () => {
  test.beforeEach(async ({ page }) => {
    await cargarEscenario(page, { localPreseleccionado: true, capacidadCancha: 10 });
  });

  test('Error: Cupo mínimo igual a cero', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-minimo"]', '0');
    await page.click('[data-testid="btn-confirmar"]');
    await expect(page.locator('[data-testid="mensaje-error"]')).toHaveText('El cupo mínimo de jugadores debe ser mayor a cero');
  });

  test('Error: Cupo mínimo mayor al máximo', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-minimo"]', '12');
    await page.click('[data-testid="btn-confirmar"]');
    await expect(page.locator('[data-testid="mensaje-error"]')).toHaveText('El cupo mínimo no puede ser mayor al cupo máximo');
  });

  test('Error: Cupo máximo superior a la capacidad', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-maximo"]', '15');
    await page.click('[data-testid="btn-confirmar"]');
    await expect(page.locator('[data-testid="mensaje-error"]')).toHaveText('El cupo máximo no puede superar la capacidad máxima de la cancha');
  });

  test('Error: Tiempo de cancelación fuera de rango', async ({ page }) => {
    await page.fill('[data-testid="input-tiempo-cancelacion"]', '48');
    await page.click('[data-testid="btn-confirmar"]');
    await expect(page.locator('[data-testid="mensaje-error"]')).toHaveText('El tiempo límite de cancelación de participación debe estar entre 1 y 24 horas');
  });
});

test.describe('E3-H01 | Caso de Borde: Error sin espacio', () => {
  test('Intento de creación de evento sin haber seleccionado espacio', async ({ page }) => {
    await cargarEscenario(page, { localPreseleccionado: false });
    // Assuming if no space is selected, the confirm button either errors or isn't shown correctly.
    // We try to click it, or maybe it fails right away on the page load.
    // The previous test logic just clicked and expected the error.
    const btn = page.locator('[data-testid="btn-confirmar"]');
    if (await btn.isVisible()) {
      await btn.click();
    }
    await expect(page.locator('[data-testid="mensaje-error"]')).toHaveText('Debe seleccionar y reservar un espacio para el evento');
  });
});