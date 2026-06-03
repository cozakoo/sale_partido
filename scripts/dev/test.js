#!/usr/bin/env node

/**
 * Ejecutar tests del proyecto
 *
 * Uso:
 *   node scripts/dev/test.js
 *   node scripts/dev/test.js backend
 *   node scripts/dev/test.js frontend
 *   node scripts/dev/test.js all
 *   node scripts/dev/test.js backend --coverage
 */

const { execSync } = require('child_process');
const path = require('path');
const fs = require('fs');

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

let exitCode = 0;

function log(color, message) {
  console.log(`${colors[color]}${message}${colors.reset}`);
}

function info(message) {
  log('cyan', `[*] ${message}`);
}

function success(message) {
  log('green', `[OK] ${message}`);
}

function warn(message) {
  log('yellow', `[WARN] ${message}`);
}

function error(message) {
  log('red', `[ERROR] ${message}`);
}

function showUsage() {
  console.log(`
Ejecutar tests

Uso:
  node scripts/dev/test.js [ubicación] [opciones]

Ubicaciones:
  (sin argumentos)  Tests de todo
  backend           Tests de backend (Java/JUnit)
  frontend          Tests de frontend (Angular)
  all               Todos los tests (mismo que sin argumentos)

Opciones:
  --coverage        Generar reporte de cobertura
  --watch           Modo watch (frontend)
  --fix             Ejecutar linting y fix (frontend)

Ejemplos:
  node scripts/dev/test.js
  node scripts/dev/test.js backend
  node scripts/dev/test.js frontend --coverage
  node scripts/dev/test.js backend --coverage
  `);
}

const arg1 = process.argv[2];
const arg2 = process.argv[3];

if (arg1 === '--help' || arg1 === '-h') {
  showUsage();
  process.exit(0);
}

function runTests(where, args = '') {
  try {
    if (where === 'backend') {
      if (!fs.existsSync(backendDir)) {
        error(`Backend no encontrado en: ${backendDir}`);
        return false;
      }

      info('Ejecutando tests de Backend...');

      const cmd = process.platform === 'win32'
        ? `cd ${backendDir} && mvnw.cmd verify ${args}`
        : `cd ${backendDir} && ./mvnw verify ${args}`;

      execSync(cmd, { stdio: 'inherit' });
      success('Tests de Backend completados');
      return true;

    } else if (where === 'frontend') {
      if (!fs.existsSync(frontendDir)) {
        error(`Frontend no encontrado en: ${frontendDir}`);
        return false;
      }

      info('Ejecutando tests de Frontend...');

      let cmd = 'npm test';

      if (arg2 === '--coverage') {
        cmd = 'npm run test -- --code-coverage';
      } else if (arg2 === '--watch') {
        cmd = 'npm test -- --watch';
      }

      const child = require('child_process').spawnSync('npm', cmd.split(' ').slice(1), {
        cwd: frontendDir,
        stdio: 'inherit'
      });

      if (child.status === 0) {
        success('Tests de Frontend completados');
        return true;
      } else {
        error('Tests de Frontend fallaron');
        return false;
      }
    }
  } catch (err) {
    error(`Error: ${err.message}`);
    return false;
  }
}

function main() {
  log('blue', '=================================');
  log('blue', 'Ejecutar Tests');
  log('blue', '=================================');
  console.log('');

  const location = arg1 || 'all';
  const coverage = arg2 === '--coverage' || arg1 === '--coverage';

  let success_count = 0;

  if (location === 'backend' || location === 'all') {
    const result = runTests('backend', coverage ? '-DargLine="-javaagent:$HOME/.m2/repository/org/jacoco/org.jacoco.agent/0.8.8/org.jacoco.agent-0.8.8-runtime.jar=destfile=target/jacoco.exec"' : '');
    if (result) success_count++;
    console.log('');
  }

  if (location === 'frontend' || location === 'all') {
    const result = runTests('frontend', coverage ? '--coverage' : '');
    if (result) success_count++;
    console.log('');
  }

  if (location !== 'backend' && location !== 'frontend' && location !== 'all') {
    warn(`Ubicación desconocida: ${location}`);
    showUsage();
    process.exit(1);
  }

  log('blue', '=================================');

  if (success_count === 0) {
    error('Todos los tests fallaron');
    process.exit(1);
  } else if (location === 'all' && success_count === 2) {
    success('Todos los tests pasaron');
  } else {
    success('Tests completados');
  }

  log('blue', '=================================');
  process.exit(0);
}

main();
