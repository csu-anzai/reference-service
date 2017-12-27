package ch.admin.seco.service.reference.repository.search;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import ch.admin.seco.service.reference.domain.search.OccupationLabelSuggestion;

/**
 * Spring Data Elasticsearch repository for the OccupationLabel entity.
 */
public interface OccupationLabelSearchRepository extends ElasticsearchRepository<OccupationLabelSuggestion, UUID> {

}
