package ch.admin.seco.service.reference.service.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.UUID;

/**
 * Spring Data Elasticsearch repository for the OccupationLabel entity.
 */
public interface OccupationLabelSearchRepository extends ElasticsearchRepository<OccupationLabelSuggestion, UUID> {

}
