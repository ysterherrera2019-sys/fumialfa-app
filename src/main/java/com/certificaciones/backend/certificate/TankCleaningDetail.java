package com.certificaciones.backend.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tank_cleaning_details")
public class TankCleaningDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String establishmentType;
    private String tankType;
    private String tankCapacity;
    private String tankQuantity;
    private String tankMeasures;
    private String tankMaterial;
    private String chemicalProduct;
    private String systemType;
    private String toolUsed;

    @Column(length = 1000)
    private String observations;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "certificate_id")
    private Certificate certificate;
}