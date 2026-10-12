package com.motatoelectronics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 * CLASE PRINCIPAL DE LA APLICACIÓN
 * ============================================================
 *
 * Es el punto de entrada del proyecto Spring Boot.
 *
 * Al ejecutar el método main(), Spring Boot:
 *  - Levanta el contenedor de Spring.
 *  - Escanea controllers, services y repositories.
 *  - Se conecta a MySQL.
 *  - Inicia el servidor Tomcat en el puerto 8080.
 */
@SpringBootApplication
public class MotatoelectronicsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotatoelectronicsApplication.class, args);
    }

}