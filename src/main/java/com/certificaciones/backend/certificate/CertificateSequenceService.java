package com.certificaciones.backend.certificate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
public class CertificateSequenceService {

    private final CertificateSequenceRepository sequenceRepository;

    public CertificateSequenceService(CertificateSequenceRepository sequenceRepository) {
        this.sequenceRepository = sequenceRepository;
    }

    @Transactional
    public String generateCertificateNumber(CertificateType type) {
        int currentYear = Year.now().getValue();

        CertificateSequence sequence = sequenceRepository
                .findByTypeAndYear(type, currentYear)
                .orElseGet(() -> {
                    CertificateSequence newSequence = new CertificateSequence();
                    newSequence.setType(type);
                    newSequence.setYear(currentYear);
                    newSequence.setCurrentNumber(0L);
                    return newSequence;
                });

        Long nextNumber = sequence.getCurrentNumber() + 1;
        sequence.setCurrentNumber(nextNumber);

        sequenceRepository.save(sequence);

        String prefix = switch (type) {
            case TANK_CLEANING -> "LT";
            case PEST_CONTROL -> "CP";
        };

        return String.format("%s-%d-%06d", prefix, currentYear, nextNumber);
    }
}
