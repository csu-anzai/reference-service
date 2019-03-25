package ch.admin.seco.service.reference.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportingObligationRepository extends JpaRepository<ReportingObligation, UUID> {

    Optional<ReportingObligation> findOneBySbn5Code(String sbn5Code);

    List<ReportingObligation> findAllByCantonCodesIsNull();

    @Query(value = "select * from reporting_obligation where canton_codes like %?1%", nativeQuery = true)
    List<ReportingObligation> findAllByCantonCodesContaining(String cantonCode);
}
