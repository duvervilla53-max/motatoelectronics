package com.motatoelectronics.reportes;

import com.motatoelectronics.entity.Cita;
import com.motatoelectronics.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * CONTROLADOR: ReporteController
 * ============================================================
 * Endpoints para generar y descargar reportes en Excel y PDF.
 *
 * URL base: /api/admin/reportes
 * ⚠️ Requiere token de ADMINISTRADOR.
 * ============================================================
 */
@RestController
@RequestMapping("/api/admin/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService service;

    @Autowired
    private ReporteExcelService excelService;

    @Autowired
    private ReportePdfService pdfService;

    // =========================================================
    // INFORME DE CITAS
    // GET /api/admin/reportes/citas/excel?desde=...&hasta=...
    // =========================================================
    @GetMapping("/citas/excel")
    public ResponseEntity<ByteArrayResource> citasExcel(
            @RequestParam String desde,
            @RequestParam String hasta) throws Exception {

        LocalDate d = LocalDate.parse(desde);
        LocalDate h = LocalDate.parse(hasta);

        List<Cita> citas = service.obtenerCitasPorRango(d, h);
        byte[] bytes = excelService.generarExcelCitas(citas);

        return descarga(bytes, "reporte-citas.xlsx");
    }

    @GetMapping("/citas/pdf")
    public ResponseEntity<ByteArrayResource> citasPdf(
            @RequestParam String desde,
            @RequestParam String hasta) throws Exception {

        LocalDate d = LocalDate.parse(desde);
        LocalDate h = LocalDate.parse(hasta);

        List<Cita> citas = service.obtenerCitasPorRango(d, h);
        byte[] bytes = pdfService.generarPdfCitas(citas, d, h);

        return descarga(bytes, "reporte-citas.pdf");
    }

    // =========================================================
    // INFORME FINANCIERO
    // =========================================================
    @GetMapping("/financiero/excel")
    public ResponseEntity<ByteArrayResource> financieroExcel(
            @RequestParam String desde,
            @RequestParam String hasta) throws Exception {

        Map<String, Object> resumen = service.obtenerResumenFinanciero(
                LocalDate.parse(desde), LocalDate.parse(hasta));
        byte[] bytes = excelService.generarExcelFinanciero(resumen);

        return descarga(bytes, "reporte-financiero.xlsx");
    }

    @GetMapping("/financiero/pdf")
    public ResponseEntity<ByteArrayResource> financieroPdf(
            @RequestParam String desde,
            @RequestParam String hasta) throws Exception {

        Map<String, Object> resumen = service.obtenerResumenFinanciero(
                LocalDate.parse(desde), LocalDate.parse(hasta));
        byte[] bytes = pdfService.generarPdfFinanciero(resumen);

        return descarga(bytes, "reporte-financiero.pdf");
    }

    // =========================================================
    // INFORME DE TÉCNICOS
    // =========================================================
    @GetMapping("/tecnicos/excel")
    public ResponseEntity<ByteArrayResource> tecnicosExcel(
            @RequestParam String desde,
            @RequestParam String hasta) throws Exception {

        List<Map<String, Object>> tecnicos = service.obtenerCumplimientoTecnicos(
                LocalDate.parse(desde), LocalDate.parse(hasta));
        byte[] bytes = excelService.generarExcelTecnicos(tecnicos);

        return descarga(bytes, "reporte-tecnicos.xlsx");
    }

    @GetMapping("/tecnicos/pdf")
    public ResponseEntity<ByteArrayResource> tecnicosPdf(
            @RequestParam String desde,
            @RequestParam String hasta) throws Exception {

        List<Map<String, Object>> tecnicos = service.obtenerCumplimientoTecnicos(
                LocalDate.parse(desde), LocalDate.parse(hasta));
        byte[] bytes = pdfService.generarPdfTecnicos(tecnicos);

        return descarga(bytes, "reporte-tecnicos.pdf");
    }

    // =========================================================
    // INFORME DE USUARIOS
    // =========================================================
    @GetMapping("/usuarios/excel")
    public ResponseEntity<ByteArrayResource> usuariosExcel() throws Exception {
        List<Usuario> usuarios = service.obtenerUsuarios();
        byte[] bytes = excelService.generarExcelUsuarios(usuarios);
        return descarga(bytes, "reporte-usuarios.xlsx");
    }

    @GetMapping("/usuarios/pdf")
    public ResponseEntity<ByteArrayResource> usuariosPdf() throws Exception {
        List<Usuario> usuarios = service.obtenerUsuarios();
        byte[] bytes = pdfService.generarPdfUsuarios(usuarios);
        return descarga(bytes, "reporte-usuarios.pdf");
    }

    // =========================================================
    // UTILIDAD PARA DESCARGAR ARCHIVOS
    // =========================================================
    private ResponseEntity<ByteArrayResource> descarga(byte[] bytes, String nombreArchivo) {

        MediaType tipo = nombreArchivo.endsWith(".pdf")
                ? MediaType.APPLICATION_PDF
                : MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        ByteArrayResource recurso = new ByteArrayResource(bytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(tipo)
                .contentLength(bytes.length)
                .body(recurso);
    }
}