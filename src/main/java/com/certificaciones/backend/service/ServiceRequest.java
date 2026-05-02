package com.certificaciones.backend.service;

import com.certificaciones.backend.client.Client;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "service_requests")
public class ServiceRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    private RequestedServiceType requestedServiceType;

    @Enumerated(EnumType.STRING)
    private ServiceRequestStatus status = ServiceRequestStatus.PENDING;

    private String notes;

    private Long certificateId;

    private LocalDateTime createdAt = LocalDateTime.now();
}
