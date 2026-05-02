package com.certificaciones.backend.certificate;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/certificates")
@CrossOrigin
public class PublicCertificateController {

    private final CertificateRepository certificateRepository;

    public PublicCertificateController(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    @GetMapping("/verify/{certificateNumber}")
    public Map<String, Object> verify(@PathVariable String certificateNumber) {

        Certificate certificate = certificateRepository
                .findByCertificateNumber(certificateNumber)
                .orElse(null);

        Map<String, Object> response = new HashMap<>();

        if (certificate == null) {
            response.put("valid", false);
            response.put("message", "Certificado no existe");
            return response;
        }

        if (certificate.getStatus() != CertificateStatus.FINALIZED) {
            response.put("valid", false);
            response.put("message", "Certificado no finalizado");
            return response;
        }

        response.put("valid", true);
        response.put("certificateNumber", certificate.getCertificateNumber());
        response.put("type", certificate.getType());
        response.put("status", certificate.getStatus());
        response.put("serviceDate", certificate.getServiceDate());
        response.put("client", certificate.getClient().getBusinessName());
        response.put("legalName", certificate.getClient().getLegalName());
        response.put("taxId", certificate.getClient().getTaxId());

        return response;
    }
}