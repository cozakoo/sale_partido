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

function buildFormularioCreacion(cfg: CreacionConfig): string {
  const localValido = cfg.localPreseleccionado !== false;
  const capacidad = cfg.capacidadCancha ?? 10;
  const fecha = cfg.fecha ?? '2026-11-20';
  const hora = cfg.hora ?? '18:00';
  
  return `<!DOCTYPE html><html lang="es"><head><meta charset="UTF-8"></head><body>
  <div data-testid="creacion-evento-form">
    ${localValido ? `
      <input type="date" data-testid="input-fecha" value="${fecha}" readonly />
      <input type="time" data-testid="input-hora" value="${hora}" readonly />
      <input type="number" data-testid="input-cupo-minimo" value="${capacidad}" />
      <input type="number" data-testid="input-cupo-maximo" value="${capacidad}" />
      <select data-testid="select-tipo-ingreso">
        <option value="Abierto">Abierto</option>
        <option value="Con Confirmación">Con Confirmación</option>
        <option value="Cerrado" selected>Cerrado</option>
      </select>
      <input type="number" data-testid="input-tiempo-cancelacion" value="1" />
      <select data-testid="select-nivel-habilidad">
        <option value="Principiante">Principiante</option>
        <option value="Intermedio">Intermedio</option>
        <option value="Avanzado">Avanzado</option>
        <option value="Sin especificar" selected>Sin especificar</option>
      </select>
      <input type="hidden" id="capacidad-cancha" value="${capacidad}" />
      <button data-testid="btn-confirmar">Confirmar Creación</button>
    ` : `
      <button data-testid="btn-confirmar">Confirmar Creación</button>
    `}
    <div data-testid="feedback"></div>
    <div data-testid="resultado-evento" style="display:none;"></div>
  </div>
  <script>
    const localValido = ${localValido};
    const feedback = document.querySelector('[data-testid="feedback"]');
    const resultado = document.querySelector('[data-testid="resultado-evento"]');
    
    function mostrarError(mensaje) {
      const el = document.createElement('p');
      el.setAttribute('data-testid', 'mensaje-error');
      el.textContent = mensaje;
      feedback.innerHTML = '';
      feedback.appendChild(el);
    }

    const btn = document.querySelector('[data-testid="btn-confirmar"]');
    if (btn) {
      btn.addEventListener('click', () => {
        if (!localValido) {
          mostrarError('Debe seleccionar y reservar un espacio para el evento');
          return;
        }

        const cupoMin = parseInt(document.querySelector('[data-testid="input-cupo-minimo"]').value, 10);
        const cupoMax = parseInt(document.querySelector('[data-testid="input-cupo-maximo"]').value, 10);
        const tiempoCancelacion = parseInt(document.querySelector('[data-testid="input-tiempo-cancelacion"]').value, 10);
        const tipoIngreso = document.querySelector('[data-testid="select-tipo-ingreso"]').value;
        const capacidadMaxima = parseInt(document.getElementById('capacidad-cancha').value, 10);

        if (cupoMin <= 0) {
          mostrarError('El cupo mínimo de jugadores debe ser mayor a cero');
          return;
        }
        if (cupoMin > cupoMax) {
          mostrarError('El cupo mínimo no puede ser mayor al cupo máximo');
          return;
        }
        if (cupoMax > capacidadMaxima) {
          mostrarError('El cupo máximo no puede superar la capacidad máxima de la cancha');
          return;
        }
        if (tiempoCancelacion < 1 || tiempoCancelacion > 24) {
          mostrarError('El tiempo límite de cancelación de participación debe estar entre 1 y 24 horas');
          return;
        }

        feedback.innerHTML = '<p data-testid="mensaje-exito">Evento registrado exitosamente</p>';
        
        resultado.setAttribute('data-estado', 'En espera de participantes');
        resultado.setAttribute('data-tipo-ingreso', tipoIngreso);
        resultado.setAttribute('data-tiempo-cancelacion', tiempoCancelacion === 1 ? '1 hora antes del inicio' : tiempoCancelacion + ' horas antes');
        resultado.style.display = 'block';
      });
    }
  </script>
</body></html>`;
}

async function cargarEscenario(page: Page, cfg: CreacionConfig): Promise<void> {
  await page.setContent(buildFormularioCreacion(cfg));
  await page.waitForSelector('[data-testid="creacion-evento-form"]');
}

test.describe('E3-H01 | Flujos Principales y Alternativos', () => {
  test('Creación exitosa de un evento deportivo - Ejemplo 1 (Cerrado)', async ({ page }) => {
    await cargarEscenario(page, { localPreseleccionado: true, capacidadCancha: 10, fecha: '2026-11-20', hora: '18:00' });
    
    // Valores precargados o establecidos según ejemplo 1:
    // Fecha: 2026-11-20, Hora: 18:00, CupoMax: 10, CupoMin: 10, Tipo: Cerrado, TiempoCancel: 1
    await expect(page.locator('[data-testid="input-fecha"]')).toHaveValue('2026-11-20');
    await expect(page.locator('[data-testid="input-hora"]')).toHaveValue('18:00');
    
    await page.fill('[data-testid="input-cupo-maximo"]', '10');
    await page.fill('[data-testid="input-cupo-minimo"]', '10');
    await page.selectOption('[data-testid="select-tipo-ingreso"]', 'Cerrado');
    await page.fill('[data-testid="input-tiempo-cancelacion"]', '1');
    await page.selectOption('[data-testid="select-nivel-habilidad"]', 'Sin especificar');

    await page.click('[data-testid="btn-confirmar"]');

    await expect(page.locator('[data-testid="mensaje-exito"]')).toBeVisible();
    
    const resultado = page.locator('[data-testid="resultado-evento"]');
    await expect(resultado).toHaveAttribute('data-estado', 'En espera de participantes');
    await expect(resultado).toHaveAttribute('data-tipo-ingreso', 'Cerrado');
    await expect(resultado).toHaveAttribute('data-tiempo-cancelacion', '1 hora antes del inicio');
  });

  test('Creación exitosa de un evento deportivo - Ejemplo 2 (Abierto)', async ({ page }) => {
    await cargarEscenario(page, { localPreseleccionado: true, capacidadCancha: 10, fecha: '2026-11-20', hora: '20:00' });
    
    // Ejemplo 2:
    // Fecha: 2026-11-20, Hora: 20:00, CupoMax: 10, CupoMin: 8, Tipo: Abierto, TiempoCancel: 24
    await expect(page.locator('[data-testid="input-fecha"]')).toHaveValue('2026-11-20');
    await expect(page.locator('[data-testid="input-hora"]')).toHaveValue('20:00');
    
    await page.fill('[data-testid="input-cupo-maximo"]', '10');
    await page.fill('[data-testid="input-cupo-minimo"]', '8');
    await page.selectOption('[data-testid="select-tipo-ingreso"]', 'Abierto');
    await page.fill('[data-testid="input-tiempo-cancelacion"]', '24');
    await page.selectOption('[data-testid="select-nivel-habilidad"]', 'Intermedio');

    await page.click('[data-testid="btn-confirmar"]');

    await expect(page.locator('[data-testid="mensaje-exito"]')).toBeVisible();
    
    const resultado = page.locator('[data-testid="resultado-evento"]');
    await expect(resultado).toHaveAttribute('data-estado', 'En espera de participantes');
    await expect(resultado).toHaveAttribute('data-tipo-ingreso', 'Abierto');
    await expect(resultado).toHaveAttribute('data-tiempo-cancelacion', '24 horas antes');
  });
});

test.describe('E3-H01 | Casos de Borde: Errores de Validación', () => {
  test.beforeEach(async ({ page }) => {
    // Iniciamos con cancha seleccionada de capacidad 10
    await cargarEscenario(page, { localPreseleccionado: true, capacidadCancha: 10 });
  });

  test('Error: Cupo mínimo igual a cero', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-maximo"]', '10');
    await page.fill('[data-testid="input-cupo-minimo"]', '0');
    
    await page.click('[data-testid="btn-confirmar"]');
    
    await expect(page.locator('[data-testid="mensaje-error"]')).toHaveText('El cupo mínimo de jugadores debe ser mayor a cero');
  });

  test('Error: Cupo mínimo mayor al máximo', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-maximo"]', '10');
    await page.fill('[data-testid="input-cupo-minimo"]', '12');
    
    await page.click('[data-testid="btn-confirmar"]');
    
    await expect(page.locator('[data-testid="mensaje-error"]')).toHaveText('El cupo mínimo no puede ser mayor al cupo máximo');
  });

  test('Error: Cupo máximo superior a la capacidad', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-maximo"]', '15');
    await page.fill('[data-testid="input-cupo-minimo"]', '10');
    
    await page.click('[data-testid="btn-confirmar"]');
    
    await expect(page.locator('[data-testid="mensaje-error"]')).toHaveText('El cupo máximo no puede superar la capacidad máxima de la cancha');
  });

  test('Error: Tiempo de cancelación fuera de rango', async ({ page }) => {
    await page.fill('[data-testid="input-cupo-maximo"]', '10');
    await page.fill('[data-testid="input-cupo-minimo"]', '10');
    await page.fill('[data-testid="input-tiempo-cancelacion"]', '48');
    
    await page.click('[data-testid="btn-confirmar"]');
    
    await expect(page.locator('[data-testid="mensaje-error"]')).toHaveText('El tiempo límite de cancelación de participación debe estar entre 1 y 24 horas');
  });
});

test.describe('E3-H01 | Caso de Borde: Error sin espacio seleccionado', () => {
  test('Intento de creación de evento sin haber seleccionado previamente un espacio', async ({ page }) => {
    // Simula organizador que no ha seleccionado cancha
    await cargarEscenario(page, { localPreseleccionado: false });
    
    await page.click('[data-testid="btn-confirmar"]');
    
    await expect(page.locator('[data-testid="mensaje-error"]')).toHaveText('Debe seleccionar y reservar un espacio para el evento');
  });
});