package com.certificaciones.backend.certificate;

import com.certificaciones.backend.pest.PestControlDetail;

import java.time.LocalDateTime;

public record CertificateResponse(
        Long id,
        CertificateType type,
        CertificateStatus status,
        String certificateNumber,
        LocalDateTime serviceDate,
        LocalDateTime createdAt,
        String createdBy,
        ClientData client,
        TankDetailData tankDetail,
        PestDetailData pestDetail
) {

    public static CertificateResponse from(Certificate certificate) {
        return new CertificateResponse(
                certificate.getId(),
                certificate.getType(),
                certificate.getStatus(),
                certificate.getCertificateNumber(),
                certificate.getServiceDate(),
                certificate.getCreatedAt(),
                certificate.getCreatedBy(),
                ClientData.from(certificate),
                TankDetailData.from(certificate.getTankDetail()),
                PestDetailData.from(certificate.getPestDetail())
        );
    }

    public record ClientData(
            Long id,
            String businessName,
            String legalName,
            String taxId,
            String email,
            String phone,
            String address,
            String city
    ) {
        public static ClientData from(Certificate certificate) {
            var client = certificate.getClient();

            if (client == null) {
                return null;
            }

            return new ClientData(
                    client.getId(),
                    client.getBusinessName(),
                    client.getLegalName(),
                    client.getTaxId(),
                    client.getEmail(),
                    client.getPhone(),
                    client.getAddress(),
                    client.getCity()
            );
        }
    }

    public record TankDetailData(
            Long id,
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
        public static TankDetailData from(TankCleaningDetail detail) {
            if (detail == null) {
                return null;
            }

            return new TankDetailData(
                    detail.getId(),
                    detail.getEstablishmentType(),
                    detail.getTankType(),
                    detail.getTankCapacity(),
                    detail.getTankQuantity(),
                    detail.getTankMeasures(),
                    detail.getTankMaterial(),
                    detail.getChemicalProduct(),
                    detail.getSystemType(),
                    detail.getToolUsed(),
                    detail.getObservations()
            );
        }
    }

    public record PestDetailData(
            Long id,
            String establishmentType,
            String pestType,
            String treatmentType,
            String chemicalProduct,
            String appliedDose,
            String treatedAreas,
            String observations
    ) {
        public static PestDetailData from(PestControlDetail detail) {
            if (detail == null) {
                return null;
            }

            return new PestDetailData(
                    detail.getId(),
                    detail.getEstablishmentType(),
                    detail.getPestType(),
                    detail.getTreatmentType(),
                    detail.getChemicalProduct(),
                    detail.getAppliedDose(),
                    detail.getTreatedAreas(),
                    detail.getObservations()
            );
        }
    }
}
