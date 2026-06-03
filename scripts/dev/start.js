#!/usr/bin/env node

/**
 * Levantar ambiente de desarrollo completo
 *
 * Uso:
 *   node scripts/dev/start.js
 *   node scripts/dev/start.js backend-only
 *   node scripts/dev/start.js frontend-only
 *   node scripts/dev/start.js with-db
 */

const { spawn, execSync } = require('child_process');
const path = require('path');
const fs = require('fs');
require('dotenv').config();

const projectRoot = path.resolve(__dirname, '..', '..');
const backendDir = path.join(projectRoot, 'backend');
const frontendDir = path.join(projectRoot, 'frontend');

// Colores
const colors = {
  reset: '\x1b[0m',
  green: '\x1b[32m',
  red: '\x1b[31m',
  blue: '\x1b[34m',
  yellow: '\x1b[33m',
  cyan: '\x1b[36m'
};

let processes = [];

function log(color, message) {
  console.log(`${colors[color]}${message}${colors.reset}`);
}

function info(message) {
  log('cyan', `[*] ${message}`);
}

function success(message) {
  log('green', `[OK] ${message}`);
}

function error(message) {
  log('red', `[ERROR] ${message}`);
  stopAll();
  process.exit(1);
}

function showUsage() {
  console.log(`
Levantar ambiente de desarrollo

Uso:
  node scripts/dev/start.js [opciones]

Opciones:
  (sin argumentos)  Levantar todo (backend + frontend + db)
  backend-only      Solo backend
  frontend-only     Solo frontend
  with-db           Backend + frontend + base de datos

Ejemplos:
  node scripts/dev/start.js
  node scripts/dev/start.js backend-only
  node scripts/dev/start.js with-db
  `);
}

const arg = process.argv[2];

if (arg === '--help' || arg === '-h') {
  showUsage();
  process.exit(0);
}

function startService(name, dir, command, args) {
  return new Promise((resolve) => {
    info(`Levantando ${name}...`);

    const child = spawn(command, args, {
      cwd: dir,
      stdio: 'inherit',
      shell: true
    });

    processes.push({ name, process: child });

    child.on('error', (err) => {
      error(`Error en ${name}: ${err.message}`);
    });

    setTimeout(() => {
      success(`${name} levantado`);
      resolve();
    }, 2000);
  });
}

function stopAll() {
  log('yellow', '\nParando servicios...\n');

  processes.forEach(({ name, process: proc }) => {
    try {
      proc.kill('SIGTERM');
      log('yellow', `[*] Parado: ${name}`);
    } catch (err) {
      log('yellow', `[*] No se pudo parar: ${name}`);
    }
  });

  processes = [];
}

async function main() {
  log('blue', '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
  log('blue', '🚀 Ambiente de Desarrollo');
  log('blue', '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');

  // Validar directorios
  if (!fs.existsSync(backendDir)) {
    error(`Backend no encontrado en: ${backendDir}`);
  }
  if (!fs.existsSync(frontendDir)) {
    error(`Frontend no encontrado en: ${frontendDir}`);
  }

  // Determinar qué levantar
  const startBackend = arg !== 'frontend-only';
  const startFrontend = arg !== 'backend-only';
  const startDb = arg === 'with-db' || (arg !== 'backend-only' && arg !== 'frontend-only');

  try {
    // Levantar base de datos (Docker)
    if (startDb && arg === 'with-db') {
      info('Levantando Docker Compose...');
      execSync('docker compose up -d', {
        cwd: projectRoot,
        stdio: 'inherit'
      });
      success('Docker Compose levantado');
      await new Promise(resolve => setTimeout(resolve, 3000));
    }

    // Levantar backend
    if (startBackend) {
      await startService(
        'Backend (Spring Boot)',
        backendDir,
        process.platform === 'win32' ? 'mvnw.cmd' : './mvnw',
        ['spring-boot:run']
      );
    }

    // Levantar frontend
    if (startFrontend) {
      await startService(
        'Frontend (Angular)',
        frontendDir,
        'npm',
        ['start']
      );
    }

    console.log('');
    log('green', '=================================');
    log('green', 'Ambiente levantado');
    log('green', '=================================');
    console.log('');

    if (startBackend) {
      log('cyan', 'Backend: http://localhost:8080');
      log('cyan', 'API Docs: http://localhost:8080/swagger-ui.html');
    }
    if (startFrontend) {
      log('cyan', 'Frontend: http://localhost:4200');
    }

    console.log('');
    log('yellow', 'Presiona Ctrl+C para detener');

  } catch (err) {
    error(`Error durante startup: ${err.message}`);
  }
}

// Manejar señales de terminación
process.on('SIGINT', () => {
  stopAll();
  process.exit(0);
});

process.on('SIGTERM', () => {
  stopAll();
  process.exit(0);
});

main().catch(err => {
  error(err.message);
});
