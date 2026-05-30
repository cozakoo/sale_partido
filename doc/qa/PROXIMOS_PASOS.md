# Próximos Pasos - E5-H01

## Checklist de Completado

- [ ] ✅ Ejecutar SETUP_LOCAL.md
- [ ] ✅ Ejecutar EJECUTAR_QA.md (todos los escenarios)
- [ ] ✅ Generar REPORTE_QA_E5-H01_FINAL.md
- [ ] ✅ Resolver bugs críticos identificados
- [ ] ✅ Re-ejecutar pruebas después de fixes
- [ ] ✅ Obtener aprobación de QA Lead
- [ ] ✅ Crear PR a rama `dev`
- [ ] ✅ Code review completado
- [ ] ✅ Merge a `dev`
- [ ] ✅ Crear PR a rama `main` (si aplica)

---

## Fase 1: Ejecución de Pruebas (Esta semana)

### Día 1-2: Setup y Pruebas Funcionales

```bash
# Lunes
bash ./doc/qa/scripts/setup-local.sh
# Ejecutar Escenarios 1-2 (disponibilidad)

# Martes
# Ejecutar Escenarios 3-4 (detalles, historial)
# Documentar en EJECUTAR_QA.md
```

### Día 3: Validaciones Técnicas

```bash
# Miércoles
# Ejecutar tests API (Fase 3.1)
# Validar console del navegador (Fase 3.2)
# Medir performance (Fase 3.3)
```

### Día 4-5: Responsive y Accesibilidad

```bash
# Jueves
# Probar en escritorio, tablet, móvil
# Validar navegación con teclado

# Viernes
# Completar reporte final
# Documentar bugs encontrados
```

---

## Fase 2: Correción de Bugs (Semana siguiente)

### Categoría: CRÍTICOS

```markdown
## Bugs Críticos Encontrados

[ ] Bug: [Descripción]
    Asignado a: [Backend/Frontend]
    Prioridad: CRÍTICA
    Plazo: 1-2 días

[ ] Bug: [Descripción]
    Asignado a: [Backend/Frontend]
    Prioridad: CRÍTICA
    Plazo: 1-2 días
```

### Categoría: ALTO

```markdown
## Bugs Alto Encontrados

[ ] Bug: [Descripción]
    Asignado a: [Backend/Frontend]
    Prioridad: ALTA
    Plazo: 3-5 días

[ ] Bug: [Descripción]
    Asignado a: [Backend/Frontend]
    Prioridad: ALTA
    Plazo: 3-5 días
```

### Workflow de Correción

1. **Crear issue en GitHub**
   ```bash
   # Título: [E5-H01] Bug: [Descripción corta]
   # Labels: bug, E5-H01, QA
   # Asignado a: [Responsable]
   ```

2. **Crear rama de fix**
   ```bash
   git checkout -b fix/E5-H01-bug-descripcion
   ```

3. **Implementar correción**
   - Seguir CLAUDE.md standards
   - Ejecutar tests locales
   - Verificar no haya regresiones

4. **Re-ejecutar prueba específica**
   - Ejecutar solo el escenario afectado
   - Documentar resultado

5. **Commit y Push**
   ```bash
   git commit -m "fix(E5-H01): [Descripción]"
   git push origin fix/E5-H01-bug-descripcion
   ```

6. **Crear PR**
   - Referencia issue: `Fixes #XXX`
   - Descripción de cambios
   - Link a reporte QA

---

## Fase 3: Re-Pruebas y Aprobación

### Después de cada fix

```bash
# 1. Actualizar rama dev
git checkout dev
git pull origin dev

# 2. Mergear fix
git merge fix/E5-H01-bug-descripcion

# 3. Re-ejecutar pruebas
# Ejecutar EJECUTAR_QA.md nuevamente

# 4. Documentar en reporte
# Actualizar REPORTE_QA_E5-H01_FINAL.md
```

### Aprobación de QA Lead

```markdown
## Checklist de Aprobación

- [ ] Todos los escenarios PASS
- [ ] Bugs críticos resueltos
- [ ] Performance aceptable
- [ ] Responsive funciona
- [ ] Accesibilidad mejorada
- [ ] Reporte documentado
- [ ] No hay regresiones

**Aprobado por:** [Nombre]
**Fecha:** [DD/MM/YYYY]
**Firma:** ________________
```

---

## Fase 4: Integración a Ramas

### Merge a `dev` (después de QA PASS)

```bash
git checkout dev
git pull origin dev
git merge feature/E5-H01/verificacion-funcional
git push origin dev
```

### Crear PR a `main` (si aplica)

```bash
# Crear PR en GitHub
# Titulo: [E5-H01] QA verificación funcional completada
# Description:
# - Todos escenarios validados ✅
# - Bugs críticos resueltos ✅
# - Reporte final adjunto
```

---

## Fase 5: Documentación Final

### Archivo de Referencia para Futuros Cambios

```markdown
# Documentación Generada

- ✅ SETUP_LOCAL.md - Instrucciones setup
- ✅ SETUP_INTERACTIVO.md - Setup automatizado
- ✅ QA_E5-H01.md - Plan de pruebas detallado
- ✅ EJECUTAR_QA.md - Pasos paso a paso
- ✅ GUIAS.md - Guías de navegación y debugging
- ✅ REPORTE_QA_E5-H01_FINAL.md - Resultados finales
- ✅ PROXIMOS_PASOS.md - Este archivo
- ✅ scripts/setup-local.sh - Script automatizado
```

### Actualizar CLAUDE.md del proyecto

```markdown
## QA E5-H01 - Completado

La historia de usuario E5-H01 "Calendario de disponibilidad y ocupación"
ha completado verificación funcional. Ver documentación en `/doc/qa/`.

Contacto QA: [nombre]
Último reporte: [fecha]
Estado: ✅ APROBADO
```

---

## Timeline Estimado

| Fase | Duración | Responsable |
|------|----------|-------------|
| Setup | 1 día | QA Engineer |
| Pruebas Funcionales | 3 días | QA Engineer |
| Validaciones Técnicas | 2 días | QA Engineer |
| Reporte | 1 día | QA Lead |
| Correción de Bugs | 3-5 días | Dev Team |
| Re-Pruebas | 2 días | QA Engineer |
| Aprobación Final | 1 día | QA Lead |
| **Total** | **2-3 semanas** | - |

---

## Dependencias Externas

- [ ] Backend desarrollado y funcional
- [ ] Frontend completamente implementado
- [ ] Base de datos con datos de prueba
- [ ] Acceso a ambiente de desarrollo
- [ ] Permissions para ejecutar scripts

---

## Escalación

Si durante QA encuentras:

### Bugs CRÍTICOS
- Notificar inmediatamente a [Dev Lead]
- Crear issue con severity CRÍTICA
- Bloquea progreso de QA

### Bugs de PERFORMANCE
- Notificar a [Tech Lead]
- Incluir métricas en reporte
- Puede requerir optimización

### Problemas de Accesibilidad
- Notificar a [Frontend Lead]
- Documentar detalles
- Plan de correción para siguiente sprint

---

## Artefactos a Entregar

```
doc/qa/
├── SETUP_LOCAL.md
├── SETUP_INTERACTIVO.md
├── QA_E5-H01.md
├── EJECUTAR_QA.md
├── GUIAS.md
├── PROXIMOS_PASOS.md
├── REPORTE_QA_E5-H01_FINAL.md
├── scripts/
│   └── setup-local.sh
└── reportes/
    └── reporte-ejecutado-[fecha].md
```

---

## Contacto

**QA Lead:** [nombre]
**Email:** [email]
**Slack:** #qa-e5-h01
**Reunión diaria:** [día, hora]

---

## Notas Adicionales

- Mantener este documento actualizado durante la ejecución
- Documentar cambios a escenarios o datos de prueba
- Crear issues en GitHub para cada bug encontrado
- Incluir screenshots/videos de bugs en reportes
- Mantener log de time-tracking
