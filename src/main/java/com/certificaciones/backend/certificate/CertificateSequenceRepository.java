package com.certificaciones.backend.certificate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificateSequenceRepository extends JpaRepository<CertificateSequence, Long> {

    Optional<CertificateSequence> findByTypeAndYear(CertificateType type, Integer year);
}
