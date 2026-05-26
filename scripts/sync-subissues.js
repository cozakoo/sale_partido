#!/usr/bin/env node

/**
 * Sincroniza sub-issues de GitHub
 *
 * Uso: node scripts/sync-subissues.js
 *
 * Obtiene todas las issues y sus sub-issues (child issues)
 * Genera: /doc/data/SUBISSUES.json
 */

const fs = require('fs');
const path = require('path');
require('dotenv').config();

const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const GITHUB_OWNER = 'cozakoo';
const GITHUB_REPO = 'sale_partido';

if (!GITHUB_TOKEN) {
  console.error('❌ Error: GITHUB_TOKEN no configurado en .env');
  process.exit(1);
}

const SUBISSUES_FILE = path.join(__dirname, '../doc/data/SUBISSUES.json');

async function getAllIssues() {
  console.log('📥 Descargando todas las issues de GitHub...\n');

  try {
    const query = `
      query {
        repository(owner: "${GITHUB_OWNER}", name: "${GITHUB_REPO}") {
          issues(first: 100, states: OPEN, orderBy: {field: CREATED_AT, direction: DESC}) {
            nodes {
              number
              title
              state
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

    return data.data.repository.issues.nodes;
  } catch (error) {
    console.error('❌ Error:', error.message);
    process.exit(1);
  }
}

async function getSubIssuesForIssue(issueNumber) {
  const query = `
    query {
      repository(owner: "${GITHUB_OWNER}", name: "${GITHUB_REPO}") {
        issue(number: ${issueNumber}) {
          number
          title
          body
        }
      }
    }
  `;

  try {
    const response = await fetch('https://api.github.com/graphql', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${GITHUB_TOKEN}`,
      },
      body: JSON.stringify({ query }),
    });

    const data = await response.json();
    if (data.errors) {
      return { number: issueNumber, subIssues: [] };
    }

    const issue = data.data.repository.issue;
    if (!issue || !issue.body) return { number: issueNumber, subIssues: [] };

    // Extraer referencias a issues del body (#123)
    const bodyMatches = issue.body.match(/#\d+/g) || [];
    const subIssueNumbers = [...new Set(bodyMatches)].map(ref => ref.replace('#', ''));

    const subIssues = subIssueNumbers.map(num => ({
      number: parseInt(num),
      url: `https://github.com/${GITHUB_OWNER}/${GITHUB_REPO}/issues/${num}`,
    }));

    return {
      number: issueNumber,
      title: issue.title,
      subIssues: subIssues,
    };
  } catch (error) {
    return { number: issueNumber, subIssues: [] };
  }
}

async function syncSubIssues() {
  const allIssues = await getAllIssues();
  console.log(`📋 Encontradas ${allIssues.length} issues\n`);
  console.log('🔍 Descargando sub-issues...');

  const results = [];
  for (let i = 0; i < allIssues.length; i++) {
    const issue = allIssues[i];
    process.stdout.write(`\r  [${i + 1}/${allIssues.length}] Issue #${issue.number}...`);

    const subIssuesData = await getSubIssuesForIssue(issue.number);
    if (subIssuesData.subIssues.length > 0) {
      results.push(subIssuesData);
    }
  }

  console.log('\n');

  // Guardar
  fs.writeFileSync(SUBISSUES_FILE, JSON.stringify(results, null, 2));

  console.log(`✅ Sub-issues descargadas exitosamente!\n`);
  console.log(`📊 Resumen:\n`);

  const issuesWithSubs = results.length;
  const totalSubs = results.reduce((sum, issue) => sum + issue.subIssues.length, 0);

  console.log(`   Issues con sub-issues: ${issuesWithSubs}`);
  console.log(`   Total de sub-issues: ${totalSubs}`);
  console.log(`\n📁 Archivo generado:`);
  console.log(`   ${SUBISSUES_FILE}`);
  console.log(`\n⏰ Actualizado: ${new Date().toISOString()}\n`);
}

syncSubIssues();
