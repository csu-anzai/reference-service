package ch.admin.seco.service.reference.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.admin.seco.service.reference.domain.OccupationLabelMappingX28;


/**
 * Spring Data JPA repository for the OccupationMapping entity.
 */
@Repository
public interface OccupationLabelMappingX28Repository extends JpaRepository<OccupationLabelMappingX28, UUID> {
}
