package com.certificaciones.backend.service;

import com.certificaciones.backend.client.Client;

public class ServiceRequestResponse {

    private Long id;
    private Client client;
    private RequestedServiceType requestedServiceType;
    private ServiceRequestStatus status;
    private Long certificateId;
    private Boolean certificateEmailSent;

    public ServiceRequestResponse(
            Long id,
            Client client,
            RequestedServiceType requestedServiceType,
            ServiceRequestStatus status,
            Long certificateId,
            Boolean certificateEmailSent
    ) {
        this.id = id;
        this.client = client;
        this.requestedServiceType = requestedServiceType;
        this.status = status;
        this.certificateId = certificateId;
        this.certificateEmailSent = certificateEmailSent;
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public RequestedServiceType getRequestedServiceType() {
        return requestedServiceType;
    }

    public ServiceRequestStatus getStatus() {
        return status;
    }

    public Long getCertificateId() {
        return certificateId;
    }

    public Boolean getCertificateEmailSent() {
        return certificateEmailSent;
    }
}