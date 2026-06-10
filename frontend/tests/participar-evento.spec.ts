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
 
interface EventoConfig {
  tipoIngreso: 'ABIERTO' | 'CON_CONFIRMACION' | 'CERRADO';
  nivelRequerido?: string;
  cupoMaximo?: number;
  participantesConfirmados?: number;
  invitacion?: { estado: 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA' };
  participacionActual?: { estado: string };
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

async function cargarEscenario(page: Page, cfg: EventoConfig): Promise<void> {
  // Log browser console messages and errors to terminal
  page.on('console', msg => {
    console.log(`BROWSER CONSOLE: [${msg.type()}] ${msg.text()}`);
  });
  page.on('pageerror', err => {
    console.log(`BROWSER ERROR: ${err.message}`);
  });

  const mockEvento: any = {
    uuid: 'test-evento-uuid',
    nombre: 'Partido de prueba',
    deporte: 'Fútbol',
    descripcion: 'Evento para testing E2E',
    tipo: cfg.tipoIngreso,
    estado: 'DISPONIBLE',
    cupoMinimo: 10,
    cupoMaximo: cfg.cupoMaximo ?? 20,
    participantesConfirmados: cfg.participantesConfirmados ?? 0,
    participantes: Array.from({ length: cfg.participantesConfirmados ?? 0 }, (_, i) => ({
      uuid: `p-${i}`,
      nombre: `Jugador ${i + 1}`
    })),
    nivelRequerido: cfg.nivelRequerido ? {
      uuid: 'test-nivel-uuid',
      nombre: cfg.nivelRequerido,
      orden: 2,
      deporte: 'Fútbol'
    } : null,
    turno: {
      uuid: 'test-turno-uuid',
      fecha: '2026-07-15',
      horaInicio: '18:00',
      horaFin: '19:00',
      cancha: { uuid: 'c-1', nombre: 'Cancha A' },
      local: { uuid: 'l-1', nombre: 'Club Atlético Norte', direccion: 'Av. Siempreviva 742' }
    }
  };

  let mockParticipacion: any = null;
  if (cfg.invitacion) {
    mockParticipacion = {
      uuid: 'test-invitacion-uuid',
      estado: cfg.invitacion.estado === 'ACEPTADA' ? 'CONFIRMADO' : (cfg.invitacion.estado === 'RECHAZADA' ? 'RECHAZADO' : 'PENDIENTE'),
      esInvitacion: true
    };
  } else if (cfg.participacionActual) {
    mockParticipacion = {
      uuid: 'test-participacion-uuid',
      estado: cfg.participacionActual.estado,
      esInvitacion: false
    };
  }

  // Inject mock data into window object before document load
  await page.addInitScript((data) => {
    (window as any).__mockEventos = {
      'test-evento-uuid': data.evento
    };
    (window as any).__mockParticipaciones = {
      'test-evento-uuid': {
        'user-participante-01': data.participacion
      }
    };
  }, { evento: mockEvento, participacion: mockParticipacion });

  // Navigate to the real events detail page
  await page.goto('http://localhost:4200/eventos/test-evento-uuid');
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
 