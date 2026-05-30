#!/usr/bin/env node

/**
 * Valida HUs según estándares INVEST + formato Sale Partido
 *
 * Uso: node scripts/validate-hu.js <huId>
 * Ejemplo: node scripts/validate-hu.js E4-H08
 */

const fs = require('fs');
const path = require('path');

const HUS_FILE = path.join(__dirname, '../doc/data/HUS.json');

const INVEST_CRITERIA = {
  independent: 'Independiente de otras HUs',
  negotiable: 'Criterios de aceptación claros',
  valuable: 'Tiene valor para el negocio',
  estimable: 'El equipo puede estimar el esfuerzo',
  small: 'Se puede completar en un sprint',
  testable: 'Tiene criterios de aceptación testeable',
};

const VALIDATION_RULES = {
  huIdFormat: {
    test: (hu) => /^E[1-9]-H\d{2}$/.test(hu.huId),
    message: 'Formato debe ser E#-H## (ej: E4-H08)',
  },
  hasTitle: {
    test: (hu) => hu.title && hu.title.length > 10,
    message: 'Título debe tener >10 caracteres',
  },
  hasDescription: {
    test: (hu) => hu.description && hu.description.length > 50,
    message: 'Descripción debe tener >50 caracteres',
  },
  hasAC: {
    test: (hu) => hu.acceptanceCriteria && hu.acceptanceCriteria.length > 0,
    message: 'Debe tener criterios de aceptación (sección ## Criterios de Aceptación)',
  },
  acTestable: {
    test: (hu) => {
      if (!hu.acceptanceCriteria) return false;
      return hu.acceptanceCriteria.every(ac =>
        /^(GET|POST|PUT|DELETE|should|must|returns|validates|displays)/.test(ac.text.toLowerCase())
      );
    },
    message: 'AC debe ser testeable (empezar con verbo: GET, POST, should, returns, etc)',
  },
  minAC: {
    test: (hu) => hu.acceptanceCriteria && hu.acceptanceCriteria.length >= 3,
    message: 'Mínimo 3 criterios de aceptación',
  },
  hasEpic: {
    test: (hu) => hu.epic && /^E[1-9]$/.test(hu.epic),
    message: 'Debe estar asignada a una épica válida (E1-E9)',
  },
};

function validateHU(huId) {
  // 1. Leer HUs
  if (!fs.existsSync(HUS_FILE)) {
    console.error(`❌ Archivo no encontrado: ${HUS_FILE}`);
    console.error('   Ejecuta: node scripts/sync-github-projects.js');
    process.exit(1);
  }

  const hus = JSON.parse(fs.readFileSync(HUS_FILE, 'utf-8'));
  const hu = hus.find(h => h.huId === huId.toUpperCase());

  if (!hu) {
    console.error(`❌ HU no encontrada: ${huId}`);
    console.error(`   HUs disponibles: ${hus.map(h => h.huId).join(', ')}`);
    process.exit(1);
  }

  // 2. Validar
  console.log(`\n🔍 Validando HU: ${hu.huId}\n`);
  console.log(`   Título: ${hu.title}`);
  console.log(`   Épica: ${hu.epic}`);
  console.log(`   Estado: ${hu.status}\n`);

  let errors = [];
  let warnings = [];

  // Validaciones
  Object.entries(VALIDATION_RULES).forEach(([rule, config]) => {
    const passed = config.test(hu);
    const icon = passed ? '✅' : '❌';
    console.log(`${icon} ${rule}: ${config.message}`);

    if (!passed) {
      errors.push(config.message);
    }
  });

  // INVEST
  console.log(`\n📋 INVEST Check:\n`);
  Object.entries(INVEST_CRITERIA).forEach(([criterion, description]) => {
    console.log(`   ⚠️  ${criterion}: ${description}`);
    console.log(`       → Verificar manualmente en: ${hu.url}\n`);
  });

  // Resumen
  console.log(`\n${'='.repeat(60)}`);
  if (errors.length === 0) {
    console.log('✅ VALIDACIÓN EXITOSA\n');
    console.log('La HU cumple con los estándares. Proceder a desarrollar.\n');
    return 0;
  } else {
    console.log(`❌ VALIDACIÓN FALLIDA (${errors.length} errores)\n`);
    errors.forEach((err, i) => {
      console.log(`   ${i + 1}. ${err}`);
    });
    console.log(`\nCorrige en GitHub y re-sincroniza:\n`);
    console.log(`   node scripts/sync-github-projects.js\n`);
    return 1;
  }
}

// Ejecutar
const huId = process.argv[2];
if (!huId) {
  console.log('Uso: node scripts/validate-hu.js <huId>');
  console.log('Ejemplo: node scripts/validate-hu.js E4-H08\n');
  process.exit(1);
}

const exitCode = validateHU(huId);
process.exit(exitCode);
