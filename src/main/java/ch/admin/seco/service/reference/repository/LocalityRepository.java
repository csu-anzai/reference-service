package ch.admin.seco.service.reference.repository;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import ch.admin.seco.service.reference.domain.Locality;


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
