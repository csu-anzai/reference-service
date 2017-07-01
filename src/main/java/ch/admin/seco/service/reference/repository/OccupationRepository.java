package ch.admin.seco.service.reference.repository;

import ch.admin.seco.service.reference.domain.Occupation;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;

import java.util.UUID;


/**
 * Spring Data JPA repository for the Occupation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OccupationRepository extends JpaRepository<Occupation,UUID> {

}
