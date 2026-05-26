#!/usr/bin/env node

/**
 * Sincroniza HUs de GitHub Projects a archivo local JSON
 *
 * Uso: node scripts/sync-github-projects.js
 *
 * Requisitos:
 * - GITHUB_TOKEN en .env (generar en https://github.com/settings/tokens)
 * - Permisos: read:project, read:org
 */

const fs = require('fs');
const path = require('path');
require('dotenv').config();

const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const GITHUB_OWNER = 'cozakoo';
const GITHUB_REPO = 'sale_partido';
const PROJECT_NUMBER = 2; // Tu proyecto

if (!GITHUB_TOKEN) {
  console.error('❌ Error: GITHUB_TOKEN no configurado en .env');
  console.error('   Genera token en: https://github.com/settings/tokens');
  process.exit(1);
}

const OUTPUT_FILE = path.join(__dirname, '../doc/data/HUS.json');

async function syncHUs() {
  console.log('📡 Sincronizando HUs de GitHub Projects...\n');

  try {
    // 1. Obtener proyecto
    const projectQuery = `
      query {
        repository(owner: "${GITHUB_OWNER}", name: "${GITHUB_REPO}") {
          projectV2(number: ${PROJECT_NUMBER}) {
            id
            title
            items(first: 50) {
              nodes {
                id
                fieldValues(first: 20) {
                  nodes {
                    ... on ProjectV2ItemFieldSingleSelectValue {
                      field { name }
                      name
                    }
                    ... on ProjectV2ItemFieldDateValue {
                      field { name }
                      date
                    }
                    ... on ProjectV2ItemFieldTextValue {
                      field { name }
                      text
                    }
                  }
                }
                content {
                  ... on Issue {
                    number
                    title
                    body
                    state
                    createdAt
                    updatedAt
                    assignees(first: 5) {
                      nodes {
                        login
                      }
                    }
                    labels(first: 10) {
                      nodes {
                        name
                      }
                    }
                  }
                  ... on DraftIssue {
                    title
                    body
                    createdAt
                  }
                }
              }
            }
          }
        }
      }
    `;

    const response = await fetch('https://api.github.com/graphql', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${GITHUB_TOKEN}`,
      },
      body: JSON.stringify({ query: projectQuery }),
    });

    if (!response.ok) {
      throw new Error(`GitHub API error: ${response.status}`);
    }

    const data = await response.json();

    if (data.errors) {
      console.error('❌ GraphQL error:', data.errors);
      process.exit(1);
    }

    const project = data.data.repository.projectV2;
    const items = project.items.nodes;

    // 2. Parsear HUs
    const hus = items
      .filter(item => item.content && item.content.number) // Solo issues, no drafts
      .map(item => {
        const issue = item.content;
        const fields = {};

        item.fieldValues.nodes.forEach(fv => {
          if (fv.field && fv.name) {
            fields[fv.field.name] = fv.name;
          } else if (fv.field && fv.date) {
            fields[fv.field.name] = fv.date;
          } else if (fv.field && fv.text) {
            fields[fv.field.name] = fv.text;
          }
        });

        // Extraer E#-H## del título
        const huMatch = issue.title.match(/\[?(E\d-H\d{2,})\]?/);
        const huId = huMatch ? huMatch[1] : `UNKNOWN-${issue.number}`;

        return {
          huId,
          number: issue.number,
          title: issue.title,
          description: issue.body || '',
          status: fields['Status'] || 'Backlog',
          epic: fields['Epic'] || extractEpic(huId),
          priority: fields['Priority'] || 'Medium',
          owner: issue.assignees.nodes[0]?.login || null,
          labels: issue.labels.nodes.map(l => l.name),
          createdAt: issue.createdAt,
          updatedAt: issue.updatedAt,
          url: `https://github.com/${GITHUB_OWNER}/${GITHUB_REPO}/issues/${issue.number}`,
          acceptanceCriteria: extractAC(issue.body),
        };
      })
      .sort((a, b) => a.huId.localeCompare(b.huId));

    // 3. Guardar archivo
    fs.writeFileSync(OUTPUT_FILE, JSON.stringify(hus, null, 2));

    // 4. Reportar
    console.log(`✅ Sincronizado exitosamente!\n`);
    console.log(`📊 Estadísticas:`);
    console.log(`   Total HUs: ${hus.length}`);
    console.log(`   Por epic:`);

    const byEpic = {};
    hus.forEach(hu => {
      byEpic[hu.epic] = (byEpic[hu.epic] || 0) + 1;
    });

    Object.entries(byEpic)
      .sort()
      .forEach(([epic, count]) => {
        console.log(`     ${epic}: ${count}`);
      });

    console.log(`\n📁 Archivo: ${OUTPUT_FILE}`);
    console.log(`⏰ Actualizado: ${new Date().toISOString()}\n`);

  } catch (error) {
    console.error('❌ Error:', error.message);
    process.exit(1);
  }
}

function extractEpic(huId) {
  const match = huId.match(/E(\d)/);
  return match ? `E${match[1]}` : 'UNKNOWN';
}

function extractAC(body) {
  if (!body) return [];

  const acMatch = body.match(/## Criterios de Aceptación\s*([\s\S]*?)(?:##|$)/);
  if (!acMatch) return [];

  return acMatch[1]
    .split('\n')
    .filter(line => line.includes('- ['))
    .map(line => ({
      text: line.replace(/- \[[ x]\]\s*/, '').trim(),
      done: line.includes('[x]'),
    }));
}

// Ejecutar
syncHUs();
