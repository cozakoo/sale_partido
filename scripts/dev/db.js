#!/usr/bin/env node

/**
 * Ejecutar SQL directamente desde archivos
 *
 * Uso:
 *   node scripts/dev/db.js run <archivo.sql>
 *   node scripts/dev/db.js run scripts/sql/seed-locales.sql
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
require('dotenv').config();

const command = process.argv[2];
const sqlFile = process.argv[3];

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
Ejecutar SQL desde archivos

Uso:
  node scripts/dev/db.js <comando> [archivo]

Comandos:
  run <archivo>     Ejecutar archivo SQL

Ejemplos:
  node scripts/dev/db.js run scripts/sql/seed-locales.sql
  node scripts/dev/db.js run my-migration.sql

Variables de entorno requeridas:
  POSTGRES_USER      (default: admin)
  POSTGRES_PASSWORD  (default: admin)
  POSTGRES_HOST      (default: localhost)
  POSTGRES_PORT      (default: 5432)
  POSTGRES_DATABASE_NAME (default: salepartido_database)
  `);
}

if (!command || command === '--help' || command === '-h') {
  showUsage();
  process.exit(0);
}

if (command === 'run') {
  if (!sqlFile) {
    error('Debes especificar un archivo SQL');
  }

  const filePath = path.resolve(sqlFile);

  if (!fs.existsSync(filePath)) {
    error(`Archivo no encontrado: ${filePath}`);
  }

  // Leer archivo SQL
  const sqlContent = fs.readFileSync(filePath, 'utf-8');

  if (!sqlContent.trim()) {
    error('El archivo SQL está vacío');
  }

  // Configuración de PostgreSQL
  const pgUser = process.env.POSTGRES_USER || 'admin';
  const pgPassword = process.env.POSTGRES_PASSWORD || 'admin';
  const pgHost = process.env.POSTGRES_HOST || 'localhost';
  const pgPort = process.env.POSTGRES_PORT || '5432';
  const pgDatabase = process.env.POSTGRES_DATABASE_NAME || 'salepartido_database';

  info(`Conectando a PostgreSQL...`);
  info(`Host: ${pgHost}:${pgPort}`);
  info(`Database: ${pgDatabase}`);
  info(`Usuario: ${pgUser}`);

  try {
    // Crear archivo temporal con SQL
    const tempFile = path.join('/tmp', `sql_${Date.now()}.sql`);
    fs.writeFileSync(tempFile, sqlContent);

    // Ejecutar psql
    const cmd = `psql -h ${pgHost} -p ${pgPort} -U ${pgUser} -d ${pgDatabase} -f ${tempFile}`;

    console.log('');
    info('Ejecutando SQL...');
    console.log('');

    process.env.PGPASSWORD = pgPassword;
    execSync(cmd, { stdio: 'inherit' });

    // Limpiar archivo temporal
    fs.unlinkSync(tempFile);

    success(`SQL ejecutado: ${path.basename(filePath)}`);

  } catch (err) {
    error(`Error ejecutando SQL: ${err.message}`);
  }
} else {
  error(`Comando desconocido: ${command}`);
}
