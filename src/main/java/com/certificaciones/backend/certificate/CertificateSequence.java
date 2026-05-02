package com.certificaciones.backend.certificate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "certificate_sequences")
public class CertificateSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CertificateType type;

    private Integer year;

    private Long currentNumber;
}
