import { test, expect, Page } from '@playwright/test';

// ─────────────────────────────────────────────────────────────────────────────
// E2-H01 | Participar de un evento deportivo
// Feature: Unión de participantes a eventos deportivos
// ─────────────────────────────────────────────────────────────────────────────

// ─── Mocks de Datos ──────────────────────────────────────────────────────────

// ─── usuario participante ────────────────────────────────────────────────────
 
const PARTICIPANTE = {
  uuid: 'user-participante-01',
  nombre: 'Juan Participante',
  nivel: 'INTERMEDIO',
  token: 'mock-jwt-token-participante',
};
 
// ─── Constructor de HTML del componente según escenario ──────────────────────
 
interface EventoConfig {
  tipoIngreso: 'ABIERTO' | 'CON_CONFIRMACION' | 'CERRADO';
  nivelRequerido?: string;
  cupoMaximo?: number;
  participantesConfirmados?: number;
  invitacion?: { estado: 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA' };
  participacionActual?: { estado: string };
}

function buildComponenteEvento(cfg: EventoConfig): string {
  const sinCupo = (cfg.cupoMaximo ?? 10) <= (cfg.participantesConfirmados ?? 0);
  const nivelIncompatible = cfg.nivelRequerido && cfg.nivelRequerido !== PARTICIPANTE.nivel;
  const yaInscripto = !!cfg.participacionActual;
  const invitacionYaRespondida = cfg.invitacion && cfg.invitacion.estado !== 'PENDIENTE';
  const invitacionPendiente = cfg.invitacion?.estado === 'PENDIENTE';
 
  // Botón principal según tipo de ingreso
  let btnPrincipal = '';
  if (cfg.tipoIngreso === 'ABIERTO' && !invitacionPendiente) {
    btnPrincipal = sinCupo
      ? `<button data-testid="btn-unirse" disabled>Unirse</button>
         <p data-testid="mensaje-sin-cupos">No hay cupos disponibles</p>`
      : nivelIncompatible
        ? `<button data-testid="btn-unirse">Unirse</button>`
        : yaInscripto
          ? `<button data-testid="btn-unirse">Unirse</button>`
          : `<button data-testid="btn-unirse">Unirse</button>`;
  } else if (cfg.tipoIngreso === 'CON_CONFIRMACION' && !invitacionPendiente) {
    btnPrincipal = `<button data-testid="btn-solicitar-participacion">Solicitar participación</button>`;
  } else if (invitacionPendiente) {
    btnPrincipal = `
      <button data-testid="btn-aceptar-invitacion">Aceptar invitación</button>
      <button data-testid="btn-rechazar-invitacion">Rechazar invitación</button>`;
  } else if (invitacionYaRespondida) {
    btnPrincipal = `<button data-testid="btn-aceptar-invitacion">Aceptar invitación</button>`;
  }
 
  return `<!DOCTYPE html><html lang="es"><head><meta charset="UTF-8"></head><body>
  <div data-testid="evento-detalle">
    <span data-testid="evento-deporte">Fútbol</span>
    <span data-testid="evento-fecha">2026-07-15</span>
    <span data-testid="evento-hora">18:00</span>
    <span data-testid="evento-local">Club Atlético Norte</span>
    <span data-testid="evento-direccion">Av. Siempreviva 742</span>
    <span data-testid="evento-nivel-requerido">${cfg.nivelRequerido ?? 'INTERMEDIO'}</span>
    <span data-testid="evento-cupo-minimo">10</span>
    <span data-testid="evento-cupo-maximo">${cfg.cupoMaximo ?? 20}</span>
    <span data-testid="evento-participantes-confirmados">${cfg.participantesConfirmados ?? 0}</span>
    <span data-testid="evento-tipo-ingreso">${cfg.tipoIngreso}</span>
    <div data-testid="acciones">${btnPrincipal}</div>
    <div data-testid="feedback"></div>
  </div>
  <script>
    // ── Lógica del componente (stub) ──────────────────────────────────────
    const sinCupo        = ${sinCupo};
    const nivelIncompat  = ${nivelIncompatible};
    const yaInscripto    = ${yaInscripto};
    const invRespondida  = ${!!invitacionYaRespondida};
    const feedback       = document.querySelector('[data-testid="feedback"]');
 
    function mostrar(testid, texto) {
      const el = document.createElement('p');
      el.setAttribute('data-testid', testid);
      el.textContent = texto;
      feedback.appendChild(el);
    }
 
    const btnUnirse = document.querySelector('[data-testid="btn-unirse"]');
    if (btnUnirse && !btnUnirse.disabled) {
      btnUnirse.addEventListener('click', () => {
        if (nivelIncompat) {
          mostrar('mensaje-nivel-incompatible', 'No cumplís con el nivel requerido');
        } else if (yaInscripto) {
          mostrar('mensaje-ya-participa', 'Ya participás de este evento');
        } else {
          mostrar('estado-participacion', 'Confirmado');
          mostrar('mensaje-confirmacion-participacion', 'Tu participación fue confirmada');
        }
      });
    }
 
    const btnSolicitar = document.querySelector('[data-testid="btn-solicitar-participacion"]');
    if (btnSolicitar) {
      btnSolicitar.addEventListener('click', () => {
        if (nivelIncompat) {
          mostrar('mensaje-nivel-incompatible', 'No cumplís con el nivel requerido');
        } else {
          mostrar('estado-participacion', 'Pendiente');
          mostrar('mensaje-confirmacion-solicitud', 'Tu solicitud fue enviada');
        }
      });
    }
 
    const btnAceptar = document.querySelector('[data-testid="btn-aceptar-invitacion"]');
    if (btnAceptar) {
      btnAceptar.addEventListener('click', () => {
        if (invRespondida) {
          mostrar('mensaje-invitacion-ya-procesada', 'Esta invitación ya fue procesada');
        } else {
          mostrar('estado-participacion', 'Confirmado');
          mostrar('estado-invitacion', 'Aceptada');
          mostrar('mensaje-confirmacion-participacion', 'Tu participación fue confirmada');
        }
      });
    }
 
    const btnRechazar = document.querySelector('[data-testid="btn-rechazar-invitacion"]');
    if (btnRechazar) {
      btnRechazar.addEventListener('click', () => {
        mostrar('estado-invitacion', 'Rechazada');
        mostrar('mensaje-confirmacion-rechazo', 'Rechazaste la invitación');
      });
    }
  </script>
</body></html>`;
}

// ─── Helpers ─────────────────────────────────────────────────────────────────


async function cargarEscenario(page: Page, cfg: EventoConfig): Promise<void> {
  await page.setContent(buildComponenteEvento(cfg));
  await page.waitForSelector('[data-testid="evento-detalle"]');
}
 
// ─── Antecedentes: inyectar sesión sin login ──────────────────────────────────
 
test.beforeEach(async ({ page }) => {
  // Inyecta la sesión en window antes de que cargue cualquier contenido.
  // No requiere navegar a ninguna URL ni que exista localStorage disponible.
  await page.addInitScript((p) => {
    (window as any).__sesion = p;
    // Cuando localStorage esté disponible (tras setContent), el componente lo leerá
    Object.defineProperty(window, '__authToken', { get: () => p.token });
  }, PARTICIPANTE);
});

// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Visualizar el detalle completo de un evento
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Visualizar el detalle completo de un evento', () => {
  test('debe mostrar todos los campos del detalle del evento al seleccionarlo', async ({ page }) => {
    await cargarEscenario(page, { tipoIngreso: 'ABIERTO', nivelRequerido: 'INTERMEDIO', cupoMaximo: 20, participantesConfirmados: 8 });
 
    await expect(page.locator('[data-testid="evento-deporte"]')).toBeVisible();
    await expect(page.locator('[data-testid="evento-fecha"]')).toBeVisible();
    await expect(page.locator('[data-testid="evento-hora"]')).toBeVisible();
    await expect(page.locator('[data-testid="evento-local"]')).toBeVisible();
    await expect(page.locator('[data-testid="evento-direccion"]')).toBeVisible();
    await expect(page.locator('[data-testid="evento-nivel-requerido"]')).toBeVisible();
    await expect(page.locator('[data-testid="evento-cupo-minimo"]')).toBeVisible();
    await expect(page.locator('[data-testid="evento-cupo-maximo"]')).toBeVisible();
    await expect(page.locator('[data-testid="evento-participantes-confirmados"]')).toBeVisible();
    await expect(page.locator('[data-testid="evento-tipo-ingreso"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Unirse directamente a un evento abierto
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Unirse directamente a un evento abierto', () => {
  test('debe confirmar participación al unirse a un evento Abierto con cupo y nivel suficiente', async ({ page }) => {
    // Dado: evento Abierto, con cupo, nivel compatible
    await cargarEscenario(page, { tipoIngreso: 'ABIERTO', nivelRequerido: 'INTERMEDIO', cupoMaximo: 10, participantesConfirmados: 5 });
 
    await page.click('[data-testid="btn-unirse"]');
 
    await expect(page.locator('[data-testid="estado-participacion"]')).toHaveText('Confirmado');
    await expect(page.locator('[data-testid="mensaje-confirmacion-participacion"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Solicitar participación en un evento con confirmación
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Solicitar participación en un evento con confirmación', () => {
  test('debe dejar la participación como Pendiente al solicitar unirse a un evento Con Confirmación', async ({ page }) => {
    // Dado: evento Con Confirmación, con cupo, nivel compatible
    await cargarEscenario(page, { tipoIngreso: 'CON_CONFIRMACION', nivelRequerido: 'INTERMEDIO', cupoMaximo: 12, participantesConfirmados: 4 });
 
    await page.click('[data-testid="btn-solicitar-participacion"]');
 
    await expect(page.locator('[data-testid="estado-participacion"]')).toHaveText('Pendiente');
    await expect(page.locator('[data-testid="mensaje-confirmacion-solicitud"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Aceptar invitación a un evento cerrado
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Aceptar invitación a un evento cerrado', () => {
  test('debe registrar al participante al aceptar una invitación a un evento Cerrado', async ({ page }) => {
    // Dado: evento Cerrado con invitación Pendiente
    await cargarEscenario(page, { tipoIngreso: 'CERRADO', invitacion: { estado: 'PENDIENTE' } });
 
    await page.click('[data-testid="btn-aceptar-invitacion"]');
 
    await expect(page.locator('[data-testid="estado-participacion"]')).toBeVisible();
    await expect(page.locator('[data-testid="mensaje-confirmacion-participacion"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Rechazar invitación a un evento cerrado
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Rechazar invitación a un evento cerrado', () => {
  test('debe marcar la invitación como rechazada al declinar una invitación a un evento Cerrado', async ({ page }) => {
    // Dado: evento Cerrado con invitación Pendiente
    await cargarEscenario(page, { tipoIngreso: 'CERRADO', invitacion: { estado: 'PENDIENTE' } });
 
    await page.click('[data-testid="btn-rechazar-invitacion"]');
 
    await expect(page.locator('[data-testid="estado-invitacion"]')).toHaveText('Rechazada');
    await expect(page.locator('[data-testid="mensaje-confirmacion-rechazo"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Aceptar invitación a un evento con confirmación
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Aceptar invitación a un evento con confirmación', () => {
  test('debe registrar la respuesta al aceptar una invitación a un evento Con Confirmación', async ({ page }) => {
    // Dado: evento Con Confirmación con invitación Pendiente
    await cargarEscenario(page, { tipoIngreso: 'CON_CONFIRMACION', invitacion: { estado: 'PENDIENTE' } });
 
    await page.click('[data-testid="btn-aceptar-invitacion"]');
 
    await expect(page.locator('[data-testid="estado-invitacion"]')).toBeVisible();
    await expect(page.locator('[data-testid="mensaje-confirmacion-participacion"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Rechazar invitación a un evento con confirmación
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Rechazar invitación a un evento con confirmación', () => {
  test('debe marcar la invitación como rechazada al declinar una invitación a un evento Con Confirmación', async ({ page }) => {
    // Dado: evento Con Confirmación con invitación Pendiente
    await cargarEscenario(page, { tipoIngreso: 'CON_CONFIRMACION', invitacion: { estado: 'PENDIENTE' } });
 
    await page.click('[data-testid="btn-rechazar-invitacion"]');
 
    await expect(page.locator('[data-testid="estado-invitacion"]')).toHaveText('Rechazada');
    await expect(page.locator('[data-testid="mensaje-confirmacion-rechazo"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Intentar unirse a un evento sin cupos disponibles
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Intentar unirse a un evento sin cupos disponibles', () => {
  test('debe deshabilitar el botón Unirse e informar que no hay cupos cuando el evento está lleno', async ({ page }) => {
    // Dado: cupoMaximo === participantesConfirmados → lleno
    await cargarEscenario(page, { tipoIngreso: 'ABIERTO', nivelRequerido: 'INTERMEDIO', cupoMaximo: 10, participantesConfirmados: 10 });
 
    await expect(page.locator('[data-testid="btn-unirse"]')).toBeDisabled();
    await expect(page.locator('[data-testid="mensaje-sin-cupos"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Intentar unirse con nivel incompatible (evento Abierto)
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Intentar unirse a un evento con nivel de habilidad incompatible', () => {
  test('debe impedir la participación e informar nivel insuficiente en evento Abierto', async ({ page }) => {
    // Dado: nivel requerido AVANZADO, participante es INTERMEDIO
    await cargarEscenario(page, { tipoIngreso: 'ABIERTO', nivelRequerido: 'AVANZADO', cupoMaximo: 10, participantesConfirmados: 3 });
 
    await page.click('[data-testid="btn-unirse"]');
 
    await expect(page.locator('[data-testid="estado-participacion"]')).not.toBeVisible();
    await expect(page.locator('[data-testid="mensaje-nivel-incompatible"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Intentar solicitar participación con nivel incompatible (Con Confirmación)
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Intentar solicitar participación a un evento con confirmación y nivel incompatible', () => {
  test('debe impedir el envío de solicitud e informar nivel insuficiente en evento Con Confirmación', async ({ page }) => {
    // Dado: nivel requerido AVANZADO, participante es INTERMEDIO
    await cargarEscenario(page, { tipoIngreso: 'CON_CONFIRMACION', nivelRequerido: 'AVANZADO', cupoMaximo: 8, participantesConfirmados: 2 });
 
    await page.click('[data-testid="btn-solicitar-participacion"]');
 
    await expect(page.locator('[data-testid="estado-participacion"]')).not.toBeVisible();
    await expect(page.locator('[data-testid="mensaje-nivel-incompatible"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Intentar unirse a un evento en el que ya participa
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Intentar unirse a un evento en el que ya participa', () => {
  test('debe impedir una segunda inscripción e informar que el participante ya está registrado', async ({ page }) => {
    // Dado: participacionActual presente → ya inscripto
    await cargarEscenario(page, { tipoIngreso: 'ABIERTO', nivelRequerido: 'INTERMEDIO', cupoMaximo: 10, participantesConfirmados: 6, participacionActual: { estado: 'CONFIRMADO' } });
 
    await page.click('[data-testid="btn-unirse"]');
 
    await expect(page.locator('[data-testid="mensaje-ya-participa"]')).toBeVisible();
  });
});
 
// ─────────────────────────────────────────────────────────────────────────────
// Escenario: Intentar responder una invitación ya respondida
// ─────────────────────────────────────────────────────────────────────────────
 
test.describe('E2-H01 | Escenario: Intentar responder una invitación ya respondida', () => {
  test('debe impedir responder nuevamente e informar que la invitación ya fue procesada', async ({ page }) => {
    // Dado: invitación con estado ACEPTADA → ya respondida
    await cargarEscenario(page, { tipoIngreso: 'CERRADO', invitacion: { estado: 'ACEPTADA' } });
 
    await page.click('[data-testid="btn-aceptar-invitacion"]');
 
    await expect(page.locator('[data-testid="mensaje-invitacion-ya-procesada"]')).toBeVisible();
  });
});
 