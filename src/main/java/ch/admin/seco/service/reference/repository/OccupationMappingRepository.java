package ch.admin.seco.service.reference.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.admin.seco.service.reference.domain.OccupationMapping;


/**
 * Spring Data JPA repository for the OccupationMapping entity.
 */
@Repository
public interface OccupationMappingRepository extends JpaRepository<OccupationMapping, UUID> {

    List<OccupationMapping> findByAvamCode(int avamCode);

    // only X28 is unique
    Optional<OccupationMapping> findByX28Code(int x28Code);
}
