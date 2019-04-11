package ch.admin.seco.service.reference.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;


/**
 * Spring Data JPA repository for the Locality entity.
 */
@Repository
public interface LocalityRepository extends JpaRepository<Locality, UUID> {
    @QueryHints(@QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MAX_VALUE))
    @Query("select l from Locality l")
    Stream<Locality> streamAll();

    List<Locality> findByZipCode(String zipCode);
}
