package com.certificaciones.backend.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    @Query("""
        SELECT sr
        FROM ServiceRequest sr
        WHERE sr.client.id = :clientId
          AND sr.requestedServiceType = :type
          AND sr.status = :status
        ORDER BY sr.id ASC
    """)
    Optional<ServiceRequest> findExistingPending(
            @Param("clientId") Long clientId,
            @Param("type") RequestedServiceType type,
            @Param("status") ServiceRequestStatus status
    );
}