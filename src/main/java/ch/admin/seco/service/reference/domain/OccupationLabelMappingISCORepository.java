package ch.admin.seco.service.reference.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OccupationLabelMappingISCORepository extends JpaRepository<OccupationLabelMappingISCO, UUID> {
    Optional<OccupationLabelMappingISCO> findOneByBfsCode(String bfsCode);
}
