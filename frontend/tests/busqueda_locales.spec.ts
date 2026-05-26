import { test, expect } from '@playwright/test';

test.describe('Búsqueda / Listado de Locales', () => {

  test.beforeEach(async ({ page }) => {
    // Interceptar la petición de locales para el listado principal
    await page.route('**/locales', async route => {
      // Si la URL termina en /locales, devolvemos un arreglo simulado
      if (route.request().method() === 'GET') {
        await route.fulfill({
          json: [
            { uuid: '1', nombre: 'Local Norte' },
            { uuid: '2', nombre: 'Local Sur' },
            { uuid: '3', nombre: 'Complejo Centro' }
          ]
        });
      } else {
        await route.continue();
      }
    });
  });

  test('Escenario 1: Mostrar la lista de locales disponibles', async ({ page }) => {
    // Cuando el usuario ingresa a la vista de locales
    await page.goto('http://localhost:4200/locales');
    
    // Entonces se debe visualizar el título "Mis Locales"
    await expect(page.getByRole('heading', { name: 'Mis Locales' })).toBeVisible();

    // Y el sistema debe mostrar los locales devueltos por la API
    const localCards = page.locator('.card-body .card-title');
    await expect(localCards).toHaveCount(3);
    
    const nombres = await localCards.allTextContents();
    expect(nombres).toEqual(['Local Norte', 'Local Sur', 'Complejo Centro']);
  });

  test('Escenario 2: Navegar al detalle de un local específico', async ({ page }) => {
    // Dado que se listan los locales en la pantalla principal
    await page.goto('http://localhost:4200/locales');
    
    // Y hacemos un mock para la futura petición del detalle del "Local Norte"
    await page.route('**/locales/1', async route => {
      await route.fulfill({ json: { uuid: '1', nombre: 'Local Norte' } });
    });
    
    await page.route('**/locales/1/canchas?view=resumen', async route => {
      await route.fulfill({ json: [] });
    });

    // Cuando el usuario hace clic en el local "Local Norte"
    const cardLocalNorte = page.locator('.card').filter({ hasText: 'Local Norte' });
    await cardLocalNorte.click();

    // Entonces debe navegar a la vista de detalle de dicho local
    await page.waitForURL('http://localhost:4200/locales/1');
    expect(page.url()).toBe('http://localhost:4200/locales/1');
  });

  test('Escenario 3: Mostrar estado cuando no hay locales disponibles', async ({ page }) => {
    // Dado que la API no devuelve ningún local
    await page.route('**/locales', async route => {
      await route.fulfill({ json: [] });
    });

    // Cuando el usuario ingresa a la vista de locales
    await page.goto('http://localhost:4200/locales');

    // Entonces no se deben visualizar tarjetas de locales
    const localCards = page.locator('.card-body .card-title');
    await expect(localCards).toHaveCount(0);
    
    // (Opcional) Acá se podría verificar que exista un texto como "No hay locales disponibles"
    // si el diseño del frontend lo contemplara en el futuro. Por ahora aseguramos que la lista
    // esté vacía sin arrojar error.
  });

});
