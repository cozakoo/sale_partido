#!/usr/bin/env node

/**
 * Descarga el tablero completo de GitHub Projects
 *
 * Uso: node scripts/download-board.js
 *
 * Genera:
 * - /doc/data/BOARD.json (estado completo del tablero)
 * - /doc/data/BOARD.md (vista legible)
 */

const fs = require('fs');
const path = require('path');
require('dotenv').config();

const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const GITHUB_OWNER = 'cozakoo';
const GITHUB_REPO = 'sale_partido';
const PROJECT_NUMBER = 2;

if (!GITHUB_TOKEN) {
  console.error('❌ Error: GITHUB_TOKEN no configurado en .env');
  process.exit(1);
}

const BOARD_JSON = path.join(__dirname, '../doc/data/BOARD.json');
const BOARD_MD = path.join(__dirname, '../doc/data/BOARD.md');

async function downloadBoard() {
  console.log('📥 Descargando tablero de GitHub Projects...\n');

  try {
    const query = `
      query {
        repository(owner: "${GITHUB_OWNER}", name: "${GITHUB_REPO}") {
          projectV2(number: ${PROJECT_NUMBER}) {
            id
            title
            fields(first: 20) {
              nodes {
                ... on ProjectV2SingleSelectField {
                  id
                  name
                  options {
                    id
                    name
                  }
                }
              }
            }
            items(first: 100) {
              nodes {
                id
                fieldValues(first: 20) {
                  nodes {
                    ... on ProjectV2ItemFieldSingleSelectValue {
                      field {
                        ... on ProjectV2SingleSelectField {
                          name
                        }
                      }
                      name
                    }
                  }
                }
                content {
                  ... on Issue {
                    number
                    title
                    state
                    body
                    assignees(first: 3) {
                      nodes {
                        login
                      }
                    }
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
      body: JSON.stringify({ query }),
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

    // Parsear items
    const boardItems = items
      .filter(item => item.content && item.content.number)
      .map(item => {
        const issue = item.content;
        const fields = {};

        item.fieldValues.nodes.forEach(fv => {
          if (fv.field && fv.name) {
            fields[fv.field.name] = fv.name;
          }
        });

        const huMatch = issue.title.match(/\[?(E\d-H\d{2,})\]?/);
        const huId = huMatch ? huMatch[1] : `UNKNOWN-${issue.number}`;

        return {
          huId,
          number: issue.number,
          title: issue.title,
          state: issue.state,
          status: fields['Status'] || 'Backlog',
          epic: fields['Epic'] || extractEpic(huId),
          priority: fields['Priority'] || 'Medium',
          assignees: issue.assignees.nodes.map(a => a.login),
          url: `https://github.com/${GITHUB_OWNER}/${GITHUB_REPO}/issues/${issue.number}`,
        };
      })
      .sort((a, b) => a.huId.localeCompare(b.huId));

    // 1. Guardar JSON
    fs.writeFileSync(BOARD_JSON, JSON.stringify(boardItems, null, 2));

    // 2. Generar markdown
    const markdown = generateBoardMarkdown(boardItems);
    fs.writeFileSync(BOARD_MD, markdown);

    // 3. Reportar
    console.log(`✅ Tablero descargado exitosamente!\n`);
    console.log(`📊 Estado actual:\n`);

    const byStatus = {};
    boardItems.forEach(item => {
      byStatus[item.status] = (byStatus[item.status] || 0) + 1;
    });

    Object.entries(byStatus)
      .sort()
      .forEach(([status, count]) => {
        console.log(`   ${status}: ${count}`);
      });

    console.log(`\n📁 Archivos generados:`);
    console.log(`   ${BOARD_JSON}`);
    console.log(`   ${BOARD_MD}`);
    console.log(`\n⏰ Actualizado: ${new Date().toISOString()}\n`);

  } catch (error) {
    console.error('❌ Error:', error.message);
    process.exit(1);
  }
}

function extractEpic(huId) {
  const match = huId.match(/E(\d)/);
  return match ? `E${match[1]}` : 'UNKNOWN';
}

function generateBoardMarkdown(items) {
  let md = `# Tablero GitHub Projects\n\n`;
  md += `**Actualizado:** ${new Date().toISOString()}\n\n`;

  const byStatus = {};
  items.forEach(item => {
    if (!byStatus[item.status]) {
      byStatus[item.status] = [];
    }
    byStatus[item.status].push(item);
  });

  const statuses = ['Backlog', 'Todo', 'In Progress', 'In Review', 'Done'];

  statuses.forEach(status => {
    if (byStatus[status]) {
      md += `## ${status}\n\n`;
      byStatus[status].forEach(item => {
        md += `- **[${item.huId}]** ${item.title}\n`;
        md += `  - Prioridad: ${item.priority}\n`;
        md += `  - Asignado: ${item.assignees.length > 0 ? item.assignees.join(', ') : 'Sin asignar'}\n`;
        md += `  - [Ver en GitHub](${item.url})\n\n`;
      });
    }
  });

  return md;
}

downloadBoard();
