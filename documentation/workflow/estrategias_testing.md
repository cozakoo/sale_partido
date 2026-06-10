<!-- Encabezado de documentación -->
<div align="center">
	<img src="../assets/logo.png" alt="Logo" width="140" />

	<p>
		<img src="https://img.shields.io/badge/build-passing-brightgreen" alt="build" />
		<img src="https://img.shields.io/badge/coverage-80%25-yellow" alt="coverage" />
	</p>
</div>

---

**CATEGORÍA: DESARROLLO** 

# **ESTRATEGIAS DE TESTING ELEGIDAS**

**Estrategias de testing**  
Se hará TDD (Test Driven Development) para mantener el enfoque implementar lo más importante de las funcionalidades, de forma que reducir el riesgo a implementaciones externas a la historia

**Alcance de tests**  
Se describen qué aspectos del sistema se contemplan testear de forma automatizada con herramientas, según el stack tecnológico elegido.

| Componente del sistema | Tipo de test | Importancia |
| :---- | :---- | :---- |
| Frontend | Tests E2E | Importante |
| Frontend | Test unitarios de componentes con control complejo de estado o flujo | Solo si es necesario |
| Backend | Tests de integración de API (enviar un JSON y validar el JSON de respuesta) | Importante |
| Backend | Tests unitarios para lógica de servicios con flujo complejo o cálculo | Solo si es necesario |

