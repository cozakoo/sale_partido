import { test, expect } from '@playwright/test';

test.describe('Búsqueda de locales deportivos - Jugador', () => {

  const mockLocales = [
    { 
      uuid: '1', nombre: 'Local Norte', ubicacion: 'Juan B Justo 1900', 
      deportes: ['Fútbol', 'Tenis'], horario: '08:00-22:00' 
    },
    { 
      uuid: '2', nombre: 'Local Sur', ubicacion: 'Avenida Roca 1000', 
      deportes: ['Fútbol'], horario: '10:00-20:00' 
    }
  ];

  test.beforeEach(async ({ page }) => {
    // Usamos encodeURIComponent para manejar espacios y caracteres especiales en la URL
    await page.route('**/locales?*', async route => {
      const url = route.request().url();
      let resultado = mockLocales;
      
      if (url.includes(encodeURIComponent('Juan B Justo 1900'))) resultado = [mockLocales[0]];
      if (url.includes(encodeURIComponent('Avenida Roca 1000'))) resultado = [mockLocales[1]];
      if (url.includes('deporte=Tenis')) resultado = [mockLocales[0]];
      
      await route.fulfill({ json: resultado });
    });
    
    await page.goto('http://localhost:4200/locales');
  });

  test('Escenario: Visualizar listado con datos detallados', async ({ page }) => {
    const primerLocal = page.locator('.card').first();
    // Validamos que el texto contenido sea la dirección exacta
    await expect(primerLocal).toContainText('Local Norte');
    await expect(primerLocal).toContainText('Juan B Justo 1900');
    await expect(primerLocal).toContainText('Fútbol');
    await expect(primerLocal).toContainText('08:00-22:00');
  });

  test('Escenario: Filtrar locales por dirección', async ({ page }) => {
    // Es vital que el value del select coincida con la dirección
    await page.selectOption('select[name="ubicacion"]', 'Juan B Justo 1900');
    await page.getByRole('button', { name: 'Buscar' }).click();

    await expect(page.locator('.card')).toHaveCount(1);
    await expect(page.locator('.card')).toContainText('Juan B Justo 1900');
  });

  test('Escenario: Filtrar locales por deporte', async ({ page }) => {
    await page.selectOption('select[name="deporte"]', 'Tenis');
    await page.getByRole('button', { name: 'Buscar' }).click();

    await expect(page.locator('.card')).toHaveCount(1);
    await expect(page.locator('.card')).toContainText('Local Norte');
  });

  test('Escenario: No se encontraron resultados', async ({ page }) => {
    await page.route('**/locales?deporte=Padel', route => route.fulfill({ json: [] }));
    
    await page.selectOption('select[name="deporte"]', 'Padel');
    await page.getByRole('button', { name: 'Buscar' }).click();

    await expect(page.getByText('No se encontraron resultados para la búsqueda realizada')).toBeVisible();
  });

  test('Escenario: Actualización dinámica (limpieza de filtros)', async ({ page }) => {
    // Aplicar filtro por dirección
    await page.selectOption('select[name="ubicacion"]', 'Avenida Roca 1000');
    await page.getByRole('button', { name: 'Buscar' }).click();
    await expect(page.locator('.card')).toHaveCount(1);

    // Limpiar filtro
    await page.selectOption('select[name="ubicacion"]', '');
    await page.getByRole('button', { name: 'Buscar' }).click();
    
    await expect(page.locator('.card')).toHaveCount(2);
  });
});