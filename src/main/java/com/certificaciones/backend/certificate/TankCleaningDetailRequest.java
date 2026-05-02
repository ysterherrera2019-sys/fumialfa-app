package com.certificaciones.backend.certificate;

public record TankCleaningDetailRequest(
        String establishmentType,
        String tankType,
        String tankCapacity,
        String tankQuantity,
        String tankMeasures,
        String tankMaterial,
        String chemicalProduct,
        String systemType,
        String toolUsed,
        String observations
) {
}
