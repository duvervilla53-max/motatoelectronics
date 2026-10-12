package com.motatoelectronics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

/**
 * ============================================================
 * SERVICIO: EmailService
 * ============================================================
 * Envía correos electrónicos vía SMTP.
 * En desarrollo usa Mailtrap (no envía correos reales).
 * En producción usar Gmail con contraseña de aplicación.
 * ============================================================
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /** Correo desde el cual se envían los mensajes. */
    @Value("${motato.mail.from}")
    private String fromEmail;

    /** Nombre que verá el destinatario. */
    @Value("${motato.mail.from-name}")
    private String fromName;

    /**
     * Envía un código de verificación al correo indicado.
     *
     * @param destinatario correo del usuario
     * @param codigo       código de 6 dígitos
     * @param asunto       asunto del correo
     * @param encabezado   texto principal del cuerpo del correo
     */
    public void enviarCodigo(String destinatario, String codigo,
                             String asunto, String encabezado) {
        try {
            // Crear el mensaje MIME (permite HTML)
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(destinatario);
            helper.setSubject(asunto);

            // Plantilla HTML del correo (con estilos inline)
            String html = "<div style=\"font-family:Arial,sans-serif;max-width:500px;margin:auto;"
                    + "padding:30px;background:#f4f7fb;border-radius:12px;\">"

                    // Encabezado
                    + "<div style=\"text-align:center;padding-bottom:20px;\">"
                    + "<h1 style=\"color:#1E3A8A;margin:0;\">Motato Electronics</h1>"
                    + "<p style=\"color:#666;margin:5px 0 0 0;font-size:13px;\">"
                    + "Expertos en Tecnología Visual</p>"
                    + "</div>"

                    // Cuerpo
                    + "<div style=\"background:#fff;padding:30px;border-radius:10px;text-align:center;\">"
                    + "<h2 style=\"color:#1E3A8A;margin:0 0 15px 0;font-size:20px;\">"
                    + encabezado + "</h2>"
                    + "<p style=\"color:#666;font-size:14px;margin:0 0 20px 0;\">"
                    + "Usa este código para continuar. Expira en 15 minutos.</p>"

                    // Código destacado
                    + "<div style=\"background:#eff6ff;padding:20px;border-radius:8px;margin:20px 0;\">"
                    + "<span style=\"font-size:38px;font-weight:bold;color:#1E3A8A;"
                    + "letter-spacing:8px;\">" + codigo + "</span>"
                    + "</div>"

                    // Aviso
                    + "<p style=\"color:#999;font-size:12px;\">"
                    + "Si no solicitaste este código, ignora este mensaje.</p>"
                    + "</div>"

                    // Footer
                    + "<p style=\"text-align:center;color:#999;font-size:11px;margin-top:20px;\">"
                    + "© 2026 Motato Electronics — Todos los derechos reservados.</p>"
                    + "</div>";

            helper.setText(html, true);
            mailSender.send(mensaje);

            System.out.println("📧 Correo enviado a: " + destinatario + " (código: " + codigo + ")");

        } catch (Exception e) {
            System.err.println("❌ Error al enviar correo: " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo. Intenta de nuevo.");
        }
    }
}

