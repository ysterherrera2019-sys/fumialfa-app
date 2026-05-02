package com.certificaciones.backend.certificate;

import com.certificaciones.backend.pest.PestControlDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class CertificateTemplateService {

    private final TemplateEngine templateEngine;
    private final QrCodeService qrCodeService;

    @Value("${app.public-base-url}")
    private String publicBaseUrl;

    public CertificateTemplateService(
            TemplateEngine templateEngine,
            QrCodeService qrCodeService
    ) {
        this.templateEngine = templateEngine;
        this.qrCodeService = qrCodeService;
    }

    public String renderCertificate(Certificate certificate) {
        if (certificate.getType() == CertificateType.TANK_CLEANING) {
            return renderTankCleaningCertificate(certificate);
        }

        if (certificate.getType() == CertificateType.PEST_CONTROL) {
            return renderPestControlCertificate(certificate);
        }

        throw new RuntimeException("Tipo de certificado no soportado");
    }

    private String renderTankCleaningCertificate(Certificate certificate) {
        Context context = buildBaseContext(certificate);

        TankCleaningDetail detail = certificate.getTankDetail();

        if (detail == null) {
            throw new RuntimeException("El certificado no tiene datos técnicos de lavado de tanques");
        }

        context.setVariable("establishmentType", detail.getEstablishmentType());
        context.setVariable("tankType", detail.getTankType());
        context.setVariable("tankCapacity", detail.getTankCapacity());
        context.setVariable("tankQuantity", detail.getTankQuantity());
        context.setVariable("tankMeasures", detail.getTankMeasures());
        context.setVariable("tankMaterial", detail.getTankMaterial());
        context.setVariable("chemicalProduct", detail.getChemicalProduct());
        context.setVariable("systemType", detail.getSystemType());
        context.setVariable("toolUsed", detail.getToolUsed());
        context.setVariable("observations", detail.getObservations());

        return templateEngine.process("certificates/tank-cleaning-certificate", context);
    }

    private String renderPestControlCertificate(Certificate certificate) {
        Context context = buildBaseContext(certificate);

        PestControlDetail detail = certificate.getPestDetail();

        if (detail == null) {
            throw new RuntimeException("El certificado no tiene datos técnicos de control de plagas");
        }

        context.setVariable("establishmentType", detail.getEstablishmentType());
        context.setVariable("pestType", detail.getPestType());
        context.setVariable("treatmentType", detail.getTreatmentType());
        context.setVariable("chemicalProduct", detail.getChemicalProduct());
        context.setVariable("appliedDose", detail.getAppliedDose());
        context.setVariable("treatedAreas", detail.getTreatedAreas());
        context.setVariable("observations", detail.getObservations());

        return templateEngine.process("certificates/pest-control-certificate", context);
    }

    private Context buildBaseContext(Certificate certificate) {
        Context context = new Context();

        String verifyUrl = publicBaseUrl
                + "/api/public/certificates/verify/"
                + certificate.getCertificateNumber();

        context.setVariable("certificate", certificate);

        context.setVariable(
                "serviceDateFormatted",
                certificate.getServiceDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );

        context.setVariable("background", loadImageAsBase64("src/main/resources/static/images/background.png"));
        context.setVariable("footer", loadImageAsBase64("src/main/resources/static/images/footer.png"));
        context.setVariable("signature", loadImageAsBase64("src/main/resources/static/images/signature.png"));

        context.setVariable("verifyUrl", verifyUrl);
        context.setVariable("qr", qrCodeService.generateBase64Qr(verifyUrl));

        return context;
    }

    private String loadImageAsBase64(String path) {
        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(path));
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContent);
        } catch (Exception e) {
            throw new RuntimeException("Error cargando imagen: " + path, e);
        }
    }
}