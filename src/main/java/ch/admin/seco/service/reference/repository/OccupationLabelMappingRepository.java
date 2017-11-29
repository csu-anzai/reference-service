package ch.admin.seco.service.reference.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ch.admin.seco.service.reference.domain.OccupationLabelMapping;


/**
 * Spring Data JPA repository for the OccupationMapping entity.
 */
@Repository
public interface OccupationLabelMappingRepository extends JpaRepository<OccupationLabelMapping, UUID> {
    Optional<OccupationLabelMapping> findOneByAvamCode(int avamCode);

    @Query("select o from OccupationLabelMapping o where o.avamCode in (select m.avamCode from OccupationLabelMappingX28 m where m.x28Code = ?1)")
    Optional<OccupationLabelMapping> findOneByX28Code(int x28Code);

    List<OccupationLabelMapping> findByBfsCode(int bfsCode);
}
