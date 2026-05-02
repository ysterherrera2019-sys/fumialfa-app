package com.certificaciones.backend.pest;

public record PestControlDetailRequest(
        String establishmentType,
        String pestType,
        String treatmentType,
        String chemicalProduct,
        String appliedDose,
        String treatedAreas,
        String observations
) {
}