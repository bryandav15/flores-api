# Guia completa del proyecto Flores API

---

## Indice

1. Que es Spring Boot
2. Por que usarlo en Visual Studio Code
3. Que pasa cuando arranca la aplicacion
4. Como interactua con la base de datos
5. Puertos y donde corre la API
6. Como funcionan las peticiones HTTP
7. Los metodos HTTP: GET, POST, PUT, DELETE
8. Los codigos de respuesta HTTP
9. Los mensajes de la consola explicados
10. El formato JSON de respuesta
11. Como consumir la API desde Postman
12. Estructura del proyecto explicada archivo por archivo

---

## 1. Que es Spring Boot

Spring Boot es un framework de Java que permite construir aplicaciones web y APIs REST de forma rapida, sin tener que configurar manualmente un servidor, una base de datos, ni las dependencias entre capas.

Antes de Spring Boot, para hacer una API en Java habia que:
- Instalar y configurar un servidor como Tomcat manualmente
- Escribir archivos XML de configuracion extensos
- Conectar manualmente cada capa de la aplicacion

Spring Boot hace todo eso automaticamente. Tu solo escribes la logica del negocio y el framework se encarga del resto.

En resumen: Spring Boot es la infraestructura invisible que permite que tu API funcione. Tu escribes los controladores, servicios y repositorios; Spring Boot los conecta, levanta el servidor y los expone al mundo.

---

## 2. Por que usarlo en Visual Studio Code

Spring Boot no depende de ningun IDE especifico. Es simplemente un proyecto Java con Maven. Visual Studio Code puede abrirlo y trabajarlo igual que IntelliJ o Eclipse, siempre que tengas instalado:

- La extension **Extension Pack for Java** (de Microsoft)
- Java 17 o superior

VS Code reconoce el archivo `pom.xml` y entiende que es un proyecto Maven. Desde la terminal integrada puedes correr `mvn spring-boot:run` y la aplicacion arranca. No necesitas ningun boton especial ni configuracion adicional.

---

## 3. Que pasa cuando arranca la aplicacion

Cuando ejecutas `mvn spring-boot:run`, esto es lo que ocurre en orden:

### Paso 1: Spring lee la configuracion
Spring Boot lee el archivo `application.properties` y carga todos los valores: URL de la base de datos, usuario, contrasena, puerto del servidor, etc.

### Paso 2: Crea el contexto de aplicacion
Spring escanea todos los archivos Java buscando anotaciones como `@RestController`, `@Service`, `@Repository`, `@Configuration`. Cada clase anotada se convierte en un "bean" que Spring administra y conecta automaticamente.

### Paso 3: Conecta con la base de datos
Usando los datos de `application.properties`, Spring intenta conectarse a MySQL. Si la conexion falla, la aplicacion no arranca.

### Paso 4: Hibernate revisa las tablas
Hibernate (la libreria que habla con la BD) lee tus entidades (clases con `@Entity`) y compara lo que existe en la base de datos con lo que deberia existir. Con la configuracion `ddl-auto=update`, si la tabla no existe, la crea. Si ya existe pero le faltan columnas, las agrega. Nunca borra datos.

### Paso 5: Levanta el servidor Tomcat
Spring Boot incluye un servidor Tomcat embebido. No necesitas instalarlo por separado. El servidor empieza a escuchar en el puerto configurado (por defecto 8080).

### Paso 6: La aplicacion esta lista
Cuando ves en la consola `Started PlantasApplication in X.XXX seconds`, el servidor ya esta recibiendo peticiones.

---

## 4. Como interactua con la base de datos

La comunicacion entre la aplicacion y MySQL ocurre asi:

```
Tu peticion HTTP
      |
      v
FlorController  <-- recibe la peticion, valida parametros
      |
      v
FlorService     <-- ejecuta la logica
      |
      v
FlorRepository  <-- genera el SQL y lo ejecuta
      |
      v
MySQL           <-- responde con los datos
      |
   (sube el mismo camino)
      v
FlorController  <-- formatea la respuesta en JSON y la regresa
```

### El pool de conexiones (HikariCP)

Spring Boot no abre y cierra una conexion a MySQL por cada peticion, porque eso seria muy lento. En cambio usa un "pool" de conexiones: un grupo de conexiones abiertas que se reutilizan. HikariCP es el pool que Spring Boot incluye por defecto y es el mas rapido disponible.

Cuando ves en la consola:
```
HikariPool-1 - Starting...
HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@...
HikariPool-1 - Start completed.
```
Significa que el pool se creo exitosamente y hay conexiones disponibles para atender peticiones.

### JPA e Hibernate

JPA (Java Persistence API) es una especificacion, un contrato que define como debe funcionar la comunicacion con bases de datos en Java. Hibernate es la implementacion de ese contrato que Spring Boot usa por defecto.

Tu escribes esto en Java:
```java
repository.findAll();
```

Hibernate traduce eso a SQL:
```sql
SELECT * FROM flores;
```

Y cuando defines la clase `Flor` con `@Entity`, Hibernate sabe que esa clase representa la tabla `flores` en MySQL.

---

## 5. Puertos y donde corre la API

Un puerto es un numero que identifica a que aplicacion va dirigido el trafico de red en una computadora. Es como el numero de departamento en un edificio: la IP es el edificio, el puerto es el departamento.

Algunos puertos conocidos:
- `80` → HTTP normal (paginas web)
- `443` → HTTPS (paginas web seguras)
- `3306` → MySQL
- `8080` → Servidores de desarrollo (Spring Boot usa este por defecto)

Cuando la API corre en el puerto 8080, para accederla desde la misma computadora se usa:
```
http://localhost:8080
```

`localhost` significa "esta misma maquina". Es equivalente a `127.0.0.1`.

Para cambiar el puerto, en `application.properties`:
```properties
server.port=9090
```

---

## 6. Como funcionan las peticiones HTTP

HTTP (HyperText Transfer Protocol) es el protocolo que usan los navegadores y aplicaciones para comunicarse con servidores. Toda peticion HTTP tiene:

### Componentes de una peticion

**Metodo**: la accion que quieres realizar (GET, POST, PUT, DELETE).

**URL**: la direccion del recurso al que te diriges.
```
http://localhost:8080/api/flores/5
```
- `http://localhost:8080` → servidor
- `/api/flores` → recurso (flores)
- `/5` → ID especifico

**Headers**: informacion adicional sobre la peticion. El mas importante para esta API es:
```
Content-Type: application/json
```
Le dice al servidor que el body viene en formato JSON.

**Body**: el cuerpo de la peticion. Solo aplica en POST y PUT. Contiene los datos que envias en formato JSON.

### Componentes de una respuesta

El servidor responde con:

**Codigo de estado**: un numero de 3 digitos que indica si todo salio bien o que tipo de error ocurrio (explicado en la seccion 8).

**Body**: los datos de respuesta, en esta API siempre en formato JSON.

---

## 7. Los metodos HTTP: GET, POST, PUT, DELETE

Los metodos HTTP definen la intencion de la operacion. No es que tecnicamente no puedas hacer un borrado con GET, es que existe un estandar que todos siguen para que las APIs sean predecibles y faciles de consumir.

### GET - Obtener datos

Se usa para **leer** informacion. No modifica nada en el servidor.

- No lleva body
- Los parametros van en la URL

Ejemplos en esta API:
```
GET /api/flores           → trae todas las flores
GET /api/flores/3         → trae la flor con id 3
GET /api/flores/buscar?nombre=rosa  → busca por nombre
```

### POST - Crear un recurso nuevo

Se usa para **insertar** datos nuevos. El body contiene el objeto a crear.

```
POST /api/flores
Body: { "nombre": "Tulipan", "color": "amarillo" }
```

El servidor crea el registro y regresa el objeto creado con su ID asignado.

### PUT - Actualizar un recurso existente

Se usa para **modificar** un recurso que ya existe. El ID va en la URL y los campos a modificar van en el body.

En esta API el PUT es **dinamico**: solo actualiza los campos que recibe. Si solo mandas `precio`, unicamente cambia el precio.

```
PUT /api/flores/3
Body: { "precio": 30.00 }
```

### DELETE - Eliminar un recurso

Se usa para **borrar** un registro. Solo necesita el ID en la URL, no lleva body.

```
DELETE /api/flores/3
```

### Resumen

| Metodo | Accion    | Lleva body | Modifica datos |
|--------|-----------|------------|----------------|
| GET    | Leer      | No         | No             |
| POST   | Crear     | Si         | Si             |
| PUT    | Actualizar| Si         | Si             |
| DELETE | Borrar    | No         | Si             |

---

## 8. Los codigos de respuesta HTTP

Son numeros de 3 digitos que el servidor incluye en cada respuesta. Se agrupan en rangos:

- `2xx` → Todo salio bien
- `4xx` → Error del cliente (mandaste algo mal)
- `5xx` → Error del servidor (algo fallo internamente)

### Codigos que usa esta API

| Codigo | Nombre                | Cuando ocurre |
|--------|-----------------------|---------------|
| `200`  | OK                    | Operacion exitosa (GET, PUT, DELETE) |
| `201`  | Created               | Recurso creado exitosamente (POST) |
| `400`  | Bad Request           | Parametros invalidos o faltantes |
| `404`  | Not Found             | El ID solicitado no existe en la BD |
| `500`  | Internal Server Error | Error inesperado en el servidor |

### Como se ven en el JSON de respuesta

Cuando todo sale bien (200):
```json
{
  "success": true,
  "message": "OK",
  "data": { ... }
}
```

Cuando el ID no existe (404):
```json
{
  "success": false,
  "message": "Flor no encontrada con id: 99",
  "data": null
}
```

Cuando faltan campos requeridos (400):
```json
{
  "success": false,
  "message": "El campo 'nombre' es requerido",
  "data": null
}
```

---

## 9. Los mensajes de la consola explicados

Cuando arranca la aplicacion, la consola muestra muchos mensajes. Aqui los mas importantes:

```
Starting PlantasApplication using Java 17.0.11
```
La aplicacion comenzo a arrancar. Indica la version de Java.

```
Bootstrapping Spring Data JPA repositories in DEFAULT mode.
Found 1 JPA repository interface.
```
Spring encontro los repositorios (en este caso solo `FlorRepository`).

```
Tomcat initialized with port 8080 (http)
```
El servidor interno Tomcat se preparo para escuchar en el puerto 8080.

```
HikariPool-1 - Starting...
HikariPool-1 - Added connection ...
HikariPool-1 - Start completed.
```
El pool de conexiones a MySQL se creo exitosamente. Si aqui falla, las credenciales o la URL de la BD estan mal.

```
HHH90000025: MySQLDialect does not need to be specified explicitly
```
Es solo un aviso (WARN), no es un error. Significa que Hibernate puede detectar el dialecto de MySQL solo y no necesita que lo especifiques en las properties. No afecta el funcionamiento.

```
create table flores (...)
```
Hibernate creo la tabla `flores` porque no existia. Solo aparece la primera vez.

```
Initialized JPA EntityManagerFactory for persistence unit 'default'
```
La conexion entre JPA e Hibernate quedo lista.

```
spring.jpa.open-in-view is enabled by default
```
Otro aviso menor. Significa que las consultas a BD pueden ocurrir durante el renderizado de vistas. Como esta API no tiene vistas (solo JSON), no importa.

```
Tomcat started on port 8080 (http) with context path ''
```
El servidor esta escuchando. Context path vacio significa que la URL base es directamente `http://localhost:8080` sin prefijo adicional.

```
Started PlantasApplication in 5.266 seconds
```
La aplicacion arranco completamente. A partir de este momento acepta peticiones.

### Mensajes durante las peticiones

Cuando haces una peticion, aparece el SQL que Hibernate ejecuta:

```
Hibernate:
    select f1_0.id, f1_0.color, ...
    from flores f1_0
```

Esto es porque `spring.jpa.show-sql=true` esta activado en las properties. Es util para desarrollo porque puedes ver exactamente que consulta se ejecuto.

```
Initializing Spring DispatcherServlet 'dispatcherServlet'
```
Solo aparece en la primera peticion. El DispatcherServlet es el componente de Spring que recibe todas las peticiones HTTP y las dirige al controlador correcto.

---

## 10. El formato JSON de respuesta

JSON (JavaScript Object Notation) es el formato estandar para intercambiar datos entre una API y quien la consume. Es texto plano estructurado con llaves, corchetes y comillas.

### Tipos de datos en JSON

```json
{
  "texto": "hola",
  "numero": 42,
  "decimal": 25.50,
  "booleano": true,
  "nulo": null,
  "arreglo": [1, 2, 3],
  "objeto": { "clave": "valor" }
}
```

### Formato de respuesta de esta API

Todos los endpoints regresan siempre la misma estructura de 3 propiedades:

```json
{
  "success": true o false,
  "message": "texto descriptivo",
  "data": el dato o null
}
```

- `success`: indica si la operacion fue exitosa o no
- `message`: descripcion legible del resultado o del error
- `data`: el contenido (un objeto, una lista, o null si no hay datos)

### Ejemplos reales

GET todas las flores (lista vacia):
```json
{
  "success": true,
  "message": "OK",
  "data": []
}
```

GET todas las flores (con datos):
```json
{
  "success": true,
  "message": "OK",
  "data": [
    {
      "id": 1,
      "nombre": "Rosa Roja",
      "color": "rojo",
      "familia": "Rosaceae",
      "descripcion": "Rosa clasica de jardin",
      "precio": 25.5,
      "temporada": "primavera",
      "disponible": true
    }
  ]
}
```

POST crear flor (exitoso):
```json
{
  "success": true,
  "message": "Flor creada exitosamente",
  "data": {
    "id": 2,
    "nombre": "Tulipan",
    "color": "amarillo",
    "familia": null,
    "descripcion": null,
    "precio": null,
    "temporada": null,
    "disponible": true
  }
}
```

DELETE (exitoso, sin datos que regresar):
```json
{
  "success": true,
  "message": "Flor eliminada exitosamente",
  "data": null
}
```

---

## 11. Como consumir la API desde Postman

Postman es una herramienta grafica para hacer peticiones HTTP sin necesidad de escribir codigo. Es ideal para probar APIs.

### Configuracion inicial

1. Descarga Postman desde https://www.postman.com/downloads/
2. Abre Postman y crea una coleccion nueva llamada "Flores API"
3. Asegurate de que el servidor Spring Boot este corriendo antes de hacer peticiones

### Hacer un GET (obtener todas las flores)

1. Click en **New Request**
2. Selecciona el metodo **GET** en el dropdown de la izquierda
3. En la URL escribe: `http://localhost:8080/api/flores`
4. Click en **Send**
5. La respuesta aparece abajo en formato JSON

### Hacer un POST (crear una flor)

1. Metodo: **POST**
2. URL: `http://localhost:8080/api/flores`
3. Click en la pestana **Body**
4. Selecciona **raw**
5. En el dropdown de la derecha selecciona **JSON**
6. Escribe el body:
```json
{
  "nombre": "Orquidea",
  "color": "morado",
  "familia": "Orchidaceae",
  "descripcion": "Flor exotica tropical",
  "precio": 150.00,
  "temporada": "todo el año",
  "disponible": true
}
```
7. Click en **Send**

### Hacer un PUT (actualizar una flor)

1. Metodo: **PUT**
2. URL: `http://localhost:8080/api/flores/1` (cambia el 1 por el ID que quieras)
3. Body → raw → JSON:
```json
{
  "precio": 200.00,
  "disponible": false
}
```
4. Click en **Send**

Solo los campos que incluyas en el body seran modificados. El resto conserva su valor.

### Hacer un DELETE (eliminar una flor)

1. Metodo: **DELETE**
2. URL: `http://localhost:8080/api/flores/1`
3. No necesita body
4. Click en **Send**

### Buscar por parametro (query params)

1. Metodo: **GET**
2. URL: `http://localhost:8080/api/flores/buscar`
3. Click en la pestana **Params**
4. Agrega una fila: Key = `nombre`, Value = `rosa`
5. Postman construye automaticamente la URL: `http://localhost:8080/api/flores/buscar?nombre=rosa`
6. Click en **Send**

### Verificar el codigo de respuesta

En la respuesta de Postman, justo arriba del body, aparece el codigo HTTP:
- `200 OK` → todo bien
- `201 Created` → creado correctamente
- `400 Bad Request` → revisa lo que mandaste
- `404 Not Found` → el ID no existe
- `500 Internal Server Error` → error en el servidor, revisa la consola de Spring Boot

---

## 12. Estructura del proyecto explicada archivo por archivo

```
D:\Plantas\
├── pom.xml
├── index.html
├── INSTALL.md
├── GUIA_COMPLETA.md
├── maven-config/
│   └── settings.xml
├── .mvn/wrapper/
│   └── maven-wrapper.properties
└── src/
    └── main/
        ├── java/com/plantas/
        │   ├── PlantasApplication.java
        │   ├── config/
        │   │   └── CorsConfig.java
        │   ├── controller/
        │   │   └── FlorController.java
        │   ├── dto/
        │   │   └── ApiResponse.java
        │   ├── model/
        │   │   └── Flor.java
        │   ├── repository/
        │   │   └── FlorRepository.java
        │   └── service/
        │       └── FlorService.java
        └── resources/
            └── application.properties
```

---

### pom.xml

Es el archivo central de Maven. Define:
- Las coordenadas del proyecto: `groupId`, `artifactId`, `version`
- Las dependencias (librerias) que el proyecto necesita
- La version de Java a usar
- Los plugins de compilacion

Cuando corres `mvn spring-boot:run`, Maven lee este archivo, descarga todas las dependencias declaradas al repositorio local y luego compila y ejecuta la aplicacion.

Las dependencias clave de este proyecto:

| Dependencia | Para que sirve |
|---|---|
| spring-boot-starter-web | Servidor web y soporte para REST |
| spring-boot-starter-data-jpa | JPA + Hibernate para la BD |
| mysql-connector-j | Driver JDBC para conectarse a MySQL |

---

### application.properties

Archivo de configuracion de la aplicacion. Spring Boot lo lee automaticamente al arrancar.

```properties
# Con que base de datos conectarse
spring.datasource.url=jdbc:mysql://localhost:3306/plantas_db?...
spring.datasource.username=root
spring.datasource.password=root

# Que hace Hibernate con las tablas al iniciar
# update = crea si no existen, agrega columnas nuevas, nunca borra datos
spring.jpa.hibernate.ddl-auto=update

# Muestra en consola el SQL que ejecuta Hibernate
spring.jpa.show-sql=true

# Puerto donde corre el servidor
server.port=8080
```

---

### PlantasApplication.java

Es el punto de entrada de la aplicacion. Contiene el metodo `main` que arranca todo.

```java
@SpringBootApplication   // activa el escaneo automatico de componentes
public class PlantasApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlantasApplication.class, args);
    }
}
```

`@SpringBootApplication` es una anotacion combinada que activa tres cosas:
- Configuracion automatica de Spring
- Escaneo de componentes (busca @Controller, @Service, @Repository)
- Habilitacion de propiedades de configuracion

---

### model/Flor.java

Representa la tabla `flores` en la base de datos. Cada campo de la clase es una columna de la tabla.

```java
@Entity          // le dice a Hibernate que esta clase es una tabla
@Table(name = "flores")   // nombre exacto de la tabla en MySQL
public class Flor {

    @Id                                          // columna de llave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // autoincremental
    private Long id;

    @Column(nullable = false, length = 100)   // NOT NULL, VARCHAR(100)
    private String nombre;

    // ... demas campos
}
```

Cuando Hibernate lee esta clase, sabe exactamente como debe ser la tabla en MySQL y puede crear o validar su estructura.

---

### repository/FlorRepository.java

Es la capa que se comunica directamente con la base de datos. Extiende `JpaRepository` que ya incluye los metodos basicos de forma automatica:

| Metodo heredado | SQL equivalente |
|---|---|
| `findAll()` | SELECT * FROM flores |
| `findById(id)` | SELECT * FROM flores WHERE id = ? |
| `save(flor)` | INSERT INTO flores ... o UPDATE flores ... |
| `deleteById(id)` | DELETE FROM flores WHERE id = ? |

Los metodos adicionales que se definen con nombre especial:

```java
findByNombreContainingIgnoreCase(String nombre)
// → SELECT * FROM flores WHERE LOWER(nombre) LIKE LOWER('%valor%')

findByColorIgnoreCase(String color)
// → SELECT * FROM flores WHERE LOWER(color) = LOWER('valor')
```

Spring Data JPA lee el nombre del metodo y genera el SQL automaticamente. No hay que escribir ninguna consulta.

---

### service/FlorService.java

Es la capa de logica de negocio. Recibe datos ya validados del controlador, coordina las llamadas al repositorio y retorna resultados.

Reglas de esta capa:
- No usa try/catch. Si algo falla, la excepcion sube al controlador
- No retorna JSON ni formatos especiales, solo objetos Java
- Aqui iria cualquier logica adicional: calculos, comparaciones, iteraciones

El metodo `update` es el mas interesante porque implementa la actualizacion dinamica:

```java
public Flor update(Long id, Flor updates) {
    Flor existente = findById(id);  // falla si no existe

    // Solo actualiza los campos que llegaron con valor
    if (updates.getNombre() != null) existente.setNombre(updates.getNombre());
    if (updates.getPrecio() != null) existente.setPrecio(updates.getPrecio());
    // ... etc

    return repository.save(existente);
}
```

---

### controller/FlorController.java

Es la capa que recibe las peticiones HTTP. Es el unico punto de entrada externo a la aplicacion.

Responsabilidades de esta capa:
1. Validar que los parametros requeridos lleguen correctos (si no, retorna 400)
2. Llamar al service con los datos validados
3. Atrapar cualquier excepcion que suba del service y formatearla en JSON
4. Retornar la respuesta con el codigo HTTP correcto

```java
@RestController          // marca la clase como controlador REST
@RequestMapping("/api/flores")   // URL base para todos los metodos
public class FlorController {

    @GetMapping("/{id}")         // GET /api/flores/{id}
    public ResponseEntity<ApiResponse<Flor>> findById(@PathVariable Long id) {
        try {
            Flor flor = service.findById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "OK", flor));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
```

Anotaciones importantes:
- `@GetMapping` → responde a peticiones GET
- `@PostMapping` → responde a peticiones POST
- `@PutMapping` → responde a peticiones PUT
- `@DeleteMapping` → responde a peticiones DELETE
- `@PathVariable` → extrae el valor de la URL (`/flores/5` → id = 5)
- `@RequestParam` → extrae el valor de un query param (`?nombre=rosa`)
- `@RequestBody` → convierte el JSON del body en un objeto Java

---

### dto/ApiResponse.java

DTO significa Data Transfer Object. Es un objeto cuyo unico proposito es definir el formato de los datos que se transfieren entre capas o hacia el cliente.

Esta clase define las 3 propiedades que siempre aparecen en la respuesta:

```java
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;       // T es un tipo generico: puede ser Flor, List<Flor>, etc.
}
```

Cuando Spring convierte este objeto a JSON, produce exactamente:
```json
{ "success": true, "message": "OK", "data": { ... } }
```

---

### config/CorsConfig.java

CORS (Cross-Origin Resource Sharing) es un mecanismo de seguridad de los navegadores que bloquea peticiones entre diferentes origenes (dominios o protocolos) a menos que el servidor lo permita explicitamente.

El problema: el archivo `index.html` se abre desde `file://` y la API corre en `http://localhost:8080`. Son origenes diferentes, por lo que el navegador bloquearia las peticiones.

Esta clase le dice a Spring que acepte peticiones de cualquier origen:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
```

Sin esta configuracion, el HTML funcionaria desde Postman (que no es un navegador) pero fallaria desde el navegador con el error "failed to fetch".

---

### index.html

Interfaz web de prueba incluida en el proyecto. Al no ser un proyecto frontend con React o Angular, es un archivo HTML simple que se abre directamente en el navegador. Usa JavaScript puro con `fetch` para hacer las peticiones a la API.

No forma parte del servidor Spring Boot. Es completamente independiente, el servidor solo sirve la API.

---

## Flujo completo de una peticion de ejemplo

Para concretar todo, este es el recorrido completo de un `POST /api/flores`:

1. Postman envia la peticion con el JSON en el body
2. Tomcat (el servidor embebido) recibe la peticion en el puerto 8080
3. El `DispatcherServlet` de Spring analiza la URL y el metodo HTTP
4. Determina que debe llamar al metodo `create` de `FlorController`
5. Spring convierte automaticamente el JSON del body en un objeto `Flor` (deserializacion)
6. `FlorController.create()` valida que el campo `nombre` no este vacio
7. Si la validacion pasa, llama a `FlorService.save(flor)`
8. `FlorService` llama a `FlorRepository.save(flor)`
9. Hibernate genera el SQL `INSERT INTO flores (nombre, color, ...) VALUES (?, ?, ...)`
10. MySQL ejecuta el INSERT y regresa el ID generado
11. El objeto `Flor` regresa con el ID asignado hasta `FlorController`
12. `FlorController` crea un `ApiResponse` con `success=true` y el objeto `Flor`
13. Spring convierte el `ApiResponse` a JSON (serializacion)
14. El servidor regresa la respuesta con codigo HTTP 201
15. Postman muestra el JSON con los datos de la flor creada
