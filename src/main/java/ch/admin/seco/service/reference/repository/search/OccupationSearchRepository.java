package ch.admin.seco.service.reference.repository.search;

import ch.admin.seco.service.reference.domain.search.OccupationSynonym;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Spring Data Elasticsearch repository for the Occupation entity.
 */
public interface OccupationSearchRepository extends ElasticsearchRepository<OccupationSynonym, String> {

    List<OccupationSynonym> findAllByCodeEquals(int code);

    void deleteAllByCodeEquals(int code);
}
