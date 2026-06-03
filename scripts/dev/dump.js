#!/usr/bin/env node

/**
 * Exportar datos de PostgreSQL a SQL
 *
 * Uso:
 *   node scripts/dev/db-dump.js dump [archivo]
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
require('dotenv').config();

// Colores
const colors = {
  reset: '\x1b[0m',
  green: '\x1b[32m',
  red: '\x1b[31m',
  blue: '\x1b[34m',
  yellow: '\x1b[33m'
};

function log(color, message) {
  console.log(`${colors[color]}${message}${colors.reset}`);
}

function error(message) {
  log('red', `[ERROR] ${message}`);
  process.exit(1);
}

function success(message) {
  log('green', `[OK] ${message}`);
}

function info(message) {
  log('blue', `[INFO] ${message}`);
}

function showUsage() {
  console.log(`
    Exportar datos PostgreSQL

    Uso:
    node scripts/dev/db-dump.js dump [archivo]

    Ejemplos:
    node scripts/dev/db-dump.js dump
    node scripts/dev/db-dump.js dump sql/staging.sql

    Variables de entorno:
    POSTGRES_USER
    POSTGRES_DATABASE_NAME
`);
}

const command = process.argv[2];
const outputFile = process.argv[3] || 'scripts/sql/staging.sql';

if (!command || command === '--help' || command === '-h') {
  showUsage();
  process.exit(0);
}

if (command !== 'dump') {
  error(`Comando desconocido: ${command}`);
}

const pgUser = process.env.POSTGRES_USER;

const pgDatabase =
  process.env.POSTGRES_DATABASE_NAME || 'salepartido_database';

const pgServiceName = 'database';

try {
  const filePath = path.resolve(outputFile);

  fs.mkdirSync(path.dirname(filePath), { recursive: true });

  info('Generando dump...');
  info(`Database: ${pgDatabase}`);
  info(`Usuario: ${pgUser}`);
  info(`Archivo: ${filePath}`);

  const sql = execSync(
    `docker compose exec -T ${pgServiceName} pg_dump --data-only --inserts -U ${pgUser} ${pgDatabase}`,
    {
      encoding: 'utf8',
      maxBuffer: 1024 * 1024 * 50 // 50 MB
    }
  );

  fs.writeFileSync(filePath, sql);

  success(`Dump generado correctamente`);
  success(`Archivo: ${filePath}`);
} catch (err) {
  error(`Error generando dump: ${err.message}`);
}