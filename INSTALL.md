# Flores API - Manual de Instalacion

API REST con operaciones CRUD para la gestion de flores.
Construida con Spring Boot 3.2, Java 17 y MySQL.

---

## Requisitos previos

Antes de instalar, asegurate de tener lo siguiente:

| Herramienta | Version minima | Verificacion |
|-------------|----------------|--------------|
| Java JDK    | 17             | `java --version` |
| Maven       | 3.6+           | `mvn --version` |
| MySQL       | 8.0+           | `mysql --version` |

---

## Instalacion de dependencias

### Java 17

Descarga el JDK 17 desde el sitio oficial de Oracle o Adoptium:
- Oracle: https://www.oracle.com/java/technologies/downloads/
- Adoptium (gratuito): https://adoptium.net/

Agrega `JAVA_HOME` a tus variables de entorno apuntando al directorio de instalacion.

### Maven

Descarga Apache Maven desde:
- https://maven.apache.org/download.cgi

Extrae el archivo y agrega la carpeta `bin` al PATH del sistema.

Alternativa en Windows (si no quieres instalarlo globalmente): el archivo `mvnw.cmd`
incluido en este repositorio descarga Maven automaticamente en `D:\maven-wrapper`
la primera vez que se ejecuta.

### MySQL

Descarga e instala MySQL Community Server desde:
- https://dev.mysql.com/downloads/mysql/

Durante la instalacion, configura una contrasena para el usuario `root`.

---

## Pasos de instalacion

### 1. Clonar el repositorio

```bash
git clone https://github.com/TU_USUARIO/TU_REPOSITORIO.git
cd TU_REPOSITORIO
```

### 2. Crear la base de datos

Abre MySQL y ejecuta:

```sql
CREATE DATABASE IF NOT EXISTS plantas_db;
```

La tabla `flores` se crea automaticamente al iniciar la aplicacion.

### 3. Configurar credenciales de base de datos

Abre el archivo `src/main/resources/application.properties` y ajusta los valores:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/plantas_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=TU_CONTRASENA
```

Si tu usuario o puerto de MySQL son diferentes, cambialos aqui tambien.

### 4. Descargar dependencias y ejecutar

Con Maven instalado globalmente:

```bash
mvn spring-boot:run
```

En Windows, usando el script incluido (descarga Maven automaticamente):

```cmd
mvnw.cmd spring-boot:run
```

O bien, especificando un repositorio local en disco D para no ocupar espacio en C:

```cmd
mvn -s maven-config/settings.xml spring-boot:run
```

### 5. Verificar que esta corriendo

Cuando veas este mensaje en la consola, la aplicacion esta lista:

```
Started PlantasApplication in X.XXX seconds
```

Abre el navegador en:

```
http://localhost:8080/api/flores
```

Deberia responder con:

```json
{
  "success": true,
  "message": "OK",
  "data": []
}
```

---

## Endpoints disponibles

| Metodo | URL                                      | Descripcion              |
|--------|------------------------------------------|--------------------------|
| GET    | /api/flores                              | Obtener todas las flores |
| GET    | /api/flores/{id}                         | Obtener flor por ID      |
| GET    | /api/flores/buscar?nombre=xxx            | Buscar por nombre        |
| GET    | /api/flores/color?color=xxx             | Buscar por color         |
| GET    | /api/flores/temporada?temporada=xxx     | Buscar por temporada     |
| POST   | /api/flores                              | Crear flor               |
| PUT    | /api/flores/{id}                         | Actualizar flor          |
| DELETE | /api/flores/{id}                         | Eliminar flor            |

### Estructura del JSON de respuesta

Todos los endpoints retornan el mismo formato:

```json
{
  "success": true,
  "message": "OK",
  "data": { ... }
}
```

### Ejemplo de creacion (POST)

```json
{
  "nombre": "Rosa Roja",
  "color": "rojo",
  "familia": "Rosaceae",
  "descripcion": "Rosa clasica de jardin",
  "precio": 25.50,
  "temporada": "primavera",
  "disponible": true
}
```

Solo el campo `nombre` es obligatorio. Los demas son opcionales.

### Ejemplo de actualizacion parcial (PUT)

El PUT actualiza unicamente los campos que se incluyan en el body.
Los campos que no se envien conservan su valor actual.

```json
{
  "precio": 30.00,
  "disponible": false
}
```

---

## Estructura del proyecto

```
src/
  main/
    java/com/plantas/
      config/         - Configuracion CORS
      controller/     - Capa REST (validacion y formato de respuesta)
      service/        - Capa de logica de negocio
      repository/     - Capa de acceso a base de datos
      model/          - Entidad Flor
      dto/            - Clase ApiResponse (formato de respuesta)
    resources/
      application.properties  - Configuracion de base de datos y servidor
```

---

## Interfaz web incluida

El archivo `index.html` en la raiz del proyecto es una interfaz de prueba.
Abrir directamente en el navegador con el servidor corriendo.

---

## Puerto por defecto

La API corre en el puerto `8080`. Para cambiarlo, edita `application.properties`:

```properties
server.port=9090
```
