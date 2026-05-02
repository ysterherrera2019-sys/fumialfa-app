package com.certificaciones.backend.pest;

import com.certificaciones.backend.certificate.Certificate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pest_control_details")
public class PestControlDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String establishmentType;
    private String pestType;
    private String treatmentType;
    private String chemicalProduct;
    private String appliedDose;
    private String treatedAreas;

    @Column(length = 1000)
    private String observations;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "certificate_id", unique = true)
    private Certificate certificate;
}