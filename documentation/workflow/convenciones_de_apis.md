**CATEGORÍA: DESARROLLO** 

# **CONVENCIONES DE APIs**

## **Plantilla de diseño**

Plantilla para diseño preliminar y acuerdo con backend y frontend  
Una vez implementada la API, la documentación la genera swagger toda linda en http://localhost:8080/swagger-ui.html

| Modelos de API |  |
| :---- | :---- |
| `TurnoDetail` | `{   "uuid": "...",   "horaInicio": “18:00:00”   "horaFin": "19:00:00"   “fecha”: “2026-05-12”   “cancha”: { ... }   “local”: { ... } }` |
| `TurnoSummary` | `{   "uuid": "...",   "horaInicio": “18:00:00”   "horaFin": "19:00:00"   “fecha”: “2026-05-12”   “cancha”: “El olímpico (cancha 1)” }` |
|  |  |

| Endpoint: /canchas/… |  |
| :---- | :---- |
| **Objetivo** | Ver turnos de una cancha |
| **Método HTTP** | GET |
| **Ruta** | /canchas/{uuid}/turnos?date=2026-05-12 |
| **Parámetros de ruta** | uuid: el id de la cancha fecha: la fecha de los turnos que se desean obtener |
| **Petición** | \- |
| **Respuesta exitosa** | `200 OK  TurnoDetail[]` |
| **Validación: la cancha debe existir** | `404 NOT FOUND {  "error": "CANCHA_NO_EXISTE",  "message": “La cancha no existe" }` |
| **Validación: …** | `…` |

## **Diseño de la API**

### **Diseñá primero el contrato, no el controlador**

Antes de escribir código, definí:

* recursos (`/usuarios`, `/canchas`)  
* operaciones  
* request/response JSON  
* códigos HTTP  
* validaciones  
* errores

Aunque no uses OpenAPI al principio, pensar la API como contrato evita controllers improvisados.

## **REST: convenciones importantes**

### **Usá sustantivos, no verbos**

Correcto:

GET /users  
POST /users  
GET /users/15

Evitar:

/getUsers  
/createUser  
/deleteUser

Porque el verbo ya lo representa HTTP.

---

### **Usá bien los métodos HTTP**

| Método | Uso |
| ----- | ----- |
| GET | Leer |
| POST | Crear |
| PUT | Reemplazar completo |
| PATCH | Actualización parcial |
| DELETE | Eliminar |

### **Devolvé códigos HTTP correctos**

Ejemplos:

| Caso | Código |
| ----- | ----- |
| OK | 200 |
| Creado | 201 |
| Sin contenido | 204 |
| Bad request | 400 |
| No autenticado | 401 |
| Prohibido | 403 |
| No encontrado | 404 |
| Conflicto | 409 |
| Error servidor | 500 |

etc.

## **Arquitectura en Spring Boot**

### **5\. Separá responsabilidades**

Estructura típica:

Controller: HTTP  
Service: Lógica de negocio  
Repository: Acceso a datos  
Entity: Modelos de datos (persistencia)

Regla importante:

* Controller → HTTP  
* Service → lógica negocio  
* Repository → acceso datos

No metas lógica de negocio en controllers.

### **6\. Nunca expongas entidades JPA directamente**

Problemas:

* acoplás DB con API  
* filtrás campos sensibles  
* lazy loading  
* cambios internos rompen clientes

Usá DTOs

**7\. Usá DTOs distintos para request y response**

Ejemplo:

CreateUserRequest  
UpdateUserRequest  
UserResponse

Porque:

* crear ≠ actualizar  
* response ≠ entity

Esto escala muchísimo mejor.

## **Validaciones**

### **8\. Validá en el borde de entrada**

Usá Bean Validation para los DTOs (estas anotaciones ya están añadidas al proyecto, listas para usar de jakarta.validation.constraints):

@NotBlank  
@Email  
@Size

Ejemplo:

public record CreateUserRequest(  
   @NotBlank String name,  
   @Email String email  
) {}

Y en controller:

@PostMapping  
public ResponseEntity\<?\> create(  
   @Valid @RequestBody CreateUserRequest request  
)

**9\. Centraliza manejo de errores**

Usá:

@RestControllerAdvice

Ejemplo:

@ExceptionHandler(EntityNotFoundException.class)

Así evitás:

* try/catch duplicados.  
* respuestas inconsistentes

(ya se creó un Global Exception Handler en salepartido/api/infrastructure/error/

## **Persistencia y JPA**

### **10\. Evitá lógica compleja en repositories**

Repository findByEmail()

No debe hacer:

* validaciones  
* reglas negocio  
* cálculos

Eso va en service.

---

### **11\. Cuidado con relaciones JPA**

Muchos problemas en Spring vienen de:

* `FetchType.EAGER`  
* relaciones bidireccionales  
* serialización infinita

Muy recomendado:

* usar DTOs  
* `LAZY` por defecto

### **12\. No hagas queries innecesarias**

Problema clásico:  
 N+1 queries.

Aprendé:

* `JOIN FETCH`  
* projections  
* pagination

**Respuestas de API**

### **13\. Usá respuestas consistentes**

Ejemplo:

{  
"id": 1,  
"nombre": "Pedro"  
}

Errores:

{  
"timestamp": "...",  
"status": 404,  
"message": "Usuario no encontrado"  
}

No devuelvas formatos distintos arbitrariamente.

Spring boot ya viene con la clase “Problem Details” para respuestas de errores

---

### **14\. Implementa paginación desde temprano**

No hagas: GET /users que devuelve 200k filas.

Spring: Pageable pageable

Ejemplo: GET /users?page=0\&size=20

## **Seguridad**

### **15\. Nunca confíes en datos del cliente**

Aunque el frontend valide:

* validá backend también  
* verificá ownership  
* verificá roles

### **16\. No expongas información sensible**

No devolver:

* passwords  
* stack traces  
* detalles internos SQL  
* tokens

## **Mantenibilidad**

### **17\. Versioná la API si va a crecer**

Ejemplo:

/api/v1/users

No siempre hace falta al inicio, pero en sistemas reales suele terminar siendo útil.

### **18\. Documentá la API**

Muy común en Spring con OpenAPI/Swagger

Te genera:

* documentación  
* ejemplos  
* testing interactivo

## **Testing**

### **19\. Testeá services y endpoints**

Tipos útiles:

### **Unit tests**

* lógica negocio

### **Integration tests**

* controllers \+ DB

Las herramientas ya vienen con spring.

## **Cosas MUY comunes que empeoran proyectos Spring**

### **Evitar:**

#### **Controllers gigantes**

@Controller  
public class UserController {  
  // 1500 líneas  
}

#### **Services que hacen todo**

* auth  
* mails  
* lógica  
* queries  
* mappers

Separar responsabilidades.

#### **Entities como modelo universal**

Muy típico en principiantes.

La entity representa persistencia, no toda tu aplicación.

#### **Abusar de `@Autowired`**

Preferible inyección por constructor.

#### **Lógica en el frontend que debería estar en backend**

Reglas de negocio críticas SIEMPRE backend.

