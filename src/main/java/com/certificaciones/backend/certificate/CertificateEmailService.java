package com.certificaciones.backend.certificate;

import com.certificaciones.backend.client.Client;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class CertificateEmailService {

    private final JavaMailSender mailSender;
    private final CertificatePdfService pdfService;

    public CertificateEmailService(
            JavaMailSender mailSender,
            CertificatePdfService pdfService
    ) {
        this.mailSender = mailSender;
        this.pdfService = pdfService;
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

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Certificado de servicio " + certificate.getCertificateNumber());

            helper.setText(
                    """
                    Cordial saludo,

                    Adjuntamos el certificado correspondiente al servicio realizado.

                    Atentamente,
                    Certificaciones
                    """,
                    false
            );

            helper.addAttachment(
                    "certificado_" + certificate.getCertificateNumber() + ".pdf",
                    new ByteArrayResource(pdf)
            );

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error construyendo el correo del certificado");
        }
    }
}