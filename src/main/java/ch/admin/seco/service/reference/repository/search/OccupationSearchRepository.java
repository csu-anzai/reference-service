package ch.admin.seco.service.reference.repository.search;

import java.util.List;
import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import ch.admin.seco.service.reference.domain.search.OccupationSuggestion;

/**
 * Spring Data Elasticsearch repository for the OccupationSynonym entity.
 */
public interface OccupationSearchRepository extends ElasticsearchRepository<OccupationSuggestion, UUID> {

    List<OccupationSuggestion> findAllByCodeEquals(int code);

    void deleteAllByCodeEquals(int code);
}
