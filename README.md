# Proyecto: Sistema de Agendamiento de Citas

Este es un sistema full-stack para agendar citas en un salón de belleza. Permite a los clientes ver un catálogo de servicios, seleccionar los que desean y encontrar un horario disponible para su cita. La aplicación se integra con Google Calendar para la gestión de la agenda y envía una confirmación por WhatsApp.

## Tecnologías Utilizadas

El proyecto está dividido en un backend y un frontend, utilizando las siguientes tecnologías:

###  Backend
- **Java 17**: Lenguaje de programación principal.
- **Spring Boot 3**: Framework para crear la API REST.
- **Maven**: Gestor de dependencias y construcción del proyecto.
- **Spring Data JPA (Hibernate)**: Para la persistencia de datos y comunicación con la base de datos.
- **PostgreSQL**: Sistema de gestión de base de datos relacional.
- **Lombok**: Para reducir el código repetitivo en las clases de Java.
- **Google Calendar API**: Para crear y gestionar los eventos de las citas.

###  Frontend
- **Angular 17**: Framework para construir la interfaz de usuario.
- **TypeScript**: Lenguaje principal para el desarrollo en Angular.
- **Angular Signals**: Para una gestión de estado moderna y reactiva.
- **SCSS**: Preprocesador de CSS para estilos más organizados.
- **HTML5**: Para la estructura de las páginas.

### Contenerización y DevOps
- **Docker**: Para crear contenedores para cada servicio (backend, frontend, base de datos).
- **Docker Compose**: Para orquestar y ejecutar el entorno de desarrollo multi-contenedor.

---

## Prerrequisitos

Antes de empezar, asegúrate de tener instalado el siguiente software en tu máquina:

- **JDK 17** o superior (Java Development Kit).
- **Maven 3.8+**.
- **Node.js 20+** y **npm 10+**.
- **Docker** y **Docker Compose**.
- Un **IDE** para Java/Spring Boot (como IntelliJ IDEA o VS Code con extensiones de Java).
- Un **editor de código** para Angular (como VS Code).

##  Configuración del Entorno

1.  **Clonar el Repositorio**
    ```bash
    git clone <URL_DEL_REPOSITORIO>
    cd mi-proyecto-devops
    ```

2.  **Configurar el Backend**
    - Navega a la carpeta `backend`.
    - Necesitarás configurar las credenciales para la base de datos y la API de Google Calendar. Crea un archivo `application.properties` en `src/main/resources` con el siguiente contenido y reemplaza los valores:
      ```properties
      # Configuración de la Base de Datos (usada por Docker Compose)
      spring.datasource.url=jdbc:postgresql://db:5432/citasdb
      spring.datasource.username=admin
      spring.datasource.password=secret
      spring.jpa.hibernate.ddl-auto=update

      # Credenciales de Google Calendar API
      # Debes generar tu archivo credentials.json desde Google Cloud Console
      # y colocarlo en la ruta especificada.
      google.calendar.credentials.path=file:./path/to/your/credentials.json
      google.calendar.id=primary
      ```

3.  **Configurar el Frontend**
    - Navega a la carpeta `frontend`.
    - Instala todas las dependencias del proyecto con npm.
      ```bash
      npm install
      ```

---

##  Cómo Ejecutar la Aplicación

Existen dos formas de levantar el proyecto:

### 1. Usando Docker Compose (Recomendado)

Este método levanta todos los servicios (backend, frontend y base de datos) de forma orquestada.

Desde la carpeta raíz del proyecto (`mi-proyecto-devops`), ejecuta:
```bash
# El flag --build reconstruye las imágenes si hay cambios en el código
docker-compose up --build
```
- **Frontend** estará disponible en: `http://localhost:4200`
- **Backend API** estará disponible en: `http://localhost:8080`

### 2. Ejecución Local (para Desarrollo)

Si prefieres correr los servicios por separado en tu máquina local:

1.  **Base de Datos**: Inicia solo el servicio de la base de datos con Docker Compose.
    ```bash
    docker-compose up -d db
    ```
    *Asegúrate de que tu `application.properties` del backend apunte a `localhost` en lugar de `db` (`jdbc:postgresql://localhost:5432/citasdb`).*

2.  **Backend**: Dentro de la carpeta `backend`, ejecuta la aplicación Spring Boot.
    ```bash
    mvn spring-boot:run
    ```

3.  **Frontend**: Dentro de la carpeta `frontend`, inicia el servidor de desarrollo de Angular.
    ```bash
    ng serve
    ```

---