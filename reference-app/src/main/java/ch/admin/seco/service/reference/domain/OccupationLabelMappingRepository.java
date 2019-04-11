package ch.admin.seco.service.reference.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * Spring Data JPA repository for the OccupationMapping entity.
 */
@Repository
public interface OccupationLabelMappingRepository extends JpaRepository<OccupationLabelMapping, UUID> {
    Optional<OccupationLabelMapping> findOneByAvamCode(String avamCode);

    @Query("select o from OccupationLabelMapping o where o.avamCode in (select m.avamCode from OccupationLabelMappingX28 m where m.x28Code = ?1)")
    Optional<OccupationLabelMapping> findOneByX28Code(String x28Code);

    List<OccupationLabelMapping> findByBfsCode(String bfsCode);

    List<OccupationLabelMapping> findBySbn3Code(String sbn3Code);

    List<OccupationLabelMapping> findBySbn5Code(String sbn5Code);
}
