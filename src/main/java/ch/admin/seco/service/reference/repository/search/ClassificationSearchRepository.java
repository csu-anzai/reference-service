package ch.admin.seco.service.reference.repository.search;

import java.util.List;
import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import ch.admin.seco.service.reference.domain.search.ClassificationSynonym;

/**
 * Spring Data Elasticsearch repository for the Classification entity.
 */
public interface ClassificationSearchRepository extends ElasticsearchRepository<ClassificationSynonym, UUID> {

    List<ClassificationSynonym> findAllByCodeEquals(int code);

    void deleteAllByCodeEquals(int code);
}