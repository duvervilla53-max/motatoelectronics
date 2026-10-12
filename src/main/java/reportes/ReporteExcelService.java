package com.motatoelectronics.reportes;

import com.motatoelectronics.entity.Cita;
import com.motatoelectronics.entity.Usuario;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * SERVICIO: ReporteExcelService
 * ============================================================
 * Genera archivos Excel (.xlsx) usando Apache POI.
 * ============================================================
 */
@Service
public class ReporteExcelService {

    private Workbook crearLibro() {
        return new XSSFWorkbook();
    }

    private void crearEncabezado(Sheet sheet, String[] columnas) {
        Row header = sheet.createRow(0);

        CellStyle estilo = sheet.getWorkbook().createCellStyle();
        Font fuente = sheet.getWorkbook().createFont();
        fuente.setBold(true);
        fuente.setColor(IndexedColors.WHITE.getIndex());
        estilo.setFont(fuente);
        estilo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(estilo);
            sheet.setColumnWidth(i, 5000);
        }
    }

    // =========================================================
    // INFORME DE CITAS
    // =========================================================
    public byte[] generarExcelCitas(List<Cita> citas) throws Exception {
        try (Workbook wb = crearLibro();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Citas");

            String[] cols = {
                    "ID", "Fecha", "Hora", "Cliente", "Técnico",
                    "Dirección", "Descripción", "Diagnóstico", "Informe",
                    "Mano de obra", "Repuestos", "Total", "Estado", "Pagado"
            };
            crearEncabezado(sheet, cols);

            int fila = 1;
            for (Cita c : citas) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(c.getId() != null ? c.getId() : 0);
                row.createCell(1).setCellValue(c.getFecha() != null ? c.getFecha().toString() : "");
                row.createCell(2).setCellValue(c.getHora() != null ? c.getHora().toString() : "");
                row.createCell(3).setCellValue("Cliente #" + c.getClienteId());
                row.createCell(4).setCellValue(c.getTecnicoId() != null ? "Técnico #" + c.getTecnicoId() : "—");
                row.createCell(5).setCellValue(c.getDireccionVisita() != null ? c.getDireccionVisita() : "");
                row.createCell(6).setCellValue(c.getDescripcion() != null ? c.getDescripcion() : "");
                row.createCell(7).setCellValue(c.getDiagnostico() != null ? c.getDiagnostico() : "");
                row.createCell(8).setCellValue(c.getInforme() != null ? c.getInforme() : "");
                row.createCell(9).setCellValue(toDouble(c.getManoDeObra()));
                row.createCell(10).setCellValue(toDouble(c.getTotalRepuestos()));
                row.createCell(11).setCellValue(toDouble(c.getTotal()));
                row.createCell(12).setCellValue(c.getEstado() != null ? c.getEstado() : "");
                row.createCell(13).setCellValue(Boolean.TRUE.equals(c.getPagado()) ? "SÍ" : "NO");
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    // =========================================================
    // INFORME FINANCIERO
    // =========================================================
    public byte[] generarExcelFinanciero(Map<String, Object> r) throws Exception {
        try (Workbook wb = crearLibro();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Financiero");

            int fila = 0;
            fila = escribirFila(sheet, fila, "INFORME FINANCIERO", "");
            fila = escribirFila(sheet, fila, "Desde", r.get("desde").toString());
            fila = escribirFila(sheet, fila, "Hasta", r.get("hasta").toString());
            fila++;
            fila = escribirFila(sheet, fila, "Total de citas", r.get("totalCitas").toString());
            fila = escribirFila(sheet, fila, "Citas pagadas", r.get("citasPagadas").toString());
            fila = escribirFila(sheet, fila, "Citas pendientes de pago", r.get("citasPendientesPago").toString());
            fila++;
            fila = escribirFila(sheet, fila, "Ingresos por mano de obra", formatear(r.get("totalManoObra")));
            fila = escribirFila(sheet, fila, "Ingresos por repuestos", formatear(r.get("totalRepuestos")));
            fila = escribirFila(sheet, fila, "INGRESOS TOTALES", formatear(r.get("totalGeneral")));
            fila++;
            fila = escribirFila(sheet, fila, "--- POR MÉTODO DE PAGO ---", "");
            fila = escribirFila(sheet, fila, "Efectivo", formatear(r.get("ingresoEfectivo")));
            fila = escribirFila(sheet, fila, "Nequi", formatear(r.get("ingresoNequi")));
            fila = escribirFila(sheet, fila, "Tarjeta", formatear(r.get("ingresoTarjeta")));
            fila = escribirFila(sheet, fila, "PSE", formatear(r.get("ingresoPSE")));
            fila = escribirFila(sheet, fila, "Cantidad de pagos", r.get("cantidadPagos").toString());

            sheet.setColumnWidth(0, 8000);
            sheet.setColumnWidth(1, 8000);

            wb.write(out);
            return out.toByteArray();
        }
    }

    // =========================================================
    // INFORME DE TÉCNICOS
    // =========================================================
    public byte[] generarExcelTecnicos(List<Map<String, Object>> tecnicos) throws Exception {
        try (Workbook wb = crearLibro();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Técnicos");

            String[] cols = {
                    "ID", "Nombre", "Correo",
                    "Citas totales", "Pendientes", "Asignadas", "En proceso",
                    "Completadas", "Canceladas",
                    "Ingresos generados", "Tasa cumplimiento (%)"
            };
            crearEncabezado(sheet, cols);

            int fila = 1;
            for (Map<String, Object> t : tecnicos) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue((Integer) t.get("tecnicoId"));
                row.createCell(1).setCellValue((String) t.get("nombre"));
                row.createCell(2).setCellValue((String) t.get("correo"));
                row.createCell(3).setCellValue(((Number) t.get("totalCitas")).longValue());
                row.createCell(4).setCellValue(((Number) t.get("pendientes")).longValue());
                row.createCell(5).setCellValue(((Number) t.get("asignadas")).longValue());
                row.createCell(6).setCellValue(((Number) t.get("enProceso")).longValue());
                row.createCell(7).setCellValue(((Number) t.get("completadas")).longValue());
                row.createCell(8).setCellValue(((Number) t.get("canceladas")).longValue());
                row.createCell(9).setCellValue(toDouble((BigDecimal) t.get("ingresosGenerados")));
                row.createCell(10).setCellValue((Double) t.get("tasaCumplimiento"));
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    // =========================================================
    // INFORME DE USUARIOS
    // =========================================================
    public byte[] generarExcelUsuarios(List<Usuario> usuarios) throws Exception {
        try (Workbook wb = crearLibro();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Usuarios");

            String[] cols = {
                    "ID", "Nombre", "Cédula", "Correo",
                    "Teléfono", "Dirección", "Rol", "Estado", "Fecha registro"
            };
            crearEncabezado(sheet, cols);

            int fila = 1;
            for (Usuario u : usuarios) {
                Row row = sheet.createRow(fila++);
                row.createCell(0).setCellValue(u.getId() != null ? u.getId() : 0);
                row.createCell(1).setCellValue(u.getNombre() != null ? u.getNombre() : "");
                row.createCell(2).setCellValue(u.getCedula() != null ? u.getCedula() : "");
                row.createCell(3).setCellValue(u.getCorreo() != null ? u.getCorreo() : "");
                row.createCell(4).setCellValue(u.getTelefono() != null ? u.getTelefono() : "");
                row.createCell(5).setCellValue(u.getDireccion() != null ? u.getDireccion() : "");
                row.createCell(6).setCellValue(u.getRol() != null ? u.getRol() : "");
                row.createCell(7).setCellValue(u.getEstado() != null ? u.getEstado() : "");
                row.createCell(8).setCellValue(
                        u.getFechaCreacion() != null ? u.getFechaCreacion().toLocalDate().toString() : "");
            }

            wb.write(out);
            return out.toByteArray();
        }
    }

    // =========================================================
    // UTILIDADES
    // =========================================================
    private int escribirFila(Sheet sheet, int fila, String label, String valor) {
        Row row = sheet.createRow(fila);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(valor);
        return fila + 1;
    }

    private double toDouble(BigDecimal valor) {
        return valor != null ? valor.doubleValue() : 0.0;
    }

    private String formatear(Object valor) {
        if (valor == null) return "$0";
        if (valor instanceof BigDecimal) {
            return "$" + String.format("%,.2f", ((BigDecimal) valor).doubleValue());
        }
        return valor.toString();
    }
}
