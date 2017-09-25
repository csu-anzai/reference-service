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
import org.springframework.util.CollectionUtils;

import ch.admin.seco.service.reference.domain.Classification;
import ch.admin.seco.service.reference.domain.Language;


/**
 * Spring Data JPA repository for the Classification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClassificationRepository extends JpaRepository<Classification, UUID> {
    @QueryHints(@QueryHint(name = HINT_FETCH_SIZE, value = "" + Integer.MAX_VALUE))
    @Query("select j from Classification j")
    Stream<Classification> streamAll();

    @Query(nativeQuery = true, value =
        "SELECT c.* FROM classification c LEFT JOIN occupation o ON c.code = o.classification_code" +
            " WHERE o.code IN (?2) and c.language = ?1")
    Stream<Classification> findAllByOccupationCodes(String language, List<Integer> classificationCodes);

    default Stream<Classification> findAllByOccupationCodes(Language language, List<Integer> classificationCodes) {
        return CollectionUtils.isEmpty(classificationCodes)
            ? Stream.empty()
            : findAllByOccupationCodes(language.name(), classificationCodes);
    }
}
