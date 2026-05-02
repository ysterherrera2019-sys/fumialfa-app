package com.certificaciones.backend.certificate;

import com.certificaciones.backend.client.Client;
import com.certificaciones.backend.pest.PestControlDetail;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CertificateType type;

    @Enumerated(EnumType.STRING)
    private CertificateStatus status = CertificateStatus.DRAFT;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne(mappedBy = "certificate", cascade = CascadeType.ALL)
    private TankCleaningDetail tankDetail;

    @OneToOne(mappedBy = "certificate", cascade = CascadeType.ALL)
    private PestControlDetail pestDetail;

    private String certificateNumber;

    private LocalDateTime serviceDate;

    private String createdBy;

    private Boolean emailSent = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    
}