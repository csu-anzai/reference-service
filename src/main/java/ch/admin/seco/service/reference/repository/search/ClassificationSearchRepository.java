package ch.admin.seco.service.reference.repository.search;

import java.util.List;
import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import ch.admin.seco.service.reference.domain.search.ClassificationSuggestion;

/**
 * Spring Data Elasticsearch repository for the Classification entity.
 */
public interface ClassificationSearchRepository extends ElasticsearchRepository<ClassificationSuggestion, UUID> {

    List<ClassificationSuggestion> findAllByCodeEquals(int code);

    void deleteAllByCodeEquals(int code);
}
