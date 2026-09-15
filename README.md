AquaChile APP - Pre-Chequeo de Seguridad en Operaciones de Buceo

&gt; **Caso académico:** AquaCheck Buceo / AquaChile APP  
&gt; **Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles  
&gt; **Institución:** CITT Duoc UC Sede Puerto Montt  
&gt; **Socio Formador:** AquaChile  

---

## Nombre y Propósito del Proyecto

**AquaChile APP** es una solución móvil académica diseñada para apoyar y digitalizar el proceso de **pre-chequeo de seguridad previo a las faenas de buceo profesional** en los centros de engorda de salmones. 

### Problema que resuelve
Actualmente, la revisión de equipamiento y los controles preventivos de salud se realizan de forma manual en formularios de papel o bitácoras físicas. Esto dificulta la trazabilidad, genera riesgo de pérdidas de registros e impide realizar análisis preventivos en tiempo real antes de una inmersión.

### Solución propuesta (MVP)
La aplicación guía paso a paso al supervisor y buzo en terreno mediante:
* Registro de datos generales de la operación (centro, fecha/hora, buzo y supervisor).
* Encuesta preventiva de salud y parámetros simulados de condición física.
* Checklist operativo de seguridad de equipos críticos (compresores, umbilicales, oxígeno, buzo de emergencia).
* Captura de respaldo fotográfico de las condiciones del equipamiento.
* Generación de un resultado preliminar del pre-chequeo (*Cumple, Observado, Requiere revisión*).
* Persistencia local (Room/SQLite) e historial de registros para operar con conectividad limitada en terreno.

---

## Identidad Visual

### Logotipo
* **Archivo:** `docs/diseno/logo.png`
* **Representación:** Simboliza la identidad corporativa de AquaChile vinculada a la seguridad marítima.

### Paleta de Colores

| Elemento | Código HEX | Uso dentro de la App |
| :--- | :--- | :--- |
| **Color Principal** | `#73B6FF` | Botones de acción principal y elementos interactivos clave. |
| **Color Secundario** | `#5983FF` | Opciones secundarias, estados y bordes de realce. |
| **Color de Fondo** | `#FFFFFF` | Fondo limpio para maximizar el contraste y la legibilidad en terreno. |
| **Color de Texto** | `#000000` / `#FFFFFF` | Textos primarios y secundarios sobre contenedores. |
| **Color Adicional** | `#6BFAE7` | Destacados visuales, alertas leves e íconos de estado. |

---

## Flujo de Usuario (Diagrama UML)

* **Ubicación del recurso:** `docs/diseno/flujo-usuario-uml.png`

```mermaid
stateDiagram-v2
    [*] --&gt; Login: Iniciar App
    Login --&gt; Home: Credenciales Válidas
    Login --&gt; Login: Error de Credenciales
    Home --&gt; DatosGenerales: Crear Nuevo Pre-Chequeo
    DatosGenerales --&gt; EncuestaSalud: Ingresar Centro, Buzo y Supervisor
    EncuestaSalud --&gt; ChecklistSeguridad: Registrar Encuesta y Parámetros
    ChecklistSeguridad --&gt; EvidenciasFotograficas: Completar Ítems y Observaciones
    EvidenciasFotograficas --&gt; ResumenResultado: Adjuntar Fotos de Equipos
    ResumenResultado --&gt; Home: Guardar Registro Local (Room)
    ResumenResultado --&gt; [*]: Cancelar / Descartar

```

---

## Pantallas Principales del MVP

Las interfaces diseñadas responden a los componentes de **Material Design 3** y se encuentran organizadas en la carpeta `docs/diseno/interfaces/`:

| Archivo                           | Pantalla                         | Componente Material 3 | Propósito en la App                                                   |
| --------------------------------- | -------------------------------- | --------------------- | --------------------------------------------------------------------- |
| 01\_login.png                     | **Inicio de Sesión**             | TextField, Button     | Autenticar al usuario y validar su rol asignado.                      |
| 02\_home.png                      | **Home / Menú Principal**        | NavigationBar, FAB    | Panel central para acceso rápido a *"Crear Pre-Chequeo"*.             |
| 03\_nuevo\_prechequeo.png         | **Nuevo Pre-Chequeo**            | TopAppBar, TextField  | Capturar centro de operación, fecha/hora, buzo y supervisor.          |
| 04\_encuesta\_salud.png           | **Encuesta Preventiva de Salud** | Card, Icon            | Evaluar la condición del buzo mediante parámetros de salud simulados. |
| 05\_checklist\_seguridad.png      | **Checklist Operativo**          | Card, Icon            | Validar el estado de equipos (*Cumple, No cumple, Observado, N/A*).   |
| 06\_evidencias\_observaciones.png | **Evidencias y Observaciones**   | TextField, Button     | Cargar fotografías de prueba y redactar observaciones obligatorias.   |
| 07\_resumen\_resultado.png        | **Resumen y Resultado**          | Card, Dialog          | Mostrar la síntesis y estado final (*Cumple / Observado*).            |
| 08\_historial.png                 | **Historial de Pre-Chequeos**    | Card                  | Consultar registros previos almacenados en la base de datos local.    |

---

## Tecnologías Utilizadas

* **Lenguaje:** Kotlin
* **UI Framework:** Jetpack Compose
* **Sistema de Diseño:** Material Design 3
* **Arquitectura:** MVVM (Model - ViewModel - UI - Repository)
* **Persistencia Local:** Room / SQLite
* **Modelado de Flujo:** PlantUML / Mermaid (UML Activity Diagram)

---

## Equipo

* **Sección:** 002D
* **Equipo:** Equipo NAM - 6

| Nombre y Apellido   | Rol en el Proyecto         |
| ------------------- | -------------------------- |
| Nicolas Oyarzo      | Desarrollo / Diseño        |
| Alexander Gutierrez | Desarrollo / Documentación |
| Matias Mansilla     | Desarrollo / UML           |

---
