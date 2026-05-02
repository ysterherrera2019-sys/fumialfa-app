package com.certificaciones.backend.service;

import com.certificaciones.backend.certificate.Certificate;
import com.certificaciones.backend.certificate.CertificateService;
import com.certificaciones.backend.certificate.CertificateType;
import com.certificaciones.backend.client.Client;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository repository;
    private final CertificateService certificateService;

    public ServiceRequestService(
            ServiceRequestRepository repository,
            CertificateService certificateService
    ) {
        this.repository = repository;
        this.certificateService = certificateService;
    }

    public ServiceRequest create(Client client, RequestedServiceType type) {

        ServiceRequest existing = repository
                .findExistingPending(client.getId(), type, ServiceRequestStatus.PENDING)
                .orElse(null);

        if (existing != null) {
            return existing;
        }

        ServiceRequest request = new ServiceRequest();
        request.setClient(client);
        request.setRequestedServiceType(type);
        request.setStatus(ServiceRequestStatus.PENDING);

        return repository.save(request);
    }

    public List<ServiceRequest> findAll() {
        return repository.findAll();
    }

    public ServiceRequest findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
    }

    public ServiceRequest updateStatus(Long id, ServiceRequestStatus status) {
        ServiceRequest request = findById(id);
        request.setStatus(status);
        return repository.save(request);
    }

    public Certificate createCertificateFromRequest(Long requestId) {

        ServiceRequest request = findById(requestId);

        if (request.getStatus() != ServiceRequestStatus.COMPLETED) {
            throw new RuntimeException("Solo se puede crear certificado cuando la solicitud está COMPLETED");
        }

        if (request.getCertificateId() != null) {
            return certificateService.findById(request.getCertificateId());
        }

        CertificateType certificateType = mapToCertificateType(request.getRequestedServiceType());

        Certificate certificate = certificateService.create(
                request.getClient().getId(),
                certificateType,
                LocalDateTime.now()
        );

        request.setCertificateId(certificate.getId());
        repository.save(request);

        return certificate;
    }

    // 🔥 NUEVO MÉTODO CLAVE
    public boolean isCertificateEmailSent(ServiceRequest request) {
        if (request.getCertificateId() == null) {
            return false;
        }

        Certificate certificate = certificateService.findById(request.getCertificateId());

        return Boolean.TRUE.equals(certificate.getEmailSent());
    }

    private CertificateType mapToCertificateType(RequestedServiceType requestedServiceType) {

        if (requestedServiceType == RequestedServiceType.TANK_CLEANING) {
            return CertificateType.TANK_CLEANING;
        }

        if (requestedServiceType == RequestedServiceType.PEST_CONTROL) {
            return CertificateType.PEST_CONTROL;
        }

        throw new RuntimeException("Todavía no existe certificado para EXTINTORES");
    }
}
