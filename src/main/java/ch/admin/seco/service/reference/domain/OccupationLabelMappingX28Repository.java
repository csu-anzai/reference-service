package ch.admin.seco.service.reference.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


/**
 * Spring Data JPA repository for the OccupationMapping entity.
 */
@Repository
public interface OccupationLabelMappingX28Repository extends JpaRepository<OccupationLabelMappingX28, UUID> {
}
