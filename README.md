# 🔧 Motato Electronics — Sistema de Gestión de Servicio Técnico

Sistema completo de gestión para la empresa **Motato Electronics**, especializada en la reparación de televisores a domicilio. Incluye paneles diferenciados por rol (cliente, técnico, administrador), gestión de citas, pagos, repuestos, informes en Excel/PDF y seguridad con JWT.

---

## 📋 Descripción

Motato Electronics es un sistema web full-stack que permite:

- 👤 A **clientes**: agendar citas, ver el estado de sus reparaciones, aprobar repuestos y pagar en línea.
- 🔧 A **técnicos**: aceptar citas, reprogramarlas, registrar diagnósticos e informes, y solicitar repuestos.
- ⚙️ A **administradores**: gestionar usuarios, supervisar citas, reasignar técnicos y generar informes detallados.

---

## ✨ Características principales

### 🔐 Autenticación y seguridad

- Registro público (tradicional o rápido con verificación por correo).
- Login con contraseñas hasheadas con **BCrypt**.
- Autenticación con **JWT (JSON Web Token)**.
- Recuperación de contraseña mediante código enviado al correo.
- Política de contraseñas seguras (mínimo 8 caracteres, mayúscula, minúscula, número).
- Roles diferenciados: `CLIENTE`, `TECNICO`, `ADMINISTRADOR`.

### 📅 Gestión de citas

- Creación de citas con validación de fechas futuras.
- Aprobación o reprogramación por parte del técnico.
- Confirmación o rechazo de reprogramación por parte del cliente.
- Estados: `PENDIENTE`, `ASIGNADA`, `REPROGRAMACION_PENDIENTE`, `EN_PROCESO`, `COMPLETADA`, `CANCELADA`.
- Reasignación de técnicos por parte del administrador.

### 💰 Pagos

- Métodos disponibles: **Efectivo, Nequi, Tarjeta, PSE**.
- Registro de pagos con fecha, método y monto.
- Control de citas pagadas vs. pendientes.
- Validación contra pagos duplicados.

### 🔧 Repuestos

- Inventario con gestión de stock.
- Repuestos del inventario y repuestos manuales.
- Aprobación o rechazo por parte del cliente.
- Garantía configurable por repuesto.

### 📊 Informes

- Descarga en **Excel** (.xlsx) y **PDF**.
- Informes disponibles:
  - Citas por rango de fechas.
  - Financiero (ingresos totales, por método de pago).
  - Cumplimiento de técnicos.
  - Listado completo de usuarios.

### 🛡️ Diagnóstico e informe técnico

- El técnico registra:
  - **Diagnóstico**: qué encontró.
  - **Informe**: qué trabajo realizó.
  - Mano de obra, garantía y observaciones.

---

## 🛠️ Tecnologías utilizadas

### Backend

- **Java 21**
- **Spring Boot 3.5.16**
- **Spring Data JPA** + Hibernate
- **Spring Security** + **JWT (jjwt)**
- **BCrypt** para hashing de contraseñas
- **Apache POI** para generación de Excel
- **OpenPDF** para generación de PDF
- **JavaMailSender** + Mailtrap para envío de correos

### Base de datos

- **MySQL 8.0**

### Frontend

- **HTML5**, **CSS3**, **JavaScript Vanilla**
- Diseño responsivo y moderno

### Herramientas

- **NetBeans IDE**
- **Postman** para pruebas de API
- **Mailtrap** para pruebas de correo
- **Maven** para gestión de dependencias

---

## 📁 Estructura del proyecto
motatoelectronics/
├── src/main/java/com/motatoelectronics/
│ ├── config/ → Configuración (CORS, CoreConfig, DataSeeder)
│ ├── controller/ → Endpoints REST
│ ├── dto/ → Data Transfer Objects
│ ├── entity/ → Entidades JPA
│ ├── repository/ → Repositorios JPA
│ ├── reportes/ → Servicios de informes (Excel/PDF)
│ ├── security/ → Configuración JWT y Spring Security
│ ├── service/ → Lógica de negocio
│ └── util/ → Utilidades (validadores, etc.)
├── src/main/resources/
│ ├── static/ → Archivos HTML, CSS, JS
│ │ ├── css/
│ │ ├── img/
│ │ ├── js/
│ │ ├── index.html
│ │ ├── servicios.html
│ │ ├── citas.html
│ │ ├── registro.html
│ │ ├── cliente.html
│ │ ├── tecnico.html
│ │ └── admin.html
│ ├── application.properties.example
│ └── application.properties (ignorado por Git)
└── pom.xml

text

---

## 🚀 Instalación y ejecución

### Requisitos previos

- Java 21
- MySQL 8.0
- Maven
- NetBeans o IntelliJ IDEA

### Pasos

1. **Clonar el repositorio:**

   ```bash
   git clone https://github.com/duverilla53-max/motatoelectronics.git
   cd motatoelectronics
Crear la base de datos:

sql
CREATE DATABASE login CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
Luego ejecuta los scripts SQL para crear las tablas: usuarios, citas, repuestos, cita_repuestos, pagos, codigos_recuperacion.

Configurar application.properties:

Copia el archivo de ejemplo:

bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
Y edita application.properties con tus credenciales:

properties
spring.datasource.url=jdbc:mysql://localhost:3306/login
spring.datasource.username=root
spring.datasource.password=TU_CONTRASENA

jwt.secret=TU_CLAVE_SECRETA_LARGA_ALEATORIA
jwt.expiration=3600000

spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=TU_USERNAME
spring.mail.password=TU_PASSWORD
Compilar y ejecutar:

bash
./mvnw spring-boot:run
Abrir en el navegador:

text
http://localhost:8080/index.html
👥 Usuarios por defecto
Rol	Correo	Contraseña
Administrador	admin@.com	

Los clientes y técnicos se registran desde la web.

🔌 Endpoints principales
Autenticación
Método	Endpoint	Descripción
POST	/api/auth/login	Iniciar sesión
POST	/api/auth/recuperar/solicitar	Solicitar código de recuperación
POST	/api/auth/recuperar/validar	Validar código y cambiar contraseña
POST	/api/auth/registro/solicitar-codigo	Registro rápido: solicitar código
POST	/api/auth/registro/verificar-codigo	Verificar código
Citas
Método	Endpoint	Descripción
POST	/api/citas	Crear cita
GET	/api/citas/cliente/{id}	Citas de un cliente
GET	/api/citas/tecnico/{id}	Citas de un técnico
PUT	/api/citas/{id}/aprobar/{tecId}	Técnico aprueba
PUT	/api/citas/{id}/completar	Técnico completa
PUT	/api/citas/{id}/pagar	Cliente paga
Administración
Método	Endpoint	Descripción
GET	/api/admin/citas	Listar citas con filtros
PUT	/api/admin/citas/{id}/reasignar/{tecId}	Reasignar técnico
GET	/api/admin/reportes/citas/excel	Informe de citas Excel
GET	/api/admin/reportes/citas/pdf	Informe de citas PDF
GET	/api/admin/reportes/financiero/excel	Informe financiero Excel
GET	/api/admin/reportes/tecnicos/excel	Cumplimiento de técnicos
🧪 Pruebas
Las pruebas fueron realizadas con Postman y documentadas siguiendo los lineamientos de la norma ISO/IEC/IEEE 29119.

Casos ejecutados
✅ Crear cita desde el panel del cliente

✅ Cancelar cita

✅ Ver detalle de cita completada

✅ Pago con EFECTIVO

✅ Pago con NEQUI

✅ Pago con TARJETA

✅ Pago con PSE

✅ Intento de pago doble (rechazado correctamente)

🗺️ Roadmap
☑ Autenticación con JWT
☑ Módulo de citas
☑ Módulo de pagos
☑ Módulo de repuestos
☑ Informes Excel/PDF
☑ Diagnóstico e informe técnico
□ Subida de fotos del TV
□ Notificaciones por correo automáticas
□ Google Maps (autocompletado de direcciones)
□ Aplicación móvil (Android)
🤝 Contribuciones
Este proyecto es de uso interno de Motato Electronics. Para contribuir, contacta al administrador del repositorio.

📄 Licencia
© 2026 Motato Electronics — Todos los derechos reservados.

📞 Contacto
Empresa: Motato Electronics

Correo: soportetecnicomotatoelectronics@gmail.com

Teléfono: +57 3150044990

Dirección: Cra 10 #5-17, Parque Industrial, Medellín, Colombia

⭐ Agradecimientos
Desarrollado con ❤️ por el equipo de Motato Electronics.
