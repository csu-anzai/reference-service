package ch.admin.seco.service.reference.service.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

public interface LocalitySearchRepository extends ElasticsearchRepository<LocalitySuggestion, UUID> {
}
