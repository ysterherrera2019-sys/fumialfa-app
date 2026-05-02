package com.certificaciones.backend.service;

import com.certificaciones.backend.certificate.Certificate;
import com.certificaciones.backend.certificate.CertificateResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-requests")
public class ServiceRequestController {

    private final ServiceRequestService service;

    public ServiceRequestController(ServiceRequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServiceRequestResponse> getAll() {
        return service.findAll()
                .stream()
                .map(request -> new ServiceRequestResponse(
                        request.getId(),
                        request.getClient(),
                        request.getRequestedServiceType(),
                        request.getStatus(),
                        request.getCertificateId(),
                        service.isCertificateEmailSent(request)
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public ServiceRequestResponse getById(@PathVariable Long id) {
        ServiceRequest request = service.findById(id);

        return new ServiceRequestResponse(
                request.getId(),
                request.getClient(),
                request.getRequestedServiceType(),
                request.getStatus(),
                request.getCertificateId(),
                service.isCertificateEmailSent(request)
        );
    }

    @PutMapping("/{id}/status")
    public ServiceRequest updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        return service.updateStatus(id, request.status());
    }

    @PostMapping("/{id}/certificate")
    public CertificateResponse createCertificate(@PathVariable Long id) {
        Certificate certificate = service.createCertificateFromRequest(id);
        return CertificateResponse.from(certificate);
    }
}