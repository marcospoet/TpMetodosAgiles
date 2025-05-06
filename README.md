# 🪪 Sistema de Gestión de Licencias de Conducir - Municipalidad de Santa Fe

Este proyecto tiene como objetivo desarrollar un sistema de gestión de licencias de conducir para la Municipalidad de Santa Fe. La aplicación permite emitir, renovar y consultar licencias, así como gestionar titulares y usuarios administrativos.

---

## 🧩 Tecnologías Utilizadas

- Java 17+
- Spring Boot
- Maven
- Git & GitHub
- Metodologías Ágiles (Scrum)
- Commits Convencionales (Conventional Commits)

---

## 🚀 Objetivo del Proyecto

El sistema permite realizar las siguientes funcionalidades:

- Emisión, renovación y duplicación de licencias.
- Cálculo automático de vigencia y costo según edad, clase y vigencia.
- Impresión de licencia y comprobante.
- Alta y modificación de titulares y usuarios.
- Listados filtrados por estado, nombre, grupo sanguíneo, y condición de donante.
- Registro de auditoría con usuario y fecha de cada trámite.

---

## 👨‍💻 Metodología de Trabajo

Este proyecto fue desarrollado bajo los principios de **Scrum**, aplicando iteraciones y entregas incrementales. Cada historia de usuario fue planificada, refinada y estimada en base a criterios reales de negocio y riesgos de desarrollo.

### 🔁 Sprints

El desarrollo fue organizado en **dos sprints**, aplicando reuniones de planificación, daily meetings y retrospectivas.

---

## 📝 Historias de Usuario

Cada funcionalidad fue tratada como una historia de usuario con su correspondiente prioridad, estimación y riesgos.

Ejemplo de historias:

- `#01` Emitir licencia (Alta)
- `#02` Calcular vigencia (Alta)
- `#03` Calcular costo (Media)
- `#04` Imprimir licencia (Alta)
- `#05` Renovar licencia (Media)
- ...

---

## ✅ Buenas Prácticas

- Se utiliza `Conventional Commits` para los mensajes de commit.
- Se organizan las ramas por tipo de funcionalidad: `feature/`, `fix/`, `refactor/`, etc.
- Se aplica Clean Code y principios SOLID.
- Se documentan todas las historias y tareas usando los templates definidos por la cátedra.

### 📌 Tipos de commits convencionales

| Tipo        | Descripción                                               | Ejemplo                                               |
|-------------|-----------------------------------------------------------|--------------------------------------------------------|
| `feat`      | Nueva funcionalidad                                        | `feat: agregar impresión de comprobante de licencia`   |
| `fix`       | Corrección de errores                                      | `fix: corregir cálculo de vigencia para menores de 21` |
| `docs`      | Cambios en documentación                                   | `docs: actualizar README con tabla de tipos de commit` |
| `style`     | Cambios de formato (espacios, indentación, comas)         | `style: aplicar formato a clase TitularController`     |
| `refactor`  | Refactor sin cambio de funcionalidad ni bug               | `refactor: mover lógica de cálculo a servicio común`   |
| `test`      | Agregado o modificación de tests                           | `test: agregar test de validación de edad mínima`      |
| `chore`     | Tareas menores (configuración, dependencias, scripts)     | `chore: configurar .gitignore para IntelliJ y Maven`   |

---

## 🧪 Testing y Calidad

- Validaciones automáticas en backend.
- Registro de errores y auditoría de cambios.
- Pruebas manuales durante cada sprint para asegurar funcionalidad y estabilidad.

---

## 🧠 Equipo de Trabajo

- Product Owner: Rodrigo Ledesma (Docente)
- Equipo de desarrollo: Pividori Marcos, Poet Marcos, Cammisi José, Blanche Mateo, Lazzarini Bautista, Ramella Sebastian

---

## 📂 Estructura del Proyecto

> ⚠️ *La estructura actual se irá mejorando y evolucionando a medida que avance el desarrollo.*

```plaintext
📦 src  
 └── 📂 main  
     ├── 📂 java  
     │   └── 📂 com.tpagiles.app_licencia  
     │       ├── 📂 controllers              # Controladores REST  
     │       ├── 📂 dto                     # Data Transfer Objects  
     │       ├── 📂 exception               # Manejo de excepciones  
     │       ├── 📂 model                   # Entidades del dominio  
     │       │   ├── 📂 enums               # Enumeraciones  
     │       │   ├── 📄 Licencia.java  
     │       │   ├── 📄 Persona.java  
     │       │   ├── 📄 TarifarioLicencia.java  
     │       │   ├── 📄 Titular.java  
     │       │   └── 📄 Usuario.java  
     │       ├── 📂 repository              # Interfaces JPA / DAO  
     │       ├── 📂 service                 # Lógica de negocio  
     │       └── 📄 AppLicenciaApplication.java  
     └── 📂 resources  
         └── 📄 application.properties      # Configuración de Spring  

```
---

## 📦 Entregables

- Documento de planificación (Word)
- Historias refinadas y tareas asignadas
- Código fuente funcional
- Presentación de avances por sprint
- Informe final

---

## 📌 Notas Finales

> Este repositorio aplica buenas prácticas de desarrollo ágil, planificación iterativa, documentación continua y uso disciplinado de herramientas de control de versiones con commits convencionales.

