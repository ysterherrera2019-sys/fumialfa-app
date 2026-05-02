package com.certificaciones.backend.certificate;

import com.certificaciones.backend.pest.PestControlDetailRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService certificateService;
    private final CertificatePdfService pdfService;
    private final CertificateEmailService emailService;

    public CertificateController(
            CertificateService certificateService,
            CertificatePdfService pdfService,
            CertificateEmailService emailService) {
        this.certificateService = certificateService;
        this.pdfService = pdfService;
        this.emailService = emailService;
    }

    @PostMapping
    public CertificateResponse create(@RequestParam Long clientId, @RequestParam CertificateType type) {
        Certificate certificate = certificateService.create(clientId, type, LocalDateTime.now());
        return CertificateResponse.from(certificate);
    }

    @GetMapping
    public List<CertificateResponse> getAll() {
        return certificateService.findAll()
                .stream()
                .map(CertificateResponse::from)
                .toList();
    }

    @PutMapping("/{id}/finalize")
    public CertificateResponse finalizeCertificate(@PathVariable Long id) {
        Certificate certificate = certificateService.finalizeCertificate(id);
        return CertificateResponse.from(certificate);
    }

    @PutMapping("/{id}/tank-detail")
    public CertificateResponse updateTankDetail(
            @PathVariable Long id,
            @RequestBody TankCleaningDetailRequest request) {
        Certificate certificate = certificateService.updateTankDetail(id, request);
        return CertificateResponse.from(certificate);
    }

    @PutMapping("/{id}/pest-detail")
    public CertificateResponse updatePestDetail(
            @PathVariable Long id,
            @RequestBody PestControlDetailRequest request) {
        Certificate certificate = certificateService.updatePestDetail(id, request);
        return CertificateResponse.from(certificate);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        Certificate certificate = certificateService.findById(id);

        if (certificate.getStatus() != CertificateStatus.FINALIZED) {
            throw new RuntimeException("El certificado debe estar FINALIZED para generar PDF");
        }

        byte[] pdf = pdfService.generate(certificate);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=certificado_" + certificate.getCertificateNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/{id}/send-email")
    public ResponseEntity<String> sendCertificateEmail(@PathVariable Long id) {
        Certificate certificate = certificateService.findById(id);

        if (certificate.getStatus() != CertificateStatus.FINALIZED) {
            throw new RuntimeException("El certificado debe estar FINALIZED para enviarlo por correo");
        }

        if (Boolean.TRUE.equals(certificate.getEmailSent())) {
            return ResponseEntity.ok("El certificado ya fue enviado");
        }

        emailService.sendCertificate(certificate);
        certificateService.markEmailSent(id);

        return ResponseEntity.ok("Certificado enviado al correo del cliente");
    }
}