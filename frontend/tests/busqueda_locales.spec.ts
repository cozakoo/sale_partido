import { test, expect } from '@playwright/test';

test.describe('Búsqueda de locales deportivos - Jugador', () => {

  const mockLocales = [
    {
      uuid: '1', nombre: 'Club Deportivo Centro', ubicacion: 'Centro',
      deportes: ['Fútbol', 'Tenis', 'Pádel'], horario: '08:00-23:00',
      telefono: '+54 280 411-1001',
      descripcion: 'Complejo deportivo céntrico',
      deportesDisponibles: ['Fútbol', 'Tenis', 'Pádel'],
      canchas: [
        { uuid: 'c1', nombre: 'Cancha 1', deporte: 'Fútbol', capacidad: 11, configuracionesHorarios: [] },
        { uuid: 'c2', nombre: 'Cancha 2', deporte: 'Tenis', capacidad: 2, configuracionesHorarios: [] }
      ]
    },
    {
      uuid: '2', nombre: 'Estadio del Sur', ubicacion: 'Sur',
      deportes: ['Fútbol', 'Básquet'], horario: '09:00-22:00',
      telefono: '+54 280 411-1002',
      descripcion: 'Canchas de fútbol 11',
      deportesDisponibles: ['Fútbol', 'Básquet'],
      canchas: [
        { uuid: 'c3', nombre: 'Cancha 3', deporte: 'Fútbol', capacidad: 11, configuracionesHorarios: [] }
      ]
    }
  ];

  test.beforeEach(async ({ page }) => {
    // Mock las llamadas al backend para obtener locales
    await page.route('http://localhost:8080/locales*', async route => {
      const url = new URL(route.request().url());
      let resultado = [...mockLocales];

      if (url.searchParams.get('texto')) {
        const texto = url.searchParams.get('texto')?.toLowerCase() || '';
        resultado = resultado.filter(l =>
          l.nombre.toLowerCase().includes(texto)
        );
      }

      if (url.searchParams.get('ubicacion')) {
        resultado = resultado.filter(l => l.ubicacion === url.searchParams.get('ubicacion'));
      }

      if (url.searchParams.get('tipoDeporte')) {
        resultado = resultado.filter(l => l.deportes.includes(url.searchParams.get('tipoDeporte') || ''));
      }

      await route.fulfill({ json: resultado });
    });

    await page.goto('http://localhost:4200/locales');
  });

  test('Escenario: Visualizar listado con datos detallados', async ({ page }) => {
    // Esperar a que se cargue el listado
    await page.waitForSelector('.local-card', { timeout: 5000 });
    const primerLocal = page.locator('.local-card').first();
    await expect(primerLocal).toContainText('Club Deportivo Centro');
    await expect(primerLocal).toContainText('Centro');
    await expect(primerLocal).toContainText('Fútbol');
  });

  test('Escenario: Filtrar locales por dirección, deporte y horario', async ({ page }) => {
    // Filtrar por Ubicación
    await page.selectOption('select#filtroUbicacion', 'Centro');
    // Filtrar por Deporte
    await page.selectOption('select#filtroDeporte', 'Tenis');

    await page.getByRole('button', { name: 'Buscar' }).click();

    // Debe haber al menos un local en Centro con Tenis en sus deportes disponibles
    const cards = page.locator('.local-card');
    const count = await cards.count();
    await expect(count).toBeGreaterThan(0);
  });

  test('Escenario: No se encontraron resultados', async ({ page }) => {
    // Buscamos por un término que no existe
    await page.selectOption('select#filtroDeporte', 'Voley');
    await page.getByRole('button', { name: 'Buscar' }).click();

    await expect(page.getByText('No se encontraron locales')).toBeVisible();
  });

  test('Escenario: Actualización dinámica (limpieza de filtros)', async ({ page }) => {
    // Filtrar por una ubicación específica
    await page.selectOption('select#filtroUbicacion', 'Sur');
    await page.getByRole('button', { name: 'Buscar' }).click();
    const cardsFiltered = await page.locator('.local-card').count();
    await expect(cardsFiltered).toBeGreaterThan(0);

    // Limpiar filtros
    await page.getByRole('button', { name: 'Limpiar' }).click();

    // Debe haber más locales después de limpiar
    const cardsTotal = await page.locator('.local-card').count();
    await expect(cardsTotal).toBeGreaterThanOrEqual(cardsFiltered);
  });
});