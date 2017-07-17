package ch.admin.seco.service.reference.repository.search;

import java.util.List;
import java.util.UUID;

import ch.admin.seco.service.reference.domain.search.OccupationSynonym;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Occupation entity.
 */
public interface OccupationSearchRepository extends ElasticsearchRepository<OccupationSynonym, UUID> {

    List<OccupationSynonym> findAllByCodeEquals(int code);

    void deleteAllByCodeEquals(int code);
}
