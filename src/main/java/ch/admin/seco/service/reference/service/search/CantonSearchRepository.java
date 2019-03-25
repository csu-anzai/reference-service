package ch.admin.seco.service.reference.service.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

/**
 * Spring Data Elasticsearch repository for the Canton entity.
 */
public interface CantonSearchRepository extends ElasticsearchRepository<CantonSuggestion, UUID> {
}
