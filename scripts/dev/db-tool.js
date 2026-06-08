#!/usr/bin/env node

/**
 * Gestor de Base de Datos para Desarrollo
 * Unifica comandos para drop, truncate, carga de staging, seeding y dump.
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const http = require('http');
const readline = require('readline');
const dotenv = require('dotenv');

// Cargar variables de entorno desde la raíz del proyecto
dotenv.config({ path: path.resolve(__dirname, '../../.env') });

// Colores para consola
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

function error(message) {
  log('red', `[ERROR] ${message}`);
  process.exit(1);
}

function success(message) {
  log('green', `[OK] ${message}`);
}

function info(message) {
  log('cyan', `[INFO] ${message}`);
}

function warn(message) {
  log('yellow', `[WARNING] ${message}`);
}

function showUsage() {
  console.log(`
GESTOR DE BASE DE DATOS (DEV)

Uso:
  node scripts/dev/db-tool.js [comando | número_opción]

Si se ejecuta sin argumentos, se abrirá un menú interactivo.

Comandos / Opciones:
  
  [ Acciones simples ]
  1, drop                    Ejecuta drop.sql únicamente (sin reiniciar contenedores)
  2, truncate                Ejecuta truncate.sql únicamente
  3, load-staging            Carga el archivo scripts/sql/staging.sql
  4, seed                    Pobla la base de datos con datos aleatorios (GET /dev/seed-db)
  5, dump                    Exporta los datos actuales de la DB a scripts/sql/staging.sql
  6, restart                 Crear las tablas (reinicia el contenedor del backend)

  [ Acciones completas ]
  7, drop-recreate           Ejecuta drop.sql y reinicia el contenedor del backend (Requiere confirmación)
  8, truncate-load-staging   Limpia las tablas y carga scripts/sql/staging.sql
  9, truncate-seed           Limpia las tablas y pobla con datos aleatorios
  10, new-staging            Limpia las tablas, pobla con la API y exporta a staging.sql

Opciones generales:
  -h, --help                 Muestra esta ayuda
`);
}

function runSqlFile(fileName) {
  const filePath = path.resolve(__dirname, '../sql', fileName);
  if (!fs.existsSync(filePath)) {
    error(`Archivo SQL no encontrado: ${filePath}`);
  }

  const pgUser = process.env.POSTGRES_USER || 'admin';
  const pgPassword = process.env.POSTGRES_PASSWORD || 'admin';
  const pgHost = process.env.POSTGRES_HOST || 'localhost';
  const pgPort = process.env.POSTGRES_PORT || '5432';
  const pgDatabase = process.env.POSTGRES_DATABASE_NAME || 'salepartido_database';
  const pgServiceName = 'database';
  const projectRoot = path.resolve(__dirname, '../..');

  info(`Ejecutando SQL desde ${fileName}...`);
  const cmd = `docker compose exec -iT ${pgServiceName} psql -h ${pgHost} -p ${pgPort} -U ${pgUser} -d ${pgDatabase} < "${filePath}"`;

  try {
    process.env.PGPASSWORD = pgPassword;
    execSync(cmd, { cwd: projectRoot, stdio: 'inherit' });
    success(`Archivo ${fileName} ejecutado correctamente.`);
  } catch (err) {
    error(`Error al ejecutar SQL ${fileName}: ${err.message}`);
  }
}

function runDump(fileName) {
  const filePath = path.resolve(__dirname, '../sql', fileName);
  fs.mkdirSync(path.dirname(filePath), { recursive: true });

  const pgUser = process.env.POSTGRES_USER || 'admin';
  const pgPassword = process.env.POSTGRES_PASSWORD || 'admin';
  const pgDatabase = process.env.POSTGRES_DATABASE_NAME || 'salepartido_database';
  const pgServiceName = 'database';
  const projectRoot = path.resolve(__dirname, '../..');

  info(`Generando dump en ${fileName}...`);

  try {
    process.env.PGPASSWORD = pgPassword;
    const sql = execSync(
      `docker compose exec -T ${pgServiceName} pg_dump --data-only --inserts -U ${pgUser} ${pgDatabase}`,
      {
        cwd: projectRoot,
        encoding: 'utf8',
        maxBuffer: 1024 * 1024 * 50 // 50 MB
      }
    );

    fs.writeFileSync(filePath, sql);
    success(`Dump generado correctamente en ${filePath}`);
  } catch (err) {
    error(`Error al generar el dump: ${err.message}`);
  }
}

function callSeedApi() {
  return new Promise((resolve, reject) => {
    const springPort = process.env.SPRING_PORT || '8080';
    const url = `http://localhost:${springPort}/dev/seed-db`;
    info(`Iniciando GET a ${url} para poblar la base de datos...`);

    const req = http.get(url, { timeout: 120000 }, (res) => {
      let data = '';
      res.on('data', (chunk) => {
        data += chunk;
      });
      res.on('end', () => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          success(`Semilla generada. Respuesta de la API (${res.statusCode}): ${data.trim()}`);
          resolve(data);
        } else {
          reject(new Error(`La API respondió con código de estado ${res.statusCode}: ${data.trim()}`));
        }
      });
    });

    req.on('error', (err) => {
      let extraInfo = '';
      if (err.code === 'ECONNREFUSED') {
        extraInfo = '\n[TIP] Asegúrate de que el backend de Spring Boot esté corriendo y escuchando en el puerto configurado.';
      }
      reject(new Error(`Error de conexión con la API: ${err.message}${extraInfo}`));
    });

    req.on('timeout', () => {
      req.destroy();
      reject(new Error('La solicitud a la API excedió el tiempo límite (timeout de 120s)'));
    });
  });
}

function restartBackend() {
  const projectRoot = path.resolve(__dirname, '../..');
  info('Reiniciando el contenedor de Spring Boot (backend)...');
  try {
    execSync('docker compose restart backend', { cwd: projectRoot, stdio: 'inherit' });
    success('Contenedor backend reiniciado.');
    warn('Nota: Spring Boot puede tardar unos segundos en iniciarse por completo y regenerar las tablas.');
  } catch (err) {
    error(`Error al reiniciar el contenedor backend: ${err.message}`);
  }
}

function askConfirmation(question) {
  return new Promise((resolve) => {
    const rl = readline.createInterface({
      input: process.stdin,
      output: process.stdout
    });
    rl.question(question, (answer) => {
      rl.close();
      const cleaned = answer.trim().toLowerCase();
      resolve(cleaned === 'y' || cleaned === 'yes');
    });
  });
}

async function runOption(option) {
  const opt = option.toString().trim().toLowerCase();

  switch (opt) {
    // Acciones Granulares
    case '1':
    case 'drop':
      runSqlFile('drop.sql');
      break;

    case '2':
    case 'truncate':
      runSqlFile('truncate.sql');
      break;

    case '3':
    case 'load-staging':
      runSqlFile('staging.sql');
      break;

    case '4':
    case 'seed':
      try {
        await callSeedApi();
      } catch (err) {
        error(err.message);
      }
      break;

    case '5':
    case 'dump':
      runDump('staging.sql');
      break;

    case '6':
    case 'restart':
      warn('¡ATENCIÓN! Este comando reiniciará el contenedor del backend (Spring Boot).');
      const confirmed6 = await askConfirmation('¿Está seguro de que desea continuar? (y/yes para confirmar, cualquier otra tecla para cancelar): ');
      if (!confirmed6) {
        info('Operación cancelada por el usuario.');
        process.exit(0);
      }
      restartBackend();
      break;

    // Acciones Compuestas
    case '7':
    case 'drop-recreate':
      warn('¡ATENCIÓN! Este comando ejecutará "drop.sql" y reiniciará el contenedor del backend (Spring Boot).');
      const confirmed7 = await askConfirmation('¿Está seguro de que desea continuar? (y/yes para confirmar, cualquier otra tecla para cancelar): ');
      if (!confirmed7) {
        info('Operación cancelada por el usuario.');
        process.exit(0);
      }
      runSqlFile('drop.sql');
      restartBackend();
      break;

    case '8':
    case 'truncate-load-staging':
      runSqlFile('truncate.sql');
      runSqlFile('staging.sql');
      break;

    case '9':
    case 'truncate-seed':
      runSqlFile('truncate.sql');
      try {
        await callSeedApi();
      } catch (err) {
        error(err.message);
      }
      break;

    case '10':
    case 'new-staging':
      runSqlFile('truncate.sql');
      try {
        await callSeedApi();
      } catch (err) {
        error(err.message);
      }
      runDump('staging.sql');
      break;

    default:
      error(`Opción desconocida: ${option}. Use -h o --help para ver las opciones disponibles.`);
  }
}

function showInteractiveMenu() {
  console.log(`
${colors.blue}================================================================${colors.reset}
${colors.cyan}                  GESTOR DE BASE DE DATOS (DEV)                 ${colors.reset}
${colors.blue}================================================================${colors.reset}

${colors.yellow}[ Acciones Granulares / Simples ]${colors.reset}
  ${colors.green}1.${colors.reset} Dropear todas las tablas (drop)
  ${colors.green}2.${colors.reset} Limpiar datos de todas las tablas (truncate)
  ${colors.green}3.${colors.reset} Cargar datos de staging (staging)
  ${colors.green}4.${colors.reset} Poblar base de datos con datos aleatorios (API GET /dev/seed-db)
  ${colors.green}5.${colors.reset} Exportar datos actuales al archivo de staging (dump)
  ${colors.green}6.${colors.reset} Crear tablas (reinicia el contenedor del backend)

${colors.yellow}[ Acciones Compuestas / Flujos ]${colors.reset}
  ${colors.green}7.${colors.reset} Dropear todas las tablas (drop) y reiniciar contenedor del backend
  ${colors.green}8.${colors.reset} Limpiar datos de todas las tablas y cargar staging (truncate + staging)
  ${colors.green}9.${colors.reset} Limpiar datos de todas las tablas y poblar con datos aleatorios (truncate + seed-db)
 ${colors.green}10.${colors.reset} Crear nuevo staging completo (truncate + seed-db + dump)

  ${colors.red}0.${colors.reset} Salir
`);

  const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
  });

  rl.question(`${colors.yellow}Ingrese una opción [0-10]: ${colors.reset}`, async (answer) => {
    rl.close();
    const opt = answer.trim();
    if (opt === '0') {
      info('Operación cancelada.');
      process.exit(0);
    }
    if (!opt) {
      error('Debe ingresar una opción válida.');
    }
    await runOption(opt);
  });
}

async function main() {
  const arg = process.argv[2];

  if (arg === '--help' || arg === '-h') {
    showUsage();
    process.exit(0);
  }

  if (arg) {
    await runOption(arg);
  } else {
    showInteractiveMenu();
  }
}

main();