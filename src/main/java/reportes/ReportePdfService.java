package com.motatoelectronics.reportes;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.motatoelectronics.entity.Cita;
import com.motatoelectronics.entity.Usuario;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * SERVICIO: ReportePdfService
 * ============================================================
 * Genera archivos PDF con OpenPDF.
 * ============================================================
 */
@Service
public class ReportePdfService {

    private static final Color AZUL = new Color(30, 58, 138);

    // =========================================================
    // INFORME DE CITAS EN PDF
    // =========================================================
    public byte[] generarPdfCitas(List<Cita> citas, LocalDate desde, LocalDate hasta) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, out);
            doc.open();

            Paragraph titulo = new Paragraph("MOTATO ELECTRONICS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, AZUL));
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph subtitulo = new Paragraph("Informe de Citas",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY));
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(subtitulo);

            Paragraph rango = new Paragraph("Desde: " + desde + "   |   Hasta: " + hasta,
                    FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
            rango.setAlignment(Element.ALIGN_CENTER);
            doc.add(rango);
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 2, 3, 3, 3, 3, 2, 2});

            String[] cols = {"ID", "Fecha", "Cliente", "Técnico", "Diagnóstico", "Informe", "Total", "Estado"};
            for (String c : cols) {
                PdfPCell cell = new PdfPCell(new Phrase(c,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
                cell.setBackgroundColor(AZUL);
                cell.setPadding(5);
                table.addCell(cell);
            }

            for (Cita c : citas) {
                table.addCell(celda(c.getId() != null ? c.getId().toString() : ""));
                table.addCell(celda(c.getFecha() != null ? c.getFecha().toString() : ""));
                table.addCell(celda("Cliente #" + c.getClienteId()));
                table.addCell(celda(c.getTecnicoId() != null ? "Técnico #" + c.getTecnicoId() : "—"));
                table.addCell(celda(recortar(c.getDiagnostico())));
                table.addCell(celda(recortar(c.getInforme())));
                table.addCell(celda("$" + formatear(c.getTotal())));
                table.addCell(celda(c.getEstado() != null ? c.getEstado() : ""));
            }

            doc.add(table);
            doc.close();
            return out.toByteArray();
        }
    }

    // =========================================================
    // INFORME FINANCIERO EN PDF
    // =========================================================
    public byte[] generarPdfFinanciero(Map<String, Object> r) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Paragraph titulo = new Paragraph("MOTATO ELECTRONICS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, AZUL));
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph subtitulo = new Paragraph("Informe Financiero",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY));
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(subtitulo);

            Paragraph rango = new Paragraph("Desde: " + r.get("desde") + "   |   Hasta: " + r.get("hasta"),
                    FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY));
            rango.setAlignment(Element.ALIGN_CENTER);
            doc.add(rango);
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("RESUMEN GENERAL",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, AZUL)));
            doc.add(new Paragraph("Total de citas: " + r.get("totalCitas")));
            doc.add(new Paragraph("Citas pagadas: " + r.get("citasPagadas")));
            doc.add(new Paragraph("Citas pendientes de pago: " + r.get("citasPendientesPago")));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("INGRESOS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, AZUL)));
            doc.add(new Paragraph("Mano de obra: " + formatear(r.get("totalManoObra"))));
            doc.add(new Paragraph("Repuestos: " + formatear(r.get("totalRepuestos"))));
            doc.add(new Paragraph("TOTAL: " + formatear(r.get("totalGeneral")),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK)));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("POR MÉTODO DE PAGO",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, AZUL)));
            doc.add(new Paragraph("Efectivo: " + formatear(r.get("ingresoEfectivo"))));
            doc.add(new Paragraph("Nequi: " + formatear(r.get("ingresoNequi"))));
            doc.add(new Paragraph("Tarjeta: " + formatear(r.get("ingresoTarjeta"))));
            doc.add(new Paragraph("PSE: " + formatear(r.get("ingresoPSE"))));

            doc.close();
            return out.toByteArray();
        }
    }

    // =========================================================
    // INFORME DE TÉCNICOS EN PDF
    // =========================================================
    public byte[] generarPdfTecnicos(List<Map<String, Object>> tecnicos) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(doc, out);
            doc.open();

            Paragraph titulo = new Paragraph("MOTATO ELECTRONICS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, AZUL));
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph subtitulo = new Paragraph("Cumplimiento de Técnicos",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY));
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(subtitulo);
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);

            String[] cols = {"Técnico", "Citas", "Pendientes", "En proceso",
                    "Completadas", "Canceladas", "Ingresos", "Cumplimiento"};
            for (String c : cols) {
                PdfPCell cell = new PdfPCell(new Phrase(c,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
                cell.setBackgroundColor(AZUL);
                cell.setPadding(5);
                table.addCell(cell);
            }

            for (Map<String, Object> t : tecnicos) {
                table.addCell(celda((String) t.get("nombre")));
                table.addCell(celda(t.get("totalCitas").toString()));
                table.addCell(celda(t.get("pendientes").toString()));
                table.addCell(celda(t.get("enProceso").toString()));
                table.addCell(celda(t.get("completadas").toString()));
                table.addCell(celda(t.get("canceladas").toString()));
                table.addCell(celda("$" + formatear(t.get("ingresosGenerados"))));
                table.addCell(celda(t.get("tasaCumplimiento") + "%"));
            }

            doc.add(table);
            doc.close();
            return out.toByteArray();
        }
    }

    // =========================================================
    // INFORME DE USUARIOS EN PDF
    // =========================================================
    public byte[] generarPdfUsuarios(List<Usuario> usuarios) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Paragraph titulo = new Paragraph("MOTATO ELECTRONICS",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, AZUL));
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph subtitulo = new Paragraph("Listado de Usuarios",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY));
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(subtitulo);
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            String[] cols = {"ID", "Nombre", "Correo", "Rol", "Estado"};
            for (String c : cols) {
                PdfPCell cell = new PdfPCell(new Phrase(c,
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE)));
                cell.setBackgroundColor(AZUL);
                cell.setPadding(5);
                table.addCell(cell);
            }

            for (Usuario u : usuarios) {
                table.addCell(celda(u.getId() != null ? u.getId().toString() : ""));
                table.addCell(celda(u.getNombre()));
                table.addCell(celda(u.getCorreo()));
                table.addCell(celda(u.getRol()));
                table.addCell(celda(u.getEstado()));
            }

            doc.add(table);
            doc.close();
            return out.toByteArray();
        }
    }

    // =========================================================
    // UTILIDADES
    // =========================================================
    private PdfPCell celda(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(
                texto != null ? texto : "",
                FontFactory.getFont(FontFactory.HELVETICA, 8)));
        cell.setPadding(4);
        return cell;
    }

    private String recortar(String texto) {
        if (texto == null) return "";
        return texto.length() > 40 ? texto.substring(0, 37) + "..." : texto;
    }

    private String formatear(Object valor) {
        if (valor == null) return "0";
        if (valor instanceof BigDecimal) {
            return String.format("%,.0f", ((BigDecimal) valor).doubleValue());
        }
        return valor.toString();
    }
}
