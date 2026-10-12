package com.motatoelectronics;

// Importa la anotación @Test para indicar que un método corresponde a una prueba unitaria.
import org.junit.jupiter.api.Test;

// Importa SpringBootTest, que permite cargar todo el contexto de la aplicación
// para verificar que la configuración y los componentes funcionen correctamente.
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ==========================================================
 * CLASE DE PRUEBAS DE LA APLICACIÓN
 * ==========================================================
 *
 * Esta clase contiene las pruebas básicas del proyecto
 * MOTATO ELECTRONICS.
 *
 * Su función principal es comprobar que la aplicación
 * Spring Boot pueda iniciarse correctamente y que todos
 * los componentes del sistema (controladores, servicios,
 * repositorios y configuraciones) sean cargados sin errores.
 *
 * Este tipo de pruebas ayudan a detectar problemas de
 * configuración antes de ejecutar el sistema completo.
 */

// Indica que Spring Boot debe cargar el contexto completo
// de la aplicación durante la ejecución de las pruebas.
@SpringBootTest
class MotatoelectronicsApplicationTests {

    /**
     * ======================================================
     * PRUEBA DE CARGA DEL CONTEXTO
     * ======================================================
     *
     * Este método verifica que la aplicación pueda iniciarse
     * correctamente.
     *
     * Si durante la carga existe algún error de configuración,
     * dependencias faltantes o problemas en los componentes
     * del proyecto, esta prueba fallará automáticamente.
     *
     * Si la prueba finaliza correctamente significa que
     * Spring Boot logró inicializar toda la aplicación sin
     * inconvenientes.
     */

    @Test
    void contextLoads() {

        // No es necesario escribir código dentro del método.
        // Si Spring Boot logra cargar el contexto sin lanzar
        // excepciones, la prueba será considerada exitosa.

    }

}
