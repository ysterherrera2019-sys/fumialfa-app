package com.certificaciones.backend.client;

import com.certificaciones.backend.service.RequestedServiceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ClientRegistrationRequest(

        @NotBlank
        String taxId,

        @NotBlank
        String businessName,

        @NotBlank
        String legalName,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String phone,

        @NotBlank
        String address,

        @NotBlank
        String city,

        @NotNull
        List<RequestedServiceType> requestedServiceTypes

) {}
