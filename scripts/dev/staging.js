#!/usr/bin/env node

/**
 * Staging de archivos por comando (alternativa a git add)
 *
 * Uso:
 *   node scripts/dev/staging.js [opciones]
 *   node scripts/dev/staging.js all
 *   node scripts/dev/staging.js backend
 *   node scripts/dev/staging.js frontend
 *   node scripts/dev/staging.js docs
 *   node scripts/dev/staging.js <archivo>
 *   node scripts/dev/staging.js --reset
 *   node scripts/dev/staging.js --status
 */

const { execSync } = require('child_process');
const path = require('path');

// Colores
const colors = {
  reset: '\x1b[0m',
  green: '\x1b[32m',
  red: '\x1b[31m',
  blue: '\x1b[34m',
  yellow: '\x1b[33m',
  cyan: '\x1b[36m'
};

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
  process.exit(1);
}

function showUsage() {
  console.log(`
Staging de archivos (git add por comando)

Uso:
  node scripts/dev/staging.js [opciones]

Opciones:
  all              Stagear todo (git add .)
  backend          Stagear cambios de backend
  frontend         Stagear cambios de frontend
  docs             Stagear cambios de documentación
  scripts          Stagear cambios de scripts
  <archivo>        Stagear archivo específico
  --status         Ver archivos staged
  --reset          Limpiar staging (git reset)
  --help           Mostrar esta ayuda

Ejemplos:
  node scripts/dev/staging.js all
  node scripts/dev/staging.js backend
  node scripts/dev/staging.js src/main/java/MyClass.java
  node scripts/dev/staging.js --status
  node scripts/dev/staging.js --reset
  `);
}

const arg = process.argv[2];

if (!arg || arg === '--help' || arg === '-h') {
  showUsage();
  process.exit(0);
}

function executeGit(cmd) {
  try {
    const result = execSync(`git ${cmd}`, { encoding: 'utf-8' });
    return result.trim();
  } catch (err) {
    error(`Error en git: ${err.message}`);
  }
}

function main() {
  log('blue', '=================================');
  log('blue', 'Staging de archivos');
  log('blue', '=================================');
  console.log('');

  if (arg === '--status') {
    info('Archivos en staging:');
    console.log('');
    const staged = executeGit('diff --cached --name-status');
    if (staged) {
      console.log(staged);
    } else {
      log('yellow', 'No hay archivos en staging');
    }
    console.log('');
    log('blue', '=================================');
    success('Comando completado');
    process.exit(0);
  }

  if (arg === '--reset') {
    info('Limpiando staging...');
    executeGit('reset HEAD');
    console.log('');
    log('blue', '=================================');
    success('Staging limpiado');
    process.exit(0);
  }

  // Stagear por categoría
  const patterns = {
    all: '.',
    backend: 'backend/',
    frontend: 'frontend/',
    docs: 'docs/',
    scripts: 'scripts/'
  };

  let filesToAdd = patterns[arg];

  if (!filesToAdd) {
    // Asumir que es un archivo específico
    filesToAdd = arg;
    info(`Stageando archivo: ${arg}`);
  } else {
    info(`Stageando: ${arg}`);
  }

  try {
    executeGit(`add ${filesToAdd}`);
    console.log('');

    // Mostrar qué se agregó
    const status = executeGit('diff --cached --name-status');
    if (status) {
      log('cyan', 'Archivos stageados:');
      console.log(status);
    }

    console.log('');
    log('blue', '=================================');
    success('Archivos stageados');
    console.log('');
    log('yellow', 'Proximo paso: git commit -m "tu mensaje aqui"');
    log('blue', '=================================');

  } catch (err) {
    error(`No se pudo stagear: ${err.message}`);
  }
}

main();
