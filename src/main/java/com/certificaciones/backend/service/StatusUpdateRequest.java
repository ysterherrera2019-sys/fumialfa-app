package com.certificaciones.backend.service;

import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "El estado es obligatorio")
        ServiceRequestStatus status
) {
}