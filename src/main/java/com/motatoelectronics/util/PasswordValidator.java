package com.motatoelectronics.util;

/**
 * ============================================================
 * UTIL: PasswordValidator
 * ============================================================
 * Valida que una contraseña cumpla con la política de seguridad:
 *
 *   ✅ Mínimo 8 caracteres
 *   ✅ Al menos 1 letra mayúscula (A-Z)
 *   ✅ Al menos 1 letra minúscula (a-z)
 *   ✅ Al menos 1 número (0-9)
 *
 * Si falla, lanza RuntimeException con el mensaje específico.
 * ============================================================
 */
public class PasswordValidator {

    /** Longitud mínima de la contraseña. */
    public static final int MIN_LENGTH = 8;

    /**
     * Valida que la contraseña cumpla con la política de seguridad.
     *
     * @param password contraseña en texto plano
     * @throws RuntimeException si no cumple con algún requisito
     */
    public static void validar(String password) {

        if (password == null || password.isEmpty()) {
            throw new RuntimeException("La contraseña es obligatoria.");
        }

        if (password.length() < MIN_LENGTH) {
            throw new RuntimeException(
                "La contraseña debe tener al menos " + MIN_LENGTH + " caracteres.");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new RuntimeException(
                "La contraseña debe contener al menos una letra mayúscula.");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new RuntimeException(
                "La contraseña debe contener al menos una letra minúscula.");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new RuntimeException(
                "La contraseña debe contener al menos un número.");
        }
    }
}
