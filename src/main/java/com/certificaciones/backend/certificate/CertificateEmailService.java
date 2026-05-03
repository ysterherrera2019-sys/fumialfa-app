package com.certificaciones.backend.certificate;

import com.certificaciones.backend.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class CertificateEmailService {

    private final CertificatePdfService pdfService;
    private final RestClient restClient;

    @Value("${resend.api.key}")
    private String resendApiKey;

    public CertificateEmailService(CertificatePdfService pdfService) {
        this.pdfService = pdfService;
        this.restClient = RestClient.create("https://api.resend.com");
    }

    public void sendCertificate(Certificate certificate) {
        if (certificate.getStatus() != CertificateStatus.FINALIZED) {
            throw new RuntimeException("El certificado debe estar FINALIZED para enviarlo por correo");
        }

        Client client = certificate.getClient();

        if (client == null) {
            throw new RuntimeException("El certificado no tiene cliente asociado");
        }

        String email = client.getEmail();

        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("El cliente no tiene correo registrado");
        }

        byte[] pdf = pdfService.generate(certificate);

        String pdfBase64 = Base64.getEncoder().encodeToString(pdf);

        Map<String, Object> body = Map.of(
                "from", "Certificaciones <onboarding@resend.dev>",
                "to", List.of(email),
                "subject", "Certificado de servicio " + certificate.getCertificateNumber(),
                "text", """
                        Cordial saludo,

                        Adjuntamos el certificado correspondiente al servicio realizado.

                        Atentamente,
                        Certificaciones
                        """,
                "attachments", List.of(
                        Map.of(
                                "filename", "certificado_" + certificate.getCertificateNumber() + ".pdf",
                                "content", pdfBase64
                        )
                )
        );

        restClient.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}