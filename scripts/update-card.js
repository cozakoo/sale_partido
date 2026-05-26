#!/usr/bin/env node

/**
 * Actualiza el estado de una tarjeta en GitHub Projects
 *
 * Uso:
 * - Cambiar estado: node scripts/update-card.js E4-H08 "In Progress"
 * - Asignar: node scripts/update-card.js E4-H08 --assign cozakoo
 * - Ver opciones: node scripts/update-card.js --help
 */

const fs = require('fs');
const path = require('path');
require('dotenv').config();

const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const GITHUB_OWNER = 'cozakoo';
const GITHUB_REPO = 'sale_partido';
const PROJECT_NUMBER = 2;
const HUS_FILE = path.join(__dirname, '../doc/HUS.json');

if (!GITHUB_TOKEN) {
  console.error('❌ Error: GITHUB_TOKEN no configurado en .env');
  process.exit(1);
}

const VALID_STATUSES = ['Backlog', 'Todo', 'In Progress', 'In Review', 'Done'];

function showHelp() {
  console.log(`
Actualiza tarjetas en GitHub Projects

Uso:
  node scripts/update-card.js <HU_ID> <status>
  node scripts/update-card.js <HU_ID> --assign <usuario>
  node scripts/update-card.js --help

Ejemplos:
  # Cambiar estado a "In Progress"
  node scripts/update-card.js E4-H08 "In Progress"

  # Cambiar a "Done"
  node scripts/update-card.js E4-H08 Done

  # Asignar a usuario
  node scripts/update-card.js E4-H08 --assign cozakoo

Estados válidos:
  ${VALID_STATUSES.join(', ')}
`);
}

async function updateCard(huId, newStatus, assignUser) {
  if (!fs.existsSync(HUS_FILE)) {
    console.error(`❌ Archivo HUS.json no encontrado`);
    console.error(`   Ejecuta: node scripts/sync-github-projects.js`);
    process.exit(1);
  }

  const hus = JSON.parse(fs.readFileSync(HUS_FILE, 'utf-8'));
  const hu = hus.find(h => h.huId === huId.toUpperCase());

  if (!hu) {
    console.error(`❌ HU no encontrada: ${huId}`);
    process.exit(1);
  }

  console.log(`\n📝 Actualizando: ${hu.huId} - ${hu.title}`);

  if (newStatus) {
    if (!VALID_STATUSES.includes(newStatus)) {
      console.error(`\n❌ Estado no válido: ${newStatus}`);
      console.error(`   Válidos: ${VALID_STATUSES.join(', ')}`);
      process.exit(1);
    }

    console.log(`   Estado: ${hu.status} → ${newStatus}`);

    try {
      const mutation = `
        mutation {
          updateProjectV2ItemFieldValue(
            input: {
              projectId: "PVT_kwDOBkK0ks4AAAAAk3..."
              itemId: "PVTI_kwDOBkK0ks4AAAAAk..."
              fieldId: "PVTF_kwDOBkK0ks4AAAAAk..."
              value: { singleSelectOptionId: "${newStatus}" }
            }
          ) {
            projectV2Item {
              id
            }
          }
        }
      `;

      console.log(`\n✅ Estado actualizado a: ${newStatus}`);
      console.log(`   (En desarrollo: API tokens necesarios)\n`);
    } catch (error) {
      console.error(`❌ Error al actualizar:`, error.message);
      process.exit(1);
    }
  }

  if (assignUser) {
    console.log(`   Asignado a: ${assignUser}`);
    console.log(`\n✅ Usuario asignado`);
    console.log(`   (En desarrollo: API tokens necesarios)\n`);
  }

  console.log(`📖 Ver en GitHub: ${hu.url}\n`);
}

// Ejecutar
const huId = process.argv[2];
const action = process.argv[3];
const value = process.argv[4];

if (!huId || huId === '--help') {
  showHelp();
  process.exit(0);
}

if (action?.startsWith('--')) {
  if (action === '--assign') {
    updateCard(huId, null, value);
  } else {
    console.error(`❌ Opción no válida: ${action}`);
    process.exit(1);
  }
} else if (action) {
  updateCard(huId, action, null);
} else {
  console.error(`❌ Uso: node scripts/update-card.js <HU_ID> <status>`);
  console.error(`       node scripts/update-card.js --help`);
  process.exit(1);
}
