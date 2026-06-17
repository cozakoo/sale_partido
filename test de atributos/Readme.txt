Tutorial de Locust para Medir Atributos de Calidad - SalePartido

Objetivo
Medir Rendimiento, Escalabilidad y Disponibilidad de la aplicación desplegada en un VPS utilizando Locust.

Instalación
1. Instalar Python.
2. Crear entorno virtual.
3. Instalar Locust: pip install locust
4. Verificar instalación: locust --version

Configuración Inicial
Crear un archivo locustfile.py con escenarios para Buscar Locales, Crear Evento y Unirse a Evento.

Medición de Rendimiento
Ejecutar una prueba con 20 usuarios concurrentes durante 5 minutos.
Medir:
- Average Response Time
- P95
- Error Rate

Medición de Escalabilidad
Ejecutar pruebas con:
- 10 usuarios
- 50 usuarios
- 100 usuarios
- 200 usuarios
Comparar tiempos de respuesta y errores.

Medición de Disponibilidad
Ejecutar una prueba de 1 hora con 10 usuarios concurrentes.
Verificar ausencia de errores o caídas.

Monitoreo del VPS
Monitorear:
- CPU
- RAM
- Load Average
Utilizando htop o top.

Generación de Reportes
Exportar métricas utilizando:
locust --csv resultado

Valores Recomendados
- Buscar Locales < 500 ms
- Crear Evento < 1000 ms
- Unirse a Evento < 1000 ms
- 0 errores críticos

