package ch.admin.seco.service.reference.repository;

import java.util.UUID;
import java.util.stream.Stream;

import javax.persistence.QueryHint;

import ch.admin.seco.service.reference.domain.Occupation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;


/**
 * Spring Data JPA repository for the Occupation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OccupationRepository extends JpaRepository<Occupation, UUID> {
    @QueryHints(value = @QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MAX_VALUE))
    @Query(value = "select j from Occupation j")
    Stream<Occupation> streamAll();
}
