package com.certificaciones.backend.certificate;

import com.certificaciones.backend.client.Client;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;

@Service
public class CertificateEmailService {

    private final CertificatePdfService pdfService;
    private final RestClient googleTokenClient;
    private final RestClient gmailClient;

    @Value("${gmail.client.id}")
    private String gmailClientId;

    @Value("${gmail.client.secret}")
    private String gmailClientSecret;

    @Value("${gmail.refresh.token}")
    private String gmailRefreshToken;

    @Value("${mail.from}")
    private String mailFrom;

    public CertificateEmailService(CertificatePdfService pdfService) {
        this.pdfService = pdfService;
        this.googleTokenClient = RestClient.create("https://oauth2.googleapis.com");
        this.gmailClient = RestClient.create("https://gmail.googleapis.com");
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

        try {
            byte[] pdf = pdfService.generate(certificate);
            String accessToken = getAccessToken();

            MimeMessage message = buildEmail(
                    email,
                    "Certificado de servicio " + certificate.getCertificateNumber(),
                    pdf,
                    "certificado_" + certificate.getCertificateNumber() + ".pdf"
            );

            String rawMessage = encodeMessage(message);

            gmailClient.post()
                    .uri("/gmail/v1/users/me/messages/send")
                    .header("Authorization", "Bearer " + accessToken)
                    .body(Map.of("raw", rawMessage))
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {
            throw new RuntimeException("Error enviando certificado por Gmail API: " + e.getMessage(), e);
        }
    }

    private String getAccessToken() {
        Map<String, String> response = googleTokenClient.post()
                .uri("/token")
                .body(Map.of(
                        "client_id", gmailClientId,
                        "client_secret", gmailClientSecret,
                        "refresh_token", gmailRefreshToken,
                        "grant_type", "refresh_token"
                ))
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("access_token") == null) {
            throw new RuntimeException("No se pudo obtener access_token de Google");
        }

        return response.get("access_token");
    }

    private MimeMessage buildEmail(
            String to,
            String subject,
            byte[] pdf,
            String pdfName
    ) throws Exception {

        Session session = Session.getInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        message.setFrom(mailFrom);
        message.setRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject(subject, "UTF-8");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText("""
                Cordial saludo,

                Adjuntamos el certificado correspondiente al servicio realizado.

                Atentamente,
                Certificaciones
                """, "UTF-8");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        attachmentPart.setFileName(pdfName);
        attachmentPart.setContent(pdf, "application/pdf");

        MimeMultipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(attachmentPart);

        message.setContent(multipart);
        message.saveChanges();

        return message;
    }

    private String encodeMessage(MimeMessage message) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        message.writeTo(buffer);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(buffer.toByteArray());
    }
}