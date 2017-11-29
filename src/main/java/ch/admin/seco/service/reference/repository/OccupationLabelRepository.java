package ch.admin.seco.service.reference.repository;

import static org.hibernate.jpa.QueryHints.HINT_CACHE_MODE;
import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import ch.admin.seco.service.reference.domain.OccupationLabel;
import ch.admin.seco.service.reference.domain.enums.Language;

public interface OccupationLabelRepository extends JpaRepository<OccupationLabel, UUID> {
    @QueryHints({
        @QueryHint(name = HINT_FETCH_SIZE, value = "1000"),
        @QueryHint(name = HINT_CACHE_MODE, value = "IGNORE")})
    @Query("select o from OccupationLabel o")
    Stream<OccupationLabel> streamAll();

    List<OccupationLabel> findByCodeAndTypeAndLanguage(int code, String type, Language language);

    Optional<OccupationLabel> findOneByCodeAndTypeAndLanguageAndClassifier(int code, String type, Language language, String classifier);

    List<OccupationLabel> findByCodeAndTypeAndClassifier(int code, String type, String classifier);

}
