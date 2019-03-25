package ch.admin.seco.service.reference.domain;

import ch.admin.seco.service.reference.domain.enums.Language;
import ch.admin.seco.service.reference.domain.enums.ProfessionCodeType;
import ch.admin.seco.service.reference.domain.valueobject.OccupationLabelKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hibernate.jpa.QueryHints.HINT_CACHE_MODE;
import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;

public interface OccupationLabelRepository extends JpaRepository<OccupationLabel, UUID> {

    @QueryHints({
        @QueryHint(name = HINT_FETCH_SIZE, value = "1000"),
        @QueryHint(name = HINT_CACHE_MODE, value = "IGNORE")})
    @Query("select o from OccupationLabel o")
    Stream<OccupationLabel> streamAll();

    List<OccupationLabel> findByCodeAndTypeAndLanguage(String professionCode, ProfessionCodeType professionType, Language language);

    Optional<OccupationLabel> findOneByCodeAndTypeAndLanguageAndClassifier(String professionCode, ProfessionCodeType professionType, Language language, String classifier);

    List<OccupationLabel> findByCodeAndTypeAndClassifier(String professionCode, ProfessionCodeType professionType, String classifier);

    @Query("select new ch.admin.seco.service.reference.domain.valueobject.OccupationLabelKey(o.type, o.code, o.language) from OccupationLabel o group by o.type, o.code, o.language")
    Stream<OccupationLabelKey> streamAllKeys();

    @Query(nativeQuery = true, value = "SELECT count(*) FROM (SELECT count(*) FROM OCCUPATION_LABEL o GROUP BY o.type, o.code, o.language) AS groups")
    long countAllKeys();

    default Map<String, String> getLabels(String professionCode, ProfessionCodeType professionType, Language language) {
        return findByCodeAndTypeAndLanguage(professionCode, professionType, language).stream()
            .collect(Collectors.toMap(OccupationLabel::getClassifier, OccupationLabel::getLabel));
    }

    Page<OccupationLabel> findAllByTypeAndLanguage(ProfessionCodeType professionType, Language language, Pageable page);

    Page<OccupationLabel> findAllByLabelStartingWithIgnoreCaseAndTypeAndLanguage(String prefix, ProfessionCodeType professionType, Language language, Pageable page);
}
