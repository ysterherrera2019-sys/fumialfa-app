package com.certificaciones.backend.certificate;

import com.certificaciones.backend.auth.SecurityUtils;
import com.certificaciones.backend.client.Client;
import com.certificaciones.backend.client.ClientService;
import com.certificaciones.backend.pest.PestControlDetail;
import com.certificaciones.backend.pest.PestControlDetailRepository;
import com.certificaciones.backend.pest.PestControlDetailRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ClientService clientService;
    private final CertificateSequenceService sequenceService;
    private final TankCleaningDetailRepository tankCleaningDetailRepository;
    private final PestControlDetailRepository pestControlDetailRepository;

    public CertificateService(
            CertificateRepository certificateRepository,
            ClientService clientService,
            CertificateSequenceService sequenceService,
            TankCleaningDetailRepository tankCleaningDetailRepository,
            PestControlDetailRepository pestControlDetailRepository) {
        this.certificateRepository = certificateRepository;
        this.clientService = clientService;
        this.sequenceService = sequenceService;
        this.tankCleaningDetailRepository = tankCleaningDetailRepository;
        this.pestControlDetailRepository = pestControlDetailRepository;
    }

    public Certificate create(Long clientId, CertificateType type, LocalDateTime serviceDate) {
        Client client = clientService.findById(clientId);

        Certificate certificate = new Certificate();
        certificate.setClient(client);
        certificate.setType(type);
        certificate.setServiceDate(serviceDate);
        certificate.setCertificateNumber(sequenceService.generateCertificateNumber(type));
        certificate.setCreatedBy(SecurityUtils.getCurrentUsername());

        Certificate saved = certificateRepository.save(certificate);

        if (type == CertificateType.TANK_CLEANING) {
            TankCleaningDetail detail = new TankCleaningDetail();
            detail.setCertificate(saved);
            tankCleaningDetailRepository.save(detail);
            saved.setTankDetail(detail);
        }

        if (type == CertificateType.PEST_CONTROL) {
            PestControlDetail detail = new PestControlDetail();
            detail.setCertificate(saved);
            pestControlDetailRepository.save(detail);
            saved.setPestDetail(detail);
        }

        return saved;
    }

    public List<Certificate> findAll() {
        return certificateRepository.findAll();
    }

    public Certificate findById(Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado"));
    }

    public Certificate finalizeCertificate(Long id) {
        Certificate certificate = findById(id);

        if (certificate.getStatus() != CertificateStatus.DRAFT) {
            throw new RuntimeException("Solo se pueden finalizar certificados en estado DRAFT");
        }

        validateBeforeFinalize(certificate);

        certificate.setStatus(CertificateStatus.FINALIZED);
        return certificateRepository.save(certificate);
    }

    private void validateBeforeFinalize(Certificate certificate) {
        if (certificate.getType() == CertificateType.TANK_CLEANING) {
            validateTankCertificate(certificate);
            return;
        }

        if (certificate.getType() == CertificateType.PEST_CONTROL) {
            validatePestCertificate(certificate);
            return;
        }

        throw new RuntimeException("Tipo de certificado no soportado para finalizar");
    }

    private void validateTankCertificate(Certificate certificate) {
        TankCleaningDetail detail = certificate.getTankDetail();

        if (detail == null) {
            throw new RuntimeException("Debe diligenciar el formulario técnico de tanques");
        }

        requireText(detail.getEstablishmentType(), "Tipo de establecimiento obligatorio");
        requireText(detail.getTankType(), "Tipo de tanque obligatorio");
        requireText(detail.getTankCapacity(), "Capacidad del tanque obligatoria");
        requireText(detail.getTankQuantity(), "Cantidad de tanques obligatoria");
        requireText(detail.getTankMaterial(), "Material del tanque obligatorio");
        requireText(detail.getChemicalProduct(), "Producto químico obligatorio");
        requireText(detail.getSystemType(), "Sistema utilizado obligatorio");
        requireText(detail.getToolUsed(), "Herramienta utilizada obligatoria");
    }

    private void validatePestCertificate(Certificate certificate) {
        PestControlDetail detail = certificate.getPestDetail();

        if (detail == null) {
            throw new RuntimeException("Debe diligenciar el formulario técnico de control de plagas");
        }

        requireText(detail.getEstablishmentType(), "Tipo de establecimiento obligatorio");
        requireText(detail.getPestType(), "Tipo de plaga obligatorio");
        requireText(detail.getTreatmentType(), "Tipo de tratamiento obligatorio");
        requireText(detail.getChemicalProduct(), "Producto químico obligatorio");
        requireText(detail.getAppliedDose(), "Dosis aplicada obligatoria");
        requireText(detail.getTreatedAreas(), "Áreas tratadas obligatorias");
    }

    private void requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(message);
        }
    }

    public Certificate updateTankDetail(Long certificateId, TankCleaningDetailRequest request) {
        Certificate certificate = findById(certificateId);

        if (certificate.getType() != CertificateType.TANK_CLEANING) {
            throw new RuntimeException("Este certificado no es de lavado de tanques");
        }

        if (certificate.getStatus() != CertificateStatus.DRAFT) {
            throw new RuntimeException("Solo se pueden editar certificados en estado DRAFT");
        }

        TankCleaningDetail detail = certificate.getTankDetail();

        if (detail == null) {
            detail = new TankCleaningDetail();
            detail.setCertificate(certificate);
        }

        detail.setEstablishmentType(request.establishmentType());
        detail.setTankType(request.tankType());
        detail.setTankCapacity(request.tankCapacity());
        detail.setTankQuantity(request.tankQuantity());
        detail.setTankMeasures(request.tankMeasures());
        detail.setTankMaterial(request.tankMaterial());
        detail.setChemicalProduct(request.chemicalProduct());
        detail.setSystemType(request.systemType());
        detail.setToolUsed(request.toolUsed());
        detail.setObservations(request.observations());

        tankCleaningDetailRepository.save(detail);
        certificate.setTankDetail(detail);

        return certificateRepository.save(certificate);
    }

    public Certificate updatePestDetail(Long certificateId, PestControlDetailRequest request) {
        Certificate certificate = findById(certificateId);

        if (certificate.getType() != CertificateType.PEST_CONTROL) {
            throw new RuntimeException("Este certificado no es de control de plagas");
        }

        if (certificate.getStatus() != CertificateStatus.DRAFT) {
            throw new RuntimeException("Solo se pueden editar certificados en estado DRAFT");
        }

        PestControlDetail detail = certificate.getPestDetail();

        if (detail == null) {
            detail = new PestControlDetail();
            detail.setCertificate(certificate);
        }

        detail.setEstablishmentType(request.establishmentType());
        detail.setPestType(request.pestType());
        detail.setTreatmentType(request.treatmentType());
        detail.setChemicalProduct(request.chemicalProduct());
        detail.setAppliedDose(request.appliedDose());
        detail.setTreatedAreas(request.treatedAreas());
        detail.setObservations(request.observations());

        pestControlDetailRepository.save(detail);
        certificate.setPestDetail(detail);

        return certificateRepository.save(certificate);
    }

    public Certificate markEmailSent(Long certificateId) {
        Certificate certificate = findById(certificateId);
        certificate.setEmailSent(true);
        return certificateRepository.save(certificate);
    }
}