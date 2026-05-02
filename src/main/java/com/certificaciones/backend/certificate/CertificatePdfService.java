package com.certificaciones.backend.certificate;

import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class CertificatePdfService {

    private final CertificateTemplateService templateService;

    public CertificatePdfService(CertificateTemplateService templateService) {
        this.templateService = templateService;
    }

    public byte[] generate(Certificate certificate) {

        String html = templateService.renderCertificate(certificate);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        HtmlConverter.convertToPdf(html, out);

        return out.toByteArray();
    }
}