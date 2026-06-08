**CATEGORÍA: DESARROLLO** 

# **CONVENCIONES DE BRANCHING**

## **Ramas principales**

* Main: Es la rama de producción que utilizan los clientes y usuarios finales. Debe ser completamente funcional y estable  
* Pre\_prod: Es la rama que se usará para verificar con los clientes que la historia de usuario está completada, y resolver posibles detalles menores previo a su despliegue en producción. Debe ser completamente funcional y estable (sin bugs críticos)  
* Dev: Es la rama de desarrollo, a partir de las cuales se originan las ramas por cada historia de usuario. Debe mantenerse mínimamente funcional y estable (sin bugs groseros) para poder crear otras ramas a partir de esta, o llevar cambios de otras ramas de historias de usuario hacia esta.

## **Convenciones de ramas principales**

Las ramas principales estarán protegidas por reglas configuradas en la plataforma elegida para el repositorio. 

* No se debe poder realizar fusiones directas hacia las ramas principales.   
* Solo se pueden llevar modificaciones mediante PRs (Pull Requests) 

El autor de un PR debe conocer y seguir los siguientes lineamientos:

* Es exclusivamente su responsabilidad asegurarse de que los cambios se lleven a la rama principal deseada  
* Debe verificar que el pipeline está en verde.   
* Debe agregar la referencia a la tarea correspondiente (o descripción en caso de no estar relacionada con ninguna tarea)  
* Debe verificar que no hayan conflictos de merge, para garantizar una fusión limpia de ramas y no propagar conflictos.  
* Si el PR no cumple con alguna de los lineamientos, debe modificarlo o retirarlo y crear uno nuevo con las correcciones incorporadas.

## **Ramas de trabajo**

Se consideran ramas de trabajo a aquellas destinadas al desarrollo de funcionalidades, cumplimiento de tareas, mantenimiento, refactoring o cualquier otra agregación al proyecto.

Estas ramas no poseen restricciones como las ramas principales, pero deben seguir las convenciones acordadas.

## **Convenciones de ramas de trabajo**

Se debe crear una rama por cada tarea a completar, ya que esa fue la decisión para priorizar una mayor independencia entre miembros del equipo (haciendo una rama por historia de usuario incentiva a que se acumulen demasiados cambios divergentes)

Las ramas de trabajo deben cumplir con el siguiente ciclo de vida:

* Originarse de una rama principal  
* Fusionarse nuevamente con la rama principal de origen, cuando se completa la tarea por la que se creó.  
* Eliminarse una vez fusionada con la rama principal

Dependiendo del uso de la rama, se indica de qué rama debe originarse y el formato que debe tener su nombre:

| Uso | Rama de origen | Nombre |
| :---- | :---- | :---- |
| Tareas (de historias de usuarios) | dev | feature/E{número épica}\-H{número historia}/{nombre tarea}  |
| Bugs (de historias de usuarios) | dev | bugfix/E{número épica}\-H{número historia}/{nombre bug} |
| Bugs (técnicos) | dev | bugfix/{nombre del bug} |
| Bugs (urgentes en producción) | main | hotfix/{nombre del bug} |
| Modificación general sin cambio funcional | dev | refactor/{descripción corta} |
| Experimentación de ideas o herramientas | dev | test/{nombre de la herramienta o idea} |
| Documentación | dev | docs/{descripción corta} |
| Infraestructura | dev | infra/{descripción corta} |
| Cambios que no entran en ninguna otra categoría | dev | chore/{descripción corta} |

