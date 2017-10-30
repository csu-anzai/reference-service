package ch.admin.seco.service.reference.repository;

import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import ch.admin.seco.service.reference.domain.Classification;


/**
 * Spring Data JPA repository for the Classification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClassificationRepository extends JpaRepository<Classification, UUID> {
    @QueryHints(@QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MAX_VALUE))
    @Query("select c from Classification c")
    Stream<Classification> streamAll();

    @Query(nativeQuery = true, value =
        "SELECT c.* FROM classification c WHERE c.code IN (SELECT o.classification_code FROM occupation o WHERE o.code IN (?1))")
    Stream<Classification> _findAllByOccupationCodes(Set<Integer> classificationCodes);

    default Stream<Classification> findAllByOccupationCodes(Set<Integer> classificationCodes) {
        return CollectionUtils.isEmpty(classificationCodes)
            ? Stream.empty()
            : _findAllByOccupationCodes(classificationCodes);
    }
}
