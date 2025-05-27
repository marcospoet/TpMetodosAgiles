# 🪪 Sistema de Gestión de Licencias de Conducir - Municipalidad de Santa Fe

Este proyecto tiene como objetivo desarrollar un sistema de gestión de licencias de conducir para la Municipalidad de Santa Fe.  
La aplicación permite emitir, renovar y consultar licencias, así como gestionar titulares y usuarios administrativos.

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
- Listados filtrados por estado, nombre, grupo sanguíneo y condición de donante.
- Registro de auditoría con usuario y fecha de cada trámite.

---

## 👨‍💻 Metodología de Trabajo

Proyecto desarrollado bajo principios **Scrum**, con iteraciones y entregas incrementales.  
Cada historia fue planificada, refinada y estimada según valor de negocio y riesgo.

### 🔁 Sprints

El desarrollo se organizó en **dos sprints** con planning, daily meetings y retrospectivas.

---

## 📝 Historias de Usuario

- `#01` Emitir licencia (Alta)
- `#02` Calcular vigencia (Alta)
- `#03` Calcular costo (Media)
- `#04` Imprimir licencia (Alta)
- `#05` Renovar licencia (Media)
- …

---

## ✅ Buenas Prácticas

- Mensajes de commit bajo **Conventional Commits**.
- Ramas por tipo: `feature/`, `fix/`, `refactor/`, …
- **Clean Code** y principios **SOLID**.
- Historias y tareas documentadas con los templates de la cátedra.

### 📌 Tipos de commit convencionales

| Tipo       | Descripción                               | Ejemplo                                                     |
|------------|-------------------------------------------|-------------------------------------------------------------|
| `feat`     | Nueva funcionalidad                       | `feat: agregar impresión de comprobante de licencia`       |
| `fix`      | Corrección de errores                     | `fix: corregir cálculo de vigencia para menores de 21`     |
| `docs`     | Documentación                             | `docs: actualizar README con tabla de tipos de commit`     |
| `style`    | Formato (espacios, indentación, etc.)     | `style: aplicar formato a TitularController`               |
| `refactor` | Refactor sin cambio de funcionalidad      | `refactor: mover lógica de cálculo a servicio común`       |
| `test`     | Agregado o modificación de tests          | `test: agregar test de validación de edad mínima`          |
| `chore`    | Tareas menores (config, deps, scripts)    | `chore: configurar .gitignore para IntelliJ y Maven`       |
| `build`    | Cambios en sistema de build / contenedores| `build(docker): agregar docker-compose para entorno local` |

---

## ▶️ Puesta en marcha rápida (Base de datos en Docker, app local)

1. **Arranca la base de datos**

   ```bash
   docker compose up -d          # en la carpeta del docker-compose.yml
   docker compose ps             # verifica que MySQL esté "healthy"
   ```

   phpMyAdmin disponible en [http://localhost:6080](http://localhost:6080)
   Servidor: **mysql** — Usuario: **root** — Contraseña: *(vacía)*

2. **Lanza la aplicación**

   ```bash
   ./mvnw spring-boot:run   # o botón Run de IntelliJ
   ```

   Spring se conecta a `jdbc:mysql://localhost:3306/tpAgiles`.

3. **Apaga todo al terminar**

   ```bash
   docker compose down      # --volumes si quieres borrar los datos
   ```
