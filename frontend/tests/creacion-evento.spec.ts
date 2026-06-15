import { test, expect, Page } from '@playwright/test';

const BASE = 'http://localhost:4200';

// ── Helper: navegar a /eventos/new con state de reserva ───────────────────────
async function cargarEscenario(page: Page, cfg: {
  localPreseleccionado?: boolean;
  capacidadCancha?: number;
}): Promise<void> {

  if (cfg.localPreseleccionado !== false) {
    // Ir a la app primero para tener acceso al sessionStorage del origen
    await page.goto(`${BASE}/`);

    // Guardar el state en sessionStorage
    await page.evaluate((capacidad) => {
      sessionStorage.setItem('__reserva_state__', JSON.stringify({
        turnoId: 'turno-mock-123',
        localUuid: 'local-mock-uuid',
        localNombre: 'Complejo Deportivo Patagonia',
        espacioNombre: 'Cancha de Fútbol 5 — Sintético',
        canchaUuid: 'ed0509fd-c032-4d88-8efa-ee931bcbae8d',
        deporte: 'Fútbol',
        fecha: '2026-06-15',
        horaInicio: '18:00',
        horaFin: '19:00',
        capacidad: capacidad ?? 10,
      }));
    }, cfg.capacidadCancha ?? 10);

    // Navegar a la página — el componente lee sessionStorage en ngOnInit
    await page.goto(`${BASE}/eventos/new`);
    await page.waitForSelector('[data-testid="input-cupo-maximo"]', { timeout: 10000 });

  } else {
    await page.goto(`${BASE}/eventos/new`);
    await page.waitForSelector('.alert-warning', { timeout: 10000 });
  }
}

// ── Flujos principales ────────────────────────────────────────────────────────

test.describe('E3-H01 | Flujos Principales y Alternativos', () => {
  test.beforeEach(async ({ page }) => {
    await cargarEscenario(page, { localPreseleccionado: true, capacidadCancha: 10 });
  });

  test('Creación exitosa: Ejemplo 1 (Cerrado)', async ({ page }) => {
    await page.fill('[data-testid="input-nombre"]', 'Partido de fútbol de prueba');
    await page.fill('[data-testid="input-cupo-maximo"]', '10');
    await page.fill('[data-testid="input-cupo-minimo"]', '10');
    await page.selectOption('[data-testid="select-tipo-ingreso"]', 'Cerrado');
    await page.fill('[data-testid="input-tiempo-cancelacion"]', '1');
    await page.selectOption('[data-testid="select-nivel-habilidad"]', 'Sin especificar');

    await page.click('[data-testid="btn-confirmar"]');

    await expect(page.locator('[data-testid="mensaje-exito"]')).toBeVisible({ timeout: 5000 });
    const resultado = page.locator('[data-testid="resultado-evento"]');
    await expect(resultado).toHaveAttribute('data-tipo-ingreso', 'Cerrado');
    await expect(resultado).toHaveAttribute('data-nivel-habilidad', 'Sin especificar');
  });

  test('Creación exitosa: Ejemplo 2 (Abierto con Nivel Intermedio)', async ({ page }) => {
    await page.fill('[data-testid="input-nombre"]', 'Torneo de los Miércoles');
    await page.fill('[data-testid="input-cupo-maximo"]', '10');
    await page.fill('[data-testid="input-cupo-minimo"]', '8');
    await page.selectOption('[data-testid="select-tipo-ingreso"]', 'Abierto');
    await page.fill('[data-testid="input-tiempo-cancelacion"]', '24');
    await page.selectOption('[data-testid="select-nivel-habilidad"]', 'Intermedio');

    await page.click('[data-testid="btn-confirmar"]');

    const resultado = page.locator('[data-testid="resultado-evento"]');
    await expect(resultado).toHaveAttribute('data-tipo-ingreso', 'Abierto', { timeout: 5000 });
    await expect(resultado).toHaveAttribute('data-nivel-habilidad', 'Intermedio');
  });
});

// ── Errores de validación ─────────────────────────────────────────────────────

test.describe('E3-H01 | Casos de Borde: Errores de Validación', () => {
  test.beforeEach(async ({ page }) => {
    await cargarEscenario(page, { localPreseleccionado: true, capacidadCancha: 10 });
  });

  test('Error: Cupo mínimo igual a cero', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-minimo"]', '0');
    await page.click('[data-testid="btn-confirmar"]');
    await expect(page.locator('[data-testid="mensaje-error"]'))
      .toHaveText('El cupo mínimo de jugadores debe ser mayor a cero');
  });

  test('Error: Cupo mínimo mayor al máximo', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-minimo"]', '12');
    await page.click('[data-testid="btn-confirmar"]');
    await expect(page.locator('[data-testid="mensaje-error"]'))
      .toHaveText('El cupo mínimo no puede ser mayor al cupo máximo');
  });

  test('Error: Cupo máximo superior a la capacidad', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-maximo"]', '15');
    await page.click('[data-testid="btn-confirmar"]');
    await expect(page.locator('[data-testid="mensaje-error"]'))
      .toHaveText('El cupo máximo no puede superar la capacidad máxima de la cancha');
  });

  test('Error: Tiempo de cancelación fuera de rango', async ({ page }) => {
    await page.fill('[data-testid="input-tiempo-cancelacion"]', '48');
    await page.click('[data-testid="btn-confirmar"]');
    await expect(page.locator('[data-testid="mensaje-error"]'))
      .toHaveText('El tiempo límite de cancelación de participación debe estar entre 1 y 24 horas');
  });
});

// ── Sin espacio ───────────────────────────────────────────────────────────────

test.describe('E3-H01 | Caso de Borde: Error sin espacio', () => {
  test('Intento de creación de evento sin haber seleccionado espacio', async ({ page }) => {
    await cargarEscenario(page, { localPreseleccionado: false });

    // Assert warning is visible
    await expect(page.locator('.alert-warning')).toBeVisible();
    await expect(page.locator('.alert-warning')).toContainText('No hay un espacio reservado seleccionado');

    // Confirm that confirm button is NOT visible
    const btnConfirmar = page.locator('[data-testid="btn-confirmar"]');
    await expect(btnConfirmar).not.toBeVisible();

    // Confirm that cancel button is visible
    const btnCancelar = page.locator('button:has-text("Cancelar")');
    await expect(btnCancelar).toBeVisible();
  });
});